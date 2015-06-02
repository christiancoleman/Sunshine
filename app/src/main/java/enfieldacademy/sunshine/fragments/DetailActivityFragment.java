package enfieldacademy.sunshine.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import enfieldacademy.sunshine.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private final String TAG = "DetailActivityFragment";

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        String s = intent.getExtras().getString(ForecastFragment.INTENT_KEY);
        Log.d(TAG, "string s = " + s);
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }
}
