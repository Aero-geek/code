package com.example.invoicemaker.utils;

import android.app.ListActivity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.invoicemaker.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class FilePickerActivity extends ListActivity {
    public static final String EXTRA_ACCEPTED_FILE_EXTENSIONS = "accepted_file_extensions";
    public static final String EXTRA_FILE_PATH = "file_path";
    public static final String EXTRA_SHOW_HIDDEN_FILES = "show_hidden_files";
    private static final String DEFAULT_INITIAL_DIRECTORY = "/sdcard/";
    protected String[] acceptedFileExtensions;
    protected FilePickerListAdapter mAdapter;
    protected File mDirectory;
    protected ArrayList<File> mFiles;
    protected boolean mShowHiddenFiles = false;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        View inflate = ((LayoutInflater) getSystemService("layout_inflater")).inflate(R.layout.file_picker_empty_view, null);
        ((ViewGroup) getListView().getParent()).addView(inflate);
        getListView().setEmptyView(inflate);
        StringBuilder stringBuilder = new StringBuilder();
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        String parentpath = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
        stringBuilder.append(parentpath);
        stringBuilder.append("/Invoice");
        if (new File(stringBuilder.toString()).exists()) {
            stringBuilder = new StringBuilder();

            stringBuilder.append(parentpath);
            stringBuilder.append("/Invoice/backup");
            if (new File(stringBuilder.toString()).exists()) {
                this.mDirectory = new File("/sdcard/Invoice/backup/");
            } else {
                this.mDirectory = new File("/sdcard/Invoice/");
            }
        } else {
            this.mDirectory = new File(DEFAULT_INITIAL_DIRECTORY);
        }
        this.mFiles = new ArrayList();
        this.mAdapter = new FilePickerListAdapter(this, this.mFiles);
        setListAdapter(this.mAdapter);
        this.acceptedFileExtensions = new String[0];
        if (getIntent().hasExtra(EXTRA_FILE_PATH)) {
            this.mDirectory = new File(getIntent().getStringExtra(EXTRA_FILE_PATH));
        }
        if (getIntent().hasExtra(EXTRA_SHOW_HIDDEN_FILES)) {
            this.mShowHiddenFiles = getIntent().getBooleanExtra(EXTRA_SHOW_HIDDEN_FILES, false);
        }
        if (getIntent().hasExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS)) {
            ArrayList stringArrayListExtra = getIntent().getStringArrayListExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS);
            this.acceptedFileExtensions = (String[]) stringArrayListExtra.toArray(new String[stringArrayListExtra.size()]);
        }
    }

    protected void onResume() {
        refreshFilesList();
        super.onResume();
    }

    protected void refreshFilesList() {
        this.mFiles.clear();
        File[] listFiles = this.mDirectory.listFiles(new ExtensionFilenameFilter(this.acceptedFileExtensions));
        if (listFiles != null && listFiles.length > 0) {
            for (File file : listFiles) {
                if (!file.isHidden() || this.mShowHiddenFiles) {
                    this.mFiles.add(file);
                }
            }
            Collections.sort(this.mFiles, new FileComparator());
        }
        this.mAdapter.notifyDataSetChanged();
    }

    public void onBackPressed() {
        if (this.mDirectory.getParentFile() != null) {
            this.mDirectory = this.mDirectory.getParentFile();
            refreshFilesList();
            return;
        }
        super.onBackPressed();
    }

    protected void onListItemClick(ListView listView, View view, int i, long j) {
        File file = (File) listView.getItemAtPosition(i);
        if (file.isFile()) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_FILE_PATH, file.getAbsolutePath());
            setResult(-1, intent);
            finish();
        } else {
            this.mDirectory = file;
            refreshFilesList();
        }
        super.onListItemClick(listView, view, i, j);
    }

    private class ExtensionFilenameFilter implements FilenameFilter {
        private String[] mExtensions;

        public ExtensionFilenameFilter(String[] strArr) {
            this.mExtensions = strArr;
        }

        public boolean accept(File file, String str) {
            if (new File(file, str).isDirectory() || this.mExtensions == null || this.mExtensions.length <= 0) {
                return true;
            }
            for (String endsWith : this.mExtensions) {
                if (str.endsWith(endsWith)) {
                    return true;
                }
            }
            return false;
        }
    }

    private class FileComparator implements Comparator<File> {
        private FileComparator() {
        }

        public int compare(File file, File file2) {
            if (file == file2) {
                return 0;
            }
            if (file.isDirectory() && file2.isFile()) {
                return -1;
            }
            if (file.isFile() && file2.isDirectory()) {
                return 1;
            }
            return file.getName().compareToIgnoreCase(file2.getName());
        }
    }

    private class FilePickerListAdapter extends ArrayAdapter<File> {
        private List<File> mObjects;

        public FilePickerListAdapter(Context context, List<File> list) {
            super(context, R.layout.file_picker_list_item, 16908308, list);
            this.mObjects = list;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.file_picker_list_item, viewGroup, false);
            }
            File file = (File) this.mObjects.get(i);
            ImageView imageView = (ImageView) view.findViewById(R.id.file_picker_image);
            TextView textView = (TextView) view.findViewById(R.id.file_picker_text);
            textView.setSingleLine(true);
            textView.setText(file.getName());
            if (file.isFile()) {
                imageView.setImageResource(R.drawable.file);
            } else {
                imageView.setImageResource(R.drawable.folder);
            }
            return view;
        }
    }
}
