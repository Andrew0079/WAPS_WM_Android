package android.bignerdranch.waps_11;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * This fragment's responsibility is to scan for available wifi access points.
 * After successful completion of the scan, it logs all the collected data into a database.
 */
public class ScannerFragment extends Fragment {

    //instance variables

    //Recycler View to display the collected data
    private RecyclerView mRecyclerView;
    //Adapter to set and display the collected data
    private RecyclerViewAdapter mAdapter;
    //wifimanager to provide the primary API for managing all aspects of Wi-Fi connectivity.
    private WifiManager wifiManager;
    //permission code to check if network, internet, etc are available
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    //to collect all data of the AP
    private WifiReceiver receiverWifi;
    //list of wifi ACP
    private List<WifiObject> mWifiListObject = new ArrayList<>();
    //timer to specify the scanning interval
    private Timer timer;
    //timer task to specify the task that needs to perform for a timer
    private TimerTask task;
    //relative layout for UI
    private RelativeLayout relativeLayout;
    //for testing and validation check
    private boolean buttonClicked = false;
    //value to set a timer, must be retrieved from shared preferences
    private int progVal;
    //user id from database
    private String userId;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //getting the specified time(integer) from shared preference
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("progress_pref", Context.MODE_PRIVATE);
        progVal = sharedPreferences.getInt("progress_key", 2);

