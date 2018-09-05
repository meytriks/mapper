package com.dushan.dev.mapper.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dushan.dev.mapper.Adapters.SimpleListAdapter;
import com.dushan.dev.mapper.Data.User;
import com.dushan.dev.mapper.Data.UserData;
import com.dushan.dev.mapper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private SharedPreferences sharedPref;
    private UserData userData;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private Toolbar toolbar;

    private SwipeRefreshLayout swipeRefreshRecyclerList;
    private FloatingActionButton friendsAddFriendButton;
    private SimpleListAdapter mAdapter;

    private ArrayList<User> modelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mAuth = FirebaseAuth.getInstance();
        userData = UserData.getInstance(mAuth.getUid());
        sharedPref = getSharedPreferences("mapper", MODE_PRIVATE);

        toolbar = findViewById(R.id.friendsToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.friendsDrawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        connectViews();
        initiateActivity();
        setAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.friendsMenuAccount) {
            Intent activityIntent = new Intent(FriendsActivity.this, AccountActivity.class);
            String name = userData.getUser().getName();
            activityIntent.putExtra("name", userData.getUser().getName());
            activityIntent.putExtra("lastName", userData.getUser().getLastName());
            startActivity(activityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.settingsDrawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.navigationHome: {
                Intent activityIntent = new Intent(FriendsActivity.this, HomeActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationDiscover: {

                break;
            }
            case R.id.navigationFriends: {

                break;
            }
            case R.id.navigationSearch: {

                break;
            }
            case R.id.navigationStatistics: {

                break;
            }
            case R.id.navigationSettings: {
                Intent activityIntent = new Intent(FriendsActivity.this, SettingsActivity.class);
                startActivity(activityIntent);
                break;
            }
            default:
                return false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.friendsDrawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void connectViews() {
        recyclerView = findViewById(R.id.friendsRecyclerView);
        swipeRefreshRecyclerList = findViewById(R.id.friendsSwipeRefreshLayout);
        friendsAddFriendButton = findViewById(R.id.friendsAddFriendButton);
    }

    private void initiateActivity() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.friendsNavView);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navigationDrawerEmail);
        navUsername.setText(mAuth.getCurrentUser().getEmail());

        swipeRefreshRecyclerList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (swipeRefreshRecyclerList.isRefreshing())
                            swipeRefreshRecyclerList.setRefreshing(false);
                    }
                }, 2000);

            }
        });

        friendsAddFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setAdapter() {
        modelList.add(new User("Dushan", "Qwerty", "Qwerty", "Qwerty", "Qwerty", Uri.fromFile(new File("/sdcard/sample.jpg")), 0));


        mAdapter = new SimpleListAdapter(FriendsActivity.this, modelList);

        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(FriendsActivity.this, R.drawable.divider_recyclerview));
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new SimpleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, User model) {

                //handle item click events here
                Toast.makeText(FriendsActivity.this, "Hey " + model.getName(), Toast.LENGTH_SHORT).show();


            }
        });
    }
}