package com.justinhu.whattodo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

/**
 * Created by justinhu on 2017-03-24.
 */

public class MyFabMenu extends FloatingActionMenu {
    final Drawable originalImage;
    private FloatingActionButton mMenuButton;

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
        originalImage = getMenuIconView().getDrawable();
        mMenuButton = (FloatingActionButton) getChildAt(0);


    }

    @Override
    public void open(boolean animate) {
        super.open(animate);
        getMenuIconView().setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_arrow_forward_black_24dp));


    }

    @Override
    public void close(boolean animate) {
        getMenuIconView().setImageDrawable(originalImage);
        super.close(animate);
    }
}
