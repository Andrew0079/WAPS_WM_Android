package android.bignerdranch.waps_11;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private SeekBar simpleSeekBar;
    private TextView mTextView;
    private SharedPreferences mPreferences;
    private final String PREF_NAME = "progress_pref";
    private final String PROGRESS_KEY = "progress_key";
    private int progressVal = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_settings, container, false);

        simpleSeekBar = (SeekBar) mView.findViewById(R.id.seekBar); // initiate the progress bar
        simpleSeekBar.setMin(2);
        simpleSeekBar.setMax(10);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("progress_pref", Context.MODE_PRIVATE);
        int progVal = sharedPreferences.getInt("progress_key", 2);
        simpleSeekBar.setProgress(progVal);
        mTextView = mView.findViewById(R.id.seekBarText);
        seekBar();

        mPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        return mView;



    }


    private void seekBar(){
        mTextView.setText("Frequency of Scanning: " + simpleSeekBar.getProgress() + " / " + simpleSeekBar.getMax() + " Second");
        simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressVal = progress;
                mTextView.setText("Frequency of Scanning: " + progress + " / " + simpleSeekBar.getMax() + " Second");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mTextView.setText("Frequency of Scanning: " + progressVal + " / " + simpleSeekBar.getMax() + " Second");

            }

        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mPreferences.edit().putInt(PROGRESS_KEY, progressVal).apply();
    }
}
