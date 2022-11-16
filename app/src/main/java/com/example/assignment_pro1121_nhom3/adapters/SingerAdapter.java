package com.example.assignment_pro1121_nhom3.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Singer;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SingerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Singer> listSinger = new ArrayList<>();
    private Context context;
    private ItemEvent.SingerItemEvent singerItemEvent;
    public int ITEM_TYPE = 1;
    public int LOADING_TYPE = 2;
    boolean isLoadingAdd;

    public SingerAdapter(Context context, ItemEvent.SingerItemEvent singerItemEvent) {
        this.context = context;
        this.singerItemEvent = singerItemEvent;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListSinger(ArrayList<Singer> listSinger) {
        int initRange = this.listSinger.size();
        this.listSinger.addAll(listSinger);
        notifyItemRangeChanged(initRange, listSinger.size());
    }


    @Override
    public int getItemViewType(int position) {
        if (listSinger != null && position == listSinger.size() - 1 && isLoadingAdd) {
            return LOADING_TYPE;
        }
        return ITEM_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_singer, parent, false);
            return new SingerViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == ITEM_TYPE) {
            SingerViewHolder viewHolder = (SingerViewHolder) holder;
            Singer tempSinger = listSinger.get(position);
            if (tempSinger == null) return;
            viewHolder.singerName.setText(CapitalizeWord.CapitalizeWords(tempSinger.getName()));
            Glide.with(context)
                    .load(tempSinger.getAvtUrl()).apply(new RequestOptions().override(60, 60))
                    .centerCrop()
                    .error(R.drawable.fallback_img)
                    .into(viewHolder.singerImage);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    singerItemEvent.onItemClick(tempSinger);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (listSinger != null) return listSinger.size();
        return 0;
    }

    public static class SingerViewHolder extends RecyclerView.ViewHolder {
        LinearLayout singerItem;
        TextView singerName;
        CircleImageView singerImage;

        public SingerViewHolder(@NonNull View itemView) {
            super(itemView);

            singerImage = itemView.findViewById(R.id.singerImg);
            singerName = itemView.findViewById(R.id.singerName);
            singerItem = itemView.findViewById(R.id.itemSinger);
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addFooterLoading() {
        isLoadingAdd = true;
        listSinger.add(new Singer("", "", "", ""));
        notifyDataSetChanged();
    }

    public void removeFooterLoading() {
        isLoadingAdd = false;
        int positionLastItem = listSinger.size() - 1;
        Singer singer = listSinger.get(positionLastItem);
        if (singer != null) {
            listSinger.remove(positionLastItem);
            notifyItemRemoved(positionLastItem);
        }
    }
}
