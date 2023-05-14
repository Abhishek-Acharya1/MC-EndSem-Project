package com.example.garbagecollectionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class collector_main extends AppCompatActivity {
    ImageButton profile;
    Button viewMap, logout;
    private FirebaseAuth authUser;
    FirebaseFirestore firestore;
    boolean locationCheck=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector_main);
        profile = findViewById(R.id.profileImageButton);
        viewMap = findViewById(R.id.button_view_map);
        logout = findViewById(R.id.button_logout);
        authUser = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();

        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(collector_main.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(collector_main.this, UserProfile.class);
                intent.putExtra("ratingRequired",false);
                intent.putExtra("userType", "Collector");
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authUser.signOut();
                SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("auth_token");
                editor.apply();
                Log.i("TAG","DELETED SHARED PREFERENCE COLLECTOR");
                locationCheck=false;
                Intent intent = new Intent(collector_main.this, LoginActivitiy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Log.i("HERE", "1");
        // Create a LocationRequest object to specify the location update parameters
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Update location every 10 seconds
        locationRequest.setFastestInterval(5000); // Maximum update rate 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // High accuracy location updates

// Create a LocationCallback object to handle the location updates
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // Called when the location has changed
                Log.i("HERE", "CHANGED");
                Location location = locationResult.getLastLocation();
                Log.i("Lat", String.valueOf(location.getLatitude()));
                Log.i("Long", String.valueOf(location.getLongitude()));
                if(locationCheck==true){
                    DocumentReference documentReference=firestore.collection("collectors").document(authUser.getUid());
                    documentReference.update("curLat",location.getLatitude(),"curLong",location.getLongitude()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                        }
                    });
                }

            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                boolean isLocationAvailable = locationAvailability.isLocationAvailable();
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i("HERE","3");
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);

    }
}