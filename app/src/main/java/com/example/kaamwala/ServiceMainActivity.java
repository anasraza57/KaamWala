package com.example.kaamwala;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    TextView nameTextView, phoneTextView;
    ImageView imageView;
    CircleImageView profileImageView;
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
        profileImageView = headerView.findViewById(R.id.profile_image);

        loginButton = findViewById(R.id.loginButton);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        if (auth.getCurrentUser() != null) {
            isLoggedIn = true;
            loginButton.setText(R.string.logout);
            userID = auth.getCurrentUser().getUid();
            documentReference = firestore.collection("users").document(userID);
            servicesRef = documentReference.collection("my_services");
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        nameTextView.setText(documentSnapshot.getString("fullName"));
                        phoneTextView.setText(auth.getCurrentUser().getPhoneNumber());
                        String url = documentSnapshot.getString("profileUri");
                        Picasso.with(getApplicationContext()).load(url).fit().centerCrop().into(profileImageView);
                    }
                }
            });
        }

        recyclerView = findViewById(R.id.recycler_view);
        initData();

        recyclerViewAdapter = new ServicesRecyclerViewAdapter(this, servicesList, auth);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        servicesList.add(new ServiceModel("Plumber", R.drawable.plumbing));
        servicesList.add(new ServiceModel("Carpenter", R.drawable.carpenter));
        servicesList.add(new ServiceModel("Tailor", R.drawable.tailor));
        servicesList.add(new ServiceModel("Painter", R.drawable.painter));
        servicesList.add(new ServiceModel("Electrician", R.drawable.electrician));
        servicesList.add(new ServiceModel("Locksmith", R.drawable.locksmith));
        servicesList.add(new ServiceModel("Developer", R.drawable.developer));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.nav_my_profile:
                if (!isLoggedIn) {
                    intent = new Intent(getApplicationContext(), RegisterActivity.class);
                } else {
                    intent = new Intent(this, MyProfileActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.nav_my_services:
                if (!isLoggedIn) {
                    intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
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
            case R.id.nav_contact:
                intent = new Intent(getApplicationContext(), ContactUsActivity.class);
                startActivity(intent);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        MenuItem searchBar = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchBar);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                //call when we press the search button
//                searchData(s);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                //call when we type even a single letter
//                return false;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    private void searchData(String s) {
//        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//    }
}