package nz.ac.aut.dms.chickenwork;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.nio.charset.Charset;


public class WriterActivity extends ActionBarActivity implements View.OnClickListener{

    private NfcAdapter nfcAdapter;
    private Button btnWrite;
    private EditText txtTagMsg;
    private TextView txtNFCStatus;
    private boolean writeModeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        btnWrite = (Button) findViewById(R.id.btnWriteTag);
        txtTagMsg = (EditText) findViewById(R.id.txtTagMsg);
        txtNFCStatus = (TextView) findViewById(R.id.txtNFCStatus);
        writeModeEnabled = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_writer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        disableWriteMode();
    }

    @Override
    public void onNewIntent(Intent intent) {
        if(writeModeEnabled) {
            writeModeEnabled = false;

            // write to newly scanned tag
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            writeTag(tag);
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnWriteTag)
        {
            txtNFCStatus.setText("Write mode enabled, hold phone to tag");
            enableWriteMode();
        }
    }

    private boolean writeTag(Tag tag) {

        // record that contains our custom data from textfield, using custom MIME_TYPE
        String textToSend = txtTagMsg.getText().toString();
        byte[] payload = textToSend.getBytes();
        byte[] mimeBytes = "text/plain".getBytes(Charset.forName("US-ASCII"));
        NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes,
                new byte[0], payload);
        NdefMessage message = new NdefMessage(new NdefRecord[] { record});

        try {
            // see if tag is already NDEF formatted
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    txtNFCStatus.setText("Read-only tag");
                    return false;
                }

                // work out how much space we need for the data
                int size = message.toByteArray().length;
                if (ndef.getMaxSize() < size) {
                    txtNFCStatus.setText("Tag doesn't have enough free space\nMessage Size: " + size + "\nAvailable Space: " + ndef.getMaxSize());
                    return false;
                }

                ndef.writeNdefMessage(message);
                txtNFCStatus.setText("Tag written successfully.");
                return true;
            } else {
                // attempt to format tag
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        txtNFCStatus.setText("Tag written successfully!\nClose this app and scan tag.");
                        return true;
                    } catch (IOException e) {
                        txtNFCStatus.setText("Unable to format tag to NDEF.");
                        return false;
                    }
                } else {
                    txtNFCStatus.setText("Tag doesn't appear to support NDEF format.");
                    return false;
                }
            }
        } catch (Exception e) {
            txtNFCStatus.setText("Failed to write tag");
        }

        return false;
    }

    private void enableWriteMode() {
        writeModeEnabled = true;

        // set up a PendingIntent to open the app when a tag is scanned
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[] { tagDetected };

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    private void disableWriteMode() {
        nfcAdapter.disableForegroundDispatch(this);
    }
}
