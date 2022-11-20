package com.example.assignment_pro1121_nhom3.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;
import com.example.assignment_pro1121_nhom3.utils.PlaylistDiffUtil;

import java.util.ArrayList;
import java.util.List;

public class UserFavoritesListAdapter extends RecyclerView.Adapter<UserFavoritesListAdapter.FavoriteslistViewHolder> {

    private List<Playlist> list;
    private Context context;
    public ItemFavoriteslistEvent favoriteslistEvent;

    public UserFavoritesListAdapter(List<Playlist> list, Context context, ItemFavoriteslistEvent favoriteslistEvent) {
        this.list = list;
        this.context = context;
        this.favoriteslistEvent = favoriteslistEvent;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Playlist> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteslistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_favorites_list_item, parent, false);
        return new FavoriteslistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteslistViewHolder holder, int position) {
        Playlist playlistTemp = list.get(position);
        if (playlistTemp == null) return;
        holder.playlistName.setText(CapitalizeWord.CapitalizeWords(playlistTemp.getCreatorName()));
        holder.numbersong.setText(CapitalizeWord.CapitalizeWords(playlistTemp.getNumberSong()));
        Glide.with(context).load(list.get(position).getUrlThumbnail()).into(holder.musicThumbnail);
        holder.itemplaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteslistEvent.onItemclick(playlistTemp);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }


    public static class FavoriteslistViewHolder extends RecyclerView.ViewHolder {
        ImageView musicThumbnail;
        TextView playlistName, numbersong;
        LinearLayout itemplaylist, btnMoreOption;

        public FavoriteslistViewHolder(@NonNull View itemView) {
            super(itemView);
//            btnMoreOption = itemView.findViewById(R.id.btnMoreOptionUser);
            itemplaylist = itemView.findViewById(R.id.itemPlaylistUser);
            playlistName = itemView.findViewById(R.id.playlistName);
            numbersong = itemView.findViewById(R.id.numbersong);
            musicThumbnail = itemView.findViewById(R.id.userThumbnail);

        }
    }
    public interface ItemFavoriteslistEvent{
        void onItemclick(Playlist playlist);
    }
}
