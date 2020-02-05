package com.example.bustracker1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SearchScreen extends AppCompatActivity implements OnMapReadyCallback {

    Button search_btn;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    GoogleMap mMap;
    EditText txt;
    BusModel bus;
    boolean busFind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
        search_btn=(Button) findViewById(R.id.search_btn);
        databaseReference=FirebaseDatabase.getInstance().getReference().child("buses");
        progressDialog =new ProgressDialog(this);
        txt=findViewById(R.id.search);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Searching");
        progressDialog.setMessage("Please wait while searching");

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                progressDialog.show();
//                Toast.makeText(SearchScreen.this, "The entered text is "+txt.getText(), Toast.LENGTH_SHORT).show();
//                progressDialog.show();
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Toast.makeText(SearchScreen.this, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                        String edt_txt=txt.getText().toString().trim();
                        for(DataSnapshot data: dataSnapshot.getChildren())
                        {
                            String fire_txt=data.child("name").getValue().toString();
//                            Toast.makeText(SearchScreen.this,data.child("name").getValue().toString().trim(), Toast.LENGTH_SHORT).show();

                            if(edt_txt.equals(fire_txt))
                             {
//                                 Toast.makeText(SearchScreen.this, "Values are equal", Toast.LENGTH_SHORT).show();
                                bus=new BusModel();
                                bus.setPhoneNum(data.child("phoneNum").getValue().toString());
                                bus.setLongitude(data.child("longitude").getValue().toString());
                                bus.setLatitude(data.child("latitude").getValue().toString());
                                bus.setName(data.child("name").getValue().toString());
                                busFind=true;
                                SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
                                mapFragment.getMapAsync(SearchScreen.this);
                            }
                        }
                        
                        if(!busFind) {
                            Toast.makeText(SearchScreen.this, "Please enter a valid bus Name", Toast.LENGTH_SHORT).show();
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SearchScreen.this, "onCanceled Method called", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        progressDialog.cancel();
        LatLng sydney=new LatLng(Double.parseDouble(bus.getLatitude()),Double.parseDouble(bus.getLongitude()));
        mMap.addMarker(new MarkerOptions().position(sydney).title(bus.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        Toast.makeText(this, "onMapReady Functon called", Toast.LENGTH_SHORT).show();
//        progressDialog.cancel();
    }
}
