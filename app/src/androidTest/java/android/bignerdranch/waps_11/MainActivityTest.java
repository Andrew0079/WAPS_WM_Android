package android.bignerdranch.waps_11;

import android.app.Activity;
import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTest = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mMainActivityTest.getActivity();
    }

    @Test
    public void tesLaunch(){
        View mView = mActivity.findViewById(R.id.mainFrameFragment);
        assertNotNull(mView);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}