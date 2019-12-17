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
 * A simple {@link Fragment} subclass.
 */
public class LocationListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_location_list, container, false);

        recyclerView = mView.findViewById(R.id.recycler_view_Locationdatabase);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        fetch();
        return mView;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        private ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text_holder_location);
        }
        private void setTxtTitle(String string) { mTextView.setText(string); }
    }


    private void fetch() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = null;
        if (currentFirebaseUser != null) { userId = (String) currentFirebaseUser.getUid(); }
        Query query = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("location");

        FirebaseRecyclerOptions<LocationObject> options = new FirebaseRecyclerOptions.Builder<LocationObject>().setQuery(query, new SnapshotParser<LocationObject>() {
            @NonNull
            @Override
            public LocationObject parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new LocationObject((Double) snapshot.child("longitude").getValue(), (Double) snapshot.child("latitude").getValue(), (String) snapshot.child("date").getValue());
            }
        }).build();

        adapter = new FirebaseRecyclerAdapter<LocationObject, LocationListFragment.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LocationListFragment.ViewHolder viewHolder, int i, @NonNull LocationObject locationObject) {
                String content = "Date: " + locationObject.getDate() + "\nLatitude: "+ locationObject.getLatitude() + "\nLongitude: " + locationObject.getLongitude();
                viewHolder.setTxtTitle(content);
            }

            @NonNull
            @Override
            public LocationListFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_locaion, parent, false);
                return new ViewHolder(view);

            }
        };
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
