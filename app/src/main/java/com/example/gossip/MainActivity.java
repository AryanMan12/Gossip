package com.example.gossip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemReselectedListener(navListener);
    }
    private BottomNavigationView.OnNavigationItemReselectedListener navListener =
            new BottomNavigationView.OnNavigationItemReselectedListener() {
                @Override
                public void onNavigationItemReselected(@NonNull MenuItem item) {
                    Fragment selectedFrag = null;
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFrag = new home_page();
                            break;
                        case R.id.nav_chat:
                            selectedFrag = new chat_page();
                            break;
                        case R.id.nav_profile:
                            selectedFrag = new profile_page();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, selectedFrag).commit();
                }
            };
}