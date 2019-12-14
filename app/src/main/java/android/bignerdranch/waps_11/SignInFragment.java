package android.bignerdranch.waps_11;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
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
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private EditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editTextEmail = requireActivity().findViewById(R.id.email1);
        editTextPassword = requireActivity().findViewById(R.id.password1);
        CardView mCardViewButton = requireActivity().findViewById(R.id.btnCard1);
        mAuth = FirebaseAuth.getInstance();

        TextView register = requireActivity().findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameFragment, new SignUpFragment()).commit();
            }
        });

        mCardViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){ editTextEmail.setError("Email is required!"); return; }

                if(TextUtils.isEmpty(password)){ editTextPassword.setError("Password is required!"); return; }

                if(password.length() < 6){ editTextPassword.setError("Password must be >= 6 Characters!");return; }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(requireActivity().getApplicationContext(), "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(requireActivity().getApplicationContext(),AppActivity.class));
                        } else {
                            Toast.makeText(requireActivity().getApplicationContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}
