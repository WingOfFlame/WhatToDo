package com.justinhu.whattodo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.justinhu.whattodo.db.TaskDbHelper;
import com.justinhu.whattodo.fragment.AllTaskFragment;
import com.justinhu.whattodo.fragment.TaskDialog;
import com.justinhu.whattodo.fragment.TodayTaskFragment;
import com.justinhu.whattodo.model.Task;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener, TaskDialog.NewTaskDialogListener, AllTaskFragment.TaskListOwner {
    private static final String TAG = "MainActivity";

    private TaskDbHelper mTaskDbHelper;
    //private CategoryDBHelper mCategoryDBHelper;

    private FragmentManager fragmentManager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private MyFabMenu fabMenu;
    private com.github.clans.fab.FloatingActionButton fabAddTask;
    private com.github.clans.fab.FloatingActionButton fabAddGoal;
    private com.github.clans.fab.FloatingActionButton fabAddHabbit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mTaskDbHelper = TaskDbHelper.getInstance(Main2Activity.this);


        setContentView(R.layout.activity_main2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        fragmentManager = getSupportFragmentManager();
        mSectionsPagerAdapter = new SectionsPagerAdapter(fragmentManager);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fabMenu = (MyFabMenu) findViewById(R.id.fab_menu);
        fabAddTask = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_task);
        fabAddTask.setImageResource(R.drawable.ic_task_white_24dp);
        fabAddGoal = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_goal);
        fabAddGoal.setImageResource(R.drawable.ic_goal_white_24dp);
        fabAddHabbit = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_habbit);
        fabAddHabbit.setImageResource(R.drawable.ic_habit_white_24dp);
        fabAddTask.setOnClickListener(this);
        fabAddHabbit.setOnClickListener(this);
        fabAddGoal.setOnClickListener(this);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == fabAddTask) {
            if (fabMenu.isOpened()) {
                // We will change the icon when the menu opens, here we want to change to the previous icon
                Toast.makeText(this, "TASK clicked", Toast.LENGTH_SHORT).show();
                Bundle args = new Bundle();
                args.putInt(TaskDialog.ARGS_KEY_MODE, TaskDialog.TASK_DIALOG_MODE_NEW);
                spawnTaskDialog(args);
                fabMenu.close(false);
            } else {
                // Since it is closed, let's set our new icon and then open the menu
                //fabMenu.getMenuIconView().setImageDrawable(drawableManager.getDrawable(getActivity(), R.drawable.ic_edit_24dp));
                fabMenu.open(true);
            }
        } else if (v == fabAddGoal) {
            Toast.makeText(this, "GOAL clicked", Toast.LENGTH_SHORT).show();
            fabMenu.close(false);
        } else if (v == fabAddHabbit) {
            Toast.makeText(this, "HABBIT clicked", Toast.LENGTH_SHORT).show();
            fabMenu.close(false);
        }
    }

    public void spawnTaskDialog(Bundle args) {

        DialogFragment dialog = new TaskDialog();
        dialog.setArguments(args);
        dialog.setRetainInstance(true);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, dialog)
                .addToBackStack(null).commit();
    }

    @Override
    public void onTaskSaveClick(Task newTask) {
        mTaskDbHelper.saveNewTask(newTask);
        mSectionsPagerAdapter.notifyDataSetChanged();
        //getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
    }

    @Override
    public void onTaskDeleteClick(int id) {

    }

    @Override
    public void onTaskAcceptClick(Task acceptedTask) {

    }

    @Override
    public void onTaskDidClick(int id) {

    }

    @Override
    public void onTaskAbortClick(int id) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return TodayTaskFragment.newInstance();
            }

            return AllTaskFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "TODAY";
                case 1:
                    return "ALL";
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            if (object.getClass() == AllTaskFragment.class) {
                AllTaskFragment f = (AllTaskFragment) object;
                f.update();
            }

            return super.getItemPosition(object);
        }
    }
}
