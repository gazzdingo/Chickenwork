package nz.ac.aut.dms.chickenwork;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;


public class MainActivity extends ActionBarActivity implements OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onClick(View v) {

        if(v.getId() == R.id.btnWrite)
        {
            Intent intent = new Intent(this.getBaseContext(), WriterActivity.class);
            startActivity(intent);
//            for testing location with out having an nfc tag (Guy)
//            Intent intent = new Intent(this.getBaseContext(), DisplayActivity.class);
//            startActivity(intent);
        }
        else if(v.getId() == R.id.btnMap)
        {
            Intent intent = new Intent(this.getBaseContext(), MapsActivity.class);
            startActivity(intent);
        }
    }
}
