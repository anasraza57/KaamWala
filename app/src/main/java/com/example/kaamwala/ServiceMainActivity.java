package com.example.kaamwala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ServiceMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;

    List<Service> servicesList;
    RecyclerView recyclerView;
    ServicesRecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.services);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ServiceMainActivity.class));
                finish();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.recycler_view);
        initData();

        recyclerViewAdapter = new ServicesRecyclerViewAdapter(this,servicesList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
    }

    private void initData() {
        servicesList = new ArrayList<>();
        servicesList.add(new Service("Plumber",R.drawable.plumbing_icon));
        servicesList.add(new Service("Carpenter",R.drawable.carpenter_icon));
        servicesList.add(new Service("Tailor",R.drawable.carpenter_icon2));
        servicesList.add(new Service("Painter",R.drawable.logo));
        servicesList.add(new Service("Electrician",R.drawable.electrician_icon));
        servicesList.add(new Service("Locksmith",R.drawable.carpenter_icon2));
        servicesList.add(new Service("Developer",R.drawable.plumbing_icon));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.nav_my_addresses:
                Toast.makeText(this, "My Address", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_my_services:
                Toast.makeText(this, "My Services", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_help:
                Toast.makeText(this, "Need Help?", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_contact:
                Toast.makeText(this, "Contact Us", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_about:
                Toast.makeText(this, "About Us", Toast.LENGTH_LONG).show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
        }
    }
}