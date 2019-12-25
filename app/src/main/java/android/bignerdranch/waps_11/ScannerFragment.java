package android.bignerdranch.waps_11;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
 * A simple {@link Fragment} subclass.
 */
public class ScannerFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    private WifiReceiver receiverWifi;
    private List<WifiObject> mWifiListObject = new ArrayList<>();
    private Timer timer;
    private TimerTask task;
    private RelativeLayout relativeLayout;
    private boolean buttonClicked = false;
    private int progVal;
    private String userId;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("progress_pref", Context.MODE_PRIVATE);
        progVal = sharedPreferences.getInt("progress_key", 2);

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentFirebaseUser != null){ userId = currentFirebaseUser.getUid(); }


        return inflater.inflate(R.layout.fragment_scanner, container, false); }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button stopButtonScan, buttonScan, clearButton;

        buttonScan = view.findViewById(R.id.scanBtn);
        stopButtonScan = view.findViewById(R.id.stopScnBtn);
        clearButton = view.findViewById(R.id.clearList);
        relativeLayout = view.findViewById(R.id.progressBarContainer);



        wifiManager = (WifiManager) requireActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && !wifiManager.isWifiEnabled()) {
            Toast.makeText(requireContext().getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
                } else {
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


        stopButtonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.INVISIBLE);
                if(buttonClicked){ pause(); }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.INVISIBLE);
                if(!mWifiListObject.isEmpty()){
                    mWifiListObject.clear();
                    mAdapter.notifyDataSetChanged();
                }

                if(buttonClicked){ pause(); }
            }
        });
    }

    private void timer(TimerTask task) {
        int tt = this.progVal * 1000;
        int tt2 = tt * 2;
        this.timer = new Timer();
        this.timer.schedule(task , tt, tt2 );
    }

    private void pause() { this.timer.cancel();  }


    private void getWifi() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Location Turned Off", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else { Toast.makeText(getActivity(), "Location Turned On", Toast.LENGTH_SHORT).show(); }
    }

    class WifiReceiver extends BroadcastReceiver {

        WifiManager wifiManager;
        RecyclerView wifiDeviceList;
        WifiObject wifiObject;
        DatabaseReference mDatabaseReferenceAdd;
        List<ScanResult> mScanResults;

        public WifiReceiver(WifiManager wifiManager, RecyclerView wifiDeviceList) {
            this.wifiManager = wifiManager;
            this.wifiDeviceList = wifiDeviceList;
        }

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {

                mScanResults = wifiManager.getScanResults();
                mDatabaseReferenceAdd  = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

                for (ScanResult scanResult : mScanResults) {

                    long actualTimestamp = System.currentTimeMillis() - SystemClock.elapsedRealtime() + (scanResult.timestamp / 1000);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(actualTimestamp))));

                    wifiObject = new WifiObject(scanResult.SSID, scanResult.BSSID, scanResult.level, scanResult.frequency, dateString);
                    mWifiListObject.add(wifiObject);
                    mDatabaseReferenceAdd.child("wifiAp").push().setValue(wifiObject);

                }

            }
            mRecyclerView = requireActivity().findViewById(R.id.wifiList);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mAdapter = new RecyclerViewAdapter(mWifiListObject);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        receiverWifi = new WifiReceiver(wifiManager, mRecyclerView);
        IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        requireActivity().registerReceiver(receiverWifi, intentFilter);
        getWifi();
    }

    @Override
    public void onPause() { super.onPause(); requireActivity().unregisterReceiver(receiverWifi); }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    wifiManager.startScan();
                } else {
                    Toast.makeText(getContext(), "Permission not Granted", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
    }





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
            String container = "SSID: " + mList.get(position).getSSID() + "\nBSSID: " +mList.get(position).getBSSID() + "\nRSSI: " + mList.get(position).getRSSI() + "\nFrequency: " + mList.get(position).getFrequency() + "\nDate: " + mList.get(position).getDate();
            holder.mTextView.setText(container);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

}
