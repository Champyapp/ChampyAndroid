//package com.azinecllc.champy.utils;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.support.design.widget.AppBarLayout;
//import android.support.design.widget.CoordinatorLayout;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v4.view.ViewCompat;
//import android.util.AttributeSet;
//import android.view.View;
//import android.widget.Toast;
//
//import com.azinecllc.champy.R;
//
///**
// * @autor SashaKhyzhun
// * Created on 4/19/17.
// */
//
//public class FabScrollBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
//
//    private Context context;
//
//     public FabScrollBehavior(Context context) {
//         super();
//         this.context = context;
//    }
//
//    @Override
//    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
//                                       View directTargetChild, View target, int nestedScrollAxes) {
//        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
//    }
//
//    @Override
//    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target,
//                               int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
//
//        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
//
//        System.out.println(
//                  "dxConsumed:     " + dxConsumed
//                + "\ndyConsumed:   " + dyConsumed
//                + "\ndxUnconsumed: " + dxUnconsumed
//                + "\ndyUnconsumed: " + dyUnconsumed
//                + "\ntargetVision: " + target.getVisibility()
//                + "\nchild Vision: " + child.getVisibility());
//
//
//        if(dyConsumed > 0) {
//            child.hide();
//            Toast.makeText(context, "> 0", Toast.LENGTH_SHORT).show();
//        } else if(dyConsumed < 0) {
//            child.show();
//            Toast.makeText(context, "< 0 !!!!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
////    @Override
////    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
////        return dependency instanceof AppBarLayout;
////    }
////
////    @Override
////    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
////        if (dependency instanceof AppBarLayout) {
////            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
////            int fabBottomMargin = lp.bottomMargin;
////            int distanceToScroll = fab.getHeight() + fabBottomMargin;
////            float ratio = dependency.getY() / (float) toolbarHeight;
////            fab.setTranslationY(-distanceToScroll * ratio);
////        }
////        return true;
////    }
////
////    private static int getToolbarHeight(Context context) {
////        final TypedArray styledAttributes =
////                context.getTheme().obtainStyledAttributes(new int[] {
////                        R.attr.actionBarSize
////                });
////        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
////        styledAttributes.recycle();
////
////        return toolbarHeight;
////    }
//
//
//}
