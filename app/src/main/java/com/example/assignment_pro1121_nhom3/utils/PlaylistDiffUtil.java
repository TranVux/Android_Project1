package com.example.assignment_pro1121_nhom3.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.Playlist;

import java.util.ArrayList;
import java.util.Objects;

public class PlaylistDiffUtil extends DiffUtil.Callback {

    private ArrayList<Playlist> oldList;
    private ArrayList<Playlist> newList;

    public PlaylistDiffUtil(ArrayList<Playlist> oldList, ArrayList<Playlist> newList) {
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
                Objects.equals(oldList.get(oldItemPosition).getName(), newList.get(newItemPosition).getName()) &&
                Objects.equals(oldList.get(oldItemPosition).getModifyDate(), newList.get(newItemPosition).getModifyDate()) &&
                Objects.equals(oldList.get(oldItemPosition).getMusics(), newList.get(newItemPosition).getMusics());
    }
}
