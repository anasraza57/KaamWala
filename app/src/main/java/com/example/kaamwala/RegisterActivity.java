package com.example.kaamwala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth auth;
    TextInputLayout phoneNumber, otpNumber;
    Button nextButton;
    ProgressBar progressBar;
    TextView state;

    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    FirebaseFirestore firestore;
    Boolean isVerificationInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Verify Phone Number");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        phoneNumber = findViewById(R.id.phone);
        otpNumber = findViewById(R.id.codeEnter);
        progressBar = findViewById(R.id.progressBar);
        state = findViewById(R.id.state);
        nextButton = findViewById(R.id.next_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isVerificationInProgress) {
                    String phoneVal = phoneNumber.getEditText().getText().toString();
                    if (phoneVal.length() == 10 || phoneVal.length() == 11) {
                        nextButton.setEnabled(false);
                        if (phoneVal.length() == 11) {
                            phoneVal = phoneVal.substring(1, phoneVal.length());
                        }
                        String phoneNum = "+92" + phoneVal;
                        progressBar.setVisibility(View.VISIBLE);
                        state.setText(R.string.sending_otp);
                        state.setVisibility(View.VISIBLE);
                        requestOTP(phoneNum);
                    } else {
                        phoneNumber.getEditText().setError("Phone Number is Not Valid");
                    }
                } else {
                    String userOTP = otpNumber.getEditText().getText().toString();
                    if (userOTP.length() == 6) {
                        PhoneAuthCredential authCredential = PhoneAuthProvider.getCredential(verificationId, userOTP);
                        verifyAuth(authCredential);

                    } else {
                        otpNumber.getEditText().setError("Valid OTP is Required");
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null) {
            progressBar.setVisibility(View.VISIBLE);
            state.setText(R.string.checking);
            state.setVisibility(View.VISIBLE);
            checkUserProfile();
        }
    }

    private void verifyAuth(PhoneAuthCredential authCredential) {
        auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkUserProfile();
                } else {
                    Toast.makeText(getApplicationContext(), "Authentication is Failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkUserProfile() {
        final DocumentReference documentReference = firestore.collection("users").document(auth.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    startActivity(new Intent(getApplicationContext(), ServiceMainActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), PersonalDetailsActivity.class));
                }
                finish();
            }
        });
    }

    private void requestOTP(String phoneNum) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressBar.setVisibility(View.GONE);
                state.setVisibility(View.GONE);
                otpNumber.setVisibility(View.VISIBLE);
                verificationId = s;
                token = forceResendingToken;
                nextButton.setText(R.string.verify);
                nextButton.setEnabled(true);
                isVerificationInProgress = true;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(getApplicationContext(), "OTP Expired, Re - Request  the OTP.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                verifyAuth(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getApplicationContext(), "Cannot Create Account " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}