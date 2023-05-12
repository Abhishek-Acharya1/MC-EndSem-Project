package com.example.garbagecollectionproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {
    static FirebaseFirestore db;
    static Context context;
    ArrayList<Complaint> complaintsArrayList;
    public ComplaintAdapter(Context context, ArrayList collectorArrayList) {
        this.context = context;
        this.complaintsArrayList = collectorArrayList;
    }
    @NonNull
    @Override
    public ComplaintAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.complaint_item,parent,false);
        return new ComplaintAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintAdapter.ViewHolder holder, int position) {
        Complaint complaint=complaintsArrayList.get(position);
        holder.name.setText(complaint.userName);
        holder.phone.setText(complaint.mobile);


        Timestamp timestamp = complaint.dateTime;
        // Convert the timestamp to a Date object
        Date date = timestamp.toDate();

        // Convert the Date object to a formatted string
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(date);
        holder.dateTime.setText(formattedDate);
        holder.msg.setText(complaint.message);


        String collectorId=complaint.collectorId;
        Log.i("COLLECTOR ID",collectorId);
        db=FirebaseFirestore.getInstance();
        db.collection("collectors").document(collectorId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    holder.collector.setText(task.getResult().getString("fullName"));
                    holder.collectorMobile.setText(task.getResult().getString("mobile"));
                }
            }
        });
//
        Complaint comp = complaintsArrayList.get(position);

        holder.resolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("RESOLVE","BUTTON CLICKED!");
                resolve(complaint.complaintId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return complaintsArrayList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,phone,rating,collector,collectorMobile,dateTime,msg;
        ImageButton image, resolve;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.tvName);
            phone=itemView.findViewById(R.id.tvPhone);
            collector=itemView.findViewById(R.id.tvCollector);
            collectorMobile=itemView.findViewById(R.id.tvCollectorMobile);
            dateTime=itemView.findViewById(R.id.tvDateTime);
            msg=itemView.findViewById(R.id.tvMessage);
            resolve=itemView.findViewById(R.id.resolveComplaint);
        }
    }
    public static void resolve(String complaintId){
        Log.i("RESOLVE","Inside Resolve Method");
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.edit_text_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.userMsg);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String msg=userInput.getText().toString();
                        Log.i("Message",msg);
                        db = FirebaseFirestore.getInstance();
                        DocumentReference docRef = db.collection("complaints").document(complaintId);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists() ) {
                                        // Document found, you can access the data using document.getData()
                                        Log.i("Login","Complaint FOUND");
                                        Log.i("Login", "Document data: " + doc.getData());

                                        Map<String, Object> updates = new HashMap<>();
                                        long timestampInMillis = System.currentTimeMillis();
                                        Date date = new Date(timestampInMillis);
                                        Timestamp timestamp = new Timestamp(date);
                                        updates.put("queryResolved", true);
                                        updates.put("queryResolvedMessage", msg);
                                        updates.put("queryResolvedTime",timestamp);
                                        db.collection("complaints").document(complaintId).update(updates);
                                        Toast.makeText(context, "Query Resolved", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(context,admin_main.class);
                                        context.startActivity(intent);
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
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
