package com.example.finalproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private EditText etEmailLog;
    private EditText etPasswordLog;
    private CheckBox chkRemember;
    private Button btnLogin;
    private TextView tvRegister;
    private TextView tvForget;
    private FirebaseAuth firebaseAuth;
    private String email;
    private String password;
    private ProgressDialog progressDialog;
    private final int  RESULT=0;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("general_prefs", MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        if (!isConnected())
        {
           showDialog();
        }
        else {

            progressDialog = new ProgressDialog(MainActivity.this,
                    ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Authenticating...");
            if (sharedPreferences.getBoolean("signin", false) == true) {
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(sharedPreferences.getString("email", ""), sharedPreferences.getString("password", "")).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), SuccessfulActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                });
            }
        }

            chkRemember = findViewById(R.id.chkRemember);
            etEmailLog = findViewById(R.id.etEmailLog);
            etPasswordLog = findViewById(R.id.etPasswordLog);
            btnLogin = findViewById(R.id.btnLogin);
            tvRegister = findViewById(R.id.tvRegister);
            tvForget = findViewById(R.id.tvForget);
            EventHandler eventHandler = new EventHandler();
            btnLogin.setOnClickListener(eventHandler);
            tvRegister.setOnClickListener(eventHandler);
            tvForget.setOnClickListener(eventHandler);

            if (!sharedPreferences.getString("email", "").equals("")) {
                etEmailLog.setText(sharedPreferences.getString("email", ""));
            }

            chkRemember.setChecked(sharedPreferences.getBoolean("checked", false));

            if (chkRemember.isChecked()) {
                etPasswordLog.setText(sharedPreferences.getString("password", ""));
            }




    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!isConnected())
        {
          showDialog();
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        StringBuilder stringBuilder = new StringBuilder();

        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            stringBuilder.append(entry.getKey() + ": " + entry.getValue().toString() + "\n");
        }



    }

    private void customToast(String content)
    {
        Snackbar.make(findViewById(R.id.myCoordinatorLayout), content, Snackbar.LENGTH_LONG)
                .setAction("Dismiss", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
    }

    private boolean isConnected()
    {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private  void showDialog()
    {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("CONNECTION ERROR! Please check your internet conection and try again");
            alertDialogBuilder.setNeutralButton("REFRESH",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                            overridePendingTransition( 0, 0);
                            startActivity(getIntent());
                            overridePendingTransition( 0, 0);

                        }
                    });


            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


    }



    private void getValues()
    {
        email = etEmailLog.getText().toString();
        password = etPasswordLog.getText().toString();

    }

    public boolean v() {
        boolean valid = true;



        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
           etEmailLog.setError("enter a valid email address");
            valid = false;
        } else {
           etEmailLog.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            etPasswordLog.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            etPasswordLog.setError(null);
        }

        return valid;
    }

    private void validate()
    {
       if (v())
        {
            etPasswordLog.setError(null);
            etPasswordLog.setError(null);

            progressDialog.show();


            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        if(firebaseAuth.getCurrentUser().isEmailVerified())
                        {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email",email);
                            editor.putString("password",password);
                            editor.putBoolean("signin",true);
                            editor.apply();
                            finish();
                            Intent intent = new Intent(getApplicationContext(),SuccessfulActivity.class);
                            startActivity(intent);
                        }
                        else
                        {

                                progressDialog.dismiss();

                            customToast("Please verify your email to login.");
                        }

                    }
                    else
                    {

                            progressDialog.dismiss();

                        customToast("Login Failed , Please try again !");
                    }
                }
            });

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        //this will recevice the data from the late activity
        super.onActivityResult(requestCode,resultCode,data);

        //if requestCode= == ??
        if (resultCode==RESULT_OK)
        {
            etEmailLog.setText(data.getStringExtra("email"));
            etPasswordLog.setText(data.getStringExtra("password"));
        }
        else
        {
            //nothing

        }
    }


    class EventHandler implements  View.OnClickListener
    {

        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.btnLogin:
                    if (!isConnected())
                    {
                        showDialog();
                    }else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (chkRemember.isChecked()) {
                            editor.putBoolean("checked", true);
                        } else {
                            editor.putBoolean("checked", false);
                        }
                        editor.apply();
                        getValues();
                        validate();
                    }
                    break;
                case R.id.tvRegister:
                    Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                    startActivityForResult(intent,RESULT);
                    break;
                case R.id.tvForget:
                    Intent intent2 = new Intent(MainActivity.this,ForgetPasswordActivity.class);
                    startActivity(intent2);
                    break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

}

