package com.example.kaamwala;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;


public class MyServicesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyServiceRecyclerAdapter adapter;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    DocumentReference personalInfoRef;
    CollectionReference serviceRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_services);
        Objects.requireNonNull(getSupportActionBar()).setTitle("My Services");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        personalInfoRef = firestore.collection("users")
                .document(auth.getCurrentUser().getUid());
        serviceRef = personalInfoRef.collection("my_services");
        ;

        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        Query query = serviceRef.orderBy("timing", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<MyServicesModel> options = new FirestoreRecyclerOptions.Builder<MyServicesModel>()
                .setQuery(query, MyServicesModel.class).build();

        adapter = new MyServiceRecyclerAdapter(options, personalInfoRef, auth, getApplicationContext());
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}