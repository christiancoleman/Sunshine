package enfieldacademy.sunshine.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import enfieldacademy.sunshine.FetchWeatherTask;
import enfieldacademy.sunshine.R;
import enfieldacademy.sunshine.activities.DetailActivity;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public static final String INTENT_KEY = "FORECAST_DETAIL_INTENT";

    private final String TAG = "MainActivityFragment";

    private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> forecasts = new ArrayList<>();

        mForecastAdapter =
                new ArrayAdapter<>(getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        forecasts);

        ListView lv = (ListView) rootView.findViewById(R.id.listview_forecast);

        lv.setAdapter(mForecastAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailActivityIntent = new Intent(getActivity(), DetailActivity.class);
                detailActivityIntent.putExtra(INTENT_KEY, mForecastAdapter.getItem(position));
                startActivity(detailActivityIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void updateWeather(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        String zipcodePrefKey = getString(R.string.pref_location_key);
        String zipcodePrefValue = getString(R.string.pref_location_default);
        String usersZipcode = sharedPreferences.getString(zipcodePrefKey, zipcodePrefValue);

        /*String unitsPrefKey = getString(R.string.pref_units_key);
        String unitsPrefValue = getString(R.string.pref_units_default);
        String usersUnits = sharedPreferences.getString(unitsPrefKey, unitsPrefValue);*/

        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);
        weatherTask.execute(usersZipcode);
    }
}
