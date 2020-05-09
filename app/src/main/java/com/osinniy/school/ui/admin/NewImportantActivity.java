package com.osinniy.school.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.osinniy.school.R;
import com.osinniy.school.firebase.Factory;
import com.osinniy.school.obj.imp.Important;
import com.osinniy.school.utils.Status;
import com.osinniy.school.utils.Util;

public class NewImportantActivity extends AppCompatActivity {

    public static final String IMPORTANT_EXTRA = "com.osinniy.school.IMPORTANT_EXTRA";

    Toolbar appBar;
    EditText editName;
    EditText editMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_important);

        appBar = findViewById(R.id.new_imp_toolbar);
        editName = findViewById(R.id.new_imp_edit_text_name);
        editMsg = findViewById(R.id.new_imp_edit_text_message);

        appBar.setNavigationOnClickListener(v -> finish());
        appBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_butt_ok) {
                onDoneButtonClick();
                return true;
            }
            return false;
        });

        MenuItem doneButt = appBar.getMenu().findItem(R.id.menu_butt_ok);

        editName.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                doneButt.setEnabled(
                        (s.length() <= 50 && editMsg.length() <= 300) && s.length() > 0
                );
                if (doneButt.isEnabled())
                    doneButt.setIcon(R.drawable.ic_done_enabled_28dp);
                else
                    doneButt.setIcon(R.drawable.ic_done_disabled_28dp);
            }
        });

        editMsg.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                doneButt.setEnabled(
                        (s.length() <= 300 && editName.length() <= 50) && editName.length() > 0
                );
                if (doneButt.isEnabled())
                    doneButt.setIcon(R.drawable.ic_done_enabled_28dp);
                else
                    doneButt.setIcon(R.drawable.ic_done_disabled_28dp);
            }
        });
    }


    private void onDoneButtonClick() {
        if (Status.checkInternet(this)) return;

        final String name = editName.getText().toString();
        final String msg = editMsg.getText().toString();
        final String uid = FirebaseAuth.getInstance().getUid();

        Important imp = new Important(name, msg, uid);
        Intent resultIntent = new Intent().putExtra(IMPORTANT_EXTRA, imp);
        Factory.getInstance().getFirestoreDao().addImportant(imp)
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
