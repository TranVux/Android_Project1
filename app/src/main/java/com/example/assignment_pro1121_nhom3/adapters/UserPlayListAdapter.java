package com.example.assignment_pro1121_nhom3.adapters;

import android.annotation.SuppressLint;
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
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.models.Playlist;

import java.util.ArrayList;

public class UserPlayListAdapter extends RecyclerView.Adapter<UserPlayListAdapter.ViewHolder> {
    ArrayList<Playlist> playlists;
    PlaylistEvent playlistEvent;

    public UserPlayListAdapter(ArrayList<Playlist> playlists, PlaylistEvent playlistEvent) {
        this.playlists = playlists;
        this.playlistEvent = playlistEvent;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item_in_fragment_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.playListTitle.setText(playlist.getName());
        Glide.with(holder.playListImage.getContext()).load(playlist.getUrlThumbnail())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .error(R.drawable.fallback_img)
                .centerCrop()
                .into(holder.playListImage);
        holder.countSong.setText(playlist.getMusics().size() + " bài hát");

        holder.itemPlaylistInFragUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistEvent.onItemClick(playlist);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView playListImage;
        TextView playListTitle, countSong;
        LinearLayout itemPlaylistInFragUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            countSong = itemView.findViewById(R.id.itemPlaylistCountSong);
            playListImage = itemView.findViewById(R.id.itemPlaylistThumbnail);
            playListTitle = itemView.findViewById(R.id.itemPlaylistTitle);
            itemPlaylistInFragUser = itemView.findViewById(R.id.itemPlaylistInFragUser);
        }
    }

    public interface PlaylistEvent {
        void onItemClick(Playlist playlist);
    }
}
