package com.example.kaamwala;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestServiceActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    RadioGroup phoneRadioGroup, addressRadioGroup, timeRadioGroup;
    RadioButton phoneRadioButton, addressRadioButton;
    TextView selectedTimeView;
    Button selectTimeButton, requestButton, newPhoneButton, newAddressButton;
    EditText descriptionBox, dialogEditText;

    int hour, minute;
    String userID, currentDate;
    Boolean isUserDetailsExists = false;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    String service, date;
    SimpleDateFormat dateFormat;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_service);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            service = bundle.getString("service");
            Objects.requireNonNull(getSupportActionBar()).setTitle(service);
        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Request a Service");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        phoneRadioGroup = findViewById(R.id.phoneRadio);
        addressRadioGroup = findViewById(R.id.addressRadio);
        timeRadioGroup = findViewById(R.id.timeRadio);
        phoneRadioButton = findViewById(R.id.phoneRadioText);
        addressRadioButton = findViewById(R.id.addressRadioText);
        selectedTimeView = findViewById(R.id.selected_time);
        selectTimeButton = findViewById(R.id.select_time);
        requestButton = findViewById(R.id.requestButton);
        descriptionBox = findViewById(R.id.descriptionBox);
        newPhoneButton = findViewById(R.id.newPhone);
        newAddressButton = findViewById(R.id.newAddress);

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
                        phoneRadioButton.setText(auth.getCurrentUser().getPhoneNumber());
                        addressRadioButton.setText(documentSnapshot.getString("address"));
                    }
                }
            });
        }

        selectTimeButton.setOnClickListener(this);
        requestButton.setOnClickListener(this);
        newPhoneButton.setOnClickListener(this);
        newAddressButton.setOnClickListener(this);
        timeRadioGroup.setOnCheckedChangeListener(this);

        Calendar calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
        date = dateFormat.format(calendar.getTime());

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
        if (checkId == R.id.later) {
            selectTimeButton.setVisibility(View.VISIBLE);
            selectedTimeView.setVisibility(View.VISIBLE);
            currentDate = date.substring(0, date.indexOf(" "));
            selectedTimeView.setText(date);
        } else {
            selectTimeButton.setVisibility(View.GONE);
            selectedTimeView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newPhone:
                dialog = new AlertDialog.Builder(this).create();
                dialogEditText = new EditText(this);
                dialogEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                dialog.setTitle("New Phone Number");
                dialog.setView(dialogEditText);

                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final RadioButton radioButton = new RadioButton(getApplicationContext());
                        radioButton.setText(dialogEditText.getText().toString());
                        radioButton.setTextSize(15);
                        ColorStateList colorStateList = new ColorStateList(
                                new int[][]{
                                        new int[]{-android.R.attr.state_checked},
                                        new int[]{android.R.attr.state_checked}
                                },
                                new int[]{

                                        Color.GRAY //disabled
                                        , getResources().getColor(R.color.colorAccent) //enabled
                                }
                        );
                        radioButton.setButtonTintList(colorStateList);
                        phoneRadioGroup.addView(radioButton);
                    }
                });
                dialog.show();
                break;

            case R.id.newAddress:
                dialog = new AlertDialog.Builder(this).create();
                dialogEditText = new EditText(this);
                dialogEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                dialog.setTitle("New Address");
                dialog.setView(dialogEditText);

                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final RadioButton radioButton = new RadioButton(getApplicationContext());
                        radioButton.setText(dialogEditText.getText().toString());
                        radioButton.setTextSize(15);
                        ColorStateList colorStateList = new ColorStateList(
                                new int[][]{
                                        new int[]{-android.R.attr.state_checked},
                                        new int[]{android.R.attr.state_checked}
                                },
                                new int[]{

                                        Color.GRAY //disabled
                                        , getResources().getColor(R.color.colorAccent) //enabled
                                }
                        );
                        radioButton.setButtonTintList(colorStateList);
                        addressRadioGroup.addView(radioButton);
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getText(R.string.search_on_map),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(RequestServiceActivity.this, GoogleMapsActivity.class);
                                startActivityForResult(intent, 100);
                            }
                        });
                dialog.show();
                break;

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
                                    Date selectedDate = f24Hours.parse(time);
                                    //initialize 12 hours time format
                                    SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                                    String selectedTime = f12Hours.format(selectedDate);
                                    String dateNTime = currentDate + " " + selectedTime;
                                    selectedTimeView.setText(dateNTime);

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
                    CollectionReference collectionReference = firestore.collection("users").document(userID)
                            .collection("my_services");
                    Map<String, Object> user = new HashMap<>();
                    user.put("serviceName", service);
                    int selectedRadioButtonId = phoneRadioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = findViewById(selectedRadioButtonId);
                    user.put("optionalPhone", radioButton.getText().toString());
                    selectedRadioButtonId = addressRadioGroup.getCheckedRadioButtonId();
                    radioButton = findViewById(selectedRadioButtonId);
                    user.put("optionalAddress", radioButton.getText().toString());
                    if (timeRadioGroup.getCheckedRadioButtonId() == R.id.immediate) {
                        Date date1 = null;
                        try {
                            date1 = dateFormat.parse(date);
                            user.put("timing", date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Date date = dateFormat.parse(selectedTimeView.getText().toString());
                            user.put("timing", date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    user.put("note", descriptionBox.getText().toString());
                    user.put("status", "Pending");
                    collectionReference.add(user);
                    openDialog();
                }
                break;
        }
    }

    private void openDialog() {
        RequestServiceActivityDialog dialog = new RequestServiceActivityDialog();
        dialog.show(getSupportFragmentManager(), "Congratulation Dialog");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("address")) {
                final RadioButton radioButton = new RadioButton(getApplicationContext());
                radioButton.setText(data.getStringExtra("address"));
                radioButton.setTextSize(15);
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_checked},
                                new int[]{android.R.attr.state_checked}
                        },
                        new int[]{

                                Color.GRAY //disabled
                                , getResources().getColor(R.color.colorAccent) //enabled
                        }
                );
                radioButton.setButtonTintList(colorStateList);
                addressRadioGroup.addView(radioButton);
            }
        }
    }
}