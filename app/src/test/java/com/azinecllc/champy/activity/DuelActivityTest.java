//package com.azinecllc.champy.activity;
//
//import android.app.Activity;
//import android.graphics.Color;
//import android.os.Build;
//import android.support.v4.view.ViewPager;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.azinecllc.champy.BuildConfig;
//import com.azinecllc.champy.R;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricGradleTestRunner;
//import org.robolectric.annotation.Config;
//
//import static java.lang.Math.acos;
//import static java.lang.Math.round;
//import static org.junit.Assert.*;
//
///**
// * Created by SashaKhyzhun on 1/31/17.
// */
//@RunWith(RobolectricGradleTestRunner.class)
//@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
//public class DuelActivityTest {
//
//    private Activity activity;
//    private int x;
//
//    @Before
//    public void setup() throws Exception {
//        activity = Robolectric.buildActivity(DuelActivity.class).create().get();
//        x = round(activity.getWindowManager().getDefaultDisplay().getWidth() / 2);
//    }
//
//    @Test
//    public void testForActivityNotNull() throws Exception {
//        assertNotNull(activity);
//    }
//
//
//    @Test
//    public void testForImageViewMyImage() throws Exception {
//        ImageView myPhoto = (ImageView) activity.findViewById(R.id.imageMyPhoto);
//        assertNotNull(myPhoto);
//
//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) myPhoto.getLayoutParams();
//
//        System.out.println("x: " + x);
//        assertEquals(x, lp.width);
//        assertEquals(x, lp.height);
//        assertEquals(0.5, lp.weight, 0);
//        assertEquals(R.id.imageMyPhoto, myPhoto.getId());
//
//        System.out.println("myPhoto | Expected width:  " + x + ", Actual width: " + lp.width);
//        System.out.println("myPhoto | Expected height: " + x + ", Actual height: " + lp.height);
//        System.out.println("myPhoto | Expected weight: 0.5, Actual weight: " + lp.weight);
//
//    }
//
//    @Test
//    public void testForImageViewFriendImage() throws Exception {
//        ImageView friendsPhoto = (ImageView) activity.findViewById(R.id.imageFriendsPhoto);
//        assertNotNull(friendsPhoto);
//
//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) friendsPhoto.getLayoutParams();
//        assertEquals(x, lp.width);
//        assertEquals(x, lp.height);
//        assertEquals(0.5, lp.weight, 0);
//        assertEquals(R.id.imageFriendsPhoto, friendsPhoto.getId());
//        System.out.println("friendsPhoto | Expected width:  " + x + ", Actual width: " + lp.width);
//        System.out.println("friendsPhoto | Expected height: " + x + ", Actual height: " + lp.height);
//        System.out.println("friendsPhoto | Expected weight: 0.5, Actual weight: " + lp.weight);
//    }
//
//    @Test
//    public void testImageViewBG() throws Exception {
//        ImageView bg = (ImageView) activity.findViewById(R.id.imageView19);
//        assertNotNull(bg);
//
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) bg.getLayoutParams();
//
//        assertEquals(0.8, bg.getAlpha(), 1);
//        System.out.println("bg | Expected alpha: 0.8, Actual alpha: " + bg.getAlpha());
//
//        assertEquals(R.id.imageView19, bg.getId());
//        System.out.println("bg | Expected id: R.id.imageView19, Actual id: " + bg.getId());
//
//        assertEquals(0, lp.topMargin);
//        assertEquals(0, lp.bottomMargin);
//        assertEquals(0, lp.rightMargin);
//        assertEquals(0, lp.leftMargin);
//        System.out.println("bg | Expected topMargin:    0, Actual topMargin:    " + lp.topMargin);
//        System.out.println("bg | Expected bottomMargin: 0, Actual bottomMargin: " + lp.bottomMargin);
//        System.out.println("bg | Expected rightMargin:  0, Actual rightMargin:  " + lp.rightMargin);
//        System.out.println("bg | Expected leftMargin:   0, Actual leftMargin:   " + lp.leftMargin);
//
//
//    }
//
//
//    @Test
//    public void testTextViewIChallengeToMySelf() throws Exception {
//        TextView tvChallengeFriends = (TextView) activity.findViewById(R.id.tvIChallengeMyFriendTo);
//        assertNotNull(tvChallengeFriends);
//
//        assertEquals(22, tvChallengeFriends.getTextSize(), 0);
//        System.out.println("tvChallengeFriends | " +
//                "Expected testSize: 22, " +
//                "Actual textSize: " + tvChallengeFriends.getTextSize());
//        assertEquals(Color.WHITE, tvChallengeFriends.getTextColors().getDefaultColor());
//
//        assertEquals("I challenge my friend to", tvChallengeFriends.getText());
//        System.out.println("tvChallengeFriends | " +
//                "Expected text 'I challenge my friend to', " +
//                "Actual text: '" + tvChallengeFriends.getText() + "'");
//
//
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvChallengeFriends.getLayoutParams();
//        assertEquals(8, lp.topMargin);
//        System.out.println("tvChallengeFriends | Expected topMargin: 8, Actual topMargin: " + lp.topMargin);
//    }
//
//    @Test
//    public void testTextViewYouVsSomebody() throws Exception {
//        TextView tvYouVsFriend = (TextView) activity.findViewById(R.id.tvYouVsFriend);
//        assertNotNull(tvYouVsFriend);
//
//        assertEquals(18, tvYouVsFriend.getTextSize(), 0);
//        System.out.println("tvYouVsFriend | " +
//                "Expected testSize:   18, " +
//                "Actual textSize: " + tvYouVsFriend.getTextSize());
//        assertEquals(Color.WHITE, tvYouVsFriend.getTextColors().getDefaultColor());
//
//        assertNotNull(tvYouVsFriend.getText().toString());
//
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvYouVsFriend.getLayoutParams();
//        assertEquals(8, lp.leftMargin);
//        assertEquals(8, lp.rightMargin);
//        System.out.println("tvYouVsFriend | Expected leftMargin:  8, Actual leftMargin:  " + lp.leftMargin);
//        System.out.println("tvYouVsFriend | Expected rightMargin: 8, Actual rightMargin: " + lp.topMargin);
//    }
//
//
//    @Test
//    public void testProgressBar() throws Exception {
//        View spinner = activity.findViewById(R.id.loadingPanel);
//        assertNotNull(spinner);
//        assertEquals(View.VISIBLE, spinner.getVisibility());
//    }
//
//    @Test
//    public void testForViewPager() throws Exception {
//        ViewPager viewPager = (ViewPager) activity.findViewById(R.id.pager_duel);
//        assertNotNull(viewPager);
//
//        assertEquals(1, viewPager.getOffscreenPageLimit());
//        assertEquals(90, viewPager.getPaddingLeft());
//        assertEquals(90, viewPager.getPaddingRight());
//        assertEquals(0, viewPager.getPaddingTop());
//        assertEquals(0, viewPager.getPaddingBottom());
//
//        System.out.println("viewPager | Expected ScreenLimit:   1, Actual ScreenLimit:  " + viewPager.getOffscreenPageLimit());
//        System.out.println("viewPager | Expected PaddingLeft:  90, Actual PaddingLeft:  " + viewPager.getPaddingLeft());
//        System.out.println("viewPager | Expected PaddingRight: 90, Actual PaddingRight: " + viewPager.getPaddingRight());
//        System.out.println("viewPager | Expected PaddingTop:    0, Actual PaddingTop:   " + viewPager.getPaddingTop());
//        System.out.println("viewPager | Expected PaddingBot:    0, Actual PaddingBot:   " + viewPager.getPaddingBottom());
//    }
//
//
//    public void testForImageButtonAccept() throws Exception {
//        ImageButton imageButton = (ImageButton) activity.findViewById(R.id.ok);
//        assertNotNull(imageButton);
//
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageButton.getLayoutParams();
//        assertEquals(60, lp.width);
//        assertEquals(60, lp.height);
//        System.out.println("imageButton | Expected width:  60, Actual width:  " + lp.width);
//        System.out.println("imageButton | Expected height: 60, Actual height: " + lp.height);
//
//        assertEquals(8, lp.bottomMargin);
//        System.out.println("imageButton | Expected MarginBot: 60, Actual MarginBot: " + lp.bottomMargin);
//
//    }
//
//
//}