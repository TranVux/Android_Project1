package com.example.assignment_pro1121_nhom3.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.assignment_pro1121_nhom3.models.Music;

import java.util.ArrayList;
import java.util.Objects;

public class MusicDiffUtil extends DiffUtil.Callback {

    private ArrayList<Music> oldList;
    private ArrayList<Music> newList;

    public MusicDiffUtil(ArrayList<Music> oldList, ArrayList<Music> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        if (oldList != null) return oldList.size();
        return 0;
    }

    @Override
    public int getNewListSize() {
        if (newList != null) return newList.size();
        return 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return Objects.equals(oldList.get(oldItemPosition).getId(), newList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return Objects.equals(oldList.get(oldItemPosition).getId(), newList.get(newItemPosition).getId()) &&
                Objects.equals(oldList.get(oldItemPosition).getGenresId(), newList.get(newItemPosition).getGenresId()) &&
                Objects.equals(oldList.get(oldItemPosition).getName(), newList.get(newItemPosition).getName()) &&
                Objects.equals(oldList.get(oldItemPosition).getSingerId(), newList.get(newItemPosition).getSingerId()) &&
                Objects.equals(oldList.get(oldItemPosition).getSingerName(), newList.get(newItemPosition).getSingerName()) &&
                Objects.equals(oldList.get(oldItemPosition).getThumbnailUrl(), newList.get(newItemPosition).getThumbnailUrl()) &&
                Objects.equals(oldList.get(oldItemPosition).getUrl(), newList.get(newItemPosition).getUrl()) &&
                Objects.equals(oldList.get(oldItemPosition).getUpdateDate(), newList.get(newItemPosition).getUpdateDate()) &&
                Objects.equals(oldList.get(oldItemPosition).getViews(), newList.get(newItemPosition).getViews());
    }
}
