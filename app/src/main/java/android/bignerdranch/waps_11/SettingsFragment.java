package android.bignerdranch.waps_11;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private EditText mNewPsw, mOldPsw;
    private SeekBar simpleSeekBar;
    private TextView mTextView;
    private SharedPreferences mPreferences;
    private final String PREF_NAME = "progress_pref";
    private final String PROGRESS_KEY = "progress_key";
    private int progressVal = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_settings, container, false);

        mOldPsw = mView.findViewById(R.id.oldPsw);
        mNewPsw = mView.findViewById(R.id.passwordUp);
        Button mPasswordBtn = mView.findViewById(R.id.pswBtn);
        simpleSeekBar = (SeekBar) mView.findViewById(R.id.seekBar); // initiate the progress bar
        simpleSeekBar.setMin(5);
        simpleSeekBar.setMax(15);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("progress_pref", Context.MODE_PRIVATE);
        int progVal = sharedPreferences.getInt("progress_key", 5);
        simpleSeekBar.setProgress(progVal);
        mTextView = mView.findViewById(R.id.seekBarText);
        seekBar();
        mPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        mPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPsw = mOldPsw.getText().toString();
                final String newPsw = mNewPsw.getText().toString();
                if(newPsw.equals("")){ showToast("Password Required!"); }

                if(newPsw.length() < 6){ showToast("Password to Short!"); }
                else {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),oldPsw);

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                user.updatePassword(newPsw).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            showToast("Update Successful");
                                            mOldPsw.setText("");
                                            mNewPsw.setText("");
                                        } else {
                                            showToast("Update Successful");
                                        }

                                    }
                                });
                            }
                        }
                    });

                }

            }
        });


        return mView;



    }

    public void showToast(String text){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) requireActivity().findViewById(R.id.toast_root));

        TextView toastText = layout.findViewById(R.id.toast_txt);
        toastText.setText(text);


        Toast toast = new Toast(requireActivity().getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
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
