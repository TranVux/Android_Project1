package com.example.assignment_pro1121_nhom3.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;
import com.example.assignment_pro1121_nhom3.utils.PlaylistDiffUtil;

import java.util.ArrayList;

public class AdapterAddMusicToPlaylist extends RecyclerView.Adapter<AdapterAddMusicToPlaylist.AddMusicToPlaylistViewHolder> {
    ArrayList<Playlist> list;
    Context context;
    ItemPlaylistEvent itemPlaylistEvent;

    public AdapterAddMusicToPlaylist(ArrayList<Playlist> list, Context context, ItemPlaylistEvent itemPlaylistEvent) {
        this.list = list;
        this.context = context;
        this.itemPlaylistEvent = itemPlaylistEvent;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(ArrayList<Playlist> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddMusicToPlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_add_music_to_playlist, parent, false);
        return new AddMusicToPlaylistViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AddMusicToPlaylistViewHolder holder, int position) {
        Playlist tempPlaylist = list.get(position);
        if (tempPlaylist == null) return;
        holder.playlistName.setText(CapitalizeWord.CapitalizeWords(tempPlaylist.getName()));
        holder.amountOfSong.setText(tempPlaylist.getMusics().size() + " bài");
        Glide.with(context)
                .load(tempPlaylist.getUrlThumbnail())
                .apply(new RequestOptions().override(63, 63))
                .into(holder.playlistThumbnail);

        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemPlaylistEvent.onItemClick(tempPlaylist);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) return list.size();
        return 0;
    }

    public static class AddMusicToPlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName, amountOfSong, btnAdd;
        ImageView playlistThumbnail;

        public AddMusicToPlaylistViewHolder(@NonNull View itemView) {
            super(itemView);

            playlistName = itemView.findViewById(R.id.playlistName);
            amountOfSong = itemView.findViewById(R.id.amountOfSong);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            playlistThumbnail = itemView.findViewById(R.id.playlistThumbnail);
        }
    }

    public interface ItemPlaylistEvent {
        void onItemClick(Playlist playlist);
    }
}
