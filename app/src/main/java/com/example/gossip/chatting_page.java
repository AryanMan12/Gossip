package com.example.gossip;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.gossip.databinding.ActivityChattingPageBinding;

public class chatting_page extends AppCompatActivity {
    ActivityChattingPageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChattingPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}