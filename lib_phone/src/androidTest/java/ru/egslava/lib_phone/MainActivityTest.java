package ru.egslava.lib_phone;


import android.content.pm.ActivityInfo;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    public static final int phone_input = br.com.sapereaude.maskedEditText.test.R.id.vControl;

    @Rule
    public ActivityTestRule<TestActivity> mActivityTestRule = new ActivityTestRule<>(TestActivity.class);

    @Test
    public void textTypingTest() {
        onView(
            allOf(
                withId(phone_input),
                withText("+7(   )   -  -  "),
                isDisplayed())
        ).perform(click());

        // just in case, check again that it's the same view after the click
        onView(
            allOf(
                withId(phone_input),
                withText("+7(   )   -  -  "),
                isDisplayed())
        ).perform(typeText("9997055671"));

        onView(
            allOf(
                withId(phone_input),
                withText("+7(999)705-56-71"),
                isDisplayed())
        ).perform(closeSoftKeyboard());

        onView( allOf(
            withId(phone_input),
            isDisplayed())
        ).check(matches(withText("+7(999)705-56-71")));
    }

    @Test
    public void saveInstanceStateTest() {
        onView(
            allOf(
                withId(phone_input),
                withText("+7(   )   -  -  "),
                isDisplayed())
        ).perform(click());

        // just in case, check again that it's the same view after the click
        onView(
            allOf(
                withId(phone_input),
                withText("+7(   )   -  -  "),
                isDisplayed())
        ).perform(typeText("9997055671"));

        onView(
            allOf(
                withId(phone_input),
                withText("+7(999)705-56-71"),
                isDisplayed())
        ).perform(closeSoftKeyboard());

        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        onView( allOf(
            withId(phone_input),
            isDisplayed())
        ).check(matches(withText("+7(999)705-56-71")));
    }
}
