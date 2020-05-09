package com.osinniy.school.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.osinniy.school.R;
import com.osinniy.school.firebase.Factory;
import com.osinniy.school.obj.Bindable;
import com.osinniy.school.obj.dz.DZ;
import com.osinniy.school.obj.imp.Important;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.obj.timetable.Timetable;
import com.osinniy.school.utils.Util;
import com.osinniy.school.utils.listeners.OnItemClickListener;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;

@Keep
public class DashboardAdapter extends ListAdapter<Bindable, RecyclerView.ViewHolder> {

    private static final DiffUtil.ItemCallback<Bindable> COMPARATOR = new DiffUtil.ItemCallback<Bindable>() {
        public boolean areItemsTheSame(@NonNull Bindable oldItem, @NonNull Bindable newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
        public boolean areContentsTheSame(@NonNull Bindable oldItem, @NonNull Bindable newItem) {
            return oldItem.equals(newItem);
        }
    };

    private final LayoutInflater layoutInflater;
    private final OnItemClickListener itemClickListener;
    private WeakReference<Activity> a;

    private List<Bindable> selectedItems;
    private boolean editMode;


    DashboardAdapter(Activity a, OnItemClickListener itemClickListener) {
        super(COMPARATOR);
        this.itemClickListener = itemClickListener;
        this.a = new WeakReference<>(a);

        layoutInflater = LayoutInflater.from(a);
        selectedItems = new ArrayList<>();
    }


    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }


    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(
                R.layout.fragment_dashboard_empty_item,
                parent, false
        );
        RecyclerView.ViewHolder vh = new RecyclerView.ViewHolder(itemView) {};

        switch (viewType) {
            case Bindable.VIEW_TYPE_IMPORTANT: {
                itemView = layoutInflater.inflate(
                        R.layout.fragment_dashboard_item_important,
                        parent, false);
                vh = new ImportantViewHolder(itemView);
            } break;

            case Bindable.VIEW_TYPE_DZ: {
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
            case Bindable.VIEW_TYPE_IMPORTANT: ((ImportantViewHolder) holder)
                    .bind((Important) getItem(position), position); break;
            case Bindable.VIEW_TYPE_DZ: ((DZViewHolder) holder)
                    .bind((DZ) getItem(position), position); break;
        }
    }


//    private void onItemSelected(Bindable item) {
//        if (!editMode) return;
//        if (selectedItems.size() == 1) showContextActions(true);
//        else showContextActions(false);
//    }
//
//
//    private void onItemSelectionRemoved(Bindable item) {
//        if (selectedItems.size() == 0) removeContextActions();
//        else if (selectedItems.size() == 1) showContextActions(true);
//        else showContextActions(false);
//    }
//
//
//    private void showContextActions(boolean singleMode) {
//        if (a.get() == null) return;
//
//        Toolbar appBar = a.get().findViewById(R.id.dash_toolbar);
//        View appBarView = layoutInflater.inflate(
//                R.layout.fragment_dashboard_context_app_bar, appBar
//        );
//        appBar.removeView(appBar.findViewById(R.id.dash_toolbar_container));
//        appBar.addView(appBarView);
//        if (!singleMode) {
//            ImageButton editButt = appBar.findViewById(R.id.dash_context_butt_edit);
//            editButt.setVisibility(GONE);
//            editButt.setEnabled(false);
//        }
//    }
//
//
//    private void removeContextActions() {
//        if (a.get() == null) return;
//
//        Toolbar appBar = a.get().findViewById(R.id.dash_toolbar);
//        View appBarView = layoutInflater.inflate(
//                R.layout.fragment_dashboard_toolbar, appBar
//        );
//        appBar.removeView(appBar.findViewById(R.id.dash_toolbar_container));
////        appBar.addView(appBarView);
//    }


