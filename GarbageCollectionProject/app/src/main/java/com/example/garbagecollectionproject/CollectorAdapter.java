package com.example.garbagecollectionproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CollectorAdapter extends RecyclerView.Adapter<CollectorAdapter.ViewHolder> {

    Context context;
    ArrayList<Collector> collectorArrayList;
    public CollectorAdapter(Context context, ArrayList collectorArrayList) {
        this.context = context;
        this.collectorArrayList = collectorArrayList;
    }



    @NonNull
    @Override
    public CollectorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.collector_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectorAdapter.ViewHolder holder, int position) {
        Collector collector=collectorArrayList.get(position);
        holder.name.setText(collector.fullName);
        holder.phone.setText(collector.mobile);
        Log.i("RATING",String.valueOf(collector.ratingAvg));
        holder.rating.setText(String.valueOf(collector.ratingAvg));
    }

    @Override
    public int getItemCount() {
        return collectorArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,phone,rating;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.tvName);
            phone=itemView.findViewById(R.id.tvPhone);
            rating=itemView.findViewById(R.id.tvRating);

        }
    }
}
