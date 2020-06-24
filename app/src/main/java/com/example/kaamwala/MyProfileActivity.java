package com.example.kaamwala;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {

    String userID;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    TextView nameTextView, emailTextView, addressTextView, phoneTextView;
    ImageView nameEditIcon, emailEditIcon, addressEditIcon;
    EditText nameEditText, emailEditText, addressEditText;

    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Objects.requireNonNull(getSupportActionBar()).setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        phoneTextView = findViewById(R.id.phoneText);
        nameTextView = findViewById(R.id.nameText);
        emailTextView = findViewById(R.id.emailText);
        addressTextView = findViewById(R.id.addressText);
        nameEditIcon = findViewById(R.id.editName);
        emailEditIcon = findViewById(R.id.editEmail);
        addressEditIcon = findViewById(R.id.editAddress);

        nameEditIcon.setOnClickListener(this);
        emailEditIcon.setOnClickListener(this);
        addressEditIcon.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {
            userID = auth.getCurrentUser().getUid();
            DocumentReference documentReference = firestore.collection("users").document(userID);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        phoneTextView.setText(auth.getCurrentUser().getPhoneNumber());
                        nameTextView.setText(documentSnapshot.getString("fullName"));
                        emailTextView.setText(documentSnapshot.getString("email"));
                        addressTextView.setText(documentSnapshot.getString("address"));
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        final DocumentReference documentReference = firestore.collection("users").document(userID);
        final Map<String, Object> user = new HashMap<>();
        switch (view.getId()) {
            case R.id.editName:
                dialog = new AlertDialog.Builder(this).create();
                nameEditText = new EditText(this);
                dialog.setTitle("Full Name");
                dialog.setView(nameEditText);
                nameEditText.setText(nameTextView.getText().toString());
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        nameTextView.setText(nameEditText.getText().toString());
                        user.put("fullName", nameEditText.getText().toString());
                        documentReference.set(user, SetOptions.merge());
                    }
                });
                dialog.show();
                break;
            case R.id.editEmail:
                dialog = new AlertDialog.Builder(this).create();
                emailEditText = new EditText(this);
                dialog.setTitle("Email");
                dialog.setView(emailEditText);
                emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                emailEditText.setText(emailTextView.getText().toString());
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        emailTextView.setText(emailEditText.getText().toString());
                        user.put("email", emailEditText.getText().toString());
                        documentReference.set(user, SetOptions.merge());
                    }
                });
                dialog.show();
                break;
            case R.id.editAddress:
                dialog = new AlertDialog.Builder(this).create();
                addressEditText = new EditText(this);
                dialog.setTitle("Address");
                dialog.setView(addressEditText);
                addressEditText.setText(addressTextView.getText().toString());
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addressTextView.setText(addressEditText.getText().toString());
                        user.put("address", addressEditText.getText().toString());
                        documentReference.set(user, SetOptions.merge());
                    }
                });
                dialog.show();
                break;
        }
    }
}