package com.example.ivan.champy_v2.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.ivan.champy_v2.adapter.MainActivityCardPagerAdapter;
import com.example.ivan.champy_v2.helper.CHWindowView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
/**
 * This class is responsible for cards animation [MainActivity]
 * Their size, alpha, movement, position and other operation
 */
public class CustomPagerBase {

    private static final int NEXT_PAGE = 1;
    private static final int PREVIOUS_PAGE = 2;
    private static final String TAG = "Carousel";
    private boolean isTouchEnabled = true;
    private int currentPosition = 0;
    private int nextItemXPosition;
    private int previousItemXPosition;
    private View currentItem, nextItem, previousItem, removedItem;
    private View[] viewList;
    private Activity context;
    private MainActivityCardPagerAdapter pagerAdapter;
    private RelativeLayout rootView;
    private LayoutInflater inflater;

    private static CustomPagerBase customPagerBase;

    public static CustomPagerBase getInstance() {
        return customPagerBase;
    }

    public CustomPagerBase(Activity activity, RelativeLayout rootView, MainActivityCardPagerAdapter pagerAdapter) {
        this.context      = activity;
        this.rootView     = rootView;
        this.pagerAdapter = pagerAdapter;
        viewList          = new View[pagerAdapter.dataCount()];

        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        customPagerBase = this;
    }

    /**
     * This method for displaying cards on main screen in different situation.
     * @param position its our card position: left, center or right. We need to include
     *                 variable with one, two and three+ cards. Need to set touchListener for them;
     */
    public void preparePager(int position) {
        int width = CHWindowView.getWindowWidth(context);
        nextItemXPosition     = CHWindowView.getCurrentCardPositionX(context) + Math.round(width/1.65f); // was 8, need 1.5f
        previousItemXPosition = CHWindowView.getCurrentCardPositionX(context) - Math.round(width/1.65f); // but I wanna 2'

        if (pagerAdapter != null && pagerAdapter.dataCount() > 0) {
            /**
             * we have more than 0 cards and we need to do something
             */
            if (position != 0 && pagerAdapter.dataCount() > 1) {
                // Create previous view
                previousItem = createCardLayout(position - 1);
                setTouchListenerToView(previousItem, false);
                ViewHelper.setScaleX(previousItem, 0.8f);
                ViewHelper.setScaleY(previousItem, 0.8f);
                ObjectAnimator.ofFloat(previousItem, "translationX", 0, previousItemXPosition).setDuration(1).start();
            }
            if (pagerAdapter.dataCount() - 2 >= position) {
                // Create next view
                nextItem = createCardLayout(position + 1);
                setTouchListenerToView(nextItem, false);
                ViewHelper.setScaleX(nextItem, 0.8f);
                ViewHelper.setScaleY(nextItem, 0.8f);
                ObjectAnimator.ofFloat(nextItem, "translationX", 0, nextItemXPosition).setDuration(1).start();
            }
            if (pagerAdapter.dataCount() > 1) {
                // Create the view for the selected position
                currentItem = createCardLayout(position);
                setTouchListenerToView(currentItem, true);
                ObjectAnimator.ofFloat(currentItem, "translationX", 0, CHWindowView.getCurrentCardPositionX(context)).setDuration(1).start();
            }
            if (pagerAdapter.dataCount() == 1) {
                // Create the view for one page and freeze it.
                currentItem = createCardLayout(position);
                setTouchListenerToView(currentItem, false);
                ObjectAnimator.ofFloat(currentItem, "translationX", 0, CHWindowView.getCurrentCardPositionX(context)).setDuration(1).start();
            }
        }
        currentPosition = position;
    }


    // TODO: 24.10.2016 Disable click on side cards

