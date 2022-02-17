package com.example.gossip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFrag = null;
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFrag = new home_page();
//                            new databaseHandler().getdata(new databaseHandler.userCallback() {
//                                @Override
//                                public void onCallback(Map userData) {
//                                    System.out.println(userData);
//                                }
//                            }, "VLEwvs2whFszi2523q8e");
                            break;
                        case R.id.nav_chat:
                            selectedFrag = new chat_page();
                            break;
                        case R.id.nav_profile:
                            selectedFrag = new profile_page();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, selectedFrag).commit();
                    return false;
                }
            };
}