        //getting the user id from database
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentFirebaseUser != null){ userId = currentFirebaseUser.getUid(); }


        //returning this fragment's layout
        return inflater.inflate(R.layout.fragment_scanner, container, false); }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //buttons for UI
        final Button stopButtonScan, buttonScan, clearButton;

        //connecting to UI
        buttonScan = view.findViewById(R.id.scanBtn);
        stopButtonScan = view.findViewById(R.id.stopScnBtn);
        clearButton = view.findViewById(R.id.clearList);
        relativeLayout = view.findViewById(R.id.progressBarContainer);

        //requesting a wifi service a.k.a wifi manager
        wifiManager = (WifiManager) requireActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //checking if wifi is enabled
        //enabling wifi if its ont enabled
        if (wifiManager != null && !wifiManager.isWifiEnabled()) {
            showToast("Turning WiFi ON...");
            wifiManager.setWifiEnabled(true);
        }

        //scanner button, initiates a scan on on click
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setting color on pressed button
                buttonScan.setBackgroundColor(Color.WHITE);
                clearButton.setBackgroundColor(Color.BLACK);
                stopButtonScan.setBackgroundColor(Color.BLACK);
                //checking for permission
                if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
                } else {
                    //logic for a scanner button
                    //if users presses scan, but want to start again,
                    // user will be able to do that without any complication
                    buttonClicked = true;
                    if(!mWifiListObject.isEmpty()){
                        mWifiListObject.clear();
                        mAdapter.notifyDataSetChanged();
                        relativeLayout.setVisibility(View.VISIBLE);
                        relativeLayout.bringToFront();
                        task = new TimerTask() {
                            @Override
                            public void run() { wifiManager.startScan();}
                        }; timer(task);
                    } else {
                        relativeLayout.setVisibility(View.VISIBLE);
                        relativeLayout.bringToFront();
                        task = new TimerTask() {
                            @Override
                            public void run() { wifiManager.startScan(); }
                        }; timer(task);
                    }
                }
            }
        });


        //stop button, stops the scan, however the list of wifi APs will be still displayed
        stopButtonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setting color on pressed button
                buttonScan.setBackgroundColor(Color.BLACK);
                clearButton.setBackgroundColor(Color.BLACK);
                stopButtonScan.setBackgroundColor(Color.WHITE);
                relativeLayout.setVisibility(View.INVISIBLE);
                if(buttonClicked){ pause(); }
            }
        });

        //clear Button, clears the list of wifi Aps
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setting color on pressed button
                buttonScan.setBackgroundColor(Color.BLACK);
                clearButton.setBackgroundColor(Color.WHITE);
                stopButtonScan.setBackgroundColor(Color.BLACK);
                relativeLayout.setVisibility(View.INVISIBLE);
                if(!mWifiListObject.isEmpty()){
                    mWifiListObject.clear();
                    mAdapter.notifyDataSetChanged();
                }
                if(buttonClicked){ pause(); }
            }
        });
    }

    //timer method , it sets the interval of the scanning and schedule a scanning task
    private void timer(TimerTask task) {
        int tt = this.progVal * 1000;
        int tt2 = tt * 2;
        this.timer = new Timer();
        this.timer.schedule(task , tt, tt2 );
    }

    //pause method, stops the timer and the  scanning
    private void pause() { this.timer.cancel();  }


    //get wifi method,
    //checks for permission, (location) if permission enabled displays a message,
    //otherwise it requests for permission
    private void getWifi() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showToast("Location Turned Off");
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {showToast("Location Turned On"); }
    }

    //wifi receiver class to get the scan results,
    //update database
    //and display the wifi APs
    class WifiReceiver extends BroadcastReceiver {

        //class variables

        //wifimanager to provide the primary API for managing all aspects of Wi-Fi connectivity.
        WifiManager wifiManager;
        //Recycler View to display the collected data
        RecyclerView wifiDeviceList;
        //wifi object class to collect all the scanned data
        WifiObject wifiObject;
        //DatabaseReference for database connection
        DatabaseReference mDatabaseReferenceAdd;
        //list of found scan results
        List<ScanResult> mScanResults;

        //constructor of the class
        public WifiReceiver(WifiManager wifiManager, RecyclerView wifiDeviceList) {
            this.wifiManager = wifiManager;
            this.wifiDeviceList = wifiDeviceList;
        }

        //on receive method
        //will fires once the scan is finished, and received the results
        public void onReceive(Context context, Intent intent) {

            //checking if scan results are available
            String action = intent.getAction();
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {

                //getting the scan results from the wifiManager API
                mScanResults = wifiManager.getScanResults();
                //connecting to the user's database
                mDatabaseReferenceAdd  = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

                //looping through the scan results
                for (ScanResult scanResult : mScanResults) {

                    //getting the current time of the scan, and converting it to the proper time/data format
                    long actualTimestamp = System.currentTimeMillis() - SystemClock.elapsedRealtime() + (scanResult.timestamp / 1000);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(actualTimestamp))));

                    //creating a wifi object with the specific scan results
                    wifiObject = new WifiObject(scanResult.SSID, scanResult.BSSID, scanResult.level, scanResult.frequency, dateString);

                    //adding all the result of the scan to the list for display
                    mWifiListObject.add(wifiObject);

                    //pushing scan results to the database
                    mDatabaseReferenceAdd.child("wifiAp").push().setValue(wifiObject);

                }

            }

            //connecting to the UI, and setting adapter
            //to display all the scan results to the user
            mRecyclerView = requireActivity().findViewById(R.id.wifiList);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mAdapter = new RecyclerViewAdapter(mWifiListObject);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    //when the app is in the on resume state
    //the wifi receiver gets registered
    @Override
    public void onResume() {
        super.onResume();
        receiverWifi = new WifiReceiver(wifiManager, mRecyclerView);
        IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        requireActivity().registerReceiver(receiverWifi, intentFilter);
        getWifi();
    }

    //if the fragment is in the on pause state, the register will be removed
    @Override
    public void onPause() { super.onPause(); requireActivity().unregisterReceiver(receiverWifi); }


    //request permission method
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Permission Granted");
                    wifiManager.startScan();
                } else {
                    showToast("Permission not Granted");
                    return;
                }
                break;
        }
    }

    //RecyclerViewHolder and RecyclerViewAdapter to display all the results to the user
    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        private RecyclerViewHolder(LayoutInflater inflater, ViewGroup container) {
            super((inflater.inflate(R.layout.card_view, container, false)));
            mTextView = itemView.findViewById(R.id.text_holder);
        }
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<ScannerFragment.RecyclerViewHolder> {

        private List<WifiObject> mList;

        private RecyclerViewAdapter(List<WifiObject> list) {
            this.mList = list;
        }

        @Override
        @NonNull
        public ScannerFragment.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater mInflater = LayoutInflater.from(getActivity());
            return new ScannerFragment.RecyclerViewHolder(mInflater, parent);
        }

        @Override
        public void onBindViewHolder(ScannerFragment.RecyclerViewHolder holder, int position) {
            //setting the text for the UI to display the scan results
            String container = "SSID: " + mList.get(position).getSSID() + "\nBSSID: " +mList.get(position).getBSSID() + "\nRSSI: " + mList.get(position).getRSSI() + "\nFrequency: " + mList.get(position).getFrequency() + "\nDate: " + mList.get(position).getDate();
            holder.mTextView.setText(container);
        }

        @Override
        public int getItemCount() { return mList.size(); }
    }

    //custom Toast method
    //displays a custom Toast with a specified message
    private void showToast(String text){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) requireActivity().findViewById(R.id.toast_root));

        TextView toastText = layout.findViewById(R.id.toast_txt);
        toastText.setText(text);

        Toast toast = new Toast(requireActivity().getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

}