    private View.OnTouchListener touchListener(final View itemView) {
        return new View.OnTouchListener() {
            float lastX;
            float firstTouchX;


            public boolean onTouch(View v, MotionEvent event) {

                if (isTouchEnabled) {
                    int width = Math.round(CHWindowView.getWindowWidth(context) / 100); // = 10
                    final int X = (int) event.getRawX();
                    float viewXPosition = ViewHelper.getX(itemView);

                    //Log.d(TAG, "X: " + X + " | viewXPosition: " + viewXPosition);

                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        /**
                         * first touch the screen
                         */
                        case MotionEvent.ACTION_DOWN:
                            lastX = X;
                            firstTouchX = X;
                            break;

//                        case MotionEvent.ACTION_POINTER_DOWN:
//                            Log.d(TAG, "onTouch: ACTION_POINTER_DOWN");
//                            break;
                        /**
                         * Responsible for side cards when we touch central card
                         */
                        case MotionEvent.ACTION_MOVE:
                            ViewHelper.setX(itemView, viewXPosition + (X - lastX));
                            lastX = X;
                            ViewHelper.setScaleY(itemView, getScaleValue(viewXPosition));
                            ViewHelper.setScaleX(itemView, getScaleValue(viewXPosition));

                            // animation of RIGHT cards when we touch central card (from left to right)
                            if (firstTouchX - lastX > 1 && nextItem != null) {
                                ViewHelper.setTranslationX(nextItem, itemView.getX() + itemView.getWidth());
                                ViewHelper.setScaleX(nextItem, 0.8f + (1f - ViewHelper.getScaleX(itemView)));
                                ViewHelper.setScaleY(nextItem, 0.8f + (1f - ViewHelper.getScaleY(itemView)));
                                // if our previousItem != null then slide her too
                                if (previousItem != null) {
                                    ViewHelper.setTranslationX(previousItem, itemView.getX() - itemView.getWidth());
                                }
                            }

                            // animation of LEFT cards when we touch central card (from right to left)
                            if (lastX - firstTouchX > 1 && previousItem != null) {
                                ViewHelper.setTranslationX(previousItem, itemView.getX() - itemView.getWidth());
                                ViewHelper.setScaleX(previousItem, 0.8f + (1f - ViewHelper.getScaleX(itemView)));
                                ViewHelper.setScaleY(previousItem, 0.8f + (1f - ViewHelper.getScaleY(itemView)));
                                // if our nextItem != null then slide her too
                                if (nextItem != null) {
                                    ViewHelper.setTranslationX(nextItem, itemView.getX() + itemView.getWidth());
                                }
                            }

                            isTouchEnabled = true;
                            break;

                        /**
                         *  Responsible for sliding cards and their movement when your finger UP
                         */
                        case MotionEvent.ACTION_UP:
                            isTouchEnabled = false;

                            // translation cards from RIGHT to LEFT
                            if (lastX - firstTouchX > 100 && previousItem != null) {
                                // if we have previousItem then we 'prepare' her.
                                changePageTo(PREVIOUS_PAGE);
                            // translation cards from LEFT to RIGHT
                            } else if (firstTouchX - lastX > 100 && nextItem != null) {
                                // if we have nextItem then we 'prepare' her.
                                changePageTo(NEXT_PAGE);
                            }

                            // if when we pick-up our finger from the screen and X <> 100 then return card to default;
                            else {
                                moveCentralItemToDefault();
                                // if we have right and left cards then return they (central position)
                                if (previousItem != null && nextItem != null) {
                                    movePreviousItemToDefault(previousItem);
                                    moveNextItemToDefault(nextItem);
                                }
                                // return right card if @NotNull
                                if (nextItem != null) {
                                    moveNextItemToDefault(nextItem);
                                }
                                // return lift card if @NotNull
                                if (previousItem != null) {
                                    movePreviousItemToDefault(previousItem);
                                }
                            }

                            isTouchEnabled = true;
                            break;

                        default:
                            Log.d(TAG, "onTouch: Default: ...");
                            event.setAction(MotionEvent.ACTION_CANCEL);

                            moveCentralItemToDefault();

                            // for central position when we have both sides
                            if (previousItem != null) {
                                movePreviousItemToDefault(previousItem);
                            }
                            if (nextItem != null) {
                                moveNextItemToDefault(nextItem);
                            }
                            if (nextItem != null && previousItem != null) {
                                movePreviousItemToDefault(previousItem);
                                moveNextItemToDefault(nextItem);
                            }
                            isTouchEnabled = true;
                    }
                }
                return true;
            }
        };
    }


    private void changePageTo(int direction) {
        // Prepare Next card and change it to central
        if (direction == NEXT_PAGE) {
            setTouchListenerToView(currentItem, false);
            // if this is not last item then we place it above other (talking about layouts)
            if (nextItem != null) {
                nextItem.bringToFront();
                rootView.invalidate(); // ??
            }
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    // We do central card less and move it to new position (previousItem)
                    ObjectAnimator.ofFloat(currentItem, "translationX", ViewHelper.getX(currentItem), previousItemXPosition),
                    ObjectAnimator.ofFloat(currentItem, "scaleX", ViewHelper.getScaleX(currentItem), 0.8f),
                    ObjectAnimator.ofFloat(currentItem, "scaleY", ViewHelper.getScaleY(currentItem), 0.8f),

                    // We do next card larger and move it to new position (centralItem)
                    ObjectAnimator.ofFloat(nextItem, "translationX", ViewHelper.getX(nextItem), CHWindowView.getCurrentCardPositionX(context)),
                    ObjectAnimator.ofFloat(nextItem, "scaleX", ViewHelper.getScaleX(nextItem), 1f),
                    ObjectAnimator.ofFloat(nextItem, "scaleY", ViewHelper.getScaleY(nextItem), 1f)
            );
            set.setDuration(120); // was 90, but i like 270
            set.addListener(new AnimatorListener() {
                @Override
                public void onAnimationStart(Animator arg0) {
                    isTouchEnabled = false;
                }

                @Override
                public void onAnimationRepeat(Animator arg0) { }

                @Override
                public void onAnimationEnd(Animator arg0) {

                    removedItem = previousItem;
                    previousItem = currentItem;
                    currentItem = nextItem;
                    if (currentPosition + 2 >= pagerAdapter.dataCount()) {
                        nextItem = null;
                    } else {
                        View nextNext = createCardLayout(currentPosition + 2);
                        ViewHelper.setX(nextNext, nextItemXPosition);
                        ViewHelper.setScaleX(nextNext, 0.8f);
                        ViewHelper.setScaleY(nextNext, 0.8f);
                        // little shit witch make lag
//                        ObjectAnimator anim = ObjectAnimator.ofFloat(nextNext, "translationX", ViewHelper.getX(nextNext), ViewHelper.getX(nextNext));
//                        anim.setDuration(270);
//                        anim.start();
                        currentItem.bringToFront();
                        rootView.invalidate(); // ??
                        nextItem = nextNext;
                    }

                    if (removedItem != null) {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rootView.removeView(removedItem);
                            }
                        });
                    }

                    currentPosition++;
                }

                @Override
                public void onAnimationCancel(Animator arg0) { }
            });
            set.start();
            setTouchListenerToView(nextItem, true);
            nextItem.bringToFront();
            isTouchEnabled = true;
            rootView.invalidate(); // ??
        }

        // Prepare previous card and change it for central
        else if (direction == PREVIOUS_PAGE) {

            setTouchListenerToView(currentItem, false);

            if (previousItem != null) {
                previousItem.bringToFront();
                rootView.invalidate(); // ??
            }

            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    // We do central card less and move it to new position (nextItem)
                    ObjectAnimator.ofFloat(currentItem, "translationX", ViewHelper.getX(currentItem), nextItemXPosition),
                    ObjectAnimator.ofFloat(currentItem, "scaleX", ViewHelper.getScaleX(currentItem), 0.8f),
                    ObjectAnimator.ofFloat(currentItem, "scaleY", ViewHelper.getScaleY(currentItem), 0.8f),

                    // We do previous card larger and move it to new position (centralItem)
                    ObjectAnimator.ofFloat(previousItem, "translationX", ViewHelper.getX(previousItem), CHWindowView.getCurrentCardPositionX(context)),
                    ObjectAnimator.ofFloat(previousItem, "scaleX", ViewHelper.getScaleX(previousItem), 1f),
                    ObjectAnimator.ofFloat(previousItem, "scaleY", ViewHelper.getScaleY(previousItem), 1f)
            );
            set.setDuration(120); // was 90, but 180 is good, but[2] i like 270
            set.addListener(new AnimatorListener() {
                @Override
                public void onAnimationStart(Animator arg0) {
                    isTouchEnabled = false;
                }

                @Override
                public void onAnimationRepeat(Animator arg0) { }

                @Override
                public void onAnimationEnd(Animator arg0) {
                    removedItem = nextItem;
                    nextItem = currentItem;
                    currentItem = previousItem;

                    if (currentPosition - 1 <= 0) {
                        previousItem = null;
                    } else {
                        View prevPrev = createCardLayout(currentPosition - 2);
                        ViewHelper.setX(prevPrev, previousItemXPosition);
                        ViewHelper.setScaleX(prevPrev, 0.8f);
                        ViewHelper.setScaleY(prevPrev, 0.8f);
                        // little shit witch make lag
//                        ObjectAnimator anim = ObjectAnimator.ofFloat(prevPrev, "translationX", ViewHelper.getX(prevPrev), ViewHelper.getX(prevPrev));
//                        anim.setDuration(270); // was 90, but 180 is good
//                        anim.start();
                        currentItem.bringToFront();
                        rootView.invalidate();
                        previousItem = prevPrev;
                    }

                    if (removedItem != null) {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rootView.removeView(removedItem);
                            }
                        });
                    }

                    currentPosition--;

                }

                @Override
                public void onAnimationCancel(Animator arg0) { }
            });
            set.start();
            setTouchListenerToView(previousItem, true);
            previousItem.bringToFront();
            isTouchEnabled = true;
            rootView.invalidate();
        }
    }


    private float getScaleValue(float currentPoint) {
        float value = (float) (((CHWindowView.getCurrentCardPositionX(context) - currentPoint) * 0.5) / CHWindowView.getCurrentCardPositionX(context));
        if (1 - value * value < 0.8f)
            return 0.8f;
        return 1 - value * value;
    }


    private View createCardLayout(int position) {
        View itemView;
        try {
            View convertView = viewList[position];
            if(convertView == null) {
                itemView = pagerAdapter.getView(position, null);
            } else {
                itemView = pagerAdapter.getView(position, viewList[position]);
            }
        } catch(NullPointerException e) {
            itemView = pagerAdapter.getView(position, null);
        }
        viewList[position] = itemView;
        rootView.addView(itemView);
        return itemView;
    }


    private void setTouchListenerToView(final View itemView, boolean state) {
        View.OnTouchListener myTouch = (state) ? touchListener(itemView) : null;
        itemView.setOnTouchListener(myTouch);
    }


    private void moveCentralItemToDefault() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(currentItem, "translationX", ViewHelper.getX(currentItem), CHWindowView.getCurrentCardPositionX(context)),
                ObjectAnimator.ofFloat(currentItem, "scaleX",  ViewHelper.getScaleX(currentItem), 1f),
                ObjectAnimator.ofFloat(currentItem, "scaleY", ViewHelper.getScaleY(currentItem), 1f)
        );
        set.setDuration(270); // was 90, but 180 is good
        set.start();
    }


    private void movePreviousItemToDefault(View previousItem) {
//        ViewHelper.setTranslationX(previousItem, currentItem.getX() - currentItem.getWidth());
//        ViewHelper.setScaleX(previousItem, 0.8f + (1f - ViewHelper.getScaleX(currentItem)));
//        ViewHelper.setScaleY(previousItem, 0.8f + (1f - ViewHelper.getScaleY(currentItem)));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(previousItem, "translationX", ViewHelper.getX(previousItem), previousItemXPosition),
                ObjectAnimator.ofFloat(previousItem, "scaleX", ViewHelper.getScaleX(previousItem), 0.8f),
                ObjectAnimator.ofFloat(previousItem, "scaleY", ViewHelper.getScaleY(previousItem), 0.8f)
        );
        set.setDuration(270); // was 90, but 180 is good
        set.start();
    }


    private void moveNextItemToDefault(View nextItem) {
//        ViewHelper.setTranslationX(nextItem, currentItem.getX() - currentItem.getWidth());
//        ViewHelper.setScaleX(nextItem, 0.8f + (1f - ViewHelper.getScaleX(currentItem)));
//        ViewHelper.setScaleY(nextItem, 0.8f + (1f - ViewHelper.getScaleY(currentItem)));
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(nextItem, "translationX", ViewHelper.getX(nextItem), nextItemXPosition),
                ObjectAnimator.ofFloat(nextItem, "scaleX", ViewHelper.getScaleX(nextItem), 0.8f),
                ObjectAnimator.ofFloat(nextItem, "scaleY", ViewHelper.getScaleY(nextItem), 0.8f)
        );
        set.setDuration(270); // was 90, but 180 is good
        set.start();
    }



