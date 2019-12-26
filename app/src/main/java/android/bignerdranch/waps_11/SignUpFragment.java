package android.bignerdranch.waps_11;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


/**
 *This fragment class is responsible for the signup process.
 * It requires an email address and a password.
 */
public class SignUpFragment extends Fragment {

    //instance variables

    //fragment manager to swipe fragments or redirection purposes
    private FragmentManager fragmentManager;
    //edit text, to capture the user interface's text field
    private EditText editTextEmail, editTextPassword;
    //firebase auth to connect to the database
    private FirebaseAuth mAuth;


    //returning the view of this fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { return  inflater.inflate(R.layout.fragment_sign_up, container, false); }

    //after the view created, the fragment is ready to interact
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //if the user visited this page by mistake,
        // this text field takes him/her back to the login page
        TextView mTextView = requireActivity().findViewById(R.id.register);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //fragment manager to navigate back to the login page
                fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainFrameFragment, new SignInFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    //on resume the app user is ready to interact with the application
    @Override
    public void onResume() {
        super.onResume();

        //connecting to the UI
        editTextEmail = requireActivity().findViewById(R.id.email);
        editTextPassword = requireActivity().findViewById(R.id.password);
        CardView mCardViewButton = requireActivity().findViewById(R.id.btnCard);

        //getting the firebase connection
        mAuth = FirebaseAuth.getInstance();

        //if the user is already logged in, this few lines of code redirect him/her to the first page of the app
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(requireActivity().getApplicationContext(),AppActivity.class));
            requireActivity().finish();
        }

        //on click listener, after providing an email address and a valid password,
        // the user will be able to sign up pressing the signup button
        mCardViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting the input text from the UI
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                //testing the email address and password
                if(TextUtils.isEmpty(email)){ editTextEmail.setError("Email is required!"); return; }
                if(TextUtils.isEmpty(password)){ editTextPassword.setError("Password is required!"); return; }
                if(password.length() < 6){ editTextPassword.setError("Password must be >= 6 Characters!");return; }

                //creating a user in fire base
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if the email and the password is valid and the user signed up successfully,
                        // the user will be redirected to the first page of the app
                        if(task.isSuccessful()){
                            //creating an email address field in database for each user
                            FirebaseDatabase.getInstance().getReference("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            showToast("User Created");
                            startActivity(new Intent(requireActivity().getApplicationContext(),AppActivity.class));
                        } else {
                            //if something went wrong, an error message will be displayed
                            showToast("Error " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
            }
        });

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

}
