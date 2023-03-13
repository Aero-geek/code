package com.example.invoicemaker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAnalytics firebaseAnalytics;
    FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInButton = findViewById(R.id.signInButton);


        //firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        sharedPreferences = getSharedPreferences("googleClientPref", MODE_PRIVATE);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> complatedTask) {
        try {
            GoogleSignInAccount account = complatedTask.getResult(ApiException.class);
            Toast.makeText(this, "Signed In Success", Toast.LENGTH_SHORT).show();

            progressDialog.setMessage("Please Wait, Fetching Your Data");
            progressDialog.show();

            FirebaseGoogleAuth(account);
        } catch (ApiException e) {
            e.printStackTrace();
            Toast.makeText(this, "Signed In Failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignInActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    Toast.makeText(SignInActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {

            String personName = account.getDisplayName();
            String personFirstName = account.getGivenName();
            String personLastName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personID = account.getId();
            Uri personPhotoURL = account.getPhotoUrl();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name", personName);
            editor.putString("firstName", personFirstName);
            editor.putString("lastName", personLastName);
            editor.putString("email", personEmail);
            editor.putString("id", personID);
            editor.putString("photoURL", String.valueOf(personPhotoURL));
            editor.putBoolean("isSkip", false);
            editor.apply();

            progressDialog.cancel();

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //isSkip = sharedPreferences.getBoolean("isSkip", false);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Please Login", Toast.LENGTH_SHORT).show();
        }
    }
}