//    private void setIsTouchEnabled(boolean isEnabled) {
//        isTouchEnabled = isEnabled;
//    }
//
//    public void performNextPage() {
//        if (nextItem != null)
//            changePageTo(NEXT_PAGE);
//    }
//
//    public void performPreviousPage() {
//        if (previousItem != null)
//            changePageTo(PREVIOUS_PAGE);
//    }
//
//    public View getCurrentItem() {
//        return currentItem;
//    }
//
//    public void setCurrentItem(View currentItem) {
//        this.currentItem = currentItem;
//    }
//
//    public View getNextItem() {
//        return nextItem;
//    }
//
//    public void setNextItem(View nextItem) {
//        this.nextItem = nextItem;
//    }
//
//    public View getPreviousItem() {
//        return previousItem;
//    }
//
//    public void setPreviousItem(View previousItem) {
//        this.previousItem = previousItem;
//    }
//
//    public int getCurrentPosition() {
//        return currentPosition;
//    }
//
//    public void setCurrentPosition(int currentPosition) {
//        this.currentPosition = currentPosition;
//    }
//
//    public void cleanPager() {
//        rootView.removeAllViews();
//        rootView.clearAnimation();
//        currentItem = null;
//        nextItem = null;
//        previousItem = null;
//        removedItem = null;
//        currentPosition = 0;
//        viewList = null;
//    }

}