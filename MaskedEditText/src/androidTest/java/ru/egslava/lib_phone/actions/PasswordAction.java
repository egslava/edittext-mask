package ru.egslava.lib_phone.actions;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public enum PasswordAction implements ViewAction {

    passwordModeOn(true), passwordModeOff(false);

    private final boolean passwordMode;

    PasswordAction(boolean passwordMode) {
        this.passwordMode = passwordMode;
    }

    @Override
    public Matcher<View> getConstraints() {
        return Matchers.instanceOf(MaskedEditText.class);
    }

    @Override
    public String getDescription() {
        return "Set Password on View";
    }

    @Override
    public void perform(UiController uiController, View view) {
        ((MaskedEditText)view).setPasswordMode(passwordMode);
    }
}