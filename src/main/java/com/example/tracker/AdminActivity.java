package com.example.tracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    Button button_AP,location;
    EditText room_name;
    ListView near_location;
    ArrayList<String> arrayList = new  ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    //Database database =new Database();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        button_AP = (Button)findViewById(R.id.buttonAP);
        location = (Button)findViewById(R.id.location);

        room_name=(EditText)findViewById(R.id.room);

        near_location = (ListView)findViewById(R.id.nearLocation);

        button_AP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddAccessPoint.class));
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CollectionReference cn = db.collection("Access Point location");

                Log.e("Retrive function","in "+room_name.getText().toString());
                Query query = FirebaseDatabase.getInstance().getReference("Access Point Location")
                        .orderByChild("ssid").equalTo(room_name.getText().toString());

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
                            arrayList.add("SSID : "+tuser.ssid+"\nPosition 1: "+tuser.pos1+"\nPosition 2: "+tuser.pos2+"\nPosition 3: "+tuser.pos3);

                            //Toast.makeText(this,"SSID:"+tuser.ssid+"   Position 1:"+tuser.pos1+"   Position 2:"+tuser.pos2+"   Position 3:"+tuser.pos3,4,Toast.LENGTH_SHORT).
                            Log.e("Result", " " + tuser.ssid+ " " + tuser.pos1);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                arrayAdapter = new ArrayAdapter<String>(AdminActivity.this,android.R.layout.simple_list_item_1,arrayList);
                near_location.setAdapter(arrayAdapter);
               // arrayList.clear();
                //arrayAdapter.clear();

            }
        });
    }
}
