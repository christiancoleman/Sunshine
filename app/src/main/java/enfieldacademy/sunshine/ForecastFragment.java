package enfieldacademy.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public static final String INTENT_KEY = "";

    private final String TAG = "MainActivityFragment";

    private ArrayAdapter<String> forecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh){
            FetchWeatherTask weatherTask = new FetchWeatherTask("44203");
            weatherTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayList<String> forecasts = new ArrayList<>();
        forecasts.add(0, "Today — Sunny — 88 / 63");
        forecasts.add(1, "Tomorrow — Foggy — 70 / 46");
        forecasts.add(2, "Weds — Cloudy — 72 / 63");
        forecasts.add(3, "Thurs — Rainy — 64 / 51");
        forecasts.add(4, "Fri — Foggy — 70 / 46");
        forecasts.add(5, "Sat — Sunny — 76 / 88");

        forecastAdapter =
                new ArrayAdapter<>(getActivity(),
                                            R.layout.list_item_forecast,
                                            R.id.list_item_forecast_textview,
                                            forecasts);

        ListView lv = (ListView) rootView.findViewById(R.id.listview_forecast);
        lv.setAdapter(forecastAdapter);

        FetchWeatherTask weatherTask = new FetchWeatherTask("44203");
        weatherTask.execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailActivityIntent = new Intent(getActivity(), DetailActivity.class);
                detailActivityIntent.putExtra(INTENT_KEY, forecastAdapter.getItem(position));
                startActivity(detailActivityIntent);
            }
        });

        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<Void, Void, String[]>{

        private final String BASE_WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
        private final String POSTAL_CODE_QUERY = "q";
        private final String UNITS_QUERY = "units";
        private final String NUM_OF_DAYS_QUERY = "cnt";

        private URL url;
        private HttpURLConnection urlConnection = null;
        private BufferedReader reader = null;
        private String forecastJsonStr = null;

        FetchWeatherTask(String postalcode){
            try {
                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.appendQueryParameter(POSTAL_CODE_QUERY, postalcode);
                uriBuilder.appendQueryParameter(UNITS_QUERY, "metric");
                uriBuilder.appendQueryParameter(NUM_OF_DAYS_QUERY , "7");
                url = new URL(BASE_WEATHER_URL + uriBuilder.toString());
            }catch (MalformedURLException e){
                Log.e(TAG, "URL was malformed: ", e);
            }
        }

        @Override
        protected String[] doInBackground(Void... params) {
            String[] weatherForecasts = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream != null){
                    // nothing to do
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0){
                    forecastJsonStr = null;
                }

                forecastJsonStr = buffer.toString();

                try {
                    weatherForecasts = getWeatherDataFromJson(forecastJsonStr, 7);
                } catch (JSONException e){
                    Log.e(TAG, "JSONException!: ", e);
                }
            } catch (IOException e){
                Log.e(TAG, "IOException occurred: ", e);
            } finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try{
                        reader.close();
                    } catch (final IOException e){
                        Log.e(TAG, "Error closing stream");
                    }
                }
            }
            return weatherForecasts;
        }

        @Override
        protected void onPostExecute(String[] weatherForecasts) {
            super.onPostExecute(weatherForecasts);
            ArrayList<String> forecastList = new ArrayList<String>();
            for(String forecast : weatherForecasts){
                forecastList.add(forecast);
            }
            forecastAdapter.clear();
            for(String forecast : forecastList) {
                forecastAdapter.add(forecast);
            }
        }

        private String getReadableDateString(long time){
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE, MMM dd");
            return shortenedDateFormat.format(time);
        }

        private String formatHighLows(double high, double low){
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);
            return roundedHigh + " / " + roundedLow;
        }

        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            return resultStrs;
        }
    }
}
