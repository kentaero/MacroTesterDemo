package com.example.macrotester.rvGroup;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.macrotester.R;

import java.util.ArrayList;

public class rvAdapter extends RecyclerView.Adapter<rvAdapter.CustomViewHolder> {

    private ArrayList<rvItemData> arrayList;
    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    public rvAdapter(ArrayList<rvItemData> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public rvAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_list,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull rvAdapter.CustomViewHolder holder, int position) {
        holder.txtItemIdx.setText(String.valueOf(arrayList.get(position).getIdx()));
        holder.txtItemSiteKeyword.setText(arrayList.get(position).getSiteKeyword());
        holder.txtItemKeyword.setText(arrayList.get(position).getKeyword());
        holder.txtItemWaitTime.setText(String.valueOf(arrayList.get(position).getWaitTime()));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView txtItemIdx;
        protected TextView txtItemSiteKeyword;
        protected TextView txtItemKeyword;
        protected TextView txtItemWaitTime;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.txtItemIdx = (TextView) itemView.findViewById(R.id.txtItemIdx);
            this.txtItemSiteKeyword = (TextView) itemView.findViewById(R.id.txtItemSiteKeyword);
            this.txtItemKeyword = (TextView) itemView.findViewById(R.id.txtItemKeyword);
            this.txtItemWaitTime = (TextView) itemView.findViewById(R.id.txtItemWaitTime);

        }
    }
}
