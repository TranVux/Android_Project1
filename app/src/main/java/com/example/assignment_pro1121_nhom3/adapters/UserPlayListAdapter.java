package com.example.assignment_pro1121_nhom3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.models.Playlist;

import java.util.ArrayList;

public class UserPlayListAdapter extends RecyclerView.Adapter<UserPlayListAdapter.ViewHolder>{
    ArrayList<Playlist> playlists;

    public UserPlayListAdapter(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item_in_fragment_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.playListTitle.setText(playlist.getName());
        Glide.with(holder.playListImage.getContext()).load(playlist.getUrlThumbnail())
                .error(R.drawable.fallback_img)
                .centerCrop()
                .into(holder.playListImage);
        holder.countSong.setText(playlist.getMusics().size() + " bài hát");
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView playListImage;
        TextView playListTitle, countSong;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            countSong = itemView.findViewById(R.id.itemPlaylistCountSong);
            playListImage = itemView.findViewById(R.id.itemPlaylistThumbnail);
            playListTitle = itemView.findViewById(R.id.itemPlaylistTitle);
        }
    }
}
