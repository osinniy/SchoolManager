package com.osinniy.school.ui.user.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.osinniy.school.R;
import com.osinniy.school.obj.Bindable;
import com.osinniy.school.obj.dz.DZ;
import com.osinniy.school.obj.imp.Important;
import com.osinniy.school.utils.listeners.OnItemClickListener;

public class DashboardAdapter extends ListAdapter<Bindable, RecyclerView.ViewHolder> {

    private static final DiffUtil.ItemCallback<Bindable> COMPARATOR = new DiffUtil.ItemCallback<Bindable>() {
        @Override
        public boolean areItemsTheSame(@NonNull Bindable oldItem, @NonNull Bindable newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
        @Override
        public boolean areContentsTheSame(@NonNull Bindable oldItem, @NonNull Bindable newItem) {
            return oldItem.equals(newItem);
        }
    };

    private final LayoutInflater layoutInflater;

    private final OnItemClickListener itemClickListener;


    DashboardAdapter(LayoutInflater layoutInflater, OnItemClickListener itemClickListener) {
        super(COMPARATOR);
        this.layoutInflater = layoutInflater;
        this.itemClickListener = itemClickListener;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        else return 2;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(
                R.layout.dashboard_fragment_empty_item,
                parent, false
        );
        RecyclerView.ViewHolder vh = new RecyclerView.ViewHolder(itemView) {};

        switch (viewType) {
            case 1: {
                itemView = layoutInflater.inflate(
                        R.layout.fragment_dashboard_item_important,
                        parent, false);
                vh = new ImportantViewHolder(itemView);
            } break;

            case 2: {
                itemView = layoutInflater.inflate(
                        R.layout.fragment_dashboard_item_dz,
                        parent, false);
                vh = new DZViewHolder(itemView);
            } break;
        }

        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        switch (this.getItemViewType(position)) {
            case 1: ((ImportantViewHolder) holder).bind((Important) getItem(position)); break;
            case 2: ((DZViewHolder) holder).bind((DZ) getItem(position)); break;
        }
    }


    class ImportantViewHolder extends RecyclerView.ViewHolder {

        private Important binded;

        private ImportantViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> itemClickListener.onItemImportantClicked(binded));
        }

        void bind(Important imp) {
            binded = imp;
        }
    }


    class DZViewHolder extends RecyclerView.ViewHolder {

        private DZ binded;

        private DZViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> itemClickListener.onItemDZClicked(binded));
        }

        void bind(DZ dz) {
            binded = dz;
        }
    }

}
