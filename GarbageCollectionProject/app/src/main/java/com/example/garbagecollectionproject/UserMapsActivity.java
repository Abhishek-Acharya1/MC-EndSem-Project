package com.example.garbagecollectionproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.garbagecollectionproject.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Geocoder geocoder;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;
    //FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    Marker userLocationMarker;
    Circle userLocationAccuracyCircle;

    String latitude;
    String longitude;
    String Collector_Id;
    Marker m;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        FirebaseAuth authUser= FirebaseAuth.getInstance();
        firebaseUser=authUser.getCurrentUser();
        String userID=firebaseUser.getUid();

        db = FirebaseFirestore.getInstance();
        Log.i("HERE","HERE000!________________");
        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    String collectorId = task.getResult().getString("collectorId");
                    Log.i("CollctorId",collectorId);
                    db.collection("collectors").document(collectorId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            Log.i("HERE", "HERE111!________________");
                            if (error != null) {
                                Log.i("HERE2", "HERE222!________________");

                                Log.i("TAG", "Error while fetching the collector location");
                            } else {
                                Log.i("HERE3", "HERE333!________________");

                                if (value != null && value.exists()) {
                                    Log.i("HERE4", "HERE444!________________");

                                    Log.i("VALUE", String.valueOf(value.getData()));
                                    Double lati = (Double) value.get("curLat");
                                    Double lon = (Double) value.get("curLong");
                                    Log.i("curLat", String.valueOf(lati));
                                    Log.i("curLong", String.valueOf(lon));
                                    LatLng latlng_collector = new LatLng(lati, lon);
                                    if (m == null) {
                                        setUserLocationMarker(lati, lon, userID);
                                    } else {
                                    }
                                    Log.i("collector location ", "changing");
                                } else {
                                    Log.i("HERE5", "HERE555!________________");

                                }
                            }
                        }
                    });
                }
            }
        });




        DocumentReference docRef = db.collection("users").document(userID);

        final DocumentSnapshot[] doc = {null};
        // Get the document snapshot
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.i("Login","Login5");

                if (task.isSuccessful()) {
                    Log.i("Login","Login6");

                    doc[0] = task.getResult();
                    if (doc[0].exists()) {
                        // Document found, you can access the data using document.getData()
                        Log.i("Login","USER ACCOUNT FOUND");
                        Log.i("Login", "Document data: " + doc[0].getData());
                        Log.i("Login","CollectorId: "+doc[0].get("collectorId"));

                        String name= (String) doc[0].get("fullName");
                        String email= firebaseUser.getEmail();;
                        String gender= (String) doc[0].get("gender");
                        String mobile= (String) doc[0].get("mobile");
                        int bin = ((Long)  doc[0].get("bin")).intValue();

                        Collector_Id=doc[0].getString("collectorId");
                        Log.i("MAP",Collector_Id);


                        if(bin==0){
                            LatLng a = new LatLng((Double) doc[0].get("lat"),(Double)doc[0].get("long"));
                            int height = 100;
                            int width = 100;
                            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.green_bin);
                            Bitmap b = bitmapdraw.getBitmap();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                            mMap.addMarker(new MarkerOptions()
                                    .position(a)
                                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                            );
                        }else{
                            LatLng a = new LatLng((Double) doc[0].get("lat"),(Double)doc[0].get("long"));
                            int height = 100;
                            int width = 100;
                            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.red_bin);
                            Bitmap b = bitmapdraw.getBitmap();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                            mMap.addMarker(new MarkerOptions()
                                    .position(a)
                                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                            );
                        }

                    } else {
                        // Document not found
                        Log.i("Login", "No such document");
                    }
                } else {
                    Log.i("Login", "Failed to get document: " + task.getException());
                }
            }
        });


    }

    private void setUserLocationMarker(Double latitude, Double longitude, String userID)
    {
        Location location = new Location("");
        Log.i("LAT-LONG",latitude+","+longitude);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAccuracy(15.0F);
        LatLng latLng = new LatLng(latitude,longitude);
        if(userLocationMarker==null){
            //Create A New Marker
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
            markerOptions.anchor((float)0.5,(float)0.5);
            markerOptions.rotation(location.getBearing());
            userLocationMarker=mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

        }else{
            //User the Previously Created Marker
            userLocationMarker.setPosition(latLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
        }
        if(userLocationAccuracyCircle==null){
            Log.i("CIRCLE ACCURACY", String.valueOf(location.hasAccuracy()));
            CircleOptions circleOptions=new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.strokeWidth(4);
            circleOptions.strokeColor(Color.argb(255,255,0,0));
            circleOptions.fillColor(Color.argb(32,255,0,0));
            circleOptions.radius(location.getAccuracy());
            userLocationAccuracyCircle=mMap.addCircle(circleOptions);
        }else{
            Log.i("CIRCLE2222 ACCURACY", String.valueOf(location.hasAccuracy()));
            userLocationAccuracyCircle.setCenter(latLng);
            userLocationAccuracyCircle.setRadius(location.getAccuracy());
        }

        findDistance(latitude,longitude,userID);

    }

    private void findDistance(Double lat1, Double long1, String userId) {
        Log.i("FIND","DISTANCE");
        Location location1 = new Location("");
        location1.setLatitude(lat1);
        location1.setLongitude(long1);

        db=FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(userId);

        final DocumentSnapshot[] doc = {null};
        // Get the document snapshot
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.i("Login", "Login5");

                if (task.isSuccessful()) {
                    Log.i("Login", "Login6");

                    doc[0] = task.getResult();
                    if (doc[0].exists()) {
                        // Document found, you can access the data using document.getData()
                        Log.i("Login", "USER ACCOUNT FOUND");
                        Log.i("Login", "Document data: " + doc[0].getData());
                        Log.i("Login", "CollectorId: " + doc[0].get("collectorId"));


                        LatLng a = new LatLng((Double) doc[0].get("lat"), (Double) doc[0].get("long"));
                        Location location2 = new Location("");
                        location2.setLatitude((Double) doc[0].get("lat"));
                        location2.setLongitude((Double) doc[0].get("long"));
                        String destination=(Double) doc[0].get("lat")+","+(Double) doc[0].get("long");
                        float distanceInMeters = location1.distanceTo(location2);
                        Log.i("DISTANCE !!!!!", String.valueOf(distanceInMeters));
                        Log.i("DISTANCE KM", String.valueOf(distanceInMeters/1000));
                        Double time= Double.valueOf((distanceInMeters/1000)/40);

                        double totalHours = time; // example value
                        int minutesPerHour = 60;
                        int hours = (int) totalHours;
                        int minutes = (int) ((totalHours - hours) * minutesPerHour);

                        String formattedTime = String.format("%02d:%02d", hours, minutes);
                        Log.i("TIME !!!!!", String.valueOf(formattedTime));
                        Toast.makeText(UserMapsActivity.this, String.valueOf(distanceInMeters/1000)+" meters away!, Will take around "+formattedTime+" hours.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
            //zoomToUserLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We can show user a dialog why this permission is necessary
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }

        }
    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                zoomToUserLocation();
            } else {
                //We can show a dialog that permission is not granted...
            }
        }
    }
}