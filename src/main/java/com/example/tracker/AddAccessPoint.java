package com.example.tracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AddAccessPoint extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText AP_block, AP_position1, AP_position2, AP_position3, mac_id, ssid;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private int Sample;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_access_point);

        // Displaying toolbar icon
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        AP_position1 = (EditText) findViewById(R.id.AP_position1);
        AP_position2 = (EditText) findViewById(R.id.AP_position2);
        AP_position3 = (EditText) findViewById(R.id.AP_position3);
        mac_id = (EditText) findViewById(R.id.mac);
        AP_block = (EditText) findViewById(R.id.AP_block);
        ssid = (EditText) findViewById(R.id.ssid);
        btnSave = (Button) findViewById(R.id.btn_save);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("Access Point Location");
        // Save / update the user
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String AP__block = AP_block.getText().toString();
                String AP__pos1 = AP_position1.getText().toString();
                String AP__pos2 = AP_position2.getText().toString();
                String AP__pos3 = AP_position3.getText().toString();
                String mac__id = mac_id.getText().toString();
                String ssid__ = ssid.getText().toString();

                // Check for already existed userId
                if (TextUtils.isEmpty(mac__id)) {
                    createUser(AP__block, AP__pos1, AP__pos2, AP__pos3,mac__id,ssid__);
                    Toast.makeText(getApplicationContext(),"Create Success",Toast.LENGTH_LONG).show();
                } else {
                    updateUser(AP__block, AP__pos1, AP__pos2, AP__pos3,mac__id,ssid__);
                    Toast.makeText(getApplicationContext(),"Update Success",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Creating new user node under 'users'
     */
    private void createUser(String block, String pos1, String pos2, String pos3, String mac, String ssid) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth

        User user = new User(block,pos1,pos2,pos3,mac,ssid);

        mFirebaseDatabase.child(mac).setValue(user);

        addUserChangeListener();
    }

    /**
     * User data change listener
     */
    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(String.valueOf(mac_id)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.block + ", " + user.pos1 + ", " + user.pos2 + ", " + user.pos3 + ", " + user.mac + ", " + user.ssid);

                // Display newly updated name and email
          //      txtDetails.setText(user.block + ", " + user.pos + ", "+user.mac);

                // clear edit text
                AP_block.setText("");
                AP_position1.setText("");
                AP_position2.setText("");
                AP_position3.setText("");
                mac_id.setText("");
                ssid.setText("");

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    private void updateUser(String block, String pos1, String pos2, String pos3, String mac, String ssid) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(block))
            mFirebaseDatabase.child(mac).child("block").setValue(block);

        if (!TextUtils.isEmpty(pos1))
            mFirebaseDatabase.child(mac).child("pos1").setValue(pos1);

        if (!TextUtils.isEmpty(pos2))
            mFirebaseDatabase.child(mac).child("pos2").setValue(pos2);

        if (!TextUtils.isEmpty(pos3))
            mFirebaseDatabase.child(mac).child("pos3").setValue(pos3);

        if (!TextUtils.isEmpty(mac))
            mFirebaseDatabase.child(mac).child("mac").setValue(mac);

        if (!TextUtils.isEmpty(ssid))
            mFirebaseDatabase.child(mac).child("ssid").setValue(ssid);
    }
}