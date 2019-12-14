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
public class WifiListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_wifi_list, container, false);
        recyclerView = mView.findViewById(R.id.recycler_view_database);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        fetch();
        return mView;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text_holder);
        }

        public void setTxtTitle(String string) {
            mTextView.setText(string); }
    }


    private void fetch() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (String) currentFirebaseUser.getUid();
        Query query = FirebaseDatabase.getInstance().getReference().child(userId);

        FirebaseRecyclerOptions<WifiObject> options = new FirebaseRecyclerOptions.Builder<WifiObject>().setQuery(query, new SnapshotParser<WifiObject>() {
            @NonNull
            @Override
            public WifiObject parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new WifiObject(snapshot.child("ssid").getValue().toString(), snapshot.child("bssid").getValue().toString(), (Long) snapshot.child("rssi").getValue(),(Long) snapshot.child("frequency").getValue(), (String) snapshot.child("date").getValue());
            }
        }).build();

        adapter = new FirebaseRecyclerAdapter<WifiObject, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull WifiObject wifiObject) {
                String content = "SSID: " + wifiObject.getSSID() + "\nBSSID: " + wifiObject.getBSSID() + "\nRSSI: " + wifiObject.getRSSI() + "\nFrequency: " + wifiObject.getFrequency() + "\nDate: " + wifiObject.getDate();
                viewHolder.setTxtTitle(content);
            }


            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
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
