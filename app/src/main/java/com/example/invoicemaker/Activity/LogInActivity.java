package com.example.invoicemaker.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.invoicemaker.DriveServiceHelper;
import com.example.invoicemaker.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogInActivity extends AppCompatActivity {

    private static final String TAG = "Login Activity";
    FirebaseDatabase database;
    Button signinBt;
    GoogleSignInClient mGoogleSignInClient;
    DriveServiceHelper driveServiceHelper;
    List<String> fileList = new ArrayList<String>();
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        signinBt = findViewById(R.id.signinBt);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        /*signinBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Upload(filepath);
            }
        });*/

        requestSignIn();

    }

    private void requestSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), 100);

        //driveServiceHelper.createFilePDF(LogInActivity.this, "storage/emulated/0/invoice/");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                handleSignInData(data);
            }
        } else if (requestCode == 14) {
            if (resultCode == RESULT_OK) {
                //GetData(data);
            }
        } else if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                requestSignIn();
            }
        }
    }

    private void handleSignInData(Intent data) {

        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(LogInActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));

                        credential.setSelectedAccount(googleSignInAccount.getAccount());

                        Drive googleDriveService = new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(), credential)
                                .setApplicationName("Drive Demo")
                                .build();


                        driveServiceHelper = new DriveServiceHelper(googleDriveService);

                        //driveServiceHelper.createFilePDF(LogInActivity.this, "storage/emulated/0/invoice/");

                        String path = "storage/emulated/0/myPdf.pdf";

                        File root = new File("storage/emulated/0/invoice/");
                        File[] files = root.listFiles();
                        for (File file : files) {
                            fileList.add(file.getAbsolutePath());
                            Log.e("file path", "" + fileList.size());
                            Log.e("file path", "" + file.getAbsolutePath());
                            Upload(file.getPath());
                            Log.e("filelist", "" + file.getPath());
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e(TAG, "onFailure: Handle Sign In");

                    }
                });
    }

    public void Upload(String filepath) {

        // asyncronized method ma filepath mokle che
        new ListFileAsynctas(filepath).execute();


    }


    public class ListFileAsynctas extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;
        String filepath;
        String fileexit;

        public ListFileAsynctas(String filepath) {

            // filepath globly declare kryo
            this.filepath = filepath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(LogInActivity.this);
            progressDialog.setTitle("Uploading to Google Drive");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            // Dirve service mathi folder name get krse j folder drive ma hase
            String folderid = driveServiceHelper.ListFolder(LogInActivity.this);
            Log.e("doInBackground", "=============" + folderid);


            // jo folder hase to eni ander file save kri dese
            if (!folderid.isEmpty()) {

                fileexit = driveServiceHelper.ListFile(folderid, new File(filepath).getName());
            }

            return folderid;
        }

        @Override
        protected void onPostExecute(String aBoolean) {
            super.onPostExecute(aBoolean);


            if (!aBoolean.isEmpty()) {


                if (!fileexit.isEmpty()) {

                    Log.e("Already", "Uploaded");

                    progressDialog.dismiss();
                } else {

                    driveServiceHelper.uploadFile(new java.io.File(filepath).getName(), new java.io.File(filepath), aBoolean).addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {

                            Log.e("onSuccess", "=============" + s);
                            progressDialog.dismiss();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("onFailure", "=============" + e.toString());
                            Log.e(TAG, "onFailure: OnPostExecute");
                            progressDialog.dismiss();
                        }
                    });
                }

            } else {

                driveServiceHelper.createFilePDF(LogInActivity.this, filepath)
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                progressDialog.dismiss();


                                Log.e("OnSSsss", "=====" + s);
                                Toast.makeText(LogInActivity.this, "Backup Successfull!!", Toast.LENGTH_SHORT).show();


                                driveServiceHelper.uploadFile(new java.io.File(filepath).getName(), new java.io.File(filepath), s);

                                progressDialog.dismiss();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Log.e("TAG", "onFailure: " + e.getMessage());
                                Log.e(TAG, "onFailure: CreatePDF");

                                progressDialog.dismiss();
                                Toast.makeText(LogInActivity.this, "Check your google drive api key", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }


}

