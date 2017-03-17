package com.justinhu.whattodo;

import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.justinhu.whattodo.CategoryListItemTouchHelperCallback.OnStartDragListener;
import com.justinhu.whattodo.model.Category;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by justinhu on 2017-03-12.
 */

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> implements CategoryListItemTouchHelperCallback.OnItemTouchListener {
    private static final String TAG = "CategoryListAdapter";
    private OnItemClickedListener mClickListener;
    private List<Category> mDataset = new ArrayList<>();
    private OnStartDragListener mDragListener;

    public interface OnItemClickedListener{
        public void onClick(Category item);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mIconView;
        public TextView mTextView;
        public ImageView mHandleView;

        public ViewHolder(View v) {
            super(v);
            mIconView = (ImageView) v.findViewById(R.id.icon);
            mTextView = (TextView) v.findViewById(R.id.text);
            mHandleView = (ImageView) v.findViewById(R.id.handle);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CategoryListAdapter( OnStartDragListener dragListener, OnItemClickedListener clickListener) {
        mDragListener = dragListener;
        mClickListener = clickListener;
    }

    public void addData(List<Category> myDataset){
        mDataset = myDataset;
        Collections.sort(mDataset);
        notifyDataSetChanged();
    }
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Category item = mDataset.get(position);
        if(item.getIsDefault() == 1){
            holder.mIconView.setImageResource(item.getIconId());
            holder.mIconView.setColorFilter(Color.parseColor(item.color));
            holder.mTextView.setText(item.getDisplayNameId());
        }
        holder.mHandleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) ==
                        MotionEvent.ACTION_DOWN) {
                    mDragListener.onStartDrag(holder);
                }
                return false;
            }
        });

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onClick(item);
            }
        });*/

    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mDataset, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mDataset, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemSwiped(int position, int direction) {

    }

    public List<Category> getResults(){
        int size = mDataset.size();
        for(int i=0; i<size; i++){
            mDataset.get(i).setPriority(size-i);
        }
        return mDataset;
    }
}
