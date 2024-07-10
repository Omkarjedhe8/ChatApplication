package com.example.chatapplication;

import static com.example.chatapplication.R.id.editTextTextEmailAddressRegister;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity {

     EditText rname,rmail,rrepassword,rpassword;
      CircleImageView rg_profile;
      ProgressDialog progressDialog;
    String  emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
   Button signin, loginb;
     FirebaseAuth auth;
     FirebaseDatabase database;
     FirebaseStorage storage;
    Uri imageURI;
    String imageuri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rname=findViewById(R.id.editTextNameRegister);
        rmail=findViewById(R.id.editTextTextEmailAddressRegister);
        rrepassword=findViewById(R.id.editTextTextRePasswordRegister);
        rpassword=findViewById(R.id.editTextTextPasswordRegister);
        signin=findViewById(R.id.buttonRegister);
        loginb=findViewById(R.id.LoginbuttonResgister);
        rg_profile=findViewById(R.id.profile);
        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Plz wait ...");
        progressDialog.setCancelable(false);


        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

//login button

        loginb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Register.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        //resgister button
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name=rname.getText().toString();
                String Email=rmail.getText().toString();
                String Password= rpassword.getText().toString();
                String REPassword=rrepassword.getText().toString();
                String status="Hello";

                if (TextUtils.isEmpty(Name) || TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password) || TextUtils.isEmpty(REPassword) ){
                    progressDialog.dismiss();
                    Toast.makeText(Register.this, "ENter a Information", Toast.LENGTH_SHORT).show();
                } else if (!Email.matches(emailPattern)) {
                    progressDialog.dismiss();

                    rmail.setError("Type valid email");
                } else if (Password.length()<6) {
                    progressDialog.dismiss();
                    rpassword.setError(" minimum length is 6");
                } else if (!Password.equals(REPassword)) {
                    progressDialog.dismiss();
                    rrepassword.setError("password not mathches");
                }else {
                    auth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();

                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference= database.getReference().child("user").child(id);
                                StorageReference storageReference=storage.getReference().child("Upload").child(id);

                                if(imageURI!=null){
                                    progressDialog.dismiss();
                                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()){
                                              //  progressDialog.dismiss();

                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageuri=uri.toString();
                                                        //Imp error prone
                                                        Users users=new Users(id,Name,Email,Password,imageuri,status);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                progressDialog.show();

                                                                Intent intent = new Intent(Register.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }else {
                                                                Toast.makeText(Register.this, "Error Message", Toast.LENGTH_SHORT).show();
                                                            }
                                                                }
                                                        });
                                                    }
                                                });
                                            }else {
                                                Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }else {
                                    String status= "Welcome";
                                    imageuri="https://firebasestorage.googleapis.com/v0/b/chatapplication-9fc96.appspot.com/o/abccc.jpeg?alt=media&token=08d369ba-5f54-4dc3-99dc-5e08955f7de1";
                                    Users users=new Users(id,Name,Email,Password,imageuri,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(Register.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Toast.makeText(Register.this, "Error Message", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });




                                }
                            }
                        }
                    });

                }

            }
        });



        rg_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),10);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10){
            if (data!=null){
                imageURI = data.getData();
                rg_profile.setImageURI(imageURI);
            }
        }
    }
}