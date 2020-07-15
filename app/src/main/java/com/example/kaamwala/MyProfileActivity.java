package com.example.kaamwala;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    String userID;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    StorageReference storageReference;
    DocumentReference documentReference;

    TextView nameTextView, emailTextView, addressTextView, phoneTextView;
    ImageView nameEditIcon, emailEditIcon, addressEditIcon;
    EditText nameEditText, emailEditText, addressEditText;
    CircleImageView profileImage;

    Uri imageUri;
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
        profileImage = findViewById(R.id.profile_image);

        nameEditIcon.setOnClickListener(this);
        emailEditIcon.setOnClickListener(this);
        addressEditIcon.setOnClickListener(this);
        profileImage.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {
            userID = auth.getCurrentUser().getUid();
            documentReference = firestore.collection("users").document(userID);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        phoneTextView.setText(auth.getCurrentUser().getPhoneNumber());
                        nameTextView.setText(documentSnapshot.getString("fullName"));
                        emailTextView.setText(documentSnapshot.getString("email"));
                        addressTextView.setText(documentSnapshot.getString("address"));
                        String url = documentSnapshot.getString("profileUri");
                        Picasso.with(getApplicationContext()).load(url).fit().centerCrop().into(profileImage);
                    }
                }
            });
            storageReference = FirebaseStorage.getInstance().getReference("profile_images");
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
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getText(R.string.search_on_map),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(MyProfileActivity.this, GoogleMapsActivity.class);
                                startActivityForResult(intent, 100);
                            }
                        });
                dialog.show();
                break;
            case R.id.profile_image:
                openFileChooser();
                break;
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(profileImage);
            uploadFile();
        } else if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("address")) {
                addressTextView.setText(data.getStringExtra("address"));
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        if (imageUri != null) {
            final StorageReference imageReference = storageReference.child(userID + "." + getFileExtension(imageUri));
            final UploadTask uploadTask = imageReference.putFile(imageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return imageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String profileUrl = task.getResult().toString();
                                final Map<String, Object> user = new HashMap<>();
                                user.put("profileUri", profileUrl);
                                documentReference.set(user, SetOptions.merge());
                            }
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(getApplicationContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}