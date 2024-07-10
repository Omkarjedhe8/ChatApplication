package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    TextView textView;

    EditText email,password;
    Button btn ,resbtn;
    FirebaseAuth auth;
    ProgressDialog progressDialog;

    RecyclerView recyclerView;



      String  emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textView=findViewById(R.id.textViewLogin);
        email=findViewById(R.id.editTextTextEmailAddressLogin);
        password=findViewById(R.id.editTextTextPasswordLogin);
        btn=findViewById(R.id.buttonLogin);
        resbtn=findViewById(R.id.buttonR);
        auth= FirebaseAuth.getInstance();
        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Plz wait ...");
        progressDialog.setCancelable(false);
     //   recyclerView=findViewById(R.id.mainUserRecycleerView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email=email.getText().toString();
                String Pass=password.getText().toString();

  /* // This for if enter empty email
*/
                if ((TextUtils.isEmpty(Email))){
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Enter a Email", Toast.LENGTH_SHORT).show();
                } 
                else if((TextUtils.isEmpty(Pass))){
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Enter a Password", Toast.LENGTH_SHORT).show();
                }
/*
    Email pattern checking
*/
                else if (!Email.matches(emailPattern)) {
                    progressDialog.dismiss();
                    email.setError("Give Prpper Email");
                }
/*
    Password Checking
*/
                else if (Pass.length()<6) {
                    progressDialog.dismiss();
                    password.setError("Enter password Longer than 6 characters");
                }

                else{
                    auth.signInWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                               // progressDialog.show();
                                try {
                                    Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }catch( Exception e)
                                {
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }else
                            {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }



            }
        });

        resbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, Register.class);
                startActivity(intent);
                finish();
            }
        });




    }
}