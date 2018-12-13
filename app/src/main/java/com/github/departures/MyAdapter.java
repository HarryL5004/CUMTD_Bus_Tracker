package com.github.departures;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private String[][] data;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        MyViewHolder(TextView viewer) {
            super(viewer);
            textView = viewer.findViewById(R.id.title);
        }
    }

    MyAdapter(String[][] data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {

        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_list, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView.setText(data[position][0]);
        holder.textView.setBackgroundColor(Color.parseColor(data[position][1]));
        holder.textView.setTextColor(Color.parseColor(data[position][2]));
    }

    @Override
    public int getItemCount() {
        return data.length;
    }
}