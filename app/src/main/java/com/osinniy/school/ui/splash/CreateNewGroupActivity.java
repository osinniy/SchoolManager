package com.osinniy.school.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.osinniy.school.R;
import com.osinniy.school.app.ActivityStack;
import com.osinniy.school.firebase.groups.GroupManager;
import com.osinniy.school.obj.groups.GroupMetadata;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.ui.admin.AdminActivity;
import com.osinniy.school.utils.Status;

public class CreateNewGroupActivity extends AppCompatActivity {

    Toolbar appBar;
    TextInputEditText editGroupName;
    TextInputEditText editGroupDesc;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_new_group);

        ActivityStack.getInstance().add(this);

        appBar = findViewById(R.id.cr_new_gr_toolbar);
        editGroupName = findViewById(R.id.edit_group_name);
        editGroupDesc = findViewById(R.id.edit_group_desc);

        appBar.setNavigationOnClickListener(v -> finish());
        appBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_check) {
                onConfirmButtonClick();
                return true;
            }
            return false;
        });

        editGroupName.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                appBar.getMenu().findItem(R.id.menu_check).setEnabled(
                        (s.length() <= 20 && s.length() > 0) && (editGroupDesc.length() <= 200)
                );
            }
        });
        editGroupDesc.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                appBar.getMenu().findItem(R.id.menu_check).setEnabled(
                        (editGroupName.length() <= 20 && editGroupName.length() > 0) && (s.toString().length() <= 200)
                );
            }
        });
    }


    private void onConfirmButtonClick() {
        if (Status.checkInternet(this, appBar)) return;

        final String name = editGroupName.getText().toString();
        final String desc = editGroupDesc.getText().toString();

        GroupManager.newGroup(GroupMetadata.create(name, desc));

        UserOptions.getCurrent().writeToShared(this);

        startActivity(new Intent(this, AdminActivity.class));
        ActivityStack.getInstance().finishAll();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.getInstance().remove(this);
    }

}
