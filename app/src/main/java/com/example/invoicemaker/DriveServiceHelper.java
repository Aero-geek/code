package com.example.invoicemaker;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {

    private final Executor mExecutors = (Executor) Executors.newSingleThreadExecutor();
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private Drive mDriveService;

    public DriveServiceHelper(Drive mDriveService) {
        this.mDriveService = mDriveService;
    }


    public String ListFile(String folderid, String filename) {


        String pageToken = null;

        String isexit = "";

        do {
            FileList result = null;


            try {
                result = mDriveService.files().list()
                        .setQ("mimeType = 'application/pdf' and '" + folderid + "' in parents")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();


            } catch (IOException e) {
                e.printStackTrace();
            }

            for (File file : result.getFiles()) {
                System.out.printf("Found file: %s (%s)\n",
                        file.getName(), file.getId());


                if (file.getName().equals(filename)) {

                    isexit = file.getId();

                    Log.e("app_name", "=============" + isexit);

                    break;
                } else {

                    isexit = "";
                }

            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);


        return isexit;
    }


    public String ListFolder(Context context) {


        String pageToken = null;

        String isexit = "";

        do {
            FileList result = null;
            try {
                result = mDriveService.files().list()
                        .setQ("mimeType = 'application/vnd.google-apps.folder'")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();


            } catch (IOException e) {
                e.printStackTrace();
            }

            for (File file : result.getFiles()) {
                System.out.printf("Found file: %s (%s)\n",
                        file.getName(), file.getId());


                if (file.getName().equals(context.getString(R.string.app_name))) {

                    isexit = file.getId();

                    Log.e("app_name", "=============" + isexit);

                    break;
                } else {

                    isexit = "";
                }

            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);


        return isexit;


    }

    public Task<String> createFilePDF(Context context, String filePath) {
        return Tasks.call(mExecutors, () -> {

            File fileMetaData = new File();
            fileMetaData.setName(context.getString(R.string.app_name));

            java.io.File file = new java.io.File(filePath);


            FileContent mediaContent = new FileContent("application/*", file);
            fileMetaData.setMimeType("application/vnd.google-apps.folder");

            File myFile = null;
            try {
                myFile = mDriveService.files().create(fileMetaData, mediaContent).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (myFile == null) {
                throw new IOException("Null result");
            }

            return myFile.getId();

        });
    }


    public Task<String> uploadFile(String name, java.io.File fileTest, String folderpath) {

        return Tasks.call(mExecutor, () -> {
            File metadata = new File()
                    .setParents(Collections.singletonList(folderpath))
                    .setMimeType("application/pdf")
                    .setName(name);
            InputStream targetStream = new FileInputStream(fileTest);
            InputStreamContent inputStreamContent = new InputStreamContent("application/pdf", targetStream);
            File googleFile = mDriveService.files().create(metadata, inputStreamContent).execute();
            if (googleFile == null) {
                throw new IOException("Null result when requesting file creation.");
            }
            return googleFile.getId();
        });
    }


}
