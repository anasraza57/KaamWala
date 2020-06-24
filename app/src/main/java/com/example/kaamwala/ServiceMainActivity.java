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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ServiceMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    TextView nameTextView;
    TextView phoneTextView;
    Button loginButton;

    List<ServiceModel> servicesList;
    RecyclerView recyclerView;
    ServicesRecyclerViewAdapter recyclerViewAdapter;
    String userID;
    Boolean isLoggedIn = false;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    DocumentReference documentReference;
    CollectionReference servicesRef;

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
        View headerView = navigationView.getHeaderView(0);
        nameTextView = headerView.findViewById(R.id.nameText);
        phoneTextView = headerView.findViewById(R.id.phoneText);

        loginButton = findViewById(R.id.loginButton);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        if (auth.getCurrentUser() != null) {
            isLoggedIn = true;
            loginButton.setText(R.string.logout);
            userID = auth.getCurrentUser().getUid();
            documentReference = firestore.collection("users").document(userID);
            servicesRef = documentReference.collection("services");
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        nameTextView.setText(documentSnapshot.getString("fullName"));
                        phoneTextView.setText(auth.getCurrentUser().getPhoneNumber());
                    }
                }
            });
        }

        recyclerView = findViewById(R.id.recycler_view);
        initData();

        recyclerViewAdapter = new ServicesRecyclerViewAdapter(this, servicesList, auth);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn) {
                    auth.signOut();
                    Intent intent = new Intent(getApplicationContext(), ServiceMainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    private void initData() {
        servicesList = new ArrayList<>();
        servicesList.add(new ServiceModel("Plumber", R.drawable.plumber_icon));
        servicesList.add(new ServiceModel("Carpenter", R.drawable.carpenter_icon));
        servicesList.add(new ServiceModel("Tailor", R.drawable.plumber_icon));
        servicesList.add(new ServiceModel("Painter", R.drawable.logo));
        servicesList.add(new ServiceModel("Electrician", R.drawable.electrician_icon));
        servicesList.add(new ServiceModel("Locksmith", R.drawable.plumber_icon));
        servicesList.add(new ServiceModel("Developer", R.drawable.carpenter_icon));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.nav_my_profile:
                if (!isLoggedIn) {
                    intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    finish();
                } else {
                    intent = new Intent(this, MyProfileActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.nav_my_services:
                if (!isLoggedIn) {
                    intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    finish();
                } else {
                    servicesRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                final Intent intent = new Intent(getApplicationContext(), MyServicesActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "No services yet", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
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