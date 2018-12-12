package com.example.myapplication;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private String[][] data;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public MyViewHolder(TextView viewer) {
            super(viewer);
            textView = viewer.findViewById(R.id.title);
        }
    }

    public MyAdapter(String[][] data) {
        this.data = data;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText((String) data[position][0]);
        holder.textView.setBackgroundColor(Color.parseColor((String) data[position][1]));
        holder.textView.setTextColor(Color.parseColor((String) data[position][2]));
    }

    @Override
    public int getItemCount() {
        return data.length;
    }
}