package com.example.bustracker1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Route;
import okhttp3.internal.connection.RouteException;

public class SearchScreen extends AppCompatActivity  implements OnMapReadyCallback {

    Button search_btn;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    GoogleMap mMap;
    EditText txt;
    BusModel bus;
//    s
    //private GoogleMap mMap;
    //private MarkerOptions place1, place2;
//    Button getDirection;
    //private Polyline currentPolyline;

    boolean busFind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
        search_btn=(Button) findViewById(R.id.search_btn);
        databaseReference=FirebaseDatabase.getInstance("https://bus-tracker-aa22e-default-rtdb.firebaseio.com/").getReference().child("buses");
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
                Toast.makeText(SearchScreen.this, "The entered text is "+txt.getText(), Toast.LENGTH_SHORT).show();
                progressDialog.show();
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Toast.makeText(SearchScreen.this, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                        String edt_txt=txt.getText().toString().trim();
                        for(DataSnapshot data: dataSnapshot.getChildren())
                        {
                            String fire_txt=data.child("name").getValue().toString();
                            Toast.makeText(SearchScreen.this,data.child("name").getValue().toString().trim(), Toast.LENGTH_SHORT).show();

                            if(edt_txt.equals(fire_txt))
                             {
                                 Toast.makeText(SearchScreen.this, "Values are equal", Toast.LENGTH_SHORT).show();
                                bus=new BusModel();
                                //bus.setPhoneNum(data.child("phoneNum").getValue().toString());
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
        Marker dest = mMap.addMarker(new MarkerOptions().position(sydney).title(bus.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(SearchScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null) {
                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(current, 15);
                mMap.animateCamera(cameraUpdate);
//                PolylineOptions polylineOptions = new PolylineOptions()
//                        .add(current, sydney)
//                        .width(20)
//                        .color(Color.BLUE);
//                mMap.addPolyline(polylineOptions);
            }
        }


        else {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(SearchScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

    }
//    public void drawRoute(LatLng Start, LatLng End)
//    {
//        if(Start==null || End==null) {
//            Toast.makeText(SearchScreen.this,"Unable to get location",Toast.LENGTH_LONG).show();
//        }
//        else
//        {
//
//            Routing routing = new Routing.Builder()
//                    .travelMode(AbstractRouting.TravelMode.DRIVING)
//                    .withListener(this)
//                    .alternativeRoutes(true)
//                    .waypoints(Start, End)
//                    .key("AIzaSyCxEMaepmpG3LkJfOftsM3L6jzjj1OzGs8")  //also define your api key here.
//                    .build();
//            routing.execute();
//        }
//    }
//    //Routing call back functions.
//    @Override
//    public void onRoutingFailure(RouteException e) {
//        View parentLayout = findViewById(android.R.id.content);
//        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
//        snackbar.show();
////        Findroutes(start,end);
//    }
//
//    @Override
//    public void onRoutingStart() {
//        Toast.makeText(SearchScreen.this,"Finding Route...",Toast.LENGTH_LONG).show();
//    }
//
//    //If Route finding success..
//    @Override
//    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
//
//        CameraUpdate center = CameraUpdateFactory.newLatLng(Start);
//        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
//        if(polylines!=null) {
//            polylines.clear();
//        }
//        PolylineOptions polyOptions = new PolylineOptions();
//        LatLng polylineStartLatLng=null;
//        LatLng polylineEndLatLng=null;
//
//
//        polylines = new ArrayList<>();
//        //add route(s) to the map using polyline
//        for (int i = 0; i <route.size(); i++) {
//
//            if(i==shortestRouteIndex)
//            {
//                polyOptions.color(getResources().getColor(R.color.colorPrimary));
//                polyOptions.width(7);
//                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
//                Polyline polyline = mMap.addPolyline(polyOptions);
//                polylineStartLatLng=polyline.getPoints().get(0);
//                int k=polyline.getPoints().size();
//                polylineEndLatLng=polyline.getPoints().get(k-1);
//                polylines.add(polyline);
//
//            }
//            else {
//
//            }
//
//        }
//
//        //Add Marker on route starting position
//        MarkerOptions startMarker = new MarkerOptions();
//        startMarker.position(polylineStartLatLng);
//        startMarker.title("My Location");
//        mMap.addMarker(startMarker);
//
//        //Add Marker on route ending position
//        MarkerOptions endMarker = new MarkerOptions();
//        endMarker.position(polylineEndLatLng);
//        endMarker.title("Destination");
//        mMap.addMarker(endMarker);
//    }
//
//    @Override
//    public void onRoutingCancelled() {
//        drawRoute(Start,End);
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        drawRoute(Start,End);
//
//    }



//    private void drawRoute(LatLng origin, LatLng dest) {
//        String url = "https://maps.googleapis.com/maps/api/directions/json?"
//                + "origin=" + origin.latitude + "," + origin.longitude
//                + "&destination=" + dest.latitude + "," + dest.longitude
//                + "&key=AIzaSyCxEMaepmpG3LkJfOftsM3L6jzjj1OzGs8";
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        StringRequest request = new StringRequest(Request.Method.GET, url, error -> {
//            String data=null;
//            if (error != null && error.networkResponse != null) {
//                data = new String(error.networkResponse.data);
//            }
//            try {
//                JSONObject json = new JSONObject(data);
//                JSONArray routes = json.getJSONArray("routes");
//                JSONObject route = routes.getJSONObject(0);
//                JSONObject polyline = route.getJSONObject("overview_polyline");
//                String points = polyline.getString("points");
//                List<LatLng> decodedPoints = PolyUtil.decode(points);
//
//                PolylineOptions options = new PolylineOptions();
//                options.addAll(decodedPoints);
//                options.color(Color.BLUE);
//                options.width(10);
//                mMap.addPolyline(options);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            catch (NullPointerException e) {
//                e.printStackTrace();
//                // handle null response error
//            } catch (VolleyError e) {
//                // handle Volley error
//                String errorMessage = "Volley error: " + e.getMessage();
//                JSONObject errorObject = new JSONObject();
//                try {
//                    errorObject.put("error", errorMessage);
//                } catch (JSONException ex) {
//                    ex.printStackTrace();
//                }
//                e.printStackTrace();
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//                // handle other errors
//            }
//        });
//
//        queue.add(request);
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, enable the location layer and zoom to the user's current location
                if (mMap != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                        mMap.animateCamera(cameraUpdate);
                    }
                }
            }
        }
    }
}
