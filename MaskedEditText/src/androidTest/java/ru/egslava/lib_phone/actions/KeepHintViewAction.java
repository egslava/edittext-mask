package ru.egslava.lib_phone.actions;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public enum KeepHintViewAction implements ViewAction {
    keepHints(true), dontKeepHints(false);


    boolean keepHint;

    KeepHintViewAction(boolean keepHint) {
        this.keepHint = keepHint;
    }

    @Override
    public Matcher<View> getConstraints() {
        return Matchers.instanceOf(MaskedEditText.class);
    }

    @Override
    public String getDescription() {
        return "Set keepHint on/off";
    }

    @Override
    public void perform(UiController uiController, View view) {
        ((MaskedEditText)view).setKeepHint(keepHint);
    }
}