package android.bignerdranch.waps_11;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class AppActivityTest {

    @Rule
    public ActivityTestRule<AppActivity> mActivityTestRule = new ActivityTestRule<AppActivity>(AppActivity.class);

    private AppActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void tesLaunch(){
        View mView = mActivity.findViewById(R.id.drawer_layout);
        assertNotNull(mView);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}