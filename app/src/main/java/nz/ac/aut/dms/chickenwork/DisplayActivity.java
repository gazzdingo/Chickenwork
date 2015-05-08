package nz.ac.aut.dms.chickenwork;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DisplayActivity extends Activity implements View.OnClickListener{


	private TextView textView;
	private static final int CAM_REQUEST = 1313;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        textView = (TextView) findViewById(R.id.tagDisplay);
        Intent intent = getIntent();
        //Check mime type, get ndef message  from intent and display the message in text view
        if (intent.getType() != null && intent.getType().equals("text/plain")) {
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

        if(view.getId() == R.id.btnSaveLocation){
            StrictMode.ThreadPolicy p = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(p);
            LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);   //default

            // user defines the criteria

            criteria.setCostAllowed(false);
            // get the best provider depending on the criteria
            String provider = locationManager.getBestProvider(criteria, false);





            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Location location = locationManager.getLastKnownLocation(provider);
                      if(location == null){
                          Toast.makeText(this,"could not find your location", Toast.LENGTH_LONG).show();
                      }else {
                          double longitude = location.getLongitude();
                          double latitude = location.getLatitude();
                          HttpClient httpclient = new DefaultHttpClient();
                          HttpPost httppost = new HttpPost("http://langford-lee.com:8080/c/addlocation");

                          try {


                              String lat = String.valueOf(latitude);
                              String lon = String.valueOf(longitude);

                              List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                              nameValuePairs.add(new BasicNameValuePair("lat", lat));
                              nameValuePairs.add(new BasicNameValuePair("lon", lon));
                              httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                              HttpResponse responce = httpclient.execute(httppost);


                          } catch (IOException e) {
                              // TODO Auto-generated catch block
                          }
                      }
            }else{
                Toast.makeText(this, "GPS Must Be Off turn it on and Try Again", Toast.LENGTH_LONG).show();
            }
        }

        }
	}

