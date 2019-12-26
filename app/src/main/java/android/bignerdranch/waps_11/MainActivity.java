package android.bignerdranch.waps_11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

/**
 *
 * MainActivity host two fragments sign up and sign in fragments
 *
 *
 * */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //fragment manager, will automatically hosts the first fragment at runtime
        //hosts the log in fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrameFragment, new SignInFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
