package com.example.tracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class wifiScan extends AppCompatActivity {
    private WifiManager wifiManager;
    private LocationManager locationManager;

    Button scan;
    private TextView currentLoc;
    List<ScanResult> resultList;
    Context context = this;

    Database database = new Database(wifiScan.this);
    String Id,mac,date,time,currentLocation;
    String old_mac = "";
    Calendar calander;
    SimpleDateFormat simpledateformat;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                scan();

            }
            else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
                sendBroadcast(new Intent("Wifi.ON_NETWORK_STATE_CHANGED"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scan);

        currentLoc = (TextView)findViewById(R.id.currentLocation);

        scan = (Button)findViewById(R.id.scanbtn);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initwifiscan();
            }
        });

        context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI);
        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);

        if(!wifiManager.isWifiEnabled()){
            Log.v("Wifi","On");
            wifiManager.setWifiEnabled(true);
        }

        Log.e("Started"," "+wifiManager.startScan()+wifiManager.toString());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

        }else{
            //do something, permission was previously granted; or legacy device
            initwifiscan();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        check_location();
    }

    public void check_location(){
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if((!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)))&&(!(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)))){
            new AlertDialog.Builder(context)
                    .setTitle("Location On")
                    .setMessage("To access the wifi should be ON location")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    public void scan(){


        // Check whether mac is already present in database
        Query query = FirebaseDatabase.getInstance().getReference("Access Point Location")
                .orderByChild("mac");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    Log.e("in ture","compl");
                }
                else{
                    Log.e("False","failed");
                }
                for (DataSnapshot usersnapshot : dataSnapshot.getChildren()) {
                    User tuser = usersnapshot.getValue(User.class);
                    old_mac += tuser.mac+",";
                }
                Log.e("Mac ID stored : ", " "+ old_mac);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        resultList = wifiManager.getScanResults();

        Log.e("Scan Count",""+resultList.size());

        int rss = -90;

        for(ScanResult result: resultList){
            if(rss < result.level)
            {
                System.out.println(result.BSSID);

                if(old_mac.contains(result.BSSID)) {

                    System.out.println("@@@@@IN SCAN@@@@@");
                    Id = result.SSID;
                    rss = result.level;
                    mac = result.BSSID;
                    calander = Calendar.getInstance();
                    simpledateformat = new SimpleDateFormat("dd-MM-yyyy");
                    date = simpledateformat.format(calander.getTime());
                    simpledateformat = new SimpleDateFormat("hh:mm:ss");
                    time = simpledateformat.format(calander.getTime());
                    System.out.println("??????????"+time+" "+date);
                }
            }
            Log.e("IN VSLID",result.toString());
        }
        currentLocation=database.track(Id, mac, date, time);
        currentLoc.setText("YOUR LOCATION:  "+currentLocation);
    }

    public void initwifiscan(){

        final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(broadcastReceiver,filter);
        wifiManager.startScan();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        Log.e("request",requestCode+" ");
        if (requestCode == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            initwifiscan();
        }
    }
}