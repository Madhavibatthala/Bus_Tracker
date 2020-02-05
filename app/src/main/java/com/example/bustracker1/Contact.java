package com.example.bustracker1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.drm.ProcessedData;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Contact extends AppCompatActivity {

    ListView myList;
    ArrayList<BusModel> list_items;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        myList=findViewById(R.id.mListView);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("buses");
        list_items=new ArrayList<>();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait while loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(Contact.this, "onDataChange MEthod called", Toast.LENGTH_SHORT).show();
                for(DataSnapshot data:dataSnapshot.getChildren())
                {
                    BusModel bus=new BusModel();
                    bus.setName(data.child("name").getValue().toString());
                    bus.setLatitude(data.child("latitude").getValue().toString());
                    bus.setLongitude(data.child("longitude").getValue().toString());
                    bus.setPhoneNum(data.child("phoneNum").getValue().toString());
                    list_items.add(bus);
                }
                MyAdapter adapter=new MyAdapter(Contact.this,R.layout.contact_list_item,list_items);
                myList.setAdapter(adapter);
                progressDialog.cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Contact.this, "onCancelled Method Called", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
            }
        });


    }
}
