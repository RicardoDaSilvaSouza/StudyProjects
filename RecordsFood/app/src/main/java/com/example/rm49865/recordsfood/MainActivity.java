package com.example.rm49865.recordsfood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.rm49865.recordsfood.dao.RestaurantDAO;
import com.example.rm49865.recordsfood.fragment.AboutFragment;
import com.example.rm49865.recordsfood.fragment.FieldsRestaurantFragment;
import com.example.rm49865.recordsfood.fragment.ListRestaurantsFragment;
import com.example.rm49865.recordsfood.fragment.MapRestaurantsFragment;
import com.example.rm49865.recordsfood.model.Restaurant;
import com.example.rm49865.recordsfood.service.SynchronizeDataService;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements FieldsRestaurantFragment.OnFieldsFragmentInteractionListener,
        ListRestaurantsFragment.OnListRestaurantsInteractionListener{

    private final String TAG = MainActivity.class.getName();
    private final String PARAM_PREFERENCE_SYNC = "sync";
    public static final String PARAM_RESTAURANT_ID = "restaurantId";
    public static final String PREFERENCES_NAME = "RecordsFood";
    private SynchronizeDataReceiver receiver;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private List<Restaurant> restaurants;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.dlNavigation)
    DrawerLayout dlNavigation;
    @Bind(R.id.nvDrawer)
    NavigationView nvDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        receiver = new SynchronizeDataReceiver(new Handler());

        setSupportActionBar(toolbar);
        setupDrawerContent();

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, dlNavigation, toolbar, R.string.label_open, R.string.label_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        dlNavigation.setDrawerListener(actionBarDrawerToggle);

        if(handleIntent(getIntent())){
            loadRestaurants();
            Fragment fragment = ListRestaurantsFragment.newInstance(restaurants);
            updateFragment(fragment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.basic_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                dlNavigation.openDrawer(GravityCompat.START);
                break;
            case R.id.itSearch:
                onSearchRequested();
                break;
        }
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void doAction(Restaurant restaurant, int actionType) {
        RestaurantDAO dao;
        switch (actionType){
            case FieldsRestaurantFragment.ACTION_TYPE_CREATE:
                dao = new RestaurantDAO(this);
                if(dao.insert(restaurant)){
                    Toast.makeText(MainActivity.this, R.string.message_success_register,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.message_error_register,
                            Toast.LENGTH_SHORT).show();
                }

                break;
            case FieldsRestaurantFragment.ACTION_TYPE_EDIT:
                dao =  new RestaurantDAO(this);
                if(dao.update(restaurant)){
                    Toast.makeText(MainActivity.this, R.string.message_success_update,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.message_error_update,
                            Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                Toast.makeText(MainActivity.this, R.string.message_not_implement_action,
                        Toast.LENGTH_SHORT).show();
        }
        loadRestaurants();
        updateFragment(ListRestaurantsFragment.newInstance(restaurants));
    }

    @Override
    public void edit(int position) {
        updateFragment(FieldsRestaurantFragment
                .newInstance(getString(R.string.label_update),
                        restaurants.get(position).getId(), true, false));
        supportInvalidateOptionsMenu();
    }

    @Override
    public void delete(Restaurant restaurant){
        RestaurantDAO dao = new RestaurantDAO(this);
        if(dao.delete(restaurant.getId())){
            Toast.makeText(this, R.string.message_success_delete, Toast.LENGTH_SHORT).show();
            new File(restaurant.getImagePath()).delete();
            loadRestaurants();
            updateFragment(ListRestaurantsFragment.newInstance(restaurants));
        } else {
            Toast.makeText(this, R.string.message_error_delete, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void detail(int position) {
        updateFragment(FieldsRestaurantFragment.newInstance("", restaurants.get(position).getId(),
                true, true));
        supportInvalidateOptionsMenu();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private boolean handleIntent(Intent intent){
        boolean result = true;
        if(Intent.ACTION_EDIT.equals(intent.getAction())){
            updateFragment(FieldsRestaurantFragment
                    .newInstance(getString(R.string.label_update),
                            intent.getExtras().getInt(PARAM_RESTAURANT_ID), true, false));
            supportInvalidateOptionsMenu();
            result = false;
        } else if(Intent.ACTION_PICK.equals(intent.getAction())){
            updateFragment(FieldsRestaurantFragment.newInstance("",
                    intent.getExtras().getInt(PARAM_RESTAURANT_ID),
                    true, true));
            supportInvalidateOptionsMenu();
            result = false;
        }
        return result;
    }

    private void loadRestaurants(){
        RestaurantDAO dao = new RestaurantDAO(this);
        restaurants = dao.getRestaurants();
    }

    private void setupDrawerContent(){
        nvDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        selectDrawerItem(item);
                        return true;
                    }
                }
        );
    }

    public void selectDrawerItem(MenuItem menuItem){
        Fragment fragment = null;
        switch (menuItem.getItemId()){
            case R.id.itAddRest:
                fragment = FieldsRestaurantFragment
                        .newInstance(getString(R.string.label_register), null, false, false);
                break;
            case R.id.itSeeRest:
                loadRestaurants();
                fragment = ListRestaurantsFragment.newInstance(restaurants);
                break;
            case R.id.itSeeMap:
                loadRestaurants();
                fragment = MapRestaurantsFragment.newInstance(restaurants);
                break;
            case R.id.itSyncData:
                synchronizeData();
                return;
            case R.id.itSeeAbout:
                fragment = new AboutFragment();
                break;
            default:
                loadRestaurants();
                fragment = ListRestaurantsFragment.newInstance(restaurants);
        }

        updateFragment(fragment);
        setTitle(menuItem.getTitle());
        menuItem.setChecked(true);
        dlNavigation.closeDrawers();
    }

    private void updateFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    public void synchronizeData(){
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        if(!preferences.getBoolean(PARAM_PREFERENCE_SYNC, false)){
            Intent intent = new Intent(this, SynchronizeDataService.class);
            intent.putExtra(SynchronizeDataService.PARAM_RESULT_RECEIVER, receiver);
            startService(intent);
        } else {
            Toast.makeText(MainActivity.this, R.string.message_sync_ok, Toast.LENGTH_SHORT).show();
        }
    }

    public class SynchronizeDataReceiver extends ResultReceiver{

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public SynchronizeDataReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            switch (resultCode) {
                case SynchronizeDataService.DOWNLAOD_SUCESS:
                    RestaurantDAO dao = new RestaurantDAO(MainActivity.this);
                    List<Restaurant> restaurants = resultData.getParcelableArrayList(SynchronizeDataService.PARAM_RESTAURANT_LIST);
                    for (Restaurant restaurant : restaurants) {
                        dao.insert(restaurant);
                    }
                    SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(PARAM_PREFERENCE_SYNC, true);
                    editor.commit();
                    Toast.makeText(MainActivity.this, R.string.message_success_sync, Toast.LENGTH_SHORT).show();
                    loadRestaurants();
                    updateFragment(ListRestaurantsFragment.newInstance(restaurants));
                    break;
                case SynchronizeDataService.DOWNLOAD_FAIL:
                    Toast.makeText(MainActivity.this, R.string.message_error_sync, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
