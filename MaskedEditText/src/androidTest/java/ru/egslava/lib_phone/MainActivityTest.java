package ru.egslava.lib_phone;


import android.content.pm.ActivityInfo;
import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.Suppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;

import ru.egslava.lib_phone.actions.HintViewAction;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static ru.egslava.lib_phone.actions.KeepHintViewAction.dontKeepHints;
import static ru.egslava.lib_phone.actions.KeepHintViewAction.keepHints;

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
                isDisplayed())
        ).perform(click());

        // just in case, check again that it's the same view after the click
        onView(
            allOf(
                withId(phone_input),
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
                isDisplayed())
        ).perform(click());

        // just in case, check again that it's the same view after the click
        onView(
            allOf(
                withId(phone_input),
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


    /**
     * After merging with Alexander Matveychuk, the demo app started to crash
     * if there's no any text and you rotate the phone
     */
    @Test
    public void saveInstanceStateTest2() {
        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * After setKeepHint(true) a hint should appeared.
     * After setKeepHint(false) a hint should disappear.
     */
    @Test
    public void setKeepHintTest() {
        onView(withId(phone_input))
                .perform(new HintViewAction("9997055671"))
                .perform(dontKeepHints)     // just to be sure

                // the first check. After setKeepHint(true) the hint should appear immediate
                .perform(ViewActions.typeText("999"))
                .perform(keepHints)
                .check(matches(withText("+7(999)705-56-71")))

                // the second check. After setKeepHint(false) the hint should disappear immediate
                .perform(dontKeepHints)
                .check(matches(withText("+7(999)")));
    }


    /**
     * If the text is empty changing of keepHint can lead to a crash.
     * It's the regression test
     */
    @Test
    public void setEmptyTextTest() {

        // given
        onView(withId(phone_input))
                .perform(new HintViewAction("9997055671"))

        // tests
                .perform(dontKeepHints)
                .check(matches(withText("+7(999)705-56-71")))
                .perform(keepHints)
                .check(matches(withText("+7(999)705-56-71")))
                .perform(dontKeepHints)

                // YES! Because the text is empty, user need to see a hint
                .check(matches(withText("+7(999)705-56-71")));
    }
    /**
     * It should keep state of keepHint after activity recreation :-/
     * It's the regression test
     */
    @Test
    @Suppress   // TODO
    public void keepHintAfterRotationTest() throws InterruptedException {

        // ======================================
        // if initial state was keepHint(false)

        // given
        onView(withId(phone_input))
                .perform(new HintViewAction("9997055671"))
                .perform(keepHints)
                .perform(typeText("999"))
                .check(matches(withText("+7(999)705-56-71")))
                .perform(dontKeepHints);

        // rotating screen
        final TestActivity a1 = mActivityTestRule.getActivity();
        a1.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Thread.sleep(2500);
        final TestActivity a2 = mActivityTestRule.getActivity();
        a2.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Thread.sleep(2500);


        if (a1 != a2) {
            throw new InvalidParameterException("a1 != a2");
        }
        // tests
        onView(withId(phone_input))
                .check(matches(withText("+7(999)")));

        // ======================================
        // and if initial state was keepHint(true)

        onView(withId(phone_input))
                .perform(clearText())   // after previous test
                .perform(new HintViewAction("1234567890"))
                .perform(keepHints)
                .perform(typeText("999"))
                .check(matches(withText("+7(999)456-78-90")))
                ;

        // rotating screen
        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Thread.sleep(5000);
        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Thread.sleep(2500);


        // tests
        onView(withId(phone_input))
                .check(matches(withText("+7(999)456-78-90")));
    }
}
