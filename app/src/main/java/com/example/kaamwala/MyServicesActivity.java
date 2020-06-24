package com.example.kaamwala;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

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
        serviceRef = personalInfoRef.collection("services");
        ;

        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        Query query = serviceRef.orderBy("timing", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<MyServicesModel> options = new FirestoreRecyclerOptions.Builder<MyServicesModel>()
                .setQuery(query, MyServicesModel.class).build();

        adapter = new MyServiceRecyclerAdapter(options, personalInfoRef, auth);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

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