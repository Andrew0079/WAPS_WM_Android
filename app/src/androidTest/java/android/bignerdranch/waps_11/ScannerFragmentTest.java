package android.bignerdranch.waps_11;




import android.view.View;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.rule.ActivityTestRule;



import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class ScannerFragmentTest {


    @Rule
    public ActivityTestRule<AppActivity> mActivityTest = new ActivityTestRule<AppActivity>(AppActivity.class);

    private AppActivity mAppActivity = null;

    @Before
    public void setUp() throws Exception {
        mAppActivity = mActivityTest.getActivity();
    }

    @Test
    public void testLaunch(){
        DrawerLayout appContainer = (DrawerLayout) mAppActivity.findViewById(R.id.drawer_layout);
        assertNotNull(appContainer);
        ScannerFragment fragment = new ScannerFragment();
        mAppActivity.getSupportFragmentManager().beginTransaction().add(appContainer.getId(),fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = fragment.getActivity().findViewById(R.id.scanWasp);
        assertNotNull(view);

    }

    @After
    public void tearDown() throws Exception {
        mAppActivity = null;
    }
}