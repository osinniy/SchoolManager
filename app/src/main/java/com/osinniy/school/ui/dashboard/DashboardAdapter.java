package com.osinniy.school.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.osinniy.school.R;
import com.osinniy.school.obj.Bindable;
import com.osinniy.school.obj.dz.DZ;
import com.osinniy.school.obj.imp.Important;
import com.osinniy.school.utils.listeners.OnItemClickListener;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DashboardAdapter extends ListAdapter<Bindable, RecyclerView.ViewHolder> {

    private static final DiffUtil.ItemCallback<Bindable> COMPARATOR = new DiffUtil.ItemCallback<Bindable>() {
        public boolean areItemsTheSame(@NonNull Bindable oldItem, @NonNull Bindable newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
        public boolean areContentsTheSame(@NonNull Bindable oldItem, @NonNull Bindable newItem) {
            return oldItem.equals(newItem);
        }
    };

    private static final int VIEW_TYPE_IMPORTANT = 1;
    private static final int VIEW_TYPE_DZ = 2;

    private final LayoutInflater layoutInflater;
    private final OnItemClickListener itemClickListener;
    private WeakReference<Context> c;


    DashboardAdapter(LayoutInflater layoutInflater, OnItemClickListener itemClickListener, Context c) {
        super(COMPARATOR);
        this.layoutInflater = layoutInflater;
        this.itemClickListener = itemClickListener;
        this.c = new WeakReference<>(c);
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_TYPE_IMPORTANT;
        else return VIEW_TYPE_DZ;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(
                R.layout.fragment_dashboard_empty_item,
                parent, false
        );
        RecyclerView.ViewHolder vh = new RecyclerView.ViewHolder(itemView) {};

        switch (viewType) {
            case VIEW_TYPE_IMPORTANT: {
                itemView = layoutInflater.inflate(
                        R.layout.fragment_dashboard_item_important,
                        parent, false);
                vh = new ImportantViewHolder(itemView);
            } break;

            case VIEW_TYPE_DZ: {
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
            case VIEW_TYPE_IMPORTANT: ((ImportantViewHolder) holder).bind((Important) getItem(position)); break;
            case VIEW_TYPE_DZ: ((DZViewHolder) holder).bind((DZ) getItem(position)); break;
        }
    }


    class ImportantViewHolder extends RecyclerView.ViewHolder {
        private Important binded;

        private TextView title;
        private TextView text;
        private TextView time;

        private ImportantViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_important_title);
            text = itemView.findViewById(R.id.item_important_text);
            time = itemView.findViewById(R.id.item_important_time);
            itemView.setOnClickListener(v -> itemClickListener.onItemImportantClicked(binded));
        }

        void bind(Important imp) {
            Context reference = c.get();
            if (c == null) return;

            binded = imp;

            title.setText(imp.getName());
            text.setText(imp.getText());

            SimpleDateFormat formatter = new SimpleDateFormat(" dd.MM HH:mm", Locale.getDefault());
            if (imp.isChanged())
                time.setText(reference.getString(R.string.text_card_edited).concat(formatter.format(imp.getEditDate())));
            else
                time.setText(formatter.format(imp.getCreationDate()));
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
