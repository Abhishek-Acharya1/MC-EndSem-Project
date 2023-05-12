package com.example.garbagecollectionproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class userComplaints extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Complaint> complaintsArrayList;
    ComplaintAdapter complaintAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    Button back_button, logout_button;
    private FirebaseAuth authUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_complaints);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Collector Details");
        progressDialog.show();

        recyclerView=findViewById(R.id.recyclerView1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        back_button=findViewById(R.id.button_back);
        logout_button=findViewById(R.id.button_logout);
        db=FirebaseFirestore.getInstance();
        authUser=FirebaseAuth.getInstance();

        complaintsArrayList= new ArrayList<Complaint>();
        complaintAdapter= new ComplaintAdapter(userComplaints.this,complaintsArrayList);

        recyclerView.setAdapter(complaintAdapter);
        getComplaints();

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authUser.signOut();
                SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("auth_token");
                editor.apply();
                Log.i("TAG","DELETED SHARED PREFERENCE");
                Intent intent= new Intent(userComplaints.this,LoginActivitiy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(userComplaints.this, "Logged Out Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(userComplaints.this, admin_main.class);
                startActivity(intent);
            }
        });
    }

    private void getComplaints() {
        db.collection("complaints")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null){
                            Log.i("ERROR","Error Fetching from Firestore");
                            if(progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            return;
                        }

                        for(DocumentChange doc:value.getDocumentChanges()){
                            if(doc.getType()==DocumentChange.Type.ADDED){
                                Complaint c= doc.getDocument().toObject(Complaint.class);
                                if(c.queryResolved==false)
                                    complaintsArrayList.add(doc.getDocument().toObject(Complaint.class));
                            }
                            if(progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            complaintAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}