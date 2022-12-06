package com.example.Activities.fruitix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.Activities.fruitix.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountActivity extends AppCompatActivity {

    private Button btnLogout;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private TextView tv_Welcome, tv_FullName, tv_Email;
    private String fullName, email;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getSupportActionBar().setTitle("Account");

        tv_Welcome = findViewById(R.id.tv_show_welcome);
        tv_FullName = findViewById(R.id.tv_show_full_name);
        tv_Email = findViewById(R.id.tv_show_email);
        imageView = findViewById(R.id.iv_profile_dp);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(AccountActivity.this, "Something went wrong",
                    Toast.LENGTH_LONG).show();

        }else{
            showUserProfile(firebaseUser);
        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.account:
                        return true;
                    case R.id.community:
                        startActivity(new Intent(getApplicationContext(), CommunityActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.info:
                        startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }

        });



        btnLogout = findViewById(R.id.btn_logout);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editor = preferences.edit();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.clear();
                editor.commit();

                Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                startActivity(intent);


            }
        });


    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        // Extracting USER reference from DATABASE

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              User readUserDetails = snapshot.getValue(User.class);
              if (readUserDetails != null){
                  fullName = firebaseUser.getDisplayName();
                  email = firebaseUser.getEmail();

                  tv_Welcome.setText("Welcome, " + fullName + "!");
                  tv_FullName.setText(fullName);
                  tv_Email.setText(email);

                  Uri uri = firebaseUser.getPhotoUrl();
                  Glide.with(AccountActivity.this).load(uri).into(imageView);
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountActivity.this, "Something went wrong",
                        Toast.LENGTH_LONG).show();

            }
        });
    }
}