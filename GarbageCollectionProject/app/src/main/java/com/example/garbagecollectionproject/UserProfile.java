package com.example.garbagecollectionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {
    private TextView nameTV,emailTV,genderTV,mobileTV,roleTV;
    private ProgressBar progressBar;
    private String nameVal,emailVal,genderVal,mobileVal;
    private FirebaseAuth authUser;
    Button logout_button,back_button;
    private FirebaseFirestore db;
    String userType="";
    String collectorId="";
    long myLong=0;
    Double myDouble=0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        nameTV=findViewById(R.id.textView_show_full_name);
        emailTV=findViewById(R.id.textView_show_email);
        genderTV=findViewById(R.id.textView_show_gender);
        mobileTV=findViewById(R.id.textView_show_mobile);
        progressBar=findViewById(R.id.progress_bar);
        logout_button=findViewById(R.id.button_logout);
        back_button=findViewById(R.id.button_back);

        authUser=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser=authUser.getCurrentUser();

        Intent intent = getIntent();
        Boolean ratingRequired = intent.getBooleanExtra("ratingRequired",false);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.i("Bundle", "Key: " + key + ", Value: " + value);
            }
        }
        userType=(String) extras.get("userType");
        Log.i("USER MAIN STARTED", String.valueOf(ratingRequired)+" / "+(String) extras.get("userType"));
        if(ratingRequired==true){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Rate the Service!");

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(Gravity.CENTER_HORIZONTAL);

            final RatingBar ratingBar = new RatingBar(this, null, android.R.attr.ratingBarStyle);
            ratingBar.setStepSize(1);
            ratingBar.setNumStars(5);
            ratingBar.setProgress(3);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 16, 0, 16);
            ratingBar.setLayoutParams(lp);
            ratingBar.setEnabled(true);

            layout.addView(ratingBar);

            builder.setView(layout);
            builder.setCancelable(false);
            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Handle the OK button click
                    float rating = ratingBar.getRating();
                    Log.i("RATING", String.valueOf(rating));
                    Log.i("CollectorId:",collectorId);

                    db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("collectors").document(collectorId);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                if (doc.exists() ) {
                                    // Document found, you can access the data using document.getData()
                                    Log.i("Login","Collector ACCOUNT FOUND");
                                    Log.i("Login", "Document data: " + doc.getData());
//                                    Double ratingAvg= (Double) doc.get("ratingAvg");
                                    Integer totalCollections=((Long)  doc.get("totalCollections")).intValue();
                                    double newRating=0.0;
                                    Object ratingAvg = doc.get("ratingAvg");
                                    if (ratingAvg instanceof Long) {
                                        myLong = (Long) ratingAvg;
                                        if(totalCollections==0){
                                            newRating= Double.valueOf(rating);
                                        }else{
                                            Log.i("ratingAvg",String.valueOf(myLong));
                                            Log.i("totalCollections",String.valueOf(totalCollections));
                                            Log.i("newRating",String.valueOf(Math.round(rating)));
                                            newRating=((myLong*totalCollections)+rating)/(totalCollections+1);
                                        }
                                    } else if (ratingAvg instanceof Double) {
                                        myDouble = (Double) ratingAvg;
                                        if(totalCollections==0){
                                            newRating= Double.valueOf(rating);
                                        }else{
                                            Log.i("ratingAvg",String.valueOf(myDouble));
                                            Log.i("totalCollections",String.valueOf(totalCollections));
                                            Log.i("newRating",String.valueOf(Math.round(rating)));
                                            newRating=(((myDouble*totalCollections)+Math.round(rating))/(totalCollections+1));
                                        }
                                    }
                                    Log.i("finalRating",String.valueOf(newRating));
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("ratingAvg", newRating);
                                    updates.put("totalCollections", totalCollections+1);
                                    db.collection("collectors").document(collectorId).update(updates);
                                    Toast.makeText(UserProfile.this, "Rating Submitted", Toast.LENGTH_SHORT).show();

                                }else{
                                    Log.i("DOC","Document Not Found");
                                }
                            }else {
                                Log.i("Login", "Failed to get document: " + task.getException());
                            }
                        }
                    });
                }
            });


            AlertDialog dialog = builder.create();
            dialog.show();

        }
            if (firebaseUser == null) {
                Toast.makeText(this, "Something Went Wrong! User Details Unavailable! Login Again!", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                showUserProfile(firebaseUser);
            }

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authUser.signOut();
                SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("auth_token");
                editor.apply();
                Log.i("TAG","DELETED SHARED PREFERENCE");
                Intent intent= new Intent(UserProfile.this,LoginActivitiy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(UserProfile.this, "Logged Out Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
            back_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(userType.equals("User")) {
                        Intent intent= new Intent(UserProfile.this,user_main.class);
                        startActivity(intent);
                    }
                    else if(userType.equals("Collector")) {
                        Intent intent= new Intent(UserProfile.this,collector_main.class);
                        startActivity(intent);
                    }
                    else if(userType.equals("Admin")) {
                        Intent intent = new Intent(UserProfile.this, admin_main.class);
                        startActivity(intent);
                    }
                }
            });
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID=firebaseUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(userType != null) {
            Log.i("USER ROLE DEFINED",userType);
        }else{
            Intent intent=getIntent();
            userType=intent.getStringExtra("userType");
            Log.i("USER ROLE",userType);
        }
        DocumentReference docRef=null;
        if(userType.equals("User")) docRef = db.collection("users").document(userID);
        else if(userType.equals("Collector")) docRef = db.collection("collectors").document(userID);
        else if(userType.equals("Admin")) docRef=db.collection("admin").document(userID);

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

                        String name= (String) doc[0].get("fullName");
                        String email= firebaseUser.getEmail();;
                        String gender= (String) doc[0].get("gender");
                        String mobile= (String) doc[0].get("mobile");
                        collectorId=(String) doc[0].get("collectorId");

                        nameTV.setText(name);
                        emailTV.setText(email);
                        genderTV.setText(gender);
                        mobileTV.setText(mobile);

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
    }
}