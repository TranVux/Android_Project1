package com.example.assignment_pro1121_nhom3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class MusicRecentPublishAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Music> list;
    private Context context;
    private ItemEvent.MusicItemEvent itemEvent;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LOADING = 2;
    private boolean isLoadingAdd;

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

    public ArrayList<Music> getList() {
        return list;
    }

    @Override
    public int getItemViewType(int position) {
        if (list != null && position == list.size() - 1 && isLoadingAdd) {
            return TYPE_LOADING;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(context).inflate(R.layout.music_recent_publish_item, parent, false);
            return new MusicRecentPublishViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_ITEM) {
            MusicRecentPublishViewHolder publishViewHolder = (MusicRecentPublishViewHolder) holder;
            Music musicTemp = list.get(position);
            if (musicTemp == null) return;
            Glide.with(context).load(musicTemp.getThumbnailUrl())
                    .apply(new RequestOptions().override(105, 105))
                    .centerCrop()
                    .error(R.drawable.fallback_img).into(publishViewHolder.thumbnail);
            publishViewHolder.musicName.setText(CapitalizeWord.CapitalizeWords(musicTemp.getName()));
            publishViewHolder.singerName.setText(CapitalizeWord.CapitalizeWords(musicTemp.getSingerName()));
            publishViewHolder.itemMusic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemEvent.onItemClick(musicTemp, position);
                }
            });

            publishViewHolder.singerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemEvent.onSingerNameClick(musicTemp.getSingerId());
                }
            });
        } else {

        }
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

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public void addLoading() {
        isLoadingAdd = true;
        list.add(new Music("2OfAAhxDFTf3TqlKu4AM", "Nơi này có anh",
                "https://firebasestorage.googleapis.com/v0/b/project1-group3-52e2e.appspot.com/o/Musics%2FNoi-Nay-Co-Anh-Masew-Bootleg-Son-Tung-M-TP-Masew.mp3?alt=media&token=aba5a5d8-9133-4568-81d1-18c5fbbb5d37",
                "https://photo-resize-zmp3.zmdcdn.me/w600_r1x1_webp/covers/c/b/cb61528885ea3cdcd9bdb9dfbab067b1_1504988884.jpg",
                1667278226, 1667278226, "Sơn tùng M-TP", "1CV6SRGg7uxj0W1bsBu8", 1011, "SSrKhRc2FHzGIyLxjU5w"));
    }

    public void removeLoading() {
        isLoadingAdd = false;
        int position = list.size() - 1;
        Music music = list.get(position);
        if (music != null) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }
}
