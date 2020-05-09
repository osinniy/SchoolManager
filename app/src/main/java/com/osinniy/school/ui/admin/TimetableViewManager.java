package com.osinniy.school.ui.admin;

import androidx.annotation.IdRes;
import androidx.constraintlayout.widget.ConstraintSet;

import com.osinniy.school.R;

import static androidx.constraintlayout.widget.ConstraintSet.START;

public class TimetableViewManager {

    private static int cursor = 2;


//    public static View addEditText(LayoutInflater inflater, ConstraintLayout layout) {
//        TextInputLayout inflated =
//                (TextInputLayout) inflater.inflate(R.layout.activity_timetable_edit_text, layout, true);
//        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(0, 0);
//        layout.addView(inflated, -1, params);
//
//        ConstraintSet constraints = new ConstraintSet();
//        constraints.clone(layout);
//        changeEdtTextConstraints(constraints, 0);
//        TransitionManager.beginDelayedTransition(layout);
//        constraints.applyTo(layout);
//
//        return inflated;
//    }


    private static void changeEdtTextConstraints(ConstraintSet set, @IdRes int id) {
        set.connect(id, START, R.id.timetable_left_guideline, START);
    }

}
