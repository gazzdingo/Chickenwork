package nz.ac.aut.dms.chickenwork;

import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.JsonReader;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setUpMapIfNeeded();
        readInData();


    }

    private void readInData() {
        String jsonString = null;
        try {
             jsonString = httpGet("http://langford-lee.com:8080/c/getlocation");
            JSONArray array = new JSONArray(jsonString);
            for (int i = 0; i < array.length();i++){
               JSONObject obj = array.getJSONObject(i);
               String lat =  obj.getString("lat");
               String lon =  obj.getString("lon");
               double latDouble = Double.parseDouble(lat);
               double lonDouble = Double.parseDouble(lon);
               addPin(latDouble,lonDouble);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String httpGet(String uri) throws IOException, URISyntaxException {

        StrictMode.ThreadPolicy p = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(p);
        HttpClient httpclient = new DefaultHttpClient();

        HttpGet request = new HttpGet();
        URI website = new URI(uri);
        request.setURI(website);
        HttpResponse response = httpclient.execute(request);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                response.getEntity().getContent()));

        String temp = null;
        String content = "";
        while(( temp = in.readLine()) != null)
         content += temp;
        return content;
    }



    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void addPin(Double lat,Double lon) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Marker"));
    }
}
