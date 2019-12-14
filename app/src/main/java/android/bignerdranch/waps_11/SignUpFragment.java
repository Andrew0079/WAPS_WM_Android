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
public class SignUpFragment extends Fragment {

    private FragmentManager fragmentManager;
    private EditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return  inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView mTextView = requireActivity().findViewById(R.id.register);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainFrameFragment, new SignInFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        editTextEmail = requireActivity().findViewById(R.id.email);
        editTextPassword = requireActivity().findViewById(R.id.password);
        CardView mCardViewButton = requireActivity().findViewById(R.id.btnCard);
        mAuth = FirebaseAuth.getInstance();


//        if(mAuth.getCurrentUser() != null){
//            startActivity(new Intent(requireActivity().getApplicationContext(),AppActivity.class));
//            requireActivity().finish();
//        }


        mCardViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){ editTextEmail.setError("Email is required!"); return; }

                if(TextUtils.isEmpty(password)){ editTextPassword.setError("Password is required!"); return; }

                if(password.length() < 6){ editTextPassword.setError("Password must be >= 6 Characters!");return; }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(requireActivity().getApplicationContext(), "User Created", Toast.LENGTH_SHORT).show();
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
