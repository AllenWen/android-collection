package com.example.app.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.WindowManager;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;

public class BackgroundDarkPopupWindow extends PopupWindow {
    private int mScreenWidth, mScreenHeight;
    private int mRightOf, mLeftOf, mBelow, mAbove;
    private int[] mLocationInWindowPosition = new int[2];
    private WeakReference<View> mRightOfPositionView, mLeftOfPositionView, mBelowPositionView,
            mAbovePositionView, mFillPositionView;
    private boolean mIsDarkInvoked = false;
    private int mDimColor = Color.BLACK;
    private float mDimValue = 0.2f;

    public BackgroundDarkPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
        mScreenWidth = getScreenWidth();
        mScreenHeight = getScreenHeight();
        setClippingEnabled(false);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        invokeBgCover(anchor);
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        invokeBgCover(parent);
        super.showAtLocation(parent, gravity, x, y);
    }

    /**
     * 更改蒙层位置
     */
    public void resetLocation(View view) {
        if (getContentView() == null) {
            return;
        }
        resetPosition(view);
        if (mIsDarkInvoked || isShowing()) {
            if (view.getContext() != null && view.getContext() instanceof Activity) {
                Activity activity = (Activity) view.getContext();
                ViewGroup parent = (ViewGroup) activity.getWindow().getDecorView().getRootView();
                Drawable dimDrawable = new ColorDrawable(mDimColor);
                dimDrawable.setBounds(mRightOf, mBelow, mLeftOf, mAbove);
                dimDrawable.setAlpha((int) (255 * mDimValue));
                ViewGroupOverlay overlay = parent.getOverlay();
                overlay.clear();
                overlay.add(dimDrawable);
                mIsDarkInvoked = true;
            }
        }
    }

    /**
     * 重新计算位置
     */
    private void resetPosition(View view) {
        if (mLeftOfPositionView != null) {
            drakLeftOf(view);
        }
        if (mRightOfPositionView != null) {
            darkRightOf(view);
        }
        if (mBelowPositionView != null) {
            darkBelow(view);
        }
        if (mAbovePositionView != null) {
            darkAbove(view);
        }
        if (mFillPositionView != null) {
            drakFillView(view);
        }
    }

    /**
     * show dark background
     */
    private void invokeBgCover(View view) {
        if (mIsDarkInvoked || isShowing() || getContentView() == null) {
            return;
        }
        checkPosition();
        if (view != null && view.getContext() != null && view.getContext() instanceof Activity) {
            Activity activity = (Activity) view.getContext();
            ViewGroup parent = (ViewGroup) activity.getWindow().getDecorView().getRootView();
            Drawable dimDrawable = new ColorDrawable(mDimColor);
            dimDrawable.setBounds(mRightOf, mBelow, mLeftOf, mAbove);
            dimDrawable.setAlpha((int) (255 * mDimValue));
            ViewGroupOverlay overlay = parent.getOverlay();
            overlay.add(dimDrawable);
            mIsDarkInvoked = true;
        }
    }

    /**
     * check whether the position of dark is set
     */
    private void checkPosition() {
        checkPositionLeft();
        checkPositionRight();
        checkPositionBelow();
        checkPositionAbove();
        checkPositionFill();
    }

    /**
     * check whether the left-of-position of dark is set
     */
    private void checkPositionLeft() {
        if (mLeftOfPositionView != null) {
            View leftOfView = mLeftOfPositionView.get();
            if (leftOfView != null && mLeftOf == 0) {
                drakLeftOf(leftOfView);
            }
        }
    }

    /**
     * check whether the right-of-position of dark is set
     */
    private void checkPositionRight() {
        if (mRightOfPositionView != null) {
            View rightOfView = mRightOfPositionView.get();
            if (rightOfView != null && mRightOf == 0) {
                darkRightOf(rightOfView);
            }
        }
    }

    /**
     * check whether the below-position of dark is set
     */
    private void checkPositionBelow() {
        if (mBelowPositionView != null) {
            View belowView = mBelowPositionView.get();
            if (belowView != null && mBelow == 0) {
                darkBelow(belowView);
            }
        }
    }

    /**
     * check whether the above-position of dark is set
     */
    private void checkPositionAbove() {
        if (mAbovePositionView != null) {
            View aboveView = mAbovePositionView.get();
            if (aboveView != null && mAbove == 0) {
                darkAbove(aboveView);
            }
        }
    }

    /**
     * check whether the fill-position of dark is set
     */
    private void checkPositionFill() {
        if (mFillPositionView != null) {
            View fillView = mFillPositionView.get();
            if (fillView != null && (mLeftOf == 0 || mAbove == 0)) {
                drakFillView(fillView);
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mIsDarkInvoked) {
            if (getContentView() != null && getContentView().getContext() != null && getContentView().getContext() instanceof Activity) {
                Activity activity = (Activity) getContentView().getContext();
                ViewGroup parent = (ViewGroup) activity.getWindow().getDecorView().getRootView();
                ViewGroupOverlay overlay = parent.getOverlay();
                overlay.clear();
            }
            mIsDarkInvoked = false;
        }
    }

    public void setDarkAlpha(float alpha) {
        mDimValue = alpha;
    }

    public void resetDarkPosition() {
        darkFillScreen();
        if (mRightOfPositionView != null) {
            mRightOfPositionView.clear();
        }
        if (mLeftOfPositionView != null) {
            mLeftOfPositionView.clear();
        }
        if (mBelowPositionView != null) {
            mBelowPositionView.clear();
        }
        if (mAbovePositionView != null) {
            mAbovePositionView.clear();
        }
        if (mFillPositionView != null) {
            mFillPositionView.clear();
        }
        mRightOfPositionView = mLeftOfPositionView = mBelowPositionView = mAbovePositionView =
                mFillPositionView = null;
    }

    /**
     * fill screen
     */
    public void darkFillScreen() {
        mRightOf = 0;
        mLeftOf = mScreenWidth;
        mAbove = mScreenHeight;
        mBelow = 0;
    }

    /**
     * dark fill view
     *
     * @param view target view
     */
    public void drakFillView(View view) {
        mFillPositionView = new WeakReference<>(view);
        view.getLocationInWindow(mLocationInWindowPosition);
        mRightOf = mLocationInWindowPosition[0];
        mLeftOf = mLocationInWindowPosition[0] + view.getWidth();
        mAbove = mLocationInWindowPosition[1] + view.getHeight();
        mBelow = mLocationInWindowPosition[1];
    }

    /**
     * dark right of target view
     *
     * @param view
     */
    public void darkRightOf(View view) {
        mRightOfPositionView = new WeakReference<>(view);
        view.getLocationInWindow(mLocationInWindowPosition);
        mRightOf = mLocationInWindowPosition[0] + view.getWidth();
    }

    /**
     * dark left of target view
     *
     * @param view
     */
    public void drakLeftOf(View view) {
        mLeftOfPositionView = new WeakReference<>(view);
        view.getLocationInWindow(mLocationInWindowPosition);
        mLeftOf = mLocationInWindowPosition[0];
    }

    /**
     * dark above target view
     *
     * @param view
     */
    public void darkAbove(View view) {
        mAbovePositionView = new WeakReference<>(view);
        view.getLocationInWindow(mLocationInWindowPosition);
        mAbove = mLocationInWindowPosition[1];
    }

    /**
     * dark below target view
     *
     * @param view
     */
    public void darkBelow(View view) {
        mBelowPositionView = new WeakReference<>(view);
        view.getLocationInWindow(mLocationInWindowPosition);
        mBelow = mLocationInWindowPosition[1] + view.getHeight();
    }

    /**
     * Return the width of screen, in pixel.
     *
     * @return the width of screen, in pixel
     */
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContentView().getContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }

    /**
     * Return the height of screen, in pixel.
     *
     * @return the height of screen, in pixel
     */
    public int getScreenHeight() {
        WindowManager wm = (WindowManager) getContentView().getContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }
}
