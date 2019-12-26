package android.bignerdranch.waps_11;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
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


/**
 * This fragment class is responsible for the sign in process.
 *  * It requires an email address and a password.
 */
public class SignInFragment extends Fragment {

    //edit text, to capture the UI text field
    private EditText editTextEmail, editTextPassword;
    //firebase auth to connect to the database
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    //after the view created, the fragment is ready to interact
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //connecting to the UI
        editTextEmail = requireActivity().findViewById(R.id.email1);
        editTextPassword = requireActivity().findViewById(R.id.password1);
        CardView mCardViewButton = requireActivity().findViewById(R.id.btnCard1);

        //getting the firebase connection
        mAuth = FirebaseAuth.getInstance();

        //if the user doesn't have an account,
        //by clicking on the register text field the user will redirected to a register page
        // where the user can register
        TextView register = requireActivity().findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameFragment, new SignUpFragment()).commit();
            }
        });

        //on click listener, after providing an email address and a valid password,
        // the user will be able to sign in pressing the sign in button
        mCardViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting the email from user
                String email = editTextEmail.getText().toString().trim();
                //getting thte password from user
                String password = editTextPassword.getText().toString().trim();

                //validating email and password
                if(TextUtils.isEmpty(email)){ editTextEmail.setError("Email is required!"); return; }
                if(TextUtils.isEmpty(password)){ editTextPassword.setError("Password is required!"); return; }
                if(password.length() < 6){ editTextPassword.setError("Password must be >= 6 Characters!");return; }

                //if email and pasword is correct,
                // user will be signed in
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if the email and the password is valid and the user signed in successfully,
                        // the user will be redirected to the first page of the app
                        if(task.isSuccessful()){
                            showToast("Logged in Successfully");
                            startActivity(new Intent(requireActivity().getApplicationContext(),AppActivity.class));
                        } else {
                            //if something went wrong, an error message will be displayed
                            showToast("Error " + task.getException().getMessage());
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
