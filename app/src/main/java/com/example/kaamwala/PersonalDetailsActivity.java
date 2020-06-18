package com.example.kaamwala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PersonalDetailsActivity extends AppCompatActivity {

    TextInputLayout nameInputLayout, emailInputLayout, addressInputLayout;
    Button saveButton;

    String userId;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Personal Details");

        nameInputLayout = findViewById(R.id.nameText);
        emailInputLayout = findViewById(R.id.emailText);
        addressInputLayout = findViewById(R.id.addressText);
        saveButton = findViewById(R.id.saveButton);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();

        final DocumentReference documentReference = firestore.collection("users").document(userId);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = nameInputLayout.getEditText().getText().toString();
                String address = addressInputLayout.getEditText().getText().toString();
                String email = emailInputLayout.getEditText().getText().toString();
                if (fullName.isEmpty() || address.isEmpty()) {
                    if (fullName.isEmpty()) {
                        nameInputLayout.getEditText().setError("Required!");
                    }
                    if (address.isEmpty()) {
                        nameInputLayout.getEditText().setError("Required!");
                    }
                } else {
                    if (email.isEmpty()) {
                        email = "";
                    }
                    Map<String, Object> user = new HashMap<>();
                    user.put("fullName", fullName);
                    user.put("address", address);
                    user.put("email", email);

                    documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext(), ServiceMainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Data is Not Inserted!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}