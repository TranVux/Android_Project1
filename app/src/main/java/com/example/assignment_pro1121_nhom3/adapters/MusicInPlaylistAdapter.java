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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;

import java.util.ArrayList;

public class MusicInPlaylistAdapter extends RecyclerView.Adapter<MusicInPlaylistAdapter.MusicInPlaylistViewHolder> {
    private ArrayList<Music> listMusic;
    private Context context;
    private ItemEvent.MusicItemInPlayListEvent itemInPlayListEvent;

    public MusicInPlaylistAdapter(ArrayList<Music> listMusic, Context context, ItemEvent.MusicItemInPlayListEvent itemInPlayListEvent) {
        this.listMusic = listMusic;
        this.context = context;
        this.itemInPlayListEvent = itemInPlayListEvent;
    }

    public void setListMusic(ArrayList<Music> listMusic) {
        int initPosition;
        if (this.listMusic != null) {
            initPosition = this.listMusic.size() - 1;
        } else {
            initPosition = 0;
            this.listMusic = new ArrayList<>();
        }

        this.listMusic.addAll(listMusic);
        notifyItemChanged(initPosition, listMusic.size());
    }

    public ArrayList<Music> getListMusic() {
        return listMusic;
    }

    @NonNull
    @Override
    public MusicInPlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_in_playlist_layout, parent, false);
        return new MusicInPlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicInPlaylistViewHolder holder, int position) {
        Music tempMusic = listMusic.get(position);
        if (tempMusic == null) return;
        holder.singerName.setText(CapitalizeWord.CapitalizeWords(tempMusic.getSingerName()));
        holder.views.setText(String.valueOf(tempMusic.getViews()));
        holder.musicName.setText(CapitalizeWord.CapitalizeWords(tempMusic.getName()));
        Glide.with(context).load(tempMusic.getThumbnailUrl())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(new RequestOptions().override(150, 150))
                .error(R.drawable.fallback_img)
                .into(holder.musicThumbnail);

        holder.btnMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemInPlayListEvent.onMoreClick(tempMusic);
            }
        });

        holder.itemMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemInPlayListEvent.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (listMusic == null) return 0;
        return listMusic.size();
    }

    public static class MusicInPlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView musicThumbnail;
        TextView singerName, musicName, views;
        LinearLayout itemMusic, btnMoreOption;

        public MusicInPlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            itemMusic = itemView.findViewById(R.id.itemMusic);
            musicName = itemView.findViewById(R.id.musicName);
            singerName = itemView.findViewById(R.id.singerName);
            views = itemView.findViewById(R.id.labelMusicView);
            musicThumbnail = itemView.findViewById(R.id.musicThumbnail);
            btnMoreOption = itemView.findViewById(R.id.btnMoreOption);
        }
    }
}
