package com.example.garbagecollectionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;

public class LoginActivitiy extends AppCompatActivity {
    private EditText email,password;
    private ProgressBar progressBar;
    private FirebaseAuth authUser;
    private TextView register,reset;
    private String roleVal = "";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activitiy);
        email=findViewById(R.id.editText_login_email);
        password=findViewById(R.id.editText_login_pwd);
        progressBar=findViewById(R.id.progressBar);
        register=findViewById(R.id.textView_register_link);
        reset=findViewById(R.id.textView_forgot_password_link);
        authUser=FirebaseAuth.getInstance();

        ImageView imageViewShowHidePass=findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePass.setImageResource(R.drawable.ic_show_pwd);
        imageViewShowHidePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowHidePass.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePass.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        Button loginButton=findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailVal=email.getText().toString();
                String passwordVal=password.getText().toString();

                if(TextUtils.isEmpty(emailVal)){
                    Toast.makeText(LoginActivitiy.this, "Enter Email Id", Toast.LENGTH_SHORT).show();
                    email.setError("Email Id is required!");
                    email.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher((emailVal)).matches()){
                    Toast.makeText(LoginActivitiy.this, "Enter Valid Email Id", Toast.LENGTH_SHORT).show();
                    email.setError("InValid Email Id!");
                    email.requestFocus();
                }else if(TextUtils.isEmpty(passwordVal)){
                    Toast.makeText(LoginActivitiy.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    password.setError("Password is required!");
                    password.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(emailVal,passwordVal);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivitiy.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivitiy.this,ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String emailVal, String passwordVal) {
        authUser.signInWithEmailAndPassword(emailVal, passwordVal).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser=authUser.getCurrentUser();
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(LoginActivitiy.this,"User Logged In Successfully", Toast.LENGTH_SHORT).show();
                        LogIn(firebaseUser);
                    }else{
                        firebaseUser.sendEmailVerification();
                        authUser.signOut();
                        showAlertDialog();
                    }

                }else{
                    Toast.makeText(LoginActivitiy.this,"User Login Failed", Toast.LENGTH_SHORT).show();
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthInvalidUserException e){
                            email.setError("User Doesn't Exists! Register First");
                            email.requestFocus();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                            password.setError("Invalid Credentials, Check & Re-Enter!");
                            password.requestFocus();
                    }catch(Exception e){
                            Log.e("ERROR",e.getMessage());
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void LogIn(FirebaseUser firebaseUser){
        Log.i("Login","Login");
        getUserRole(firebaseUser);
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivitiy.this);
        builder.setTitle("Email Not Verified!");
        builder.setMessage("You Email is not verified, Please Check your inbox, We've again sent a verification link!");

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

    private void getUserRole(FirebaseUser firebaseUser) {
        Log.i("Login","Login2");
        String userID=firebaseUser.getUid();
        Log.i("Login User Id",userID);
        Log.i("Login","Login3");
        db = FirebaseFirestore.getInstance();
        getUserDocumentById(userID);
        getCollectorDocumentById(userID);
        getAdminDocumentById(userID);
    }

    private void getUserDocumentById(String documentId) {
        Log.i("Login","Login4");

        // Create a reference to the document with the given document ID
        DocumentReference docRef = db.collection("users").document(documentId);

        final DocumentSnapshot[] doc = {null};
        // Get the document snapshot
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.i("Login","Login5");

                if (task.isSuccessful()) {
                    Log.i("Login","Login6");

                    doc[0] = task.getResult();
                    if (doc[0].exists()) {
                        // Document found, you can access the data using document.getData()
                        Log.i("Login","USER ACCOUNT FOUND");
                        Log.i("Login", "Document data: " + doc[0].getData());
                        String authToken=authUser.getUid()+"/User";
                        SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("auth_token", authToken);
                        editor.apply();
                        Log.i("TAG","SAVED SHARED PREFERENCE USER");

                        Intent intent=new Intent(LoginActivitiy.this,user_main.class);
                        startActivity(intent);

                    } else {
                        // Document not found
                        Log.i("Login", "No such document");
                    }
                } else {
                    Log.i("Login", "Failed to get document: " + task.getException());
                }
            }
        });
    }

    private void getCollectorDocumentById(String documentId) {
        Log.i("Login","Login4");

        // Create a reference to the document with the given document ID
        DocumentReference docRef = db.collection("collectors").document(documentId);

        final DocumentSnapshot[] doc = {null};
        // Get the document snapshot
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.i("Login","Login5");

                if (task.isSuccessful()) {
                    Log.i("Login","Login6");

                    doc[0] = task.getResult();
                    if (doc[0].exists()) {
                        // Document found, you can access the data using document.getData()
                        Log.i("Login","Collector ACCOUNT FOUND");
                        Log.i("Login", "Document data: " + doc[0].getData());
                        String authToken=authUser.getUid()+"/Collector";
                        SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("auth_token", authToken);
                        editor.apply();
                        Log.i("TAG","SAVED SHARED PREFERENCE COLLECTOR");
                        Intent intent=new Intent(LoginActivitiy.this,collector_main.class);
                        startActivity(intent);
                    } else {
                        // Document not found
                        Log.i("Login", "No such document");
                    }
                } else {
                    Log.i("Login", "Failed to get document: " + task.getException());
                }
            }
        });
    }


    private void getAdminDocumentById(String documentId) {
        Log.i("Login","Login4");

        // Create a reference to the document with the given document ID
        DocumentReference docRef = db.collection("admin").document(documentId);

        final DocumentSnapshot[] doc = {null};
        // Get the document snapshot
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.i("Login","Login5");

                if (task.isSuccessful()) {
                    Log.i("Login","Login6");

                    doc[0] = task.getResult();
                    if (doc[0].exists()) {
                        // Document found, you can access the data using document.getData()
                        Log.i("Login","Admin ACCOUNT FOUND");
                        Log.i("Login", "Document data: " + doc[0].getData());
                        String authToken=authUser.getUid()+"/Admin";
                        SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("auth_token", authToken);
                        editor.apply();
                        Log.i("TAG","SAVED SHARED PREFERENCE ADMIN");
                        Intent intent=new Intent(LoginActivitiy.this,admin_main.class);
                        startActivity(intent);
                    } else {
                        // Document not found
                        Log.i("Login", "No such document");
                    }
                } else {
                    Log.i("Login", "Failed to get document: " + task.getException());
                }
            }
        });
    }
}