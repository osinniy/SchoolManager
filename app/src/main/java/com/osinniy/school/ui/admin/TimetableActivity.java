package com.osinniy.school.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.ArrayMap;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.perf.metrics.Trace;
import com.osinniy.school.R;
import com.osinniy.school.firebase.Docs;
import com.osinniy.school.firebase.Factory;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.obj.timetable.Timetable;
import com.osinniy.school.ui.dashboard.DashboardFragment;
import com.osinniy.school.utils.Status;
import com.osinniy.school.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

public class TimetableActivity extends AppCompatActivity {

    // TODO: 06.05.2020 fix etot pizdez blyat 2

    public static final String TIMETABLE_ADDED_EXTRA = "com.osinniy.school.TIMETABLE_ADDED";

    private Toolbar appBar;

    private List<List<ImageButton>> buttons;
    private List<List<TextInputLayout>> edits;

    private int[] limits = new int[5];

    private boolean timetableAdded;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timetable);

        appBar = findViewById(R.id.timetable_toolbar);

        appBar.setNavigationOnClickListener(v -> finish());
        appBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_butt_ok) {
                onDoneButtonClick();
                return true;
            }
            return false;
        });

        MenuItem doneButt = appBar.getMenu().findItem(R.id.menu_butt_ok);
        doneButt.setEnabled(true);
        doneButt.setIcon(R.drawable.ic_done_enabled_28dp);

        limits[0] = Timetable.getMonday().size();
        limits[1] = Timetable.getTuesday().size();
        limits[2] = Timetable.getWednesday().size();
        limits[3] = Timetable.getThursday().size();
        limits[4] = Timetable.getFriday().size();

        Factory.getInstance().getGroupDao().getMetadata((data, e) -> {
            if (data != null)
                if (data.contains(Docs.TIMETABLE_ADDED))
                    timetableAdded = data.getBoolean(Docs.TIMETABLE_ADDED);
        });

        fillLists();
        setAllInvisible();

        for (List<ImageButton> buttons : buttons)
            buttons.get(0).setVisibility(View.VISIBLE);

        for (List<TextInputLayout> edits : edits)
            edits.get(0).setVisibility(View.VISIBLE);

        for (int i = 0; i < 5; i++) prepareAddButtonClick(i, 1);

        setData();
    }


    @Override
    protected void onStart() {
        super.onStart();
        ((Trace) getIntent().getParcelableExtra(DashboardFragment.TIMETABLE_TRACE_EXTRA)).stop();
    }


    private void onDoneButtonClick() {
        if (Status.checkInternet(this)) return;

        Map<String, ArrayList<String>> allData = new ArrayMap<>(5);
        ArrayList<String> mondayData = new ArrayList<>(8);
        ArrayList<String> tuesdayData = new ArrayList<>(8);
        ArrayList<String> wednesdayData = new ArrayList<>(8);
        ArrayList<String> thursdayData = new ArrayList<>(8);
        ArrayList<String> fridayData = new ArrayList<>(8);

        for (int i = 0; i < 5; i++) {
            ArrayList<String> data;
            switch (i) {
                case 0: data = mondayData; break;
                case 1: data = tuesdayData; break;
                case 2: data = wednesdayData; break;
                case 3: data = thursdayData; break;
                default: data = fridayData;
            }
            for (int j = 0; j < 8; j++) {
                String text = edits.get(i).get(j).getEditText().getText().toString();
                if (text.length() > 0) data.add(text);
            }
        }

        allData.put(Timetable.MONDAY, mondayData);
        allData.put(Timetable.TUESDAY, tuesdayData);
        allData.put(Timetable.WEDNESDAY, wednesdayData);
        allData.put(Timetable.THURSDAY, thursdayData);
        allData.put(Timetable.FRIDAY, fridayData);

        Timetable.setAll(allData);
        Timetable.push()
                .addOnSuccessListener(aVoid -> {
                    if (!timetableAdded) {
                        FirebaseFirestore.getInstance()
                                .collection(UserOptions.getCurrent().getGroupId())
                                .document(Docs.DOC_METADATA)
                                .update(Docs.TIMETABLE_ADDED, true);
                    }
                    setResult(RESULT_OK, new Intent().putExtra(TIMETABLE_ADDED_EXTRA, timetableAdded));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Util.showToast(this, R.string.toast_sth_went_wrong_restart_app);
                    Util.logException("Timetable pushing was failed", e);
                    setResult(RESULT_FIRST_USER);
                });
    }


    private void fillLists() {
        buttons = new ArrayList<>(5);
        List<ImageButton> mondayButtons = new ArrayList<>(8);
        List<ImageButton> tuesdayButtons = new ArrayList<>(8);
        List<ImageButton> wednesdayButtons = new ArrayList<>(8);
        List<ImageButton> thursdayButtons = new ArrayList<>(8);
        List<ImageButton> fridayButtons = new ArrayList<>(8);

        mondayButtons.add(findViewById(R.id.timetable_add_subj_butt_monday_1));
        mondayButtons.add(findViewById(R.id.timetable_add_subj_butt_monday_2));
        mondayButtons.add(findViewById(R.id.timetable_add_subj_butt_monday_3));
        mondayButtons.add(findViewById(R.id.timetable_add_subj_butt_monday_4));
        mondayButtons.add(findViewById(R.id.timetable_add_subj_butt_monday_5));
        mondayButtons.add(findViewById(R.id.timetable_add_subj_butt_monday_6));
        mondayButtons.add(findViewById(R.id.timetable_add_subj_butt_monday_7));
        mondayButtons.add(findViewById(R.id.timetable_add_subj_butt_monday_8));
        tuesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_tuesday_1));
        tuesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_tuesday_2));
        tuesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_tuesday_3));
        tuesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_tuesday_4));
        tuesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_tuesday_5));
        tuesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_tuesday_6));
        tuesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_tuesday_7));
        tuesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_tuesday_8));
        wednesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_wednesday_1));
        wednesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_wednesday_2));
        wednesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_wednesday_3));
        wednesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_wednesday_4));
        wednesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_wednesday_5));
        wednesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_wednesday_6));
        wednesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_wednesday_7));
        wednesdayButtons.add(findViewById(R.id.timetable_add_subj_butt_wednesday_8));
        thursdayButtons.add(findViewById(R.id.timetable_add_subj_butt_thursday_1));
        thursdayButtons.add(findViewById(R.id.timetable_add_subj_butt_thursday_2));
        thursdayButtons.add(findViewById(R.id.timetable_add_subj_butt_thursday_3));
        thursdayButtons.add(findViewById(R.id.timetable_add_subj_butt_thursday_4));
        thursdayButtons.add(findViewById(R.id.timetable_add_subj_butt_thursday_5));
        thursdayButtons.add(findViewById(R.id.timetable_add_subj_butt_thursday_6));
        thursdayButtons.add(findViewById(R.id.timetable_add_subj_butt_thursday_7));
        thursdayButtons.add(findViewById(R.id.timetable_add_subj_butt_thursday_8));
        fridayButtons.add(findViewById(R.id.timetable_add_subj_butt_friday_1));
        fridayButtons.add(findViewById(R.id.timetable_add_subj_butt_friday_2));
        fridayButtons.add(findViewById(R.id.timetable_add_subj_butt_friday_3));
        fridayButtons.add(findViewById(R.id.timetable_add_subj_butt_friday_4));
        fridayButtons.add(findViewById(R.id.timetable_add_subj_butt_friday_5));
        fridayButtons.add(findViewById(R.id.timetable_add_subj_butt_friday_6));
        fridayButtons.add(findViewById(R.id.timetable_add_subj_butt_friday_7));
        fridayButtons.add(findViewById(R.id.timetable_add_subj_butt_friday_8));

        buttons.add(mondayButtons);
        buttons.add(tuesdayButtons);
        buttons.add(wednesdayButtons);
        buttons.add(thursdayButtons);
        buttons.add(fridayButtons);

        edits = new ArrayList<>(5);
        List<TextInputLayout> mondayEdits = new ArrayList<>(8);
        List<TextInputLayout> tuesdayEdits = new ArrayList<>(8);
        List<TextInputLayout> wednesdayEdits = new ArrayList<>(8);
        List<TextInputLayout> thursdayEdits = new ArrayList<>(8);
        List<TextInputLayout> fridayEdits = new ArrayList<>(8);

        mondayEdits.add(findViewById(R.id.timetable_text_input_monday_1));
        mondayEdits.add(findViewById(R.id.timetable_text_input_monday_2));
        mondayEdits.add(findViewById(R.id.timetable_text_input_monday_3));
        mondayEdits.add(findViewById(R.id.timetable_text_input_monday_4));
        mondayEdits.add(findViewById(R.id.timetable_text_input_monday_5));
        mondayEdits.add(findViewById(R.id.timetable_text_input_monday_6));
        mondayEdits.add(findViewById(R.id.timetable_text_input_monday_7));
        mondayEdits.add(findViewById(R.id.timetable_text_input_monday_8));
        tuesdayEdits.add(findViewById(R.id.timetable_text_input_tuesday_1));
        tuesdayEdits.add(findViewById(R.id.timetable_text_input_tuesday_2));
        tuesdayEdits.add(findViewById(R.id.timetable_text_input_tuesday_3));
        tuesdayEdits.add(findViewById(R.id.timetable_text_input_tuesday_4));
        tuesdayEdits.add(findViewById(R.id.timetable_text_input_tuesday_5));
        tuesdayEdits.add(findViewById(R.id.timetable_text_input_tuesday_6));
        tuesdayEdits.add(findViewById(R.id.timetable_text_input_tuesday_7));
        tuesdayEdits.add(findViewById(R.id.timetable_text_input_tuesday_8));
        wednesdayEdits.add(findViewById(R.id.timetable_text_input_wednesday_1));
        wednesdayEdits.add(findViewById(R.id.timetable_text_input_wednesday_2));
        wednesdayEdits.add(findViewById(R.id.timetable_text_input_wednesday_3));
        wednesdayEdits.add(findViewById(R.id.timetable_text_input_wednesday_4));
        wednesdayEdits.add(findViewById(R.id.timetable_text_input_wednesday_5));
        wednesdayEdits.add(findViewById(R.id.timetable_text_input_wednesday_6));
        wednesdayEdits.add(findViewById(R.id.timetable_text_input_wednesday_7));
        wednesdayEdits.add(findViewById(R.id.timetable_text_input_wednesday_8));
        thursdayEdits.add(findViewById(R.id.timetable_text_input_thursday_1));
        thursdayEdits.add(findViewById(R.id.timetable_text_input_thursday_2));
        thursdayEdits.add(findViewById(R.id.timetable_text_input_thursday_3));
        thursdayEdits.add(findViewById(R.id.timetable_text_input_thursday_4));
        thursdayEdits.add(findViewById(R.id.timetable_text_input_thursday_5));
        thursdayEdits.add(findViewById(R.id.timetable_text_input_thursday_6));
        thursdayEdits.add(findViewById(R.id.timetable_text_input_thursday_7));
        thursdayEdits.add(findViewById(R.id.timetable_text_input_thursday_8));
        fridayEdits.add(findViewById(R.id.timetable_text_input_friday_1));
        fridayEdits.add(findViewById(R.id.timetable_text_input_friday_2));
        fridayEdits.add(findViewById(R.id.timetable_text_input_friday_3));
        fridayEdits.add(findViewById(R.id.timetable_text_input_friday_4));
        fridayEdits.add(findViewById(R.id.timetable_text_input_friday_5));
        fridayEdits.add(findViewById(R.id.timetable_text_input_friday_6));
        fridayEdits.add(findViewById(R.id.timetable_text_input_friday_7));
        fridayEdits.add(findViewById(R.id.timetable_text_input_friday_8));

        edits.add(mondayEdits);
        edits.add(tuesdayEdits);
        edits.add(wednesdayEdits);
        edits.add(thursdayEdits);
        edits.add(fridayEdits);
    }


    private void setAllInvisible() {
        List<TextInputLayout> mondayEdits = edits.get(0);
        List<TextInputLayout> tuesdayEdits = edits.get(1);
        List<TextInputLayout> wednesdayEdits = edits.get(2);
        List<TextInputLayout> thursdayEdits = edits.get(3);
        List<TextInputLayout> fridayEdits = edits.get(4);

        for (TextInputLayout layout : mondayEdits) layout.setVisibility(GONE);
        for (TextInputLayout layout : tuesdayEdits) layout.setVisibility(GONE);
        for (TextInputLayout layout : wednesdayEdits) layout.setVisibility(GONE);
        for (TextInputLayout layout : thursdayEdits) layout.setVisibility(GONE);
        for (TextInputLayout layout : fridayEdits) layout.setVisibility(GONE);
    }