    private void showDialog(Bindable item, int position) {
        if (a.get() == null) return;

        new MaterialAlertDialogBuilder(a.get())
                .setTitle(R.string.dialog_title_delete_item)
                .setMessage(R.string.dialog_msg_delete_item)
                .setPositiveButton(R.string.dialog_butt_delete, (dialog, which) -> {
                    switch (item.getType()) {
                        case Bindable.VIEW_TYPE_DZ: {
                            Factory.getInstance().getFirestoreDao().deleteDZ((DZ) item)
                                    .addOnSuccessListener(aVoid -> {
                                        Snackbar.make(
                                                a.get().findViewById(R.id.dash_coordinator),
                                                R.string.snackbar_deleted,
                                                Snackbar.LENGTH_SHORT
                                        ).show();

                                        List<Bindable> newList = new ArrayList<>(getCurrentList());
                                        newList.remove(item);
                                        submitList(newList);
                                        notifyItemRemoved(position);
                                    })
                                    .addOnFailureListener(e -> {
                                        Util.showToast(a.get(), R.string.toast_sth_went_wrong_restart_app);
                                    });
                            break;
                        }
                        case Bindable.VIEW_TYPE_IMPORTANT: {
                            Factory.getInstance().getFirestoreDao().deleteImportant((Important) item)
                                    .addOnSuccessListener(aVoid -> {
                                        Snackbar.make(
                                                a.get().findViewById(R.id.dash_coordinator),
                                                R.string.snackbar_deleted,
                                                Snackbar.LENGTH_SHORT
                                        ).show();

                                        List<Bindable> newList = new ArrayList<>(getCurrentList());
                                        newList.remove(item);
                                        submitList(newList);
                                        notifyItemRemoved(position);
                                    })
                                    .addOnFailureListener(e -> {
                                        Util.showToast(a.get(), R.string.toast_sth_went_wrong_restart_app);
                                    });
                            break;
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_butt_cancel, (dialog, which) -> dialog.cancel())
                .create()
                .show();
    }


    class ImportantViewHolder extends RecyclerView.ViewHolder {
        private Important binded;

        private MaterialCardView card;
        private TextView title;
        private TextView text;
        private TextView time;

        private ImportantViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_item_important);
            title = itemView.findViewById(R.id.item_important_title);
            text = itemView.findViewById(R.id.item_important_text);
            time = itemView.findViewById(R.id.item_important_time);

            itemView.setOnClickListener(v -> itemClickListener.onItemImportantClicked(binded));
        }

        void bind(Important imp, int position) {
            Context reference = a.get();
            if (a == null) return;

            binded = imp;

            title.setText(imp.getName());

            if (imp.getText().length() > 0)
                text.setText(imp.getText());
            else
                text.setVisibility(GONE);

            SimpleDateFormat formatter = new SimpleDateFormat("  dd.MM HH:mm", Locale.getDefault());
            if (imp.isChanged())
                time.setText(reference.getString(R.string.text_edited).concat(formatter.format(imp.getEditDate())));
            else
                time.setText(formatter.format(imp.getCreationDate()));

            if (UserOptions.getCurrent().isAdmin()) {
                card.setClickable(true);
                card.setFocusable(true);
                card.setCheckable(true);
            }

//            card.setOnLongClickListener(v -> {
//                card.toggle();
//                if (card.isChecked()) {
//                    editMode = true;
//                    selectedItems.add(binded);
////                    DashboardAdapter.this.onItemSelected(binded);
//                    card.setOnClickListener(view -> {
//                        editMode = false;
//                        card.setChecked(false);
////                        DashboardAdapter.this.onItemSelectionRemoved(binded);
//                        card.setOnClickListener(view2 -> itemClickListener.onItemImportantClicked(binded));
//                    });
//                } else {
//                    editMode = false;
////                    DashboardAdapter.this.onItemSelectionRemoved(binded);
//                    card.setOnClickListener(view2 -> itemClickListener.onItemImportantClicked(binded));
//                }
//                return true;
//            });

            if (!UserOptions.getCurrent().isAdmin()) return;

            card.setOnLongClickListener(v -> {
                showDialog(binded, position);
                return true;
            });
        }

    }


    class DZViewHolder extends RecyclerView.ViewHolder {
        private DZ binded;

        private MaterialCardView card;
        private TextView title;
        private TextView time;
        private List<TextView> subjectList = new ArrayList<>(8);
        private List<TextView> homeworkList = new ArrayList<>(8);

        private DZViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_item_dz);
            title = itemView.findViewById(R.id.item_dz_title);
            time = itemView.findViewById(R.id.item_dz_time);
            subjectList.add(itemView.findViewById(R.id.item_dz_subj_1));
            subjectList.add(itemView.findViewById(R.id.item_dz_subj_2));
            subjectList.add(itemView.findViewById(R.id.item_dz_subj_3));
            subjectList.add(itemView.findViewById(R.id.item_dz_subj_4));
            subjectList.add(itemView.findViewById(R.id.item_dz_subj_5));
            subjectList.add(itemView.findViewById(R.id.item_dz_subj_6));
            subjectList.add(itemView.findViewById(R.id.item_dz_subj_7));
            subjectList.add(itemView.findViewById(R.id.item_dz_subj_8));
            homeworkList.add(itemView.findViewById(R.id.item_dz_homework_1));
            homeworkList.add(itemView.findViewById(R.id.item_dz_homework_2));
            homeworkList.add(itemView.findViewById(R.id.item_dz_homework_3));
            homeworkList.add(itemView.findViewById(R.id.item_dz_homework_4));
            homeworkList.add(itemView.findViewById(R.id.item_dz_homework_5));
            homeworkList.add(itemView.findViewById(R.id.item_dz_homework_6));
            homeworkList.add(itemView.findViewById(R.id.item_dz_homework_7));
            homeworkList.add(itemView.findViewById(R.id.item_dz_homework_8));

            itemView.setOnClickListener(v -> itemClickListener.onItemDZClicked(binded));
        }

