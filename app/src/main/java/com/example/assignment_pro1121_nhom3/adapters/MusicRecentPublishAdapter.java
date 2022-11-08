package com.example.assignment_pro1121_nhom3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;
import com.example.assignment_pro1121_nhom3.utils.MusicDiffUtil;

import java.util.ArrayList;

public class MusicRecentPublishAdapter extends RecyclerView.Adapter<MusicRecentPublishAdapter.MusicRecentPublishViewHolder> {
    private ArrayList<Music> list;
    private Context context;
    private ItemEvent.MusicItemEvent itemEvent;

    public MusicRecentPublishAdapter(ArrayList<Music> list, Context context, ItemEvent.MusicItemEvent itemEvent) {
        this.list = list;
        this.context = context;
        this.itemEvent = itemEvent;
    }

    public void setList(ArrayList<Music> list) {
        MusicDiffUtil musicDiffUtil = new MusicDiffUtil(this.list, list);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(musicDiffUtil);
        this.list = list;
        result.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public MusicRecentPublishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_recent_publish_item, parent, false);
        return new MusicRecentPublishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicRecentPublishViewHolder holder, int position) {
        Music musicTemp = list.get(position);
        if (musicTemp == null) return;
        Glide.with(context).load(musicTemp.getThumbnailUrl())
                .apply(new RequestOptions().override(105, 105))
                .centerCrop()
                .error(R.drawable.fallback_img).into(holder.thumbnail);
        holder.musicName.setText(CapitalizeWord.CapitalizeWords(musicTemp.getName()));
        holder.singerName.setText(CapitalizeWord.CapitalizeWords(musicTemp.getSingerName()));
        holder.itemMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemEvent.onItemClick(musicTemp);
            }
        });

        holder.singerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemEvent.onSingerNameClick(musicTemp.getSingerId());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public static class MusicRecentPublishViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView singerName, musicName;
        private LinearLayout itemMusic;

        public MusicRecentPublishViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            singerName = itemView.findViewById(R.id.singerName);
            musicName = itemView.findViewById(R.id.musicName);
            itemMusic = itemView.findViewById(R.id.itemMusic);
        }
    }
}
