package enfieldacademy.sunshine.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import enfieldacademy.sunshine.R;


public class DetailActivity extends ActionBarActivity {

    private final String TAG = "DetailActivity";

    private Toast mToast;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
            if(mapIntent.resolveActivity(getPackageManager()) != null){
                startActivity(mapIntent);
            } else {
                mToast = Toast.makeText(this, "No apps found to perform the request", Toast.LENGTH_SHORT);
                mToast.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
