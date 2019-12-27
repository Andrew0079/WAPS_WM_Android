package android.bignerdranch.waps_11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This activity hosts multiple fragments.
 * Soul of the application
 *
 * */
public class AppActivity extends AppCompatActivity {

    //navigation view UI
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);


        //setting adn connecting to UI
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //connecting to nav View UI
        NavigationView mNavigationView = findViewById(R.id.nav_view);

        //getting the menu items from navigation
        Menu menuNav = mNavigationView.getMenu();
        //logout item, user can log out
        MenuItem logoutItem = menuNav.findItem(R.id.Logout);
        logoutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                logOut();
                return false;
            }
        });


        //navigation menu, user can navigate between fragments
        mDrawerLayout = findViewById(R.id.drawer_layout);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                //list of menu items, each connects to a specific fragment
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScannerFragment()).commit();
                        break;
                    case R.id.nav_list:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WifiListFragment()).commit();
                        break;
                    case R.id.location:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();
                        break;
                    case R.id.indoorLocation:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new IARegionFragment()).commit();
                        break;
                    case R.id.locationList:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LocationListFragment()).commit();
                        break;
                    case R.id.nav_settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                        break;
                    case R.id.Logout:
                        logOut();
                        break;
                    case R.id.deleteAc:
                        deleteUser();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        //toggle between ope and close of the navigation menu
        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        //if the user logs in for first time and the activity doesn't host any fragment
        //this code will draws the first fragment on the screen
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScannerFragment()).commit();
            mNavigationView.setCheckedItem(R.id.nav_home);
        }
    }

    //handling back pressd on navigation
    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){ mDrawerLayout.closeDrawer(GravityCompat.START); }
        else { super.onBackPressed(); }
    }

    //deleting user from firebase database, and all the user's data
    public void deleteUser() {
        //getting password from shared pref
        SharedPreferences sharedPreferences = getSharedPreferences("progress_pref", Context.MODE_PRIVATE);
        String pwd = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("pwd", "");

        //getting user id from database
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        //deleting user's data from database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(currentFirebaseUser.getUid());
        dbRef.removeValue();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get auth credentials from the user for re-authentication.
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), pwd);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showToast("Account Deleted.");
                            startActivity(new Intent(AppActivity.this, MainActivity.class));
                        } else {
                            showToast("Unable to Delete Account.\nTry Again!");
                        }
                    }
                });
            }
        });
    }

    //log out method to log the user out
    public void logOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    //custom Toast method
    //displays a custom Toast with a specified message
    private void showToast(String text){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout,  (ViewGroup) findViewById(R.id.toast_root));

        TextView toastText = layout.findViewById(R.id.toast_txt);
        toastText.setText(text);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
