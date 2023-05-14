package com.example.garbagecollectionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private Button resetButton;
    private EditText email;
    private ProgressBar progressBar;
    private FirebaseAuth authUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        resetButton=findViewById(R.id.button_password_reset);
        email=findViewById(R.id.editText_password_reset_email);
        progressBar=findViewById(R.id.progressBar);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailVal=email.getText().toString();
                if(TextUtils.isEmpty(emailVal)){
                    Toast.makeText(ForgotPassword.this, "Enter Email Id", Toast.LENGTH_SHORT).show();
                    email.setError("Email is Required!");
                    email.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()){
                    Toast.makeText(ForgotPassword.this, "Enter Valid Email Id", Toast.LENGTH_SHORT).show();
                    email.setError("Invalid Email Id!");
                    email.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(emailVal);
                }
            }
        });
    }

    private void resetPassword(String email) {
        authUser=FirebaseAuth.getInstance();
        authUser.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    showAlertDialog();
                }else{
                    Toast.makeText(ForgotPassword.this,"Something Went Wrong! Please try Again!",Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(ForgotPassword.this);
        builder.setTitle("Password Reset!");
        builder.setMessage("Please Check your inbox, We've again sent a password reset link!");

        builder.setPositiveButton("Open Mail!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}