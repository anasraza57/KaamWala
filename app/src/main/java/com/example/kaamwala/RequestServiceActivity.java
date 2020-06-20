package com.example.kaamwala;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestServiceActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    RadioGroup timingGroup;
    TextView selectedTimeView, phoneNumberView, addressTextView;
    Button selectTimeButton, requestButton;
    EditText optionalPhoneText, optionalAddressText, noteText;

    int hour, minute;
    String userID;
    Boolean isUserDetailsExists = false;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_service);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String service = bundle.getString("service");
            Objects.requireNonNull(getSupportActionBar()).setTitle(service);
        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Request a Service");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        phoneNumberView = findViewById(R.id.phoneNumber);
        addressTextView = findViewById(R.id.addressText);
        optionalPhoneText = findViewById(R.id.optionalPhoneNumber);
        optionalAddressText = findViewById(R.id.optionalAddress);
        noteText = findViewById(R.id.note);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {
            userID = auth.getCurrentUser().getUid();
            DocumentReference documentReference = firestore.collection("users").document(userID);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        isUserDetailsExists = true;
                        phoneNumberView.setText(auth.getCurrentUser().getPhoneNumber());
                        addressTextView.setText(documentSnapshot.getString("address"));
                    }
                }
            });
        }

        timingGroup = findViewById(R.id.timing_radio);
        selectedTimeView = findViewById(R.id.selected_time);
        selectTimeButton = findViewById(R.id.select_time);
        requestButton = findViewById(R.id.requestButton);
        selectTimeButton.setOnClickListener(this);
        requestButton.setOnClickListener(this);

        timingGroup.setOnCheckedChangeListener(this);
        timingGroup.check(R.id.immediate);

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
        if (checkId == R.id.later) {
            selectTimeButton.setVisibility(View.VISIBLE);
            selectedTimeView.setVisibility(View.VISIBLE);
        } else {
            selectTimeButton.setVisibility(View.GONE);
            selectedTimeView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_time:
                TimePickerDialog timePickerDialog = new TimePickerDialog(RequestServiceActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourofDay, int mint) {
                                //initialize hour and minute
                                hour = hourofDay;
                                minute = mint;
                                String time = hour + ":" + minute;
                                //initialize 24 hours time format
                                SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                                try {
                                    Date date = f24Hours.parse(time);
                                    //initialize 12 hours time format
                                    SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                                    selectedTimeView.setText(f12Hours.format(date));

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 12, 0, false);
                //Set transparent background
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                //Displayed previous selected time
                timePickerDialog.updateTime(hour, minute);
                //show dialog
                timePickerDialog.show();
                break;
            case R.id.requestButton:
                if (!isUserDetailsExists) {
                    startActivity(new Intent(this, PersonalDetailsActivity.class));
                } else {
                    DocumentReference documentReference = firestore.collection("users").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("optionalPhone", optionalPhoneText.getText().toString());
                    user.put("optionalAddress", optionalAddressText.getText().toString());
                    if (timingGroup.getCheckedRadioButtonId() == R.id.immediate) {
                        user.put("timing", "Immediate");
                    } else {
                        user.put("timing", selectedTimeView.getText().toString());
                    }
                    user.put("note", noteText.getText().toString());
                    documentReference.set(user, SetOptions.merge());
                    openDialog();
                }
                break;
        }
    }

    private void openDialog() {
        RequestServiceActivityDialog dialog = new RequestServiceActivityDialog();
        dialog.show(getSupportFragmentManager(), "Congratulation Dialog");
    }
}