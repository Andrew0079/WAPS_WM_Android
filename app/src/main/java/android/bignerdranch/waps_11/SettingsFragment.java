package android.bignerdranch.waps_11;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_settings, container, false);

        SeekBar simpleSeekBar=(SeekBar) mView.findViewById(R.id.seekBar); // initiate the progress bar
        simpleSeekBar.setMax(10); // 200 maximum value for the Seek bar
        simpleSeekBar.setProgress(2); // 50 default progress value

        return mView;
    }

}
