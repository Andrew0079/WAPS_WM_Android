package android.bignerdranch.waps_11;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;


/**
 * This fragment is responsible for the time set for the wifi scanner.
 * It is also responsible for updating the password and the email address of the user..
 */
public class SettingsFragment extends Fragment {

    //instance variables
    //edit text to conect to the UI
    private EditText mNewPsw, mOldPsw;
    //seek bar to connect to the UI
    private SeekBar simpleSeekBar;
    //text view to connect to the UI
    private TextView mTextView;
    //shared prafarance to save data and retrieve data i multiple fragments
    private SharedPreferences mPreferences;
    //name that is constant in shared preference
    private final String PREF_NAME = "progress_pref";
    private final String PROGRESS_KEY = "progress_key";
    //minimum time of the scanner
    private int progressVal = 5;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //returning the view of this fragment
        View mView = inflater.inflate(R.layout.fragment_settings, container, false);

        //connecting to the UI
        mOldPsw = mView.findViewById(R.id.oldPsw);
        mNewPsw = mView.findViewById(R.id.passwordUp);
        mTextView = mView.findViewById(R.id.seekBarText);

        //initiate the button in the UI
        Button mPasswordBtn = mView.findViewById(R.id.pswBtn);

        // initiate the progress bar
        simpleSeekBar = mView.findViewById(R.id.seekBar);

        //seting the minimum and maximum time of the scanning interval
        simpleSeekBar.setMin(5);
        simpleSeekBar.setMax(15);

        //assigning the min/ and any given value by the user
        // to shared preference, for later use in other fragments
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("progress_pref", Context.MODE_PRIVATE);
        int progVal = sharedPreferences.getInt("progress_key", 5);

        //setting the value on the progress bar
        simpleSeekBar.setProgress(progVal);

        //calling the seek bar method
        seekBar();

        //connecting to shared preference for later use
        mPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        //pasword button to update old password
        mPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getting the old password from user
                String oldPsw = mOldPsw.getText().toString();
                //getting new password from user
                final String newPsw = mNewPsw.getText().toString();
                //validating old and new password
                if(oldPsw.equals("")){ mOldPsw.setError("Password Required!"); }
                if(newPsw.equals("")){ mNewPsw.setError("Password Required!"); }
                if(newPsw.length() < 6){ mNewPsw.setError("Password to Short!"); }
                else {
                    //connecting to firebase and getting current user
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    //checking if user is valid or not null
                    if(user != null) {
                        //getting the user's old email and password for re authentication
                        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), oldPsw);
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //if the re authentication is correct,
                                    // the user's old password will be updated
                                    user.updatePassword(newPsw).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                showToast("Update Successful");
                                                mOldPsw.setText("");
                                                mNewPsw.setText("");
                                            } else {
                                                showToast("Update Failed");
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
        return mView;
    }

    //custom Toast method
    //displays a custom Toast with a specified message
    private void showToast(String text){
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


    //seek bar method to set the desired scanning time of the wifi by the user
    //displays and sets the time for scanning by the user
    @SuppressLint("SetTextI18n")
    private void seekBar(){
        mTextView.setText("Frequency of Scanning: " + simpleSeekBar.getProgress() + " / " + simpleSeekBar.getMax() + " Second");
        simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressVal = progress;
                mTextView.setText("Frequency of Scanning: " + progress + " / " + simpleSeekBar.getMax() + " Second");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { mTextView.setText("Frequency of Scanning: " + progressVal + " / " + simpleSeekBar.getMax() + " Second"); }

        });
    }

    //on stop the given time for scanning by the user will be saved in shared preference
    // for later use by other fragments
    @Override
    public void onStop() {
        super.onStop();
        mPreferences.edit().putInt(PROGRESS_KEY, progressVal).apply();
    }
}
