package android.bignerdranch.waps_11;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


/**
 * This fragment displays the list of saved location's information.
 */
public class LocationListFragment extends Fragment {

    //Recycler View to display all the information from the database
    private RecyclerView recyclerView;
    //an adapter to display all the information from the database
    private FirebaseRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_location_list, container, false);

        //connecting to the UI
        recyclerView = mView.findViewById(R.id.recycler_view_Locationdatabase);
        //setting the layout manager

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        //calling the fetch method
        fetch();

        //returning the view of this fragment
        return mView;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        //text view to connect to the UI
        private TextView mTextView;

        private ViewHolder(View itemView) {
            super(itemView);
            //connecting to the UI
            mTextView = itemView.findViewById(R.id.text_holder_location);
        }
        //setting the retrieved information in a text view inside the UI
        private void setTxtTitle(String string) { mTextView.setText(string); }
    }


    //fetch method to retrieve all the data from the database
    private void fetch() {
        //getting the current user's information
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //getting the user's id
        String userId = null;
        if (currentFirebaseUser != null) { userId = (String) currentFirebaseUser.getUid(); }
        assert userId != null;
        //getting all the informations from the database, using the user's id
        Query query = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("location");

        //gettig the loations information and saving it in a LOCATION OBJECT CLASS
        FirebaseRecyclerOptions<LocationObject> options = new FirebaseRecyclerOptions.Builder<LocationObject>().setQuery(query, new SnapshotParser<LocationObject>() {
            @NonNull
            @Override
            public LocationObject parseSnapshot(@NonNull DataSnapshot snapshot) {
                //returning the LOCATION OBJECT CLASS
                return new LocationObject((Double) snapshot.child("longitude").getValue(), (Double) snapshot.child("latitude").getValue(), (String) snapshot.child("date").getValue());
            }
        }).build();

        //setting the adapter and passing the data to the UI for display
        adapter = new FirebaseRecyclerAdapter<LocationObject, LocationListFragment.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LocationListFragment.ViewHolder viewHolder, int i, @NonNull LocationObject locationObject) {
                //setting a text on the UI
                String content = "Date: " + locationObject.getDate() + "\nLatitude: "+ locationObject.getLatitude() + "\nLongitude: " + locationObject.getLongitude();
                viewHolder.setTxtTitle(content);
            }

            //returning the new view with the displayed data
            @NonNull
            @Override
            public LocationListFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_locaion, parent, false);
                return new ViewHolder(view);

            }
        };
        //setting the adapter for recycler View
        recyclerView.setAdapter(adapter);

    }

    //if the fragment is on the top of the stack the adapter starts listening and retrieves and display all the data from database
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    //if the fragment is removed from the stack, the adapter stops displaying data from the database
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
