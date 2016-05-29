package com.example.rm49865.recordsfood;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.rm49865.recordsfood.dao.RestaurantDAO;
import com.example.rm49865.recordsfood.fragment.ListRestaurantsFragment;
import com.example.rm49865.recordsfood.model.Restaurant;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity
        implements ListRestaurantsFragment.OnListRestaurantsInteractionListener{

    private final String TAG = SearchActivity.class.getName();
    private List<Restaurant> restaurants;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.basic_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.itSearch:
                onSearchRequested();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String filter = intent.getStringExtra(SearchManager.QUERY);
            RestaurantDAO dao = new RestaurantDAO(this);
            restaurants = dao.findByFilter(filter);
            updateFragment(ListRestaurantsFragment.newInstance(restaurants));
        }
    }

    private void updateFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void edit(int position) {
        startActivity(createIntentToMainActivity(Intent.ACTION_EDIT,
                restaurants.get(position).getId()));
        finish();
    }

    @Override
    public void detail(int position) {
        startActivity(createIntentToMainActivity(Intent.ACTION_PICK,
                restaurants.get(position).getId()));
        finish();
    }

    private Intent createIntentToMainActivity(String action, Integer restaurantId){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.PARAM_RESTAURANT_ID, restaurantId);
        intent.setAction(action);
        return intent;
    }
}
