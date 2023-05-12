package com.example.garbagecollectionproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Geocoder geocoder;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    Marker userLocationMarker;
    Circle userLocationAccuracyCircle;

    FirebaseFirestore db;
    private FirebaseAuth authUser;

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


    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d("Tag", "onLocationResult: " + locationResult.getLastLocation());
            if (mMap != null) {
                setUserLocationMarker(locationResult.getLastLocation());
            }
        }
    };

    private void setUserLocationMarker(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userLocationMarker == null) {
            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
            markerOptions.rotation(location.getBearing());
            markerOptions.title("Collector Car");
            markerOptions.anchor((float) 0.5, (float) 0.5);
            userLocationMarker = mMap.addMarker(markerOptions);
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));



            FirebaseFirestore db = FirebaseFirestore.getInstance();
            authUser= FirebaseAuth.getInstance();

             DocumentReference docRef = db.collection("collectors").document(authUser.getUid());

            // Get the document snapshot
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.i("Login","Login5");

                    if (task.isSuccessful()) {
                        Log.i("Login","Login6");

                        DocumentSnapshot docCol = task.getResult();
                        if (docCol.exists()) {
                            // Document found, you can access the data using document.getData()
                            Log.i("Login","USER ACCOUNT FOUND");
                            Log.i("Login", "Document data: " + docCol.getData());

                            ArrayList<String> usersAllocated= (ArrayList<String>) docCol.get("usersAllocated");
                            for(int i=0;i<usersAllocated.size();i++){
                                String userId=usersAllocated.get(i);
                                Log.i("USERS",userId);
                                DocumentReference docRef = db.collection("users").document(userId);

                                // Get the document snapshot
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        Log.i("Login","Login5");

                                        if (task.isSuccessful()) {
                                            Log.i("Login","Login6");

                                            DocumentSnapshot doc = task.getResult();
                                            if (doc.exists()) {
                                                // Document found, you can access the data using document.getData()
                                                Log.i("Login","USER ACCOUNT FOUND");
                                                Log.i("Login", "Document data: " + doc.getData());

                                                int bin = ((Long)  doc.get("bin")).intValue();
                                                LatLng a = new LatLng((Double) doc.get("lat"),(Double) doc.get("long"));

                                                if(bin==0){
                                                    int height = 100;
                                                    int width = 100;
                                                    BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.green_bin);
                                                    Bitmap b = bitmapdraw.getBitmap();
                                                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                                                    mMap.addMarker(new MarkerOptions()
                                                            .position(a)
                                                            .title(doc.getId())
                                                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                                    );
                                                }else{
                                                    int height = 100;
                                                    int width = 100;
                                                    BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.red_bin);
                                                    Bitmap b = bitmapdraw.getBitmap();
                                                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                                                    mMap.addMarker(new MarkerOptions()
                                                            .position(a)
                                                            .title(doc.getId())
                                                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                                    );
                                                }
                                                mMap.setOnMarkerClickListener(marker -> {
                                                    Log.i("BIN CLICKED",marker.getTitle());
                                                    Toast.makeText(MapsActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();// display toast
                                                    if(!marker.getTitle().equals("Collector Car")){
                                                        scanCode();
                                                    }
                                                    return true;
                                                });

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

                        } else {
                            // Document not found
                            Log.i("Login", "No such document");
                        }
                    } else {
                        Log.i("Login", "Failed to get document: " + task.getException());
                    }
                }
            });

        } else {
            //use the previously created marker
            userLocationMarker.setPosition(latLng);
            userLocationMarker.setRotation(location.getBearing());
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }

        if (userLocationAccuracyCircle == null) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.strokeWidth(4);
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
            circleOptions.fillColor(Color.argb(32, 255, 0, 0));
            circleOptions.radius(location.getAccuracy());
            userLocationAccuracyCircle = mMap.addCircle(circleOptions);
        } else {
            userLocationAccuracyCircle.setCenter(latLng);
            userLocationAccuracyCircle.setRadius(location.getAccuracy());
        }
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        try {
            if (result.getContents() != null) {
                Log.i("ID",result.getContents());
                String userId=result.getContents();
                Log.i("ID Val",result.getContents());
                db = FirebaseFirestore.getInstance();
                authUser=FirebaseAuth.getInstance();
                DocumentReference docRef = db.collection("users").document(userId.trim());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            Log.i("CHECK",(String)doc.get("collectorId"));
                            Log.i("CHECK2",authUser.getUid());
                            boolean isThisAllocatedToMe= ((String)doc.get("collectorId")).equals(authUser.getUid());
                            if (doc.exists()  && isThisAllocatedToMe ) {
                                Log.i("Login","USER ACCOUNT FOUND");
                                Log.i("Login", "Document data: " + doc.getData());
                                String userId=doc.getId();
                                int bin = ((Long)  doc.get("bin")).intValue();
                                if(bin==1) {
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("bin", 0);
                                    updates.put("notifSent", false);
                                    db.collection("users").document(doc.getId()).update(updates);
                                    Toast.makeText(MapsActivity.this, "Dustbin Set to be Emptied", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(MapsActivity.this,collector_main.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(MapsActivity.this, "Bin is Already Empty", Toast.LENGTH_SHORT).show();
                                }

                            } else if ( !authUser.getUid().equals(userId) ){
                                Toast.makeText(MapsActivity.this,"Oops! This Bin is Not Assigned to you.",Toast.LENGTH_LONG).show();
                            } else{
                                Log.i("DOC","Document Not Found");
                            }
                        }else {
                            Log.i("Login", "Failed to get document: " + task.getException());
                        }
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdates();

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
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

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        try {
            List<Address> addresses = geocoder.getFromLocationName("kolkata", 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                LatLng london = new LatLng(address.getLatitude(), address.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(london)
                        .title(address.getLocality());
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(london, 16));
            }
        } catch (IOException e) {
            e.printStackTrace();
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