        void bind(DZ dz, int position) {
            Context reference = a.get();
            if (a == null) return;

            binded = dz;

            SimpleDateFormat formatter = new SimpleDateFormat("  dd.MM HH:mm", Locale.getDefault());
            if (dz.isChanged())
                time.setText(reference.getString(R.string.text_edited).concat(formatter.format(dz.getEditDate())));
            else
                time.setText(formatter.format(dz.getCreationDate()));

            if (UserOptions.getCurrent().isAdmin()) {
                card.setClickable(true);
                card.setFocusable(true);
                card.setCheckable(true);
            }

            setData(dz);

//            card.setOnLongClickListener(v -> {
//                card.toggle();
//                if (card.isChecked()) {
//                    editMode = true;
//                    selectedItems.add(binded);
////                    DashboardAdapter.this.onItemSelected(binded);
//                    card.setOnClickListener(view -> {
//                        editMode = false;
//                        card.setChecked(false);
//                        selectedItems.remove(binded);
////                        DashboardAdapter.this.onItemSelectionRemoved(binded);
//                        card.setOnClickListener(view2 -> itemClickListener.onItemDZClicked(binded));
//                    });
//                } else {
//                    editMode = false;
////                    DashboardAdapter.this.onItemSelectionRemoved(binded);
//                    card.setOnClickListener(view2 -> itemClickListener.onItemDZClicked(binded));
//                }
//                return true;
//            });

            if (!UserOptions.getCurrent().isAdmin()) return;

            card.setOnLongClickListener(v -> {
                showDialog(binded, position);
                return true;
            });
        }

        private void setData(DZ dz) {
//            fill text views
            for (int i = 0; i < dz.getHomework().size(); i++) {
                subjectList.get(i).setText(
                        Timetable.getFromDate(dz.getTargetDate())
                                .get(dz.getHomework().indexOfKey(i))
                );
                homeworkList.get(i).setText(dz.getHomework().get(i));
            }
//            set all visible
            for (int i = 0; i < 8; i++) {
                subjectList.get(i).setVisibility(View.VISIBLE);
                homeworkList.get(i).setVisibility(View.VISIBLE);
            }
//            change text views visibility
            for (int i = 0; i < 8; i++) {
                TextView subjView = subjectList.get(i);
                TextView homeworkView = homeworkList.get(i);
                if (homeworkView.length() == 0) {
                    subjView.setVisibility(GONE);
                    homeworkView.setVisibility(GONE);
                }
            }
        }
    }

}
