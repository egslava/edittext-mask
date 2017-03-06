package ru.egslava.lib_phone.actions;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;

import br.com.sapereaude.maskedEditText.MaskedEditText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

public class SetTextViewAction implements ViewAction {

    private String value;

    public SetTextViewAction(String value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Matcher<View> getConstraints() {
        return allOf(instanceOf(MaskedEditText.class));
    }

    @Override
    public void perform(UiController uiController, View view) {
        MaskedEditText maskedEditText = (MaskedEditText) view;
        maskedEditText.setText(maskedEditText.getRawText());
    }

    @Override
    public String getDescription() {
        return "replace text";
    }
};