package com.osinniy.dz.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.osinniy.dz.R;
import com.osinniy.dz.obj.DZ;
import com.osinniy.dz.util.OnItemClickListener;

public class DZAdapter extends ListAdapter<DZ, DZAdapter.DZViewHolder> {

    private static final DiffUtil.ItemCallback<DZ> COMPARATOR = new DiffUtil.ItemCallback<DZ>() {
        @Override
        public boolean areItemsTheSame(@NonNull DZ oldItem, @NonNull DZ newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
        @Override
        public boolean areContentsTheSame(@NonNull DZ oldItem, @NonNull DZ newItem) {
            return oldItem.equals(newItem);
        }
    };

    private final LayoutInflater layoutInflater;

    private final OnItemClickListener<DZ> dzClickListener;


    public DZAdapter(LayoutInflater layoutInflater, OnItemClickListener<DZ> dzClickListener) {
        super(COMPARATOR);
        this.layoutInflater = layoutInflater;
        this.dzClickListener = dzClickListener;
    }


    @NonNull
    @Override
    public DZViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DZViewHolder(
                layoutInflater.inflate(R.layout.activity_main_item_dz, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull DZViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    class DZViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvDescription;

        private DZ binded;

        private DZViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.title_1);
            tvDescription = itemView.findViewById(R.id.text_1);
            itemView.setOnClickListener(v -> dzClickListener.onItemClicked(binded));
        }

        private void bind(DZ dz) {
            binded = dz;
            tvTitle.setText("title");
            tvDescription.setText("text");
        }

    }

}
