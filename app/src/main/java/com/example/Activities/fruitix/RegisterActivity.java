package com.example.Activities.fruitix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Activities.fruitix.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    ImageView ImgUserPhoto;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;


    private EditText regName, regEmail, regPass, regPass2;

    private Button regBtn;
    TextView haveAccount;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Register");



        setContentView(R.layout.activity_register);
        regName = findViewById(R.id.register_name);
        regEmail = findViewById(R.id.register_email);
        regPass = findViewById(R.id.register_password);
        regPass2 = findViewById(R.id.register_password2);
        regBtn = findViewById(R.id.register_button);


        mAuth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regBtn.setVisibility(View.VISIBLE);

                final String email = regEmail.getText().toString();
                final String password = regPass.getText().toString();
                final String password2 = regPass2.getText().toString();
                final String name = regName.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                    regName.setError("This field is required");
                    regEmail.setError("This field is required");
                    regPass.setError("This field is required");
                    regPass2.setError("This field is required");
                    return;
                }
                if (email.isEmpty()){
                    regEmail.setError("Email is required");
                    return;
                }
                if (password.isEmpty()) {
                    regPass.setError("Password is required");
                    return;
                }
                if (!password.equals(password2)) {
                    regPass2.setError("Password not match");
                }
                if (password.length() < 6) {
                    regPass.setError("password should be 6 characters long");
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    regEmail.setError("Please Provide Valid E-mail");
                    return;



                }
                else{
                    CreateUserAccount(email,name,password);
                }






            }
        });


       haveAccount  = findViewById(R.id.haveAccount);

        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        ImgUserPhoto = findViewById(R.id.register_userPhoto);

        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 22){

                    checkAndRequestForPermission();
                }else{
                    openGallery();
                }




            }
        });
    }


    private void CreateUserAccount(String email, String name, String password) {


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User user = new User(name, email);
                            FirebaseDatabase.getInstance("https://fruitix-d0a2c-default-rtdb.firebaseio.com/").getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                            if (pickedImgUri != null) {
                                showMessage("Account Successfully Created");
                                updateUserInfo(name ,pickedImgUri,mAuth.getCurrentUser());
                            }else {
                                updateUserInfoWithoutPhoto(name,mAuth.getCurrentUser());
                            }




                        }else{
                            showMessage("Account Creation Failed!" + task.getException().getMessage());
                            regBtn.setVisibility(View.VISIBLE);

                        }
                    }
                });




    }
    private void updateUserInfo(String name, Uri pickedImgUri, FirebaseUser currentUser) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photo");
        StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();


                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            showMessage("Register Complete!");
                                            updateUI();
                                        }


                                    }
                                });


                    }
                });
            }
        });


    }
    private void updateUserInfoWithoutPhoto(String name,  FirebaseUser currentUser) {


        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();


        currentUser.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            showMessage("Register Complete!");
                            updateUI();
                        }


                    }
                });


    }






    private void updateUI() {


        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeActivity);
        finish();


    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);



    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(RegisterActivity.this, "Please Accept for required permission", Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);
            }
        }
        else
            openGallery();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null){


            pickedImgUri = data.getData();
            ImgUserPhoto.setImageURI(pickedImgUri);

        }
    }
}
