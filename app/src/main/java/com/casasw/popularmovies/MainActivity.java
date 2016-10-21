package com.casasw.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.casasw.popularmovies.sync.PopularMoviesSyncAdapter;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String DETAILFRAGMENT_TAG = "DFTAG";
    private String mList;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mList = Utilities.getMoviesList(this);
        if (findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }else {
            mTwoPane = false;
        }

        PopularMoviesSyncAdapter.initializeSyncAdapter(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.action_settings) {
            intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        String list = Utilities.getMoviesList(this);
        if (list != null && !list.equals(list)) {
            MainFragment mf =  (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            if (mf != null) {
                mf.onListChanged();
            }
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (df != null){
                df.onListChanged(list);
            }
            mList = list;
        }
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
            Intent intent = new Intent(this,DetailActivity.class);
            intent.putExtra("EXTRA_MOVIE", movie);
            //startActivity(intent);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(intent.getExtras());
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.detail_container, detailFragment);
            ft.commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).putExtra("EXTRA_MOVIE",movie);
            startActivity(intent);
        }

    }
}
