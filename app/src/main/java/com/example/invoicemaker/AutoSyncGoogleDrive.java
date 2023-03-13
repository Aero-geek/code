package com.example.invoicemaker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

public class AutoSyncGoogleDrive {

    private static List<String> fileList = new ArrayList<String>();
    FirebaseDatabase database;
    Button signinBt;
    GoogleSignInClient mGoogleSignInClient;
    DriveServiceHelper driveServiceHelper;
    Context context;
    Activity activity;
    private FirebaseAuth auth;

    public AutoSyncGoogleDrive(Context context) {
        this.context = context;
        activity = (Activity) context;
        requestSignIn();
        ContextWrapper contextWrapper = new ContextWrapper(context.getApplicationContext());
        String parentpath = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
        File root = new File(parentpath + "/invoice/");
        Sync(root);
    }

    public void Sync(File f) {
        File[] files = f.listFiles();
        fileList.clear();
        for (File file : files) {
            fileList.add(file.getPath());
            UploadPDFFile(file.getPath());
            Log.e("filelist", "" + file.getPath());
        }
    }

    private void requestSignIn() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        activity.startActivityForResult(mGoogleSignInClient.getSignInIntent(), 1);
    }

    /*  @Override
      protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
          super.onActivityResult(requestCode, resultCode, data);

          switch (requestCode) {
              case 1:
                  if (requestCode == 1) {
                      handleSignInClient(data);
                  }
                  break;
          }
      }
  */
    private void handleSignInClient(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {

                        GoogleAccountCredential credential = GoogleAccountCredential
                                .usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE_FILE));

                        credential.setSelectedAccount(googleSignInAccount.getAccount());
                        Drive googleDriveService = new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName("My Invoices")
                                .build();
                        driveServiceHelper = new DriveServiceHelper(googleDriveService);
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "onFailure: " + e.getMessage());
                    }
                });

    }

    public void UploadPDFFile(String filepath) {
        new ListFileAsynctas(filepath).execute();
    }


    public class ListFileAsynctas extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;
        String filepath;
        String fileexit;

        public ListFileAsynctas(String filepath) {


            this.filepath = filepath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading to Google Drive");
            progressDialog.setMessage("Plese wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            String folderid = driveServiceHelper.ListFolder(context);
            Log.e("doInBackground", "=============" + folderid);


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
                            progressDialog.dismiss();
                        }
                    });
                }

            } else {

                driveServiceHelper.createFilePDF(context, filepath)
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                progressDialog.dismiss();


                                Log.e("OnSSsss", "=====" + s);
                                Toast.makeText(context, "Backup Successfull!!", Toast.LENGTH_SHORT).show();


                                driveServiceHelper.uploadFile(new java.io.File(filepath).getName(), new java.io.File(filepath), s);

                                progressDialog.dismiss();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Log.e("TAG", "onFailure: " + e.getMessage());

                                progressDialog.dismiss();
                                Toast.makeText(context, "Check your google drive api key", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

}
