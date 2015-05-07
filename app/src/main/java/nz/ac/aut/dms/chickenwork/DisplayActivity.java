package nz.ac.aut.dms.chickenwork;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

public class DisplayActivity extends Activity implements View.OnClickListener{

	private TextView textView;
	private static final int CAM_REQUEST = 1313;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
		textView = (TextView)findViewById(R.id.tagDisplay);
		Intent intent = getIntent();
		//Check mime type, get ndef message  from intent and display the message in text view
        if(intent.getType() != null && intent.getType().equals("text/plain")) {
        	Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            NdefRecord record = msg.getRecords()[0];
            String message = new String(record.getPayload());
            textView.setText(message);
        }
		
	}

	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.cambutton)
		{
			Intent cameraintent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(cameraintent, CAM_REQUEST);
		}
	}
}
