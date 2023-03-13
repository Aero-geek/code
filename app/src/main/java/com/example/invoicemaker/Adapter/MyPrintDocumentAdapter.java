package com.example.invoicemaker.Adapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintDocumentInfo.Builder;

import androidx.annotation.RequiresApi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@RequiresApi(api = 19)
public class MyPrintDocumentAdapter extends PrintDocumentAdapter {
    private String filepath;
    private Activity mActivity;

    public MyPrintDocumentAdapter(Activity activity, String str) {
        this.mActivity = activity;
        this.filepath = str;
    }

    public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes2, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {
        if (cancellationSignal.isCanceled()) {
            layoutResultCallback.onLayoutCancelled();
        } else {
            layoutResultCallback.onLayoutFinished(new Builder(this.filepath).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build(), true);
        }
    }

    public void onWrite(PageRange[] pageRangeArr, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {
        FileNotFoundException e;
        Exception e2;
        Throwable th;
        FileNotFoundException e3;
        Exception e4;
        Throwable th2;
        FileInputStream fileInputStream;
        OutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(this.filepath);
            try {
                fileOutputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
                try {
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int read = fileInputStream.read(bArr);
                        if (read > 0) {
                            fileOutputStream.write(bArr, 0, read);
                        } else {
                            writeResultCallback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                            try {
                                fileInputStream.close();
                                fileOutputStream.close();
                                return;
                            } catch (IOException e5) {
                                e5.printStackTrace();
                                return;
                            }
                        }
                    }
                } catch (FileNotFoundException e6) {
                    e = e6;
                    e.printStackTrace();
                    fileInputStream.close();
                    fileOutputStream.close();
                } catch (Exception e7) {
                    e2 = e7;
                    try {
                        e2.printStackTrace();
                        fileInputStream.close();
                        fileOutputStream.close();
                    } catch (Throwable th3) {
                        th = th3;
                        try {
                            fileInputStream.close();
                            fileOutputStream.close();
                        } catch (IOException e8) {
                            e8.printStackTrace();
                        }
                        throw th;
                    }
                }
            } catch (FileNotFoundException e9) {
                e3 = e9;
                fileOutputStream = null;
                e = e3;
                e.printStackTrace();
                fileInputStream.close();
                fileOutputStream.close();
            } catch (Exception e10) {
                e4 = e10;
                fileOutputStream = null;
                e2 = e4;
                e2.printStackTrace();
                fileInputStream.close();
                fileOutputStream.close();
            } catch (Throwable th4) {
                th2 = th4;
                fileOutputStream = null;
                th = th2;
                fileInputStream.close();
                fileOutputStream.close();
                throw th;
            }
        } catch (FileNotFoundException e11) {
            e3 = e11;
            fileInputStream = null;

            e = e3;
            e.printStackTrace();
            try {
                fileInputStream.close();
                fileOutputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } catch (Exception e12) {
            e4 = e12;
            fileInputStream = null;

            e2 = e4;
            e2.printStackTrace();
            try {
                fileInputStream.close();
                fileOutputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (Throwable th5) {
            th2 = th5;
            fileInputStream = null;

            th = th2;
            try {
                fileInputStream.close();
                fileOutputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                throw th;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
