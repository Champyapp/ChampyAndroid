//package com.azinecllc.champy.fragment;
//
//import android.graphics.Color;
//import android.os.Build;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.azinecllc.champy.BuildConfig;
//import com.azinecllc.champy.R;
//import com.azinecllc.champy.activity.PendingDuelActivity;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricGradleTestRunner;
//import org.robolectric.annotation.Config;
//
//import static com.facebook.FacebookSdk.getApplicationContext;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
///**
// * Created by SashaKhyzhun on 3/1/17.
// */
//@RunWith(RobolectricGradleTestRunner.class)
//@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
//public class ChallengePendingFragmentTest {
//
//    private View view;
//    private ChallengePendingFragment fragment;
//    private PendingDuelActivity activity;
//
//    @Before
//    public void setUp() throws Exception {
//        activity = Robolectric.buildActivity(PendingDuelActivity.class).create().get();
//        fragment = new ChallengePendingFragment();
//        view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_card_pending_duel, null);
//    }
//
//    @Test
//    public void testForActivity() throws Exception {
//        assertNotNull(activity);
//        assertEquals("PendingDuelActivity", activity.getClass().getSimpleName());
//    }
//
//    @Test
//    public void testForFragment() throws Exception {
//        assertNotNull(fragment);
//        assertEquals("ChallengePendingFragment", fragment.getClass().getSimpleName());
//    }
//
//    @Test
//    public void testForTextViewYouVsSomebody() throws Exception {
//        TextView textView = (TextView) view.findViewById(R.id.tvYouVsSomebody);
//        assertNotNull(textView);
//
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
//        assertEquals(8, lp.leftMargin);
//        assertEquals(8, lp.rightMargin);
//        assertEquals(Color.parseColor("#808080"), textView.getCurrentTextColor());
//        assertEquals(Gravity.CENTER, textView.getGravity());
//        assertEquals(24, textView.getTextSize(), 0);
//        assertTrue(View.VISIBLE == textView.getVisibility());
//
//        assertTrue(R.id.tvYouVsSomebody == textView.getId());
//    }
//
//    @Test
//    public void testForTextViewGoal() throws Exception {
//        TextView textView = (TextView) view.findViewById(R.id.tv_goal);
//        assertNotNull(textView);
//
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
//        assertEquals(5, lp.bottomMargin);
//        assertEquals(Color.parseColor("#339997"), textView.getCurrentTextColor());
//        assertTrue(View.VISIBLE == textView.getVisibility());
//
//        assertTrue(R.id.tv_goal == textView.getId());
//    }
//
//    @Test
//    public void testForTextViewEveryDayForTheNext() throws Exception {
//        TextView textView = (TextView) view.findViewById(R.id.tvEveryDayPending);
//        assertNotNull(textView);
//
//        assertEquals("everyday for the next", textView.getText());
//        assertEquals(Gravity.CENTER, textView.getGravity());
//
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
//        assertEquals(0, lp.topMargin);
//        assertEquals(0, lp.bottomMargin);
//        assertEquals(0, lp.rightMargin);
//        assertEquals(0, lp.leftMargin);
//        assertTrue(R.id.tvEveryDayPending == textView.getId());
//    }
//
//    @Test
//    public void testForTextViewDays() throws Exception {
//        TextView textView = (TextView) view.findViewById(R.id.textViewDuring);
//        assertNotNull(textView);
//        assertEquals("Days", textView.getText());
//        assertEquals(Color.parseColor("#339997"), textView.getCurrentTextColor());
//        assertTrue(Gravity.CENTER == textView.getGravity());
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
//        assertEquals(5, lp.topMargin);
//
//        assertTrue(R.id.textViewDuring == textView.getId());
//    }
//
//    @Test
//    public void testForImageButtonCancelInvisible() throws Exception {
//        ImageButton imageButton = (ImageButton) view.findViewById(R.id.btn_cancelc);
//        assertNotNull(imageButton);
//
//        assertTrue(View.INVISIBLE == imageButton.getVisibility());
//        assertTrue(imageButton.isClickable());
//
//        assertTrue(R.id.btn_cancelc == imageButton.getId());
//
//        assertEquals(35, imageButton.getLayoutParams().height);
//        assertEquals(35, imageButton.getLayoutParams().width);
//    }
//
//    @Test
//    public void testForImageButtonCancelVisible() throws Exception {
//        ImageButton imageButton = (ImageButton) view.findViewById(R.id.btn_cancel);
//        assertNotNull(imageButton);
//
//        assertTrue(View.VISIBLE == imageButton.getVisibility());
//        assertTrue(imageButton.isClickable());
//
//        assertTrue(R.id.btn_cancel == imageButton.getId());
//
//        assertEquals(35, imageButton.getLayoutParams().height);
//        assertEquals(35, imageButton.getLayoutParams().width);
//    }
//
//    @Test
//    public void testForImageButtonAccept() throws Exception {
//        ImageButton imageButton = (ImageButton) view.findViewById(R.id.btn_accept);
//        assertNotNull(imageButton);
//
//        assertTrue(View.VISIBLE == imageButton.getVisibility());
//        assertTrue(imageButton.isClickable());
//
//        assertTrue(R.id.btn_accept == imageButton.getId());
//
//        assertEquals(35, imageButton.getLayoutParams().height);
//        assertEquals(35, imageButton.getLayoutParams().width);
//    }
//
//    @After
//    public void testAfter() throws Exception {
//
//    }
//
//}