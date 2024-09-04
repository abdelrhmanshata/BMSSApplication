package com.example.bmssapplication.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.bmssapplication.Fragment.DebitsFragment;
import com.example.bmssapplication.Fragment.GoalsFragment;
import com.example.bmssapplication.Fragment.HistoryFragment;
import com.example.bmssapplication.Fragment.HomeFragment;
import com.example.bmssapplication.Fragment.PlanningFragment;
import com.example.bmssapplication.Fragment.SettingsFragment;
import com.example.bmssapplication.Model.User;
import com.example.bmssapplication.R;
import com.example.bmssapplication.Registration.LoginActivity;
import com.example.bmssapplication.databinding.ActivityHomeBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityHomeBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refUsers = database.getReference("Users");

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ImageView UserImage;
    TextView UserName, UserEmail;

    HomeFragment homeFragment = new HomeFragment();
    PlanningFragment planningFragment = new PlanningFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    GoalsFragment goalsFragment = new GoalsFragment();
    DebitsFragment debitsFragment = new DebitsFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        UserImage = headerLayout.findViewById(R.id.userImage);
        UserName = headerLayout.findViewById(R.id.userName);
        UserEmail = headerLayout.findViewById(R.id.userEmail);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        UserImage.setOnClickListener(v -> {
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(navigationView);
        });

        getUserData();
        getHomePage();
    }

    void getUserData() {
        refUsers.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    UserName.setText(user.getUserName());
                    UserEmail.setText(user.getuEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation item clicks here.
        if (item.getItemId() == R.id.nav_home) {
            getHomePage();
        } else if (item.getItemId() == R.id.nav_planning) {
            getPlanningPage();
        } else if (item.getItemId() == R.id.nav_history) {
            getHistoryPage();
        } else if (item.getItemId() == R.id.nav_goals) {
            getGoalsPage();
        } else if (item.getItemId() == R.id.nav_debits) {
            getDebitsPage();
        } else if (item.getItemId() == R.id.nav_settings) {
            getSettingsPage();
        } else if (item.getItemId() == R.id.nav_logout) {
            auth.signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        } else {
            getHomePage();
        }
        drawerLayout.closeDrawer(navigationView);
        return true;
    }

    void getHomePage() {
        toolbar.setTitle(getString(R.string.main));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
    }

    void getPlanningPage() {
        toolbar.setTitle(getString(R.string.planning));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, planningFragment).commit();
    }

    void getGoalsPage() {
        toolbar.setTitle(getString(R.string.goals));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, goalsFragment).commit();
    }

    void getDebitsPage() {
        toolbar.setTitle(getString(R.string.debits));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, debitsFragment).commit();
    }

    void getHistoryPage() {
        toolbar.setTitle(getString(R.string.history));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, historyFragment).commit();
    }


    void getSettingsPage() {
        toolbar.setTitle(getString(R.string.setting));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, settingsFragment).commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
}