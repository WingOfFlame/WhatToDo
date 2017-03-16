package com.justinhu.whattodo;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by justinhu on 2017-03-12.
 */

public class CategoryListItemTouchHelperCallback extends ItemTouchHelper.Callback {

    public interface OnItemTouchListener {

        boolean onItemMove(int fromPosition, int toPosition);

        void onItemSwiped(int position, int direction);
    }

    public interface OnStartDragListener {

        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }


    private final OnItemTouchListener mListener;

    public CategoryListItemTouchHelperCallback(OnItemTouchListener listener) {
        this.mListener = listener;
    }


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = 0;//ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mListener.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mListener.onItemSwiped(viewHolder.getAdapterPosition(),direction);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        //return super.isLongPressDragEnabled();
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        //return super.isItemViewSwipeEnabled();
        return false;
    }
}
