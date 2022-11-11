package com.example.assignment_pro1121_nhom3.adapters;


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
import com.example.assignment_pro1121_nhom3.models.Playlist;

import java.util.List;

public class ChartPlaylistAdapter extends RecyclerView.Adapter<ChartPlaylistAdapter.ChartplaylistViewHolder> {

    private Context context;
    private List<Music> list;
    public ItemChartEvent itemChartEvent;

    public ChartPlaylistAdapter(Context context, ItemChartEvent itemChartEvent) {
        this.context = context;
        this.itemChartEvent = itemChartEvent;
    }

    public void setData(List<Music> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChartplaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.charts_item, parent, false);
        return new ChartplaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartplaylistViewHolder holder, int position) {
        Music music = list.get(position);
        if (music == null) {
            return;
        }
        Glide.with(context).load(list.get(position).getImg()).into(holder.imgSong);
        holder.tvNumber.setText(music.getId());
        holder.imgSong.setImageResource(music.getImg());
        holder.tvSong.setText(music.getName());
        holder.tvSinger.setText(music.getSingerName());
        holder.tvView.setText(music.getViews().toString());
        holder.chartMusicItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemChartEvent.onItemClick(music);
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

    public static class ChartplaylistViewHolder extends RecyclerView.ViewHolder {

        ImageView imgSong;
        TextView tvSong, tvSinger, tvView, tvNumber;
        LinearLayout chartMusicItem;

        public ChartplaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            imgSong = itemView.findViewById(R.id.imgSong);
            tvSong = itemView.findViewById(R.id.tvSong);
            tvSinger = itemView.findViewById(R.id.tvSinger);
            tvView = itemView.findViewById(R.id.tvView);
            chartMusicItem = itemView.findViewById(R.id.itemChart);
        }
    }

    public interface ItemChartEvent {
        void onItemClick(Music music);
    }
}
