package android.bignerdranch.waps_11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;

public class RegionActivity extends FragmentActivity implements IALocationListener, IARegion.Listener{

    private IALocationManager mManager;
    private IARegion mCurrentVenue = null;
    private IARegion mCurrentFloorPlan = null;
    private Integer mCurrentFloorLevel = null;
    private Float mCurrentCertainty = null;

    private TextView mUiVenue;
    private TextView mUiVenueId;
    private TextView mUiFloorPlan;
    private TextView mUiFloorPlanId;
    private TextView mUiFloorLevel;
    private TextView mUiFloorCertainty;
    private TextView mlatLong;

    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        mManager = IALocationManager.create(this);
        mManager.registerRegionListener(this);
        mManager.requestLocationUpdates(IALocationRequest.create(), this);

        mUiVenue = findViewById(R.id.text_view_venue);
        mUiVenueId = findViewById(R.id.text_view_venue_id);
        mUiFloorPlan = findViewById(R.id.text_view_floor_plan);
        mUiFloorPlanId = findViewById(R.id.text_view_floor_plan_id);
        mUiFloorLevel = findViewById(R.id.text_view_floor_level);
        mUiFloorCertainty = findViewById(R.id.text_view_floor_certainty);
        mlatLong =  findViewById(R.id.latLONG);

        updateUi();

        Toolbar mToolbar = findViewById(R.id.toolbar2);
        //setSupportActionBar(mToolbar);

        NavigationView mNavigationView = findViewById(R.id.nav_view2);

        Menu menuNav = mNavigationView.getMenu();
        MenuItem logoutItem = menuNav.findItem(R.id.Logout);
        logoutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                logOut();
                return false;
            }
        });


        mDrawerLayout = findViewById(R.id.drawer_layout2);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.nav_home:

                        break;
                    case R.id.nav_list:

                        break;
                    case R.id.location:

                        break;
                    case R.id.indoorLocation:

                        break;
                    case R.id.locationList:

                        break;
                    case R.id.nav_settings:

                        break;
                    case R.id.Logout:
                        logOut();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){ mDrawerLayout.closeDrawer(GravityCompat.START); }
        else { super.onBackPressed(); }
    }

    public void logOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mManager.destroy();
    }

    @Override
    public void onLocationChanged(IALocation iaLocation) {
        LatLng latLng = new LatLng(iaLocation.getLatitude(), iaLocation.getLongitude());
        setText(mlatLong,latLng.toString(),true);
        mCurrentFloorLevel = iaLocation.hasFloorLevel() ? iaLocation.getFloorLevel() : null;
        mCurrentCertainty = iaLocation.hasFloorCertainty() ? iaLocation.getFloorCertainty() : null;
        updateUi();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    @Override
    public void onEnterRegion(IARegion iaRegion) {
        if (iaRegion.getType() == IARegion.TYPE_VENUE) {
            mCurrentVenue = iaRegion;
        } else if (iaRegion.getType() == IARegion.TYPE_FLOOR_PLAN) {
            mCurrentFloorPlan = iaRegion;
        }
        updateUi();
    }

    @Override
    public void onExitRegion(IARegion iaRegion) {
        if (iaRegion.getType() == IARegion.TYPE_VENUE) {
            mCurrentVenue = iaRegion;
        } else if (iaRegion.getType() == IARegion.TYPE_FLOOR_PLAN) {
            mCurrentFloorPlan = iaRegion;
        }
        updateUi();
    }

    public void updateUi() {
        String venue = getString(R.string.venue_outside);
        String venueId = "";
        String floorPlan = "";
        String floorPlanId = "";
        String level = "";
        String certainty = "";
        if (mCurrentVenue != null) {
            venue = getString(R.string.venue_inside);
            venueId = mCurrentVenue.getId();
            if (mCurrentFloorPlan != null) {
                floorPlan = mCurrentFloorPlan.getName();
                floorPlanId = mCurrentFloorPlan.getId();
            } else {
                floorPlan = getString(R.string.floor_plan_outside);
            }
        }

        if (mCurrentFloorLevel != null) { level = mCurrentFloorLevel.toString(); }
        if (mCurrentCertainty != null) { certainty = getString(R.string.floor_certainty_percentage, mCurrentCertainty * 100.0f); }
        setText(mUiVenue, venue, true);
        setText(mUiVenueId, venueId, true);
        setText(mUiFloorPlan, floorPlan, true);
        setText(mUiFloorPlanId, floorPlanId, true);
        setText(mUiFloorLevel, level, true);
        setText(mUiFloorCertainty, certainty, false); // do not animate as changes can be frequent
    }

    /**
     * Set the text of a TextView and make a animation to notify when the value has changed
     */
    public void setText(@NonNull TextView view, @NonNull String text, boolean animateWhenChanged) {
        if (!view.getText().toString().equals(text)) {
            view.setText(text);
            if (animateWhenChanged) {
                view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.notify_change));
            }
        }
    }
}