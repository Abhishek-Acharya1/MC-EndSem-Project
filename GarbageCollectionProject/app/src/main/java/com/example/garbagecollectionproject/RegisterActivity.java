package com.example.garbagecollectionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullName, email, mobile, password, password2, confirmPassword;
    private ProgressBar progressBar;
    private CheckBox checkBox_terms_conditions;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonGender;
    private RadioGroup radioGroupRole;
    private Button buttonRegister;
    private RadioButton radioButtonRole;
    private MaterialAlertDialogBuilder alert;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    double lat=0,lng=0;
    String colWithMinDist="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullName = findViewById(R.id.editText_register_full_name);
        email = findViewById(R.id.editText_register_email);
        mobile = findViewById(R.id.editText_register_mobile);
        password = findViewById(R.id.editText_register_password);
        password2 = findViewById(R.id.editText_register_password2);
        confirmPassword = findViewById(R.id.editText_register_password2);
        checkBox_terms_conditions = findViewById(R.id.checkBox_terms_conditions);
        radioGroupGender = findViewById(R.id.radio_group_register_gender);
        radioGroupGender.clearCheck();
        buttonRegister = findViewById(R.id.button_register);

        radioGroupRole = findViewById(R.id.radio_group_register_role);
        radioGroupRole.clearCheck();

        progressBar = findViewById(R.id.progressBar);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();
        checkBox_terms_conditions.setChecked(false);
        buttonRegister.setEnabled(false);
        alert = new MaterialAlertDialogBuilder(this);
        checkBox_terms_conditions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE); // disable activity window
                    alert.setTitle("Terms And Conditions");
                    alert.setMessage("1. Acceptance of Terms: By using this app, you agree to be bound by these terms and conditions, which constitute a binding agreement between you and the app's owner. \n" +
                            "2. Privacy Policy: Your use of this app is subject to our privacy policy, which outlines how we collect, use, and share your data.\n" +
                            "3. Termination: The app's owner reserves the right to terminate your access to this app at any time and for any reason without notice.");
                    alert.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            buttonRegister.setEnabled(true);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE); // enable activity window
                            dialog.dismiss();
                        }
                    });
                    alert.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE); // enable activity window
                            dialog.dismiss();
                            checkBox_terms_conditions.setChecked(false);
                        }
                    });
                    alert.setCancelable(false);
                    alert.show();
                }else{
                    buttonRegister.setEnabled(false);
                    checkBox_terms_conditions.setChecked(false);
                }
            }
        });


        ImageView imageViewShowHidePass = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePass.setImageResource(R.drawable.ic_show_pwd);
        imageViewShowHidePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowHidePass.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePass.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        ImageView imageViewShowHidePass2 = findViewById(R.id.imageView_show_hide_pwd2);
        imageViewShowHidePass2.setImageResource(R.drawable.ic_show_pwd);
        imageViewShowHidePass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password2.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    password2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowHidePass2.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    password2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePass2.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });




        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
                radioButtonGender = findViewById(selectedGenderId);

                int selectedRoleId = radioGroupRole.getCheckedRadioButtonId();
                radioButtonRole = findViewById(selectedRoleId);

                String nameVal = fullName.getText().toString();
                String emailVal = email.getText().toString();
                String mobileVal = mobile.getText().toString();
                String passwordVal = password.getText().toString();
                String passwordVal2 = confirmPassword.getText().toString();
                String genderVal;
                String roleVal;

                String mobileRegex = "[6-9][0-9]{9}";
                Matcher mobMatcher;
                Pattern mobPattern = Pattern.compile(mobileRegex);
                mobMatcher = mobPattern.matcher(mobileVal);



                if (TextUtils.isEmpty(nameVal)) {
                    Toast.makeText(RegisterActivity.this, "Enter Full Name", Toast.LENGTH_SHORT).show();
                    fullName.setError("Full Name is Required");
                    fullName.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()) {
                    Toast.makeText(RegisterActivity.this, "Enter Correct Email Id", Toast.LENGTH_SHORT).show();
                    email.setError("Valid Email is Required");
                    email.requestFocus();
                } else if (TextUtils.isEmpty(mobileVal)) {
                    Toast.makeText(RegisterActivity.this, "Enter Mobile No.", Toast.LENGTH_SHORT).show();
                    mobile.setError("Mobile No. is Required");
                    mobile.requestFocus();
                } else if (TextUtils.isEmpty(passwordVal)) {
                    Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    password.setError("Password is Required");
                    password.requestFocus();
                } else if (radioGroupGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Select a Gender", Toast.LENGTH_SHORT).show();
                    radioButtonGender.requestFocus();
                } else if (radioGroupRole.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Select a Role", Toast.LENGTH_SHORT).show();
                    radioButtonRole.requestFocus();
                } else if (mobMatcher.find() == false) {
                    Toast.makeText(RegisterActivity.this, "Re-Enter Mobile No.", Toast.LENGTH_SHORT).show();
                    mobile.setError("Valid Mobile No. is Required");
                    mobile.requestFocus();
                } else if (passwordVal.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password Should be atleast 6 digits", Toast.LENGTH_SHORT).show();
                    mobile.setError("Password too weak!");
                    mobile.requestFocus();
                } else if (TextUtils.isEmpty(passwordVal2)) {
                    Toast.makeText(RegisterActivity.this, "Confirm Password", Toast.LENGTH_SHORT).show();
                    password.setError("Password Confirmation is Required");
                    password.requestFocus();
                } else if (!passwordVal.equals(passwordVal2)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Same Password", Toast.LENGTH_SHORT).show();
                    confirmPassword.setError("Password Confirmation is required");
                    confirmPassword.requestFocus();
                    confirmPassword.clearComposingText();
                    confirmPassword.clearComposingText();
                } else {
                    genderVal = radioButtonGender.getText().toString();
                    roleVal = radioButtonRole.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(nameVal, emailVal, mobileVal, passwordVal, passwordVal2, genderVal, roleVal, lat, lng, 0);
                }
            }
        });
    }



    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            lat=location.getLatitude();
                            lng=location.getLongitude();
                            Log.i("LOCATION",location.getLatitude() + "");
                            Log.i("LOCATION",location.getLongitude() + "");
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.i("Latitude: " , mLastLocation.getLatitude() + "");
            Log.i("Longitude: " , mLastLocation.getLongitude() + "");
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }



    private void registerUser(String nameVal, String emailVal, String mobileVal, String passwordVal, String passwordVal2, String genderVal, String roleVal, Double lat, Double lng, int bin) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Log.i("LOGIN", "LOGIN1");
        auth.createUserWithEmailAndPassword(emailVal, passwordVal).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.i("LOGIN", "LOGIN2");

                if (task.isSuccessful()) {
                    Log.i("LOGIN", "LOGIN3");
                    FirebaseUser user = auth.getCurrentUser();

                    ReadWriteUserDetails writer = new ReadWriteUserDetails(nameVal,genderVal,mobileVal,lat,lng,0);
                    Log.i("WRITER LAT", String.valueOf(writer.lat));
                    Log.i("WRITER LNG", String.valueOf(writer.lng));

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Log.i("DB",roleVal);
                    if(roleVal.equals("User")){
                        Log.i("DB","Storing");
                        Log.i("LOCATION Storing", String.valueOf(lat));
                        Log.i("LOCATION Storing",String.valueOf(lng));

                        Map<String, Object> userDetails = new HashMap<>();
                        userDetails.put("fullName", writer.fullName);
                        userDetails.put("gender", writer.gender);
                        userDetails.put("mobile", writer.mobile);
                        userDetails.put("lat", writer.lat);
                        userDetails.put("long", writer.lng);
                        userDetails.put("bin", writer.bin);
                        userDetails.put("notifSent",true);


                        Log.i("COLLECTOR FIND","START");
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                findNearestCollector(auth.getUid(),writer.lat,writer.lng,userDetails);
                            }});

                        t.start(); // spawn thread

                        try {
                            t.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }



                    }else if(roleVal.equals("Collector")){
                        Map<String, Object> collectorDetails = new HashMap<>();
                        collectorDetails.put("fullName", writer.fullName);
                        collectorDetails.put("gender", writer.gender);
                        collectorDetails.put("mobile", writer.mobile);
                        collectorDetails.put("fixedLat", writer.lat);
                        collectorDetails.put("fixedLong", writer.lng);
                        collectorDetails.put("curLat", writer.lat);
                        collectorDetails.put("curLong", writer.lng);
                        collectorDetails.put("ratingAvg",0);
                        collectorDetails.put("totalCollections",0);

                        ArrayList<String> usersAllocated=new ArrayList<>();
                        collectorDetails.put("usersAllocated",usersAllocated);


                        db.collection("collectors")
                                .document(auth.getUid())
                                .set(collectorDetails)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void avoid) {
                                        Log.i("USER", "DocumentSnapshot added");
                                        Intent intent=new Intent(RegisterActivity.this,LoginActivitiy.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("USER", "Error adding document", e);
                                        Toast.makeText(RegisterActivity.this,"User Registration Failed!",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else if(roleVal.equals("Admin")){
                        Map<String, Object> collectorDetails = new HashMap<>();
                        collectorDetails.put("fullName", writer.fullName);
                        collectorDetails.put("gender", writer.gender);
                        collectorDetails.put("mobile", writer.mobile);


                        db.collection("admin")
                                .document(auth.getUid())
                                .set(collectorDetails)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void avoid) {
                                        Log.i("USER", "DocumentSnapshot added");
                                        Intent intent=new Intent(RegisterActivity.this,LoginActivitiy.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("USER", "Error adding document", e);
                                        Toast.makeText(RegisterActivity.this,"User Registration Failed!",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }


                    progressBar.setVisibility(View.GONE);
                    user.sendEmailVerification();
                    Toast.makeText(RegisterActivity.this, "User Successfully Registered, Verification Email Sent!", Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("LOGIN-ERROR",task.getException().getLocalizedMessage());
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        email.setError("Email Id is Invalid or Account Already Exists!");
                        email.requestFocus();
                    }catch(FirebaseAuthUserCollisionException e){
                        email.setError("User With This Email ID Already Exists");
                        email.requestFocus();
                    }catch(Exception e){
                        Log.e("EXCEPTION",e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /* TODO */
    private float findDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

    private void findNearestCollector(String userId, Double userLat, Double userLong, Map<String, Object> userDetails) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.i("COLLECTORS","Checking All Collectors");
        db.collection("collectors").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Float minDist=Float.MAX_VALUE;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.i("COLLECTOR ID=",document.getId());
                        Double colLat = (Double) document.get("fixedLat");
                        Double colLong = (Double) document.get("fixedLong");
                        Float distBetween=findDistance(userLat,userLong,colLat,colLong);
                        Log.i("COLLECTOR", String.valueOf(distBetween));
                        if(distBetween<minDist){
                            minDist=distBetween;
                            colWithMinDist=document.getId();
                        }
                    }

                    Log.i("COLLECTOR ID SELECTED=",colWithMinDist);
                    Log.i("COLLECTOR ALLOCATED", colWithMinDist);
                    if(!colWithMinDist.equals("")){
                        userDetails.put("collectorAllocated",true);
                    }else{
                        userDetails.put("collectorAllocated",false);
                    }
                    Log.i("PUTTING COLLECTOR ID",colWithMinDist);
                    userDetails.put("collectorId",colWithMinDist);

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    db.collection("users")
                            .document(auth.getUid())
                            .set(userDetails)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("USER", "DocumentSnapshot added");
                                    Toast.makeText(RegisterActivity.this, "User Details Saved Successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(RegisterActivity.this,LoginActivitiy.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("USER", "Error adding document", e);
                                    Toast.makeText(RegisterActivity.this,"User Registration Failed!",Toast.LENGTH_SHORT).show();
                                }
                            });

                    DocumentReference docRef= db.collection("collectors").document(colWithMinDist);
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

                                    ArrayList<String> users= (ArrayList<String>) doc.get("usersAllocated");
//                                    List<String> users= Arrays.asList(usersList);
                                    users.add(auth.getUid());
                                    db.collection("collectors").document(colWithMinDist).update("usersAllocated",users);
                                    progressBar.setVisibility(View.GONE);

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
                    Log.i("ALL DOCS", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
