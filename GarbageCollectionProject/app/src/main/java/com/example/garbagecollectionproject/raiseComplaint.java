package com.example.garbagecollectionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class raiseComplaint extends AppCompatActivity {
    Button submit, back, logout;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    EditText msg;
    ImageView clickedImage;
    Boolean imageUploaded=false;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_complaint);
        submit=findViewById(R.id.submit);
        back=findViewById(R.id.button_back);
        logout=findViewById(R.id.button_logout);
        clickedImage=findViewById(R.id.clickedImage);
        msg=findViewById(R.id.complaintMsg);
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(raiseComplaint.this,user_main.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("auth_token");
                editor.apply();
                Log.i("TAG","DELETED SHARED PREFERENCE");
                Intent intent= new Intent(raiseComplaint.this,LoginActivitiy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(raiseComplaint.this, "Logged Out Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(msg.getText().toString().equals("")){
                    Toast.makeText(raiseComplaint.this, "Please Enter a Short Description Message", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.i("Msg Recieved",msg.getText().toString());

                    String userId=auth.getUid();
                    db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                Map<String, Object> complaintDetails = new HashMap<>();

                                long timestampInMillis = System.currentTimeMillis();
                                Date date = new Date(timestampInMillis);
                                Timestamp timestamp = new Timestamp(date);

                                DocumentSnapshot doc=task.getResult();
                                String name=doc.getString("fullName");
                                complaintDetails.put("userId", userId);
                                complaintDetails.put("userName", doc.getString("fullName"));
                                complaintDetails.put("mobile", doc.getString("mobile"));
                                complaintDetails.put("dateTime", timestamp);
                                complaintDetails.put("message",msg.getText().toString());
                                complaintDetails.put("queryResolved", false);
                                complaintDetails.put("queryResolvedMessage", "");
                                complaintDetails.put("queryResolvedTime", timestamp);
                                complaintDetails.put("collectorId",doc.getString("collectorId"));


                                for (Map.Entry<String, Object> entry : complaintDetails.entrySet()) {
                                    Log.i("DATA",entry.getKey() + " = " + entry.getValue());
                                }
                                DocumentReference dRef=db.collection("complaints").document();
                                String complaintId=dRef.getId();
                                complaintDetails.put("complaintId",complaintId);
                                dRef.set(complaintDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.i("COMPLAINT","Complaint Created");
                                        Intent intent=new Intent(raiseComplaint.this,user_main.class);
                                        Toast.makeText(raiseComplaint.this,"Complaint Submitted",Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    });

                }

            }
        });
    }
}