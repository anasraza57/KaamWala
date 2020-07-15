package com.example.kaamwala;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PersonalDetailsActivity extends AppCompatActivity {
    private static final String TAG = "GoogleMapsActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    TextInputLayout nameInputLayout, emailInputLayout, addressInputLayout;
    Button saveButton;

    String userId;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    DocumentReference documentReference;

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
        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
            documentReference = firestore.collection("users").document(userId);
        }

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

        if (isServiceOK()) {
            init();
        }

    }

    private void init() {
        Button button = findViewById(R.id.map_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(PersonalDetailsActivity.this)
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent(PersonalDetailsActivity.this, GoogleMapsActivity.class);
                                startActivityForResult(intent, 100);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if (response.isPermanentlyDenied()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PersonalDetailsActivity.this);
                                    builder.setTitle("Permission Denied")
                                            .setMessage("Permission to access device location is permanently denied. you need to go to setting to allow the permission.")
                                            .setNegativeButton("Cancel", null)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                                                }
                                            })
                                            .show();
                                } else {
                                    Toast.makeText(PersonalDetailsActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("address")) {
                addressInputLayout.getEditText().setText(data.getStringExtra("address"));
            }
        }
    }

    public boolean isServiceOK() {
        Log.d(TAG, "isServiceOK: checking google service version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PersonalDetailsActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and user can make map requests
            Log.d(TAG, "isServiceOK: google play service is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occurred but we can resolve it
            Log.d(TAG, "isServiceOK: can error occurred but we can resolve it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(PersonalDetailsActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map request", Toast.LENGTH_LONG).show();
        }
        return false;
    }
}