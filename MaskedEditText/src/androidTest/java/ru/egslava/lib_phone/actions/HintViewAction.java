package ru.egslava.lib_phone.actions;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

public class HintViewAction implements ViewAction {


    String hint;

    public HintViewAction(String hint) {
        this.hint = hint;
    }

    @Override
    public Matcher<View> getConstraints() {
        return Matchers.instanceOf(TextView.class);
    }

    @Override
    public String getDescription() {
        return "Set hints on view";
    }

    @Override
    public void perform(UiController uiController, View view) {
        ((TextView)view).setHint(hint);
    }
}