//    TODO: fix bug
    private void prepareAddButtonClick(int day, int num) {
        if (num == 8) return;
        if (limits[day] > 1) {
//        set icon respectively limits
            buttons.get(day).get(num - 1).setImageResource(R.drawable.ic_add_circle);
            // TODO: 07.05.2020 set on click listeners when timetable has subjects
        }
//        onClickListener on previous button
        buttons.get(day).get(num - 1).setOnClickListener(v -> {
//            set current edit and button visible
            edits.get(day).get(num).setVisibility(View.VISIBLE);
            buttons.get(day).get(num).setVisibility(View.VISIBLE);
//            set cursor to current edit
            edits.get(day).get(num).requestFocus();
//            change previous icon to 'minus' icon
            buttons.get(day).get(num - 1).setImageResource(R.drawable.ic_minus);
//            change current icon
            if (num != 7) buttons.get(day).get(num).setImageResource(R.drawable.ic_add_circle);
            else buttons.get(day).get(num).setVisibility(GONE);
//            set new onClickListener after button clicked
            buttons.get(day).get(num - 1).setOnClickListener(v1 -> {
//                hide previous edit and button
                buttons.get(day).get(num - 1).setVisibility(GONE);
                edits.get(day).get(num - 1).setVisibility(GONE);
//                animate
                TransitionManager.beginDelayedTransition(findViewById(R.id.timetable_container));
            });
//            recursive call with next index
            prepareAddButtonClick(day, num + 1);
//            animate
            TransitionManager.beginDelayedTransition(findViewById(R.id.timetable_container));
        });
    }


    private void setData() {
        ArrayList<String> monday = Timetable.getMonday();
        ArrayList<String> tuesday = Timetable.getTuesday();
        ArrayList<String> wednesday = Timetable.getWednesday();
        ArrayList<String> thursday = Timetable.getThursday();
        ArrayList<String> friday = Timetable.getFriday();

        for (int i = 0; i < monday.size(); i++)
            updateData(0, i);

        for (int i = 0; i < tuesday.size(); i++)
            updateData(1, i);

        for (int i = 0; i < wednesday.size(); i++)
            updateData(2, i);

        for (int i = 0; i < thursday.size(); i++)
            updateData(3, i);

        for (int i = 0; i < friday.size(); i++)
            updateData(4, i);
    }


    private void updateData(int day, int number) {
        ArrayList<String> data;
        switch (day) {
            case 0: data = Timetable.getMonday(); break;
            case 1: data = Timetable.getTuesday(); break;
            case 2: data = Timetable.getWednesday(); break;
            case 3: data = Timetable.getThursday(); break;
            default: data = Timetable.getFriday();
        }
        EditText edit = edits.get(day).get(number).getEditText();
        edit.setText(data.get(number));
        edits.get(day).get(number).setVisibility(View.VISIBLE);
        buttons.get(day).get(number).setVisibility(View.VISIBLE);
        if (number == data.size() - 1) {
            buttons.get(day).get(0).setImageResource(R.drawable.ic_minus);
            buttons.get(day).get(data.size() - 1).setImageResource(R.drawable.ic_add_circle);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        buttons = null;
        edits = null;
        setResult(RESULT_CANCELED);
    }

}
