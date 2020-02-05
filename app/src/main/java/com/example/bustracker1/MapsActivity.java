package com.example.bustracker1;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference databaseReference;

    ArrayList<BusModel> busList;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait while Loading");

        databaseReference= FirebaseDatabase.getInstance().getReference().child("buses");
        busList=new ArrayList<>();
        progressDialog.show();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren())
                {
                    BusModel bus=new BusModel();
                    bus.setName(data.child("name").getValue().toString());
                    bus.setLatitude(data.child("latitude").getValue().toString());
                    bus.setLongitude(data.child("longitude").getValue().toString());
                    bus.setPhoneNum(data.child("phoneNum").getValue().toString());
                    busList.add(bus);
                }
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(MapsActivity.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, "onCancelled Method called", Toast.LENGTH_SHORT).show();
            }
        });
        //        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot data:dataSnapshot.getChildren()){
//                    Toast.makeText(MapsActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
//                }
////
////                Toast.makeText(MapsActivity.this, dataSnapshot.toString(), Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(MapsActivity.this, "onCancelled Method called", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for(int i=0;i<busList.size();i++)
        {
            LatLng sydney=new LatLng(Double.parseDouble(busList.get(i).getLatitude()),Double.parseDouble(busList.get(i).getLongitude()));
            mMap.addMarker(new MarkerOptions().position(sydney).title(busList.get(i).getName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//            mMap.getMinZoomLevel();
        }
        progressDialog.cancel();
    }
}