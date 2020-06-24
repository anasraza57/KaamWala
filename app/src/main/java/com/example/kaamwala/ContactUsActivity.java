package com.example.kaamwala;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Objects;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.contact_us);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}