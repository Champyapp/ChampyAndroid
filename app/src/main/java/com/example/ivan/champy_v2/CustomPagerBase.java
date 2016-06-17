package com.example.ivan.champy_v2;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.ivan.champy_v2.adapter.CustomPagerAdapter;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by ivan on 09.12.15.
 */
public class CustomPagerBase {

    private static final int NEXT_PAGE = 1;
    private static final int PREVIOUS_PAGE = 2;
    private Activity context;
    private RelativeLayout rootView;
    private LayoutInflater inflater;
    private View currentItem, nextItem, previousItem, removedItem;
    private View[] viewList;
    private int currentPosition = 0;
    private int nextItemXPosition;
    private int previousItemXPosition;
    private boolean isTouchEnabled = true;
    private CustomPagerAdapter pagerAdapter;

    private static CustomPagerBase customPagerBase;

    public static CustomPagerBase getInstance() {
        return customPagerBase;
    }

    public CustomPagerBase(Activity context, RelativeLayout rootView, CustomPagerAdapter pagerAdapter) {
        this.context      = context;
        this.rootView     = rootView;
        this.pagerAdapter = pagerAdapter;
        viewList          = new View[pagerAdapter.dataCount()];

        if (inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        customPagerBase = this;
    }

    public void preparePager(int position) {
        int width = HelperClass.getWindowWidth(context);
        nextItemXPosition     = HelperClass.getCurrentCardPositionX(context) + Math.round(width/8);
        previousItemXPosition = HelperClass.getCurrentCardPositionX(context) - Math.round(width/8);
        if (pagerAdapter != null && pagerAdapter.dataCount() > 0) {
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
            if (pagerAdapter.dataCount() >= 1) {
                // Create the view for the selected position
                currentItem = createCardLayout(position);
                setTouchListenerToView(currentItem, true);
                ObjectAnimator.ofFloat(currentItem, "translationX", 0, HelperClass.getCurrentCardPositionX(context)).setDuration(1).start();
            }
        }
        currentPosition = position;
    }

    private View createCardLayout(int position) {
        View itemView;
        try{
            View convertView = viewList[position];
            if(convertView == null)
                itemView = pagerAdapter.getView(position, null);
            else
                itemView = pagerAdapter.getView(position, viewList[position]);
        }catch(NullPointerException e){
            itemView = pagerAdapter.getView(position, null);
        }
        viewList[position] = itemView;
        rootView.addView(itemView);
        return itemView;
    }

    private void setTouchListenerToView(final View itemView, boolean state) {
        if (state) {
            itemView.setOnTouchListener(touchListener(itemView));
        } else {
            itemView.setOnTouchListener(null);
        }
    }

    public void setIsTouchEnabled(boolean isEnabled)
    {
        isTouchEnabled = isEnabled;
    }

    private View.OnTouchListener touchListener(final View itemView) {
        return new View.OnTouchListener() {
            int lastX;
            int firstTouchX;

            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TAG", "Touch :" + isTouchEnabled);

                if(isTouchEnabled){
                    int width = Math.round(HelperClass.getWindowWidth(context) / 100);
                    Log.d("TAG", "Width: ="+width);

                    final int X = (int) event.getRawX();

                    float viewXPosition = ViewHelper.getX(itemView);

                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            lastX = X;
                            firstTouchX = X;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (X > width*25 && X < width*80) {
                                ViewHelper.setX(itemView, viewXPosition + (X - lastX));
                            }
                            // Прокрутка карточкоч
                            /*if (X > width*50 & X < width*50) {
                                ViewHelper.setX(itemView, viewXPosition + (X - lastX));
                            }*/

                            lastX = X;
                            //   Log.d("TAG", "Move "+ViewHelper.getAlpha(nextItem));
                            Log.d("TAG", "Move " + ViewHelper.getScaleX(itemView));
                            ViewHelper.setScaleY(itemView, getScaleValue(viewXPosition));
                            ViewHelper.setScaleX(itemView, getScaleValue(viewXPosition));

                            // отвечает за прозначность карточок
                            if (firstTouchX - lastX > 10 && nextItem != null)  {
                                if (ViewHelper.getAlpha(nextItem) < 0.93f) {
                                    ViewHelper.setAlpha(itemView, ViewHelper.getScaleX(itemView));
                                }
                                else {
                                    ViewHelper.setAlpha(itemView, 1f);
                                }
                                ViewHelper.setAlpha (nextItem, 0.8f + (1f - ViewHelper.getScaleX(itemView)));
                                ViewHelper.setScaleX(nextItem, 0.8f + (1f - ViewHelper.getScaleX(itemView)));
                                ViewHelper.setScaleY(nextItem, 0.8f + (1f - ViewHelper.getScaleY(itemView)));
                                if (ViewHelper.getAlpha(nextItem) > 0.9f) {
                                    nextItem.bringToFront();
                                }
                                else if (currentItem != null) {
                                    currentItem.bringToFront();
                                    Log.d("TAG", "Bring to Front");
                                }
                            } else if (lastX - firstTouchX > 10 && previousItem != null) {
                                if (ViewHelper.getAlpha(previousItem) < 0.93f) {
                                    ViewHelper.setAlpha(itemView, ViewHelper.getScaleX(itemView));
                                }
                                else {
                                    ViewHelper.setAlpha(itemView, 1f);
                                }
                                ViewHelper.setAlpha (previousItem, 0.8f + (1f - ViewHelper.getScaleX(itemView)));
                                ViewHelper.setScaleX(previousItem, 0.8f + (1f - ViewHelper.getScaleX(itemView)));
                                ViewHelper.setScaleY(previousItem, 0.8f + (1f - ViewHelper.getScaleY(itemView)));
                                if (ViewHelper.getAlpha(previousItem) > 0.9f) {
                                    previousItem.bringToFront();
                                }
                                else if (currentItem != null) {
                                    currentItem.bringToFront();
                                    Log.d("TAG", "Bring to Front");
                                }
                            } else if  (currentItem != null) {
                                Log.d("TAG", "Bring to Front");
                                currentItem.bringToFront();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            isTouchEnabled = false;
                            if (Math.abs(lastX - firstTouchX) < 5) {
                                // Click state
                                // поворот карточок справа вліво
                            } else if (lastX - firstTouchX > 100) {
                                if (previousItem != null)
                                    changePageTo(PREVIOUS_PAGE);
                                else {
                                    AnimatorSet set = new AnimatorSet();
                                    set.playTogether(
                                            ObjectAnimator.ofFloat(itemView, "translationX", viewXPosition, HelperClass.getCurrentCardPositionX(context)),
                                            ObjectAnimator.ofFloat(itemView, "scaleX", ViewHelper.getScaleX(itemView), 1f),
                                            ObjectAnimator.ofFloat(itemView, "scaleY", ViewHelper.getScaleY(itemView), 1f),
                                            ObjectAnimator.ofFloat(itemView, "alpha", ViewHelper.getAlpha(itemView), 1f)
                                    );
                                    set.setDuration(90);
                                    set.start();
                                }
                                // поворот карточок зліва на право
                            } else if (firstTouchX - lastX > 100) {
                                if (nextItem != null) {
                                    Log.d("TAG", "Ready to next:"+(firstTouchX - lastX));
                                    changePageTo(NEXT_PAGE);}
                                else {
                                    AnimatorSet set = new AnimatorSet();
                                    set.playTogether(
                                            ObjectAnimator.ofFloat(itemView, "translationX", viewXPosition, HelperClass.getCurrentCardPositionX(context)),
                                            ObjectAnimator.ofFloat(itemView, "scaleX", ViewHelper.getScaleX(itemView), 1f),
                                            ObjectAnimator.ofFloat(itemView, "scaleY", ViewHelper.getScaleY(itemView), 1f),
                                            ObjectAnimator.ofFloat(itemView, "alpha", ViewHelper.getAlpha(itemView), 1f)
                                    );
                                    set.setDuration(90);
                                    set.start();
                                }
                            } else {
                                // збільшує розмір бокових карточок, якщо вибрана одна із центральних
                                if (nextItem != null && previousItem != null) {
                                    AnimatorSet set = new AnimatorSet();
                                    set.playTogether(
                                            ObjectAnimator.ofFloat(itemView, "translationX", viewXPosition, HelperClass.getCurrentCardPositionX(context)),
                                            ObjectAnimator.ofFloat(itemView, "scaleX", ViewHelper.getScaleX(itemView), 1f),
                                            ObjectAnimator.ofFloat(itemView, "scaleY", ViewHelper.getScaleY(itemView), 1f),
                                            ObjectAnimator.ofFloat(itemView, "alpha", ViewHelper.getAlpha(itemView), 1f),
                                            ObjectAnimator.ofFloat(nextItem, "scaleX", ViewHelper.getScaleX(itemView), 0.8f),
                                            ObjectAnimator.ofFloat(nextItem, "scaleY", ViewHelper.getScaleY(itemView), 0.8f),
                                            ObjectAnimator.ofFloat(nextItem, "alpha", ViewHelper.getAlpha(itemView), 0.8f),
                                            ObjectAnimator.ofFloat(previousItem, "scaleX", ViewHelper.getScaleX(itemView), 0.8f),
                                            ObjectAnimator.ofFloat(previousItem, "scaleY", ViewHelper.getScaleY(itemView), 0.8f),
                                            ObjectAnimator.ofFloat(previousItem, "alpha", ViewHelper.getAlpha(itemView), 0.8f)

                                    );
                                    set.setDuration(90);
                                    currentItem.bringToFront();
                                    Log.d("TAG", "Bring to Front");
                                    set.start();
                                }
                                // збільшує праву, якщо вибрана сама ліва
                                else if (nextItem == null && previousItem != null){
                                    AnimatorSet set = new AnimatorSet();
                                    set.playTogether(
                                            ObjectAnimator.ofFloat(itemView, "translationX", viewXPosition, HelperClass.getCurrentCardPositionX(context)),
                                            ObjectAnimator.ofFloat(itemView, "scaleX", ViewHelper.getScaleX(itemView), 1f),
                                            ObjectAnimator.ofFloat(itemView, "scaleY", ViewHelper.getScaleY(itemView), 1f),
                                            ObjectAnimator.ofFloat(itemView, "alpha", ViewHelper.getAlpha(itemView), 1f),
                                            ObjectAnimator.ofFloat(previousItem, "scaleX", ViewHelper.getScaleX(itemView), 0.8f),
                                            ObjectAnimator.ofFloat(previousItem, "scaleY", ViewHelper.getScaleY(itemView), 0.8f),
                                            ObjectAnimator.ofFloat(previousItem, "alpha", ViewHelper.getAlpha(itemView), 0.8f)

                                    );
                                    set.setDuration(90);
                                    currentItem.bringToFront();
                                    Log.d("TAG", "Bring to Front");
                                    set.start();
                                }
                                // збільшує ліву, якшо вибрана сама права
                                else if (previousItem == null && nextItem != null) {
                                    AnimatorSet set = new AnimatorSet();
                                    set.playTogether(
                                            ObjectAnimator.ofFloat(itemView, "translationX", viewXPosition, HelperClass.getCurrentCardPositionX(context)),
                                            ObjectAnimator.ofFloat(itemView, "scaleX", ViewHelper.getScaleX(itemView), 1f),
                                            ObjectAnimator.ofFloat(itemView, "scaleY", ViewHelper.getScaleY(itemView), 1f),
                                            ObjectAnimator.ofFloat(itemView, "alpha", ViewHelper.getAlpha(itemView), 1f),
                                            ObjectAnimator.ofFloat(nextItem, "scaleX", ViewHelper.getScaleX(itemView), 0.8f),
                                            ObjectAnimator.ofFloat(nextItem, "scaleY", ViewHelper.getScaleY(itemView), 0.8f),
                                            ObjectAnimator.ofFloat(nextItem, "alpha", ViewHelper.getAlpha(itemView), 0.8f)

                                    );
                                    set.setDuration(90);
                                    currentItem.bringToFront();
                                    set.start();
                                }
                            }
                            isTouchEnabled = true;
                            break;
                    }
                }
                return true;

            }
        };
    }

