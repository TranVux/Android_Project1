package com.example.assignment_pro1121_nhom3.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.models.Genres;

import java.util.ArrayList;

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.GenresViewHolder> {
    private ArrayList<Genres> listGenres;
    private Context context;
    private EventItem eventItem;

    public GenresAdapter(ArrayList<Genres> listGenres, Context context, EventItem eventItem) {
        this.listGenres = listGenres;
        this.context = context;
        this.eventItem = eventItem;
    }

    public ArrayList<Genres> getListGenres() {
        return listGenres;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListGenres(ArrayList<Genres> listGenres) {
        this.listGenres = listGenres;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GenresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.genres_item_layout, parent, false);
        return new GenresViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenresViewHolder holder, int position) {
        Genres tempGenres = listGenres.get(position);
        if (tempGenres == null) return;

        Glide.with(context).load(tempGenres.getUrlThumbnail()).diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.thumbnail);

        int bottom = dbToPx(10);
        int left = dbToPx(5);
        int right = dbToPx(5);

        boolean isRightSide = ((position + 1) % 2 == 0);
        boolean isLeftSide = !isRightSide;

        if (isRightSide) {
            right = 0;
        }
//
        if (isLeftSide) {
            left = 0;
        }

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.itemGenres.getLayoutParams();
        layoutParams.setMargins(left, bottom, right, 0);
        holder.itemGenres.setLayoutParams(layoutParams);

        holder.itemGenres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventItem.onItemClick(tempGenres);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listGenres != null) return listGenres.size();
        return 0;
    }

    private int dbToPx(int db) {
        float px = db * context.getResources().getDisplayMetrics().density;
        return (int) px;
    }

    public static class GenresViewHolder extends RecyclerView.ViewHolder {
        CardView itemGenres;
        ImageView thumbnail;

        public GenresViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.genresThumbnail);
            itemGenres = itemView.findViewById(R.id.itemGenres);
        }
    }

    public interface EventItem {
        void onItemClick(Genres genres);
    }
}
