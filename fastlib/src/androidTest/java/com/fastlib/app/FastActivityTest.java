package com.fastlib.app;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.fastlib.MainActivity;
import com.fastlib.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by sgfb on 18/7/18.
 */
@RunWith(AndroidJUnit4.class)
public class FastActivityTest{
    @Rule
    public ActivityTestRule<MainActivity> mActivity=new ActivityTestRule<>(MainActivity.class);

    @Test
    public void checkPrepare(){
//        Assert.assertEquals(true,mActivity.getActivity().isAlreadyPrepared);
    }

    @Test
    public void checkViewInject(){
        Espresso.onView(ViewMatchers.withId(R.id.bt)).check(ViewAssertions.doesNotExist());
    }

    @Test
    public void checkEventBroadcast(){
        Espresso.onView(ViewMatchers.withId(R.id.bt)).perform(ViewActions.click());
//        Espresso.onView(ViewMatchers.withId(R.id.text)).check(ViewAssertions.matches(ViewMatchers.withText(mActivity.getActivity().mTestStr)));
    }

    @Test
    public void checkSuperThread(){
        Espresso.onView(ViewMatchers.withId(R.id.bt2)).perform(ViewActions.click());
//        Assert.assertTrue(mActivity.getActivity().mTestValue!=-1);
    }
}
