package com.example.invoicemaker;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.invoicemaker.Database.DatabaseHelper;
import com.example.invoicemaker.Fragment.ClientFragment;
import com.example.invoicemaker.Fragment.EstimateFragment;
import com.example.invoicemaker.Fragment.InvoiceFragment;
import com.example.invoicemaker.Fragment.MyItemFragment;
import com.example.invoicemaker.Fragment.ReportFragment;
import com.example.invoicemaker.Fragment.SettingFragment;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.FilePickerActivity;
import com.example.invoicemaker.utils.LockScreen;
import com.example.invoicemaker.utils.MyConstants;
import com.example.invoicemaker.utils.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.infideap.drawerbehavior.Advance3DDrawerLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG_CLIENTS = "tag_clients";
    private static final String TAG_ESTIMATES = "tag_estimates";
    private static final String TAG_HOME = "tag_home";
    private static final String TAG_MY_ITEMS = "tag_my_items";
    private static final String TAG_REPORTS = "tag_reports";
    private static final String TAG_SETTINGS = "tag_settings";
    public static String CURRENT_TAG = "tag_home";
    public static int navItemIndex;
    private static List<String> fileList = new ArrayList<String>();
    FirebaseDatabase database;
    DriveServiceHelper driveServiceHelper;
    private String TAG = "MainActivity";
    private String[] activityTitles;
    private Advance3DDrawerLayout mDrawerLayout;
    private Handler mHandler;
    private NavigationView navigationView;
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Toolbar toolbar;
    private FirebaseAuth auth;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @SuppressLint("ResourceAsColor")
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        LinearLayout bgElement = (LinearLayout) findViewById(R.id.container);
//
//        switch (item.getItemId()){
//            case R.id.add_item_layout:
//                bgElement.setBackgroundColor(R.color.newAppColor);
//                break;
//            case R.id.add_photo_layout:
//                bgElement.setBackgroundColor(R.color.white);
//                break;
//            case R.id.app_bar_layout:
//                bgElement.setBackgroundColor(R.color.background_color);
//                break;
//            case R.id.bottom_layout:
//                bgElement.setBackgroundColor(R.color.newWhiteColor);
//                break;
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private void toggleFab() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        if (AppCore.isNetworkAvailable(MainActivity.this)) {
            addBanner();
        }

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //  requestSignIn();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }


        initLayout();
        if (MyConstants.CATALOG_TYPE == 1) {
            navItemIndex = 1;
            CURRENT_TAG = TAG_ESTIMATES;
            loadHomeFragment();
        } else if (bundle == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
        if (!getIntent().getBooleanExtra("from_app", false)) {
            passwordCheck();
        }

        // requestSignIn();


    }


    public void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    private void passwordCheck() {
        if (SessionManager.getInstance(getApplicationContext()).getPasscode() != -1) {
            Intent intent = new Intent(getApplicationContext(), LockScreen.class);
            intent.addFlags(67141632);
            intent.putExtra("AppPasscode", SessionManager.getInstance(getApplicationContext()).getPasscode());
            intent.putExtra("ScreenType", 2);
            startActivity(intent);
        }
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, MainActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void initLayout() {

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.mHandler = new Handler();
        this.mDrawerLayout = (Advance3DDrawerLayout) findViewById(R.id.drawer_layout);
        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        mDrawerLayout.useCustomBehavior(GravityCompat.START);
        setUpNavigationView();

    }

    private void setUpNavigationView() {
        this.navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_clients:
                        MainActivity.navItemIndex = 4;
                        MainActivity.CURRENT_TAG = MainActivity.TAG_CLIENTS;
                        break;
                    case R.id.nav_estimates:
                        MainActivity.navItemIndex = 1;
                        MainActivity.CURRENT_TAG = MainActivity.TAG_ESTIMATES;
                        break;
                    case R.id.nav_invoices:
                        MainActivity.navItemIndex = 0;
                        MainActivity.CURRENT_TAG = MainActivity.TAG_HOME;
                        break;
                    case R.id.nav_my_items:
                        MainActivity.navItemIndex = 3;
                        MainActivity.CURRENT_TAG = MainActivity.TAG_MY_ITEMS;
                        break;
                    case R.id.nav_reports:
                        MainActivity.navItemIndex = 2;
                        MainActivity.CURRENT_TAG = MainActivity.TAG_REPORTS;
                        break;
                    case R.id.nav_settings:
                        MainActivity.navItemIndex = 5;
                        MainActivity.CURRENT_TAG = MainActivity.TAG_SETTINGS;
                        break;
                    case R.id.rateus:
                        rateUs();
                        break;
                    case R.id.moreapp:
                        moreApp();
                        break;
                    default:
                        MainActivity.navItemIndex = 0;
                        MainActivity.CURRENT_TAG = MainActivity.TAG_HOME;
                        break;
                }


                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                startAnotherActivity();
                return true;
            }
        });
        ActionBarDrawerToggle anonymousClass2 = new ActionBarDrawerToggle(this, this.mDrawerLayout, this.toolbar, R.string.openDrawer, R.string.closeDrawer) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View view) {
                hideKeyboard(MainActivity.this);
                super.onDrawerOpened(view);
            }
        };
        this.mDrawerLayout.setDrawerListener(anonymousClass2);
        mDrawerLayout.setRadius(GravityCompat.START, 25);//set end container's corner radius (dimension)
        mDrawerLayout.setViewRotation(GravityCompat.START, 15); // set degree of Y-rotation ( value : 0 -> 45)
        anonymousClass2.syncState();
    }

    public void moreApp() {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/developer?id=" + getString(R.string.moreapp))));
    }

    public void rateUs() {
        String appPackageName = getPackageName();
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void setToolbarTitle() {
        TextView textView = findViewById(R.id.toolbarText);
        textView.setText(this.activityTitles[navItemIndex]);
        //getSupportActionBar().setTitle(this.activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        this.navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                MyConstants.CATALOG_TYPE = 0;
                return new InvoiceFragment();
            case 1:
                MyConstants.CATALOG_TYPE = 1;
                return new EstimateFragment();
            case 2:
                return new ReportFragment();
            case 3:
                return new MyItemFragment();
            case 4:
                return new ClientFragment();
            case 5:
                return new SettingFragment();
            default:
                return new InvoiceFragment();
        }
    }

    private void loadHomeFragment() {
        invalidateOptionsMenu();
        selectNavMenu();
        setToolbarTitle();
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            this.mDrawerLayout.closeDrawers();
            toggleFab();
            return;
        }
        Runnable anonymousClass3 = new Runnable() {
            public void run() {
                Fragment access$100 = MainActivity.this.getHomeFragment();
                FragmentTransaction beginTransaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
                beginTransaction.setCustomAnimations(17432576, 17432577);
                beginTransaction.replace(R.id.content, access$100, MainActivity.CURRENT_TAG);
                beginTransaction.commitAllowingStateLoss();
            }
        };
        if (anonymousClass3 != null) {
            this.mHandler.post(anonymousClass3);
        }
        toggleFab();
        this.mDrawerLayout.closeDrawers();
    }

    public void onClick(View view) {
        view.getId();
    }

    public void onBackPressed() {

        hideKeyboard(MainActivity.this);
        if (this.mDrawerLayout.isDrawerOpen((int) GravityCompat.START)) {
            this.mDrawerLayout.closeDrawers();
        } else if (!this.shouldLoadHomeFragOnBackPress || navItemIndex == 0) {
            //super.onBackPressed();
            hideKeyboard(MainActivity.this);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Exit Application?");
            alertDialogBuilder
                    .setMessage("Click yes to exit!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    moveTaskToBack(true);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);
                                }
                            })

                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    protected void onActivityResult(int i, int i2, final Intent intent) {
        super.onActivityResult(i, i2, intent);

        if (i == 100) {
            if (i2 == RESULT_OK) {
                handleSignInData(intent);
            }
        } else if (i == 10) {
            if (i2 == RESULT_OK) {
                requestSignIn();
            }
        }


        if (i == 80 && i2 == -1) {
            importOperation(intent.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));
        } else if (i == 14 && i2 == -1) {
            Uri data = intent.getData();
            if (data != null) {
                final String substring;
                String str;
                Cursor query = getContentResolver().query(data, null, null, null, null);
                if (query == null) {
                    String substring2 = data.getPath().substring(data.getPath().lastIndexOf("/") + 1);
                    substring = data.getPath().substring(data.getPath().lastIndexOf(".") + 1);
                    str = substring2;
                } else {
                    query.moveToFirst();
                    str = query.getString(2);
                    substring = query.getString(4);
                    query.close();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
                builder.setTitle((CharSequence) "Restore");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Do you want to restore from file : ");
                stringBuilder.append(str);
                builder.setMessage(stringBuilder.toString());
                builder.setPositiveButton((CharSequence) "Restore", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (substring.equals("application/x-sqlite3") || substring.equals("bak")) {
                            final ProgressDialog show = ProgressDialog.show(MainActivity.this, MainActivity.this.getString(R.string.app_name), "Proccessing...", true);
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        MainActivity.this.restoreDB(MainActivity.this.getContentResolver().openInputStream(intent.getData()));
                                        ((AppCore) MainActivity.this.getApplication()).init();
                                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                        intent.putExtra("from_app", true);
                                        intent.addFlags(67141632);
                                        MainActivity.this.startActivity(intent);
                                        show.dismiss();
                                        MainActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(MainActivity.this, "File is imported successfully", 0).show();
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            return;
                        }
                        MainActivity.this.showErrorDialog();
                    }
                });
                builder.setNegativeButton((CharSequence) "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.create();
                builder.show();
            }
        }
    }

    public void importOperation(String str) {
        getString(R.string.app_name);
        Boolean.valueOf(true);
        if (getExtension(str).equals("bak")) {
            new updateDatabase().execute(new String[]{str});
            return;
        }
        Toast.makeText(this, "Wrong file is uploaded.", Toast.LENGTH_SHORT).show();
    }

    private String getExtension(String str) {
        return str.contains(".") ? str.substring(str.lastIndexOf(46) + 1) : null;
    }

    private void restoreDB(InputStream inputStream) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(getDatabasePath(DatabaseHelper.DATABASE_NAME).toString());
        while (true) {
            int read = inputStream.read();
            if (read != -1) {
                fileOutputStream.write(read);
            } else {
                inputStream.close();
                fileOutputStream.close();
                return;
            }
        }
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle((CharSequence) "Restore");
        builder.setMessage((CharSequence) "You have chosen wrong file! Please select a valid file to restore.");
        builder.create();
        builder.show();
    }

    private void startAnotherActivity() {
        if ((CURRENT_TAG == TAG_ESTIMATES || CURRENT_TAG == TAG_REPORTS || CURRENT_TAG == TAG_SETTINGS)) {
            loadHomeFragment();


        } else {
            loadHomeFragment();
        }
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

// autosync code

    private void handleSignInData(Intent data) {

        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(MainActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));

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

    class updateDatabase extends AsyncTask<String, Void, Boolean> {
        ProgressDialog progDailog;

        updateDatabase() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.progDailog = ProgressDialog.show(MainActivity.this, MainActivity.this.getString(R.string.app_name), "Proccessing...", true);
        }

        protected Boolean doInBackground(String... strArr) {
            boolean copyDbOperation;
            try {
                copyDbOperation = new DatabaseHelper(MainActivity.this).copyDbOperation(strArr[0], MainActivity.this.getDatabasePath(DatabaseHelper.DATABASE_NAME).toString());
            } catch (Exception e) {
                e.printStackTrace();
                copyDbOperation = false;
            }
            return Boolean.valueOf(copyDbOperation);
        }

        protected void onPostExecute(final Boolean bool) {
            super.onPostExecute(bool);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (bool.booleanValue()) {
                        Toast.makeText(MainActivity.this, "File is imported successfully", Toast.LENGTH_SHORT).show();
                        ((AppCore) MainActivity.this.getApplication()).init();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.putExtra("from_app", true);
                        intent.addFlags(67141632);
                        MainActivity.this.startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                    updateDatabase.this.progDailog.dismiss();
                }
            }, 2000);
        }
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

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Backup to your Google Drive");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            // Dirve service mathi folder name get krse j folder drive ma hase
            String folderid = driveServiceHelper.ListFolder(MainActivity.this);
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

                driveServiceHelper.createFilePDF(MainActivity.this, filepath)
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                progressDialog.dismiss();


                                Log.e("OnSSsss", "=====" + s);
                                Toast.makeText(MainActivity.this, "Backup Successfull!!", Toast.LENGTH_SHORT).show();


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
                                Toast.makeText(MainActivity.this, "Check your google drive api key", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }


}
