package com.osinniy.school.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.osinniy.school.R;
import com.osinniy.school.firebase.Factory;
import com.osinniy.school.obj.dz.DZ;
import com.osinniy.school.obj.timetable.Timetable;
import com.osinniy.school.utils.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;

public class NewDZActivity extends AppCompatActivity {

    public static final String DZ_EXTRA = "com.osinniy.school.DZ_EXTRA";

    private Toolbar appBar;

    private List<TextInputLayout> edits;

    private MaterialDatePicker<Long> datePicker;

    private long selectedDate;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_dz);

        appBar = findViewById(R.id.new_dz_toolbar);

        appBar.setNavigationOnClickListener(v -> finish());
        appBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_butt_ok) {
                onDoneButtonClick();
                return true;
            }
            return false;
        });

        edits = new ArrayList<>(8);
        edits.add(findViewById(R.id.new_dz_text_input_1));
        edits.add(findViewById(R.id.new_dz_text_input_2));
        edits.add(findViewById(R.id.new_dz_text_input_3));
        edits.add(findViewById(R.id.new_dz_text_input_4));
        edits.add(findViewById(R.id.new_dz_text_input_5));
        edits.add(findViewById(R.id.new_dz_text_input_6));
        edits.add(findViewById(R.id.new_dz_text_input_7));
        edits.add(findViewById(R.id.new_dz_text_input_8));

        createDatePicker();

        TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                MenuItem doneButt = appBar.getMenu().findItem(R.id.menu_butt_ok);
                doneButt.setEnabled(
                    (edits.get(0).getEditText().length() <= 70) &&
                    (edits.get(1).getEditText().length() <= 70) &&
                    (edits.get(2).getEditText().length() <= 70) &&
                    (edits.get(3).getEditText().length() <= 70) &&
                    (edits.get(4).getEditText().length() <= 70) &&
                    (edits.get(5).getEditText().length() <= 70) &&
                    (edits.get(6).getEditText().length() <= 70) &&
                    (edits.get(7).getEditText().length() <= 70)
                );
                doneButt.setIcon(
                        doneButt.isEnabled() ?
                        R.drawable.ic_done_enabled_28dp :
                        R.drawable.ic_done_disabled_28dp
                );
            }
        };
        for (TextInputLayout layout : edits)
            layout.getEditText().addTextChangedListener(watcher);

        Timetable.refresh();
    }


    private void createDatePicker() {
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.clear();
        utc.setTimeInMillis(MaterialDatePicker.todayInUtcMilliseconds());
        utc.roll(DAY_OF_MONTH, 1);

        CalendarConstraints constraints =
                new CalendarConstraints.Builder().setValidator(
                        new CalendarConstraints.DateValidator() {
                            public boolean isValid(long date) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(date);
                                return !((cal.get(DAY_OF_WEEK) == SATURDAY) || (cal.get(DAY_OF_WEEK) == SUNDAY));
                            }
                            public int describeContents() {
                                return 0;
                            }
                            public void writeToParcel(Parcel dest, int flags) {}
                        }).build();

        datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.dialog_select_date)
                .setSelection(utc.getTimeInMillis())
                .setCalendarConstraints(constraints)
                .build();
        datePicker.addOnPositiveButtonClickListener(this::onDateSelected);
        datePicker.addOnNegativeButtonClickListener(view -> datePicker.dismiss());
    }


    public void onSelectDateButtonClick(View v) {
        datePicker.show(getSupportFragmentManager(), datePicker.toString());
    }


    private void onDateSelected(long selection) {
        MenuItem doneButt = appBar.getMenu().findItem(R.id.menu_butt_ok);
        doneButt.setIcon(R.drawable.ic_done_disabled_28dp);

        ArrayList<String> data = Timetable.getFromDate(new Date(selection));
        if (data == null) {
            Snackbar.make(
                    findViewById(R.id.new_dz_toolbar),
                    R.string.snackbar_invalid_date,
                    Snackbar.LENGTH_SHORT
            ).show();
            return;
        }
        if (data.size() == 0) {
            Snackbar.make(
                    findViewById(R.id.new_dz_toolbar),
                    R.string.snackbar_should_add_timetable,
                    Snackbar.LENGTH_SHORT
            ).show();
            return;
        }

        selectedDate = selection;

        doneButt.setEnabled(true);
        doneButt.setIcon(R.drawable.ic_done_enabled_28dp);

        findViewById(R.id.new_dz_edits_container).setVisibility(View.VISIBLE);
        for (TextInputLayout edit : edits) edit.setVisibility(View.GONE);

        for (int i = 0; i < data.size(); i++) {
            TextInputLayout edit = edits.get(i);
            edit.setVisibility(View.VISIBLE);
            edit.setHint(data.get(i));
        }
        TransitionManager.beginDelayedTransition(findViewById(R.id.new_dz_constraint_container));
    }


    private void onDoneButtonClick() {
        SparseArray<String> homework = new SparseArray<>(8);
        if (Timetable.getFromDate(new Date(selectedDate)) == null) return;
        int size = Timetable.getFromDate(new Date(selectedDate)).size();
        for (int i = 0; i < size; i++) {
            EditText edit = edits.get(i).getEditText();
            homework.put(i, edit.getText().toString());
        }

        DZ dz = new DZ(homework, new Date(selectedDate));
        Intent resultIntent = new Intent().putExtra(DZ_EXTRA, dz);
        Factory.getInstance().getFirestoreDao().addDZ(dz)
                .addOnSuccessListener(aVoid -> {
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Util.showToast(this, R.string.toast_sth_went_wrong_restart_app);
                    setResult(RESULT_FIRST_USER);
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_CANCELED);
    }

}
