package com.justinhu.whattodo;

import android.animation.AnimatorSet;
import android.content.Context;
import android.util.AttributeSet;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

/**
 * Created by justinhu on 2017-03-24.
 */

public class MyFabMenu extends FloatingActionMenu {
    //final Drawable closeImage;
    //final Drawable openImage;
    private FloatingActionButton mMenuButton;
    private AnimatorSet openSet;
    private AnimatorSet closeSet;

    public MyFabMenu(Context context) {
        this(context, null);
    }

    public MyFabMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingActionButton getMenuButton() {
        return mMenuButton;
    }

    public MyFabMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClosedOnTouchOutside(true);
        //closeImage = ContextCompat.getDrawable(this.getContext(), R.drawable.ic_add_white_24dp);

        mMenuButton = (FloatingActionButton) getChildAt(0);

    }

    @Override
    public void open(boolean animate) {
        super.open(animate);
        //getMenuIconView().setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_task_white_24dp));
    }

    @Override
    public void close(boolean animate) {
        //getMenuIconView().setImageDrawable(originalImage);
        super.close(animate);
    }
}