    private void changePageTo(int direction) {
        if (direction == NEXT_PAGE) {
            setTouchListenerToView(nextItem, true);
            setTouchListenerToView(currentItem, false);
            if (nextItem != null) {
                nextItem.bringToFront();
                rootView.invalidate();
            }
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(currentItem, "translationX", ViewHelper.getX(currentItem), previousItemXPosition),
                    ObjectAnimator.ofFloat(currentItem, "scaleX", ViewHelper.getScaleX(currentItem), 0.8f),
                    ObjectAnimator.ofFloat(currentItem, "scaleY", ViewHelper.getScaleY(currentItem), 0.8f),
                    ObjectAnimator.ofFloat(currentItem, "alpha", ViewHelper.getAlpha(currentItem), 0.8f),

                    ObjectAnimator.ofFloat(nextItem, "translationX", nextItemXPosition, HelperClass.getCurrentCardPositionX(context)),
                    ObjectAnimator.ofFloat(nextItem, "scaleX", ViewHelper.getScaleX(nextItem), 1f),
                    ObjectAnimator.ofFloat(nextItem, "scaleY", ViewHelper.getScaleY(nextItem), 1f),
                    ObjectAnimator.ofFloat(nextItem, "alpha", ViewHelper.getAlpha(nextItem), 1f)
            );
            set.setDuration(90);
            set.addListener(new AnimatorListener() {

                @Override
                public void onAnimationStart(Animator arg0) {
                    isTouchEnabled = false;
                }

                @Override
                public void onAnimationRepeat(Animator arg0) {
                }

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
                        ViewHelper.setAlpha(nextNext, 0.8f);
                        currentItem.bringToFront();
                        Log.d("TAG", "Bring to Front");
                        rootView.invalidate();
                        ObjectAnimator anim = ObjectAnimator.ofFloat(nextNext, "translationX", ViewHelper.getX(nextNext), ViewHelper.getX(nextNext));
                        anim.setDuration(90);
                        anim.start();
                        nextItem = nextNext;
                    }

                    if (removedItem != null) {
                        rootView.post(new Runnable() {
                            public void run() {
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rootView.removeView(removedItem);
                                    }
                                });
                            }
                        });
                    }
                    currentPosition++;
                    isTouchEnabled = true;
                }

                @Override
                public void onAnimationCancel(Animator arg0) {}
            });
            set.start();
            isTouchEnabled = true;

        } else if (direction == PREVIOUS_PAGE) {
            setTouchListenerToView(previousItem, true);
            setTouchListenerToView(currentItem, false);

            if (previousItem != null) {
                previousItem.bringToFront();
                rootView.invalidate();
            }
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(currentItem, "translationX", ViewHelper.getX(currentItem), nextItemXPosition),
                    ObjectAnimator.ofFloat(currentItem, "scaleX", ViewHelper.getScaleX(currentItem), 0.8f),
                    ObjectAnimator.ofFloat(currentItem, "scaleY", ViewHelper.getScaleY(currentItem), 0.8f),
                    ObjectAnimator.ofFloat(currentItem, "alpha", ViewHelper.getAlpha(currentItem), 0.8f),

                    ObjectAnimator.ofFloat(previousItem, "translationX", previousItemXPosition, HelperClass.getCurrentCardPositionX(context)),
                    ObjectAnimator.ofFloat(previousItem, "scaleX", ViewHelper.getScaleX(previousItem), 1f),
                    ObjectAnimator.ofFloat(previousItem, "scaleY", ViewHelper.getScaleY(previousItem), 1f),
                    ObjectAnimator.ofFloat(previousItem, "alpha", ViewHelper.getAlpha(previousItem), 1f)
            );
            set.setDuration(90);
            set.addListener(new AnimatorListener() {

                @Override
                public void onAnimationStart(Animator arg0) {
                    isTouchEnabled = false;
                }

                @Override
                public void onAnimationRepeat(Animator arg0) {
                }

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
                        ViewHelper.setAlpha(prevPrev, 0.8f);
                        ObjectAnimator anim = ObjectAnimator.ofFloat(prevPrev, "translationX", ViewHelper.getX(prevPrev), ViewHelper.getX(prevPrev));
                        anim.setDuration(90);
                        anim.start();
                        currentItem.bringToFront();
                        Log.d("TAG", "Bring to Front");
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
                public void onAnimationCancel(Animator arg0) {}
            });
            set.start();
            previousItem.bringToFront();
            isTouchEnabled = true;
            rootView.invalidate();
        }
    }


    private float getScaleValue(float currentPoint) {
        float value = (float) (((HelperClass.getCurrentCardPositionX(context) - currentPoint) * 0.5) / HelperClass.getCurrentCardPositionX(context));
        if (1 - value * value < 0.8f)
            return 0.8f;
        return 1 - value * value;
    }


    public void performNextPage() {
        if (nextItem != null)
            changePageTo(NEXT_PAGE);
    }

    public void performPreviousPage() {
        if (previousItem != null)
            changePageTo(PREVIOUS_PAGE);
    }

    public View getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(View currentItem) {
        this.currentItem = currentItem;
    }

    public View getNextItem() {
        return nextItem;
    }

    public void setNextItem(View nextItem) {
        this.nextItem = nextItem;
    }

    public View getPreviousItem() {
        return previousItem;
    }

    public void setPreviousItem(View previousItem) {
        this.previousItem = previousItem;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void cleanPager() {
        rootView.removeAllViews();
        rootView.clearAnimation();
        currentItem = null;
        nextItem = null;
        previousItem = null;
        removedItem = null;
        currentPosition = 0;
        viewList = null;
    }
}