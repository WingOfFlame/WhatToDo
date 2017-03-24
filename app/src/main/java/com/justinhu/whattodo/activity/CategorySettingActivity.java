package com.justinhu.whattodo.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.justinhu.whattodo.CategoryListAdapter;
import com.justinhu.whattodo.CategoryListItemTouchHelperCallback;
import com.justinhu.whattodo.R;
import com.justinhu.whattodo.db.CategoryDBHelper;
import com.justinhu.whattodo.fragment.CategoryDialog;
import com.justinhu.whattodo.model.Category;

import java.util.List;

/*https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf#.xd1fovhdu

License

Copyright (C) 2015 Paul Burke

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

public class CategorySettingActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor>,CategoryListItemTouchHelperCallback.OnStartDragListener, CategoryListAdapter.OnItemClickedListener,View.OnClickListener {
    private static final String TAG = "CategorySettingActivity";

    private ProgressBar emprtView;
    private RecyclerView mRecyclerView;
    private CategoryListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemTouchHelper mItemTouchHelper;
    String[] myDataset;
    private CategoryDBHelper mCategoryDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        mCategoryDBHelper = CategoryDBHelper.getInstance(CategorySettingActivity.this);

        myDataset = getResources().getStringArray(R.array.category_default);

        emprtView = (ProgressBar) findViewById(R.id.list_empty);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CategoryListAdapter(this,this);
        mRecyclerView.setAdapter(mAdapter);


        ItemTouchHelper.Callback callback = new CategoryListItemTouchHelperCallback((CategoryListItemTouchHelperCallback.OnItemTouchListener) mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);


        getSupportLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        List<Category> data = mAdapter.getResults();
        Category.updateLookupTable(data);
        mCategoryDBHelper.saveCategory(data);
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onClick(Category item) {
        CategoryDialog dialog = CategoryDialog.newInstance(item);
        dialog.setRetainInstance(true);
        dialog.show(getSupportFragmentManager(),"Category Dialog");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader");
        return new AsyncTaskLoader<Cursor>(CategorySettingActivity.this) {
            @Override
            public Cursor loadInBackground() {
                Log.i(TAG, "Getting Cursor");
                return mCategoryDBHelper.getCategory();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(TAG, "onLoadFinished");
        List<Category> data =mCategoryDBHelper.getCategoryList(this,cursor);
        mAdapter.addData(data);
        emprtView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        emprtView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

    }
}
