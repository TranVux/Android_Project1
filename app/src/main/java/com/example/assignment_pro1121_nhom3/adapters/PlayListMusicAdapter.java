package com.example.assignment_pro1121_nhom3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;
import com.example.assignment_pro1121_nhom3.utils.PlaylistDiffUtil;

import java.util.ArrayList;

public class PlayListMusicAdapter extends RecyclerView.Adapter<PlayListMusicAdapter.PlayListMusicAdapterViewHolder> {
    private ArrayList<Playlist> list;
    private Context context;
    private ItemEvent.PlaylistItemEvent itemEvent;

    public PlayListMusicAdapter(ArrayList<Playlist> list, Context context, ItemEvent.PlaylistItemEvent itemEvent) {
        this.list = list;
        this.context = context;
        this.itemEvent = itemEvent;
    }

    public void setList(ArrayList<Playlist> list) {
        PlaylistDiffUtil musicDiffUtil = new PlaylistDiffUtil(this.list, list);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(musicDiffUtil);
        this.list = list;
        result.dispatchUpdatesTo(this);
    }


    @NonNull
    @Override
    public PlayListMusicAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_item, parent, false);
        return new PlayListMusicAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListMusicAdapterViewHolder holder, int position) {
        Playlist playlistTemp = list.get(position);
        if (playlistTemp == null) return;
        holder.creatorName.setText(CapitalizeWord.CapitalizeWords(playlistTemp.getCreatorName()));
        holder.playlistName.setText(CapitalizeWord.CapitalizeWords(playlistTemp.getName()));
        Glide.with(context).load(playlistTemp.getUrlThumbnail())
                .apply(new RequestOptions().override(92, 92))
                .error(R.drawable.fallback_img)
                .centerCrop()
                .into(holder.thumbnail);

        holder.itemPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemEvent.onItemClick(playlistTemp);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public static class PlayListMusicAdapterViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView creatorName, playlistName;
        private FrameLayout itemPlaylist;

        public PlayListMusicAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            creatorName = itemView.findViewById(R.id.singerName);
            playlistName = itemView.findViewById(R.id.musicName);
            itemPlaylist = itemView.findViewById(R.id.itemPlaylist);
        }
    }
}
