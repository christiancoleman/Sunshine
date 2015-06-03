package enfieldacademy.sunshine.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.net.Uri;


import enfieldacademy.sunshine.R;


public class MainActivity extends ActionBarActivity {

    private Toast mToast;

    private final String TAG = "MainActivity";

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
        // as you specify a parent activity in AndroidManifest.pref_general.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        if(id == R.id.action_view_preferred_location){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String zipcodePrefKey = getString(R.string.pref_location_key);
            String zipcodePrefValue = getString(R.string.pref_location_default);
            String usersZipcode = sharedPreferences.getString(zipcodePrefKey, zipcodePrefValue);

            Intent mapIntent = new Intent(Intent.ACTION_VIEW);
            Uri mapUri = Uri.parse("geo:0,0?q=" + usersZipcode);
            mapIntent.setData(mapUri);
            Log.d(TAG, "heyoo" + mapUri);
            if(mapIntent.resolveActivity(getPackageManager()) != null){
                startActivity(mapIntent);
            } else {
                mToast = Toast.makeText(this, "NOPE :(", Toast.LENGTH_SHORT);
                mToast.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
