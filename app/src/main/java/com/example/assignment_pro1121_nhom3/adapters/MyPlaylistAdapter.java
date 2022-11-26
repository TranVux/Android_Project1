package com.example.assignment_pro1121_nhom3.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;

import java.util.ArrayList;
import java.util.List;

public class MyPlaylistAdapter extends RecyclerView.Adapter<MyPlaylistAdapter.MyPlaylistViewHolder> {


    private Context context;
    private List<Music> list;
    public MyPlaylistAdapter.ItemChartEvent itemChartEvent;

    public MyPlaylistAdapter(Context context, MyPlaylistAdapter.ItemChartEvent itemChartEvent) {
        this.context = context;
        this.itemChartEvent = itemChartEvent;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Music> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ArrayList<Music> getList() {
        return (ArrayList<Music>) list;
    }

    @NonNull
    @Override
    public MyPlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new MyPlaylistViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyPlaylistViewHolder holder, int position) {
        Music music = list.get(position);
        if (music == null) {
            return;
        }
        Glide.with(context).load(list.get(position).getThumbnailUrl()).into(holder.imgSong1);
        holder.txtSong.setText(CapitalizeWord.CapitalizeWords(music.getName()));
        holder.txtSinger.setText(CapitalizeWord.CapitalizeWords(music.getSingerName()));
        holder.txtView.setText(music.getViews().toString());
        holder.MyPlaylistItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemChartEvent.onItemClick(music, position);
            }
        });
        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemChartEvent.onMoreButtonClick(music);
            }
        });

    }


    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public static class MyPlaylistViewHolder extends RecyclerView.ViewHolder {

        ImageView imgSong1;
        TextView txtSong, txtSinger, txtView;
        LinearLayout MyPlaylistItem, btnMore;


        public MyPlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong1 = itemView.findViewById(R.id.imgSong1);
            txtSong = itemView.findViewById(R.id.txtSong);
            txtSinger = itemView.findViewById(R.id.txtSinger);
            txtView = itemView.findViewById(R.id.txtView);
            btnMore = itemView.findViewById(R.id.btnMore);
            MyPlaylistItem = itemView.findViewById(R.id.itemplaylist);


        }
    }

    public interface ItemChartEvent {
        void onItemClick(Music music, int position);

        void onMoreButtonClick(Music music);
    }
}


