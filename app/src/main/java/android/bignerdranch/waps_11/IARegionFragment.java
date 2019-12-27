package android.bignerdranch.waps_11;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;


/**
 * This fragment deals with Automatic Venue and Floor Detection
 * Demonstrates automatic region changes i.e. automatic venue detection and floor detection.
 *source code: https://github.com/IndoorAtlas/android-sdk-examples
 * this fragment example is slightly modified,
 * however the base belongs to Indoor Atlas
 * been taken from the examples
 *
 * Displayed data doesn't get saved into a database
 *
 */
public class IARegionFragment extends Fragment implements IALocationListener, IARegion.Listener {

    //instance variable

    //indoor atlas instances
    private IALocationManager mManager;
    private IARegion mCurrentVenue = null;
    private IARegion mCurrentFloorPlan = null;
    private Integer mCurrentFloorLevel = null;
    private Float mCurrentCertainty = null;

    //text view to connect and interact with the UI
    private TextView mUiVenue;
    private TextView mUiVenueId;
    private TextView mUiFloorPlan;
    private TextView mUiFloorPlanId;
    private TextView mUiFloorLevel;
    private TextView mUiFloorCertainty;

    //longitude and latitude
    private TextView mlatLong;


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_iaregion, container, false);

        //requiring context, listener from Indoor Atlas API
        mManager = IALocationManager.create(requireContext());
        mManager.registerRegionListener(this);
        mManager.requestLocationUpdates(IALocationRequest.create(), this);

        //connecting to the UI
        mUiVenue = mView.findViewById(R.id.text_view_venue);
        mUiVenueId = mView.findViewById(R.id.text_view_venue_id);
        mUiFloorPlan = mView.findViewById(R.id.text_view_floor_plan);
        mUiFloorPlanId = mView.findViewById(R.id.text_view_floor_plan_id);
        mUiFloorLevel = mView.findViewById(R.id.text_view_floor_level);
        mUiFloorCertainty = mView.findViewById(R.id.text_view_floor_certainty);
        mlatLong = mView.findViewById(R.id.latLONG);


        //returning this fragment's view
        return mView;
    }

    @Override
    public void onLocationChanged(IALocation iaLocation) {
        //getting Lang and Long and assigning it to the LatLng object for later use
        LatLng latLng = new LatLng(iaLocation.getLatitude(), iaLocation.getLongitude());
        //setting the lat long values onto the UI
        setText(mlatLong,latLng.toString(),true);
        //getting the floor level, null if there's non or not mapped
        mCurrentFloorLevel = iaLocation.hasFloorLevel() ? iaLocation.getFloorLevel() : null;
        //getting Certainty
        mCurrentCertainty = iaLocation.hasFloorCertainty() ? iaLocation.getFloorCertainty() : null;
        //updating UI
        updateUi();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    @Override
    public void onEnterRegion(IARegion iaRegion) {
        //entering region and updating UI
        if (iaRegion.getType() == IARegion.TYPE_VENUE) {
            mCurrentVenue = iaRegion;
        } else if (iaRegion.getType() == IARegion.TYPE_FLOOR_PLAN) {
            mCurrentFloorPlan = iaRegion;
        }
        updateUi();
    }

    @Override
    public void onExitRegion(IARegion iaRegion) {
        //on exit region and updating UI
        if (iaRegion.getType() == IARegion.TYPE_VENUE) {
            mCurrentVenue = iaRegion;
        } else if (iaRegion.getType() == IARegion.TYPE_FLOOR_PLAN) {
            mCurrentFloorPlan = iaRegion;
        }
        updateUi();
    }

    //update UI method deals with all the data received, retrieved
    private void updateUi() {
        //initializing values that will be displayed
        String venue = getString(R.string.venue_outside);
        String venueId = "";
        String floorPlan = "";
        String floorPlanId = "";
        String level = "";
        String certainty = "";
        //checking if current venue is null or not
        if (mCurrentVenue != null) {
            venue = getString(R.string.venue_inside);
            venueId = mCurrentVenue.getId();
            //checking if floor pla exist or not
            if (mCurrentFloorPlan != null) {
                //getting floor plan details
                floorPlan = mCurrentFloorPlan.getName();
                floorPlanId = mCurrentFloorPlan.getId();
            } else {
                //if no floor plan
                floorPlan = getString(R.string.floor_plan_outside);
            }
        }

        //checking for the current floor
        if (mCurrentFloorLevel != null) { level = mCurrentFloorLevel.toString(); }
        if (mCurrentCertainty != null) { certainty = getString(R.string.floor_certainty_percentage, mCurrentCertainty * 100.0f); }
        //updating UI with the retrieved data
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
    private void setText(@NonNull TextView view, @NonNull String text, boolean animateWhenChanged) {
        if (!view.getText().toString().equals(text)) {
            view.setText(text);
            if (animateWhenChanged) {
                view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.notify_change));
            }
        }
    }

    //destroying IALocationManager if user leaves the fragment
    @Override
    public void onStop() {
        super.onStop();
        mManager.destroy();

    }
}
