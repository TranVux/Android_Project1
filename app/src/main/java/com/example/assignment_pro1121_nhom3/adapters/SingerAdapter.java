package com.example.assignment_pro1121_nhom3.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Singer;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.SingerViewHolder> {
    private ArrayList<Singer> listSinger;
    private Context context;
    private ItemEvent.SingerItemEvent singerItemEvent;

    public SingerAdapter(ArrayList<Singer> listSinger, Context context, ItemEvent.SingerItemEvent singerItemEvent) {
        this.listSinger = listSinger;
        this.context = context;
        this.singerItemEvent = singerItemEvent;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListSinger(ArrayList<Singer> listSinger) {
        this.listSinger = listSinger;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SingerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_singer, parent, false);
        return new SingerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingerViewHolder holder, int position) {
        Singer tempSinger = listSinger.get(position);
        if (tempSinger == null) return;
        holder.singerName.setText(CapitalizeWord.CapitalizeWords(tempSinger.getName()));
        Glide.with(context)
                .load(tempSinger.getAvtUrl()).apply(new RequestOptions().override(60, 60))
                .centerCrop()
                .into(holder.singerImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singerItemEvent.onItemClick(tempSinger);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listSinger != null) return listSinger.size();
        return 0;
    }

    public static class SingerViewHolder extends RecyclerView.ViewHolder {
        LinearLayout singerItem;
        TextView singerName;
        CircleImageView singerImage;

        public SingerViewHolder(@NonNull View itemView) {
            super(itemView);

            singerImage = itemView.findViewById(R.id.singerImg);
            singerName = itemView.findViewById(R.id.singerName);
            singerItem = itemView.findViewById(R.id.itemSinger);
        }
    }
}
