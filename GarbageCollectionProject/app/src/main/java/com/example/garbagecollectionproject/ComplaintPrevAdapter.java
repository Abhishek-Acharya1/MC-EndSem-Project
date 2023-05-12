package com.example.garbagecollectionproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ComplaintPrevAdapter extends RecyclerView.Adapter<ComplaintPrevAdapter.ViewHolder> {
    static FirebaseFirestore db;
    static Context context;
    ArrayList<Complaint> complaintsArrayList;
    public ComplaintPrevAdapter(Context context, ArrayList collectorArrayList) {
        this.context = context;
        this.complaintsArrayList = collectorArrayList;
    }
    @NonNull
    @Override
    public ComplaintPrevAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.complaint_prev_item,parent,false);
        return new ComplaintPrevAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintPrevAdapter.ViewHolder holder, int position) {
        Complaint complaint=complaintsArrayList.get(position);

        Timestamp timestamp = complaint.dateTime;
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(date);
        holder.dateTime.setText(formattedDate);


        holder.msg.setText(complaint.message);
        String status="";
        if(complaint.queryResolved==true) {
            status = "Resolved";
            holder.status.setText(status);
            holder.status.setTextColor(context.getResources().getColor(R.color.darkGreen));
            holder.status.setTypeface(null, Typeface.BOLD);

            holder.timeLay.setVisibility(View.VISIBLE);
            holder.msgLay.setVisibility(View.VISIBLE);

            Timestamp timestamp2 = complaint.queryResolvedTime;
            Date date2 = timestamp2.toDate();
            String formattedDate2 = sdf.format(date2);
            holder.resolvedTime.setText(formattedDate2);

            holder.resolvedMsg.setText(complaint.queryResolvedMessage);

        }
        else {
            status = "Pending";
            holder.status.setText(status);
            holder.status.setTextColor(Color.RED);


        }
    }

    @Override
    public int getItemCount() {
        return complaintsArrayList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView dateTime,msg,status,resolvedTime,resolvedMsg;
        LinearLayout timeLay,msgLay;
        ImageButton image, resolve;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTime=itemView.findViewById(R.id.tvDateTime);
            msg=itemView.findViewById(R.id.tvMessage);
            status=itemView.findViewById(R.id.tvStatus);
            resolvedTime=itemView.findViewById(R.id.tvResolvedTime);
            resolvedMsg=itemView.findViewById(R.id.tvResolvedMessage);
            timeLay=itemView.findViewById(R.id.timeLayout);
            msgLay=itemView.findViewById(R.id.msgLayout);
        }
    }
}
