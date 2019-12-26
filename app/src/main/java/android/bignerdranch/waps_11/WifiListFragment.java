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

import java.util.Objects;


/**
 * This fragment displays the list of saved wifi access point information.
 */
public class WifiListFragment extends Fragment {

    //Recycler View to display all the information from the database
    private RecyclerView recyclerView;
    //an adapter to display all the information from the database
    private FirebaseRecyclerAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_wifi_list, container, false);
        //connecting to the UI
        recyclerView = mView.findViewById(R.id.recycler_view_database);
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
            mTextView = itemView.findViewById(R.id.text_holder);
        }

        //setting the retrieved information in a text view inside the UI
        private void setTxtTitle(String string) { mTextView.setText(string); }}


    //fetch method to retrieve all the data from the database
    private void fetch() {
        //getting the current user's informtion
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //getting the user's id
        String userId = (String) currentFirebaseUser.getUid();
        //getting all the informations from the database, using the user's id
        Query query = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("wifiAp");

        //gettig the WIFI AP information and saving it in a WIFI OBJECT CLASS
        FirebaseRecyclerOptions<WifiObject> options = new FirebaseRecyclerOptions.Builder<WifiObject>().setQuery(query, new SnapshotParser<WifiObject>() {
            @NonNull
            @Override
            public WifiObject parseSnapshot(@NonNull DataSnapshot snapshot) {
                //returning the WIFI OBJECT CLASS
                return new WifiObject(Objects.requireNonNull(snapshot.child("ssid").getValue()).toString(), Objects.requireNonNull(snapshot.child("bssid").getValue()).toString(), (Long) snapshot.child("rssi").getValue(),(Long) snapshot.child("frequency").getValue(), (String) snapshot.child("date").getValue());
            }
        }).build();

        //setting the adapter and passing the data to the UI for display
        adapter = new FirebaseRecyclerAdapter<WifiObject, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull WifiObject wifiObject) {
                //setting a text on the UI
                String content = "SSID: " + wifiObject.getSSID() + "\nBSSID: " + wifiObject.getBSSID() + "\nRSSI: " + wifiObject.getRSSI() + "\nFrequency: " + wifiObject.getFrequency() + "\nDate: " + wifiObject.getDate();
                viewHolder.setTxtTitle(content);
            }

            //returning the new view with the displayed data
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
                return new ViewHolder(view);

            }
        };
        //setting the adapter for recycler View
        recyclerView.setAdapter(adapter);
    }

    //if the fragment is on the top of the stack the adapter starts listening and retrieves and display all the data from database
    @Override
    public void onStart() { super.onStart(); adapter.startListening(); }

    //if the fragment is removed from the stack, the adapter stops displaying data from the database
    @Override
    public void onStop() { super.onStop(); adapter.stopListening(); }

}
