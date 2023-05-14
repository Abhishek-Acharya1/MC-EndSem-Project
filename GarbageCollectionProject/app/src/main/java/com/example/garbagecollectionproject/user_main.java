package com.example.garbagecollectionproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.time.LocalTime;
import java.util.Calendar;

public class user_main extends AppCompatActivity {
    ImageButton profile;
    Button viewMap, scanQR, logout, complaint, viewComplaints;
    private FirebaseAuth authUser;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        profile=findViewById(R.id.profileImageButton);
        viewMap=findViewById(R.id.button_view_map);
        scanQR=findViewById(R.id.button_scan_qr);
        logout=findViewById(R.id.button_logout);
        complaint=findViewById(R.id.button_raise_complaint);
        viewComplaints=findViewById(R.id.button_view_PrevComplaints);
        authUser=FirebaseAuth.getInstance();


        db=FirebaseFirestore.getInstance();
        Log.i("USER ID", authUser.getUid());
        db.collection("users").document(authUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error)
            {
                Log.i("HERE","HERE111!________________");
                if(error!=null)
                {
                    Log.i("HERE2","HERE222!________________");
                    Log.i("TAG", "Error while fetching the collector location");
                }
                else
                {
                    Log.i("HERE3","HERE333!________________");
                    if(value!=null && value.exists())
                    {
                        Log.i("HERE4","HERE444!________________");
                        Log.i("VALUE", String.valueOf(value.getData()));
                        Log.i("HERE4", String.valueOf(value.get("bin")));
                        Log.i("HERE4", String.valueOf(value.getBoolean("notifSent")));
                        if(((Long)value.get("bin")).intValue()==0 && value.getBoolean("notifSent")==false){
                            db.collection("users").document(value.getId()).update("notifSent",true);
                            sendNotification();
                        }
                    }
                    else
                    {
                        Log.i("HERE5","HERE555!________________");
                    }
                }
            }
        });



        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(user_main.this,UserMapsActivity.class);
                startActivity(intent);
            }
        });

        viewComplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(user_main.this, UserPrevComplaints.class);
                startActivity(intent);
            }
        });

        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(user_main.this,UserProfile.class);
                intent.putExtra("ratingRequired",false);
                intent.putExtra("userType","User");
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
                Log.i("TAG","DELETED SHARED PREFERENCE USER");
                Intent intent = new Intent(user_main.this,LoginActivitiy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(user_main.this, "Logged Out Successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(user_main.this,raiseComplaint.class);
                startActivity(intent);
            }
        });

    }

    private void sendNotification() {
        Log.i("NOTIFICATION","Started");
        // Create an intent to launch the activity you want to open
        Intent intent = new Intent(this, UserProfile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("ratingRequired",true);
        String user="User";
        intent.putExtra("userType",user);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"n")
                .setContentTitle("Garbage Collection System")
                .setContentText("Your Bin Has Been Cleared, Kindly rate the service!")
                .setSmallIcon(R.drawable.car2)
                .setAutoCancel(true)
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notif))
                .setContentIntent(pendingIntent);
        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());


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
                DocumentReference docRef = db.collection("users").document(userId);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()  && authUser.getUid().equals(userId) ) {
                                // Document found, you can access the data using document.getData()
                                Log.i("Login","USER ACCOUNT FOUND");
                                Log.i("Login", "Document data: " + doc.getData());
                                String userId=doc.getId();
                                int bin = ((Long)  doc.get("bin")).intValue();
                                if(bin==0) {
                                    Calendar currentTime = Calendar.getInstance();

                                    // Set the calendar time to 11 am
                                    Calendar elevenAM = Calendar.getInstance();
                                    elevenAM.set(Calendar.HOUR_OF_DAY, 11);
                                    elevenAM.set(Calendar.MINUTE, 0);
                                    elevenAM.set(Calendar.SECOND, 0);

                                    // Set the calendar time to 5 pm
                                    Calendar fivePM = Calendar.getInstance();
                                    fivePM.set(Calendar.HOUR_OF_DAY, 17);
                                    fivePM.set(Calendar.MINUTE, 0);
                                    fivePM.set(Calendar.SECOND, 0);

                                    // Check if current time is between 11 am and 5 pm
                                    if (currentTime.after(elevenAM) && currentTime.before(fivePM)) {
                                        Toast.makeText(user_main.this, "Since You Are Marking the Bin after 11 AM, Your Garbage Might be collected in second round or tomorrow, So Kindly Cooperate for that!", Toast.LENGTH_LONG).show();
                                    }


                                    db.collection("users").document(doc.getId()).update("bin",1);
                                    Toast.makeText(user_main.this, "Dustbin Set to be full", Toast.LENGTH_SHORT).show();
                                }
                                if(bin==1 && authUser.getUid().equals(userId)) {
                                    Toast.makeText(user_main.this, "Bin is Already marked as full", Toast.LENGTH_SHORT).show();
                                }

                            } else if ( !authUser.getUid().equals(userId) ){
                                Toast.makeText(user_main.this,"Oops! This is Not Your Bin.",Toast.LENGTH_LONG).show();
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
}