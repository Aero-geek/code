package com.example.invoicemaker.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PrintManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.invoicemaker.Adapter.MyPrintDocumentAdapter;
import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.Contract;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.BusinessDTO;
import com.example.invoicemaker.Dto.CatalogDTO;
import com.example.invoicemaker.Dto.ClientDTO;
import com.example.invoicemaker.Dto.ImageDTO;
import com.example.invoicemaker.Dto.InvoiceShippingDTO;
import com.example.invoicemaker.Dto.ItemAssociatedDTO;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.TabSettings;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.shockwave.pdfium.PdfDocument.Bookmark;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class InvoicePreviewActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {
    public String TAG = "InvoicePreviewActivity";
    Font normalWhite = new Font(FontFamily.HELVETICA, 12.0f, 0, BaseColor.WHITE);
    Integer pageNumber = Integer.valueOf(0);
    String pdfFileName;
    PDFView pdfView;
    BaseFont textBaseFont;
    Font textFont = new Font(FontFamily.TIMES_ROMAN, 14.0f);
    Font titleFont = new Font(FontFamily.TIMES_ROMAN, 18.0f, 1, BaseColor.BLACK);
    int menuoption = 0;
    private BusinessDTO businessDTO;
    private CatalogDTO catalogDTO;
    private String currency_sign;
    private ArrayList<ImageDTO> imageDTOS = new ArrayList();
    private ArrayList<ItemAssociatedDTO> itemAssociatedDTOS;
    private SettingsDTO settingsDTO;
    private Toolbar toolbar;
    private float widthParcentage = 90.0f;

    public static void start(Context context, CatalogDTO catalogDTO) {
        Intent intent = new Intent(context, InvoicePreviewActivity.class);
        intent.putExtra(MyConstants.CATALOG_DTO, catalogDTO);
        context.startActivity(intent);
    }

    private static void requestPermission(final Context context) {
        Activity activity = (Activity) context;
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            new AlertDialog.Builder(context).setMessage(context.getResources().getString(R.string.permission_storage)).setPositiveButton((CharSequence) "ok", new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
                }
            }).setNegativeButton((CharSequence) "no", new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
            return;
        }
        ActivityCompat.requestPermissions(activity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_preview);
        if (AppCore.isNetworkAvailable(InvoicePreviewActivity.this)) {
            addBanner();
        }
        this.itemAssociatedDTOS = new ArrayList();
        getIntentData();
        initLayout();
        loadData();
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            createPDF();
            viewPdf();
            return;
        }
        requestPermission(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preview_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.download) {
            menuoption = 1;
            invoiceOption();
            return true;
        } else if (itemId == R.id.open_in) {
            menuoption = 2;
            invoiceOption();
            return true;
        } else if (itemId == R.id.print) {
            printPDF();
            return true;
        } else if (itemId == R.id.share) {
            menuoption = 4;
            invoiceOption();
            //  sharePdfFile();
            return true;
        } else {
            Log.e("logs", "" + itemId);
            return super.onOptionsItemSelected(menuItem);
        }
    }

    private void invoiceOption() {
        Log.e("TAG", "downloadInvoice: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(InvoicePreviewActivity.this);
        builder.setItems(new String[]{"As PDF", "As JPG"}, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        downloadPDF();
                        break;
                    case 1:
                        pdfToJpgConvertor();
                        downloadJPG();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.create();
        builder.show();
    }

    public void downloadJPG() {
        if (menuoption == 1) {
            Toast.makeText(this, "Download Complete", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        if (menuoption == 2) {
            openJPGFile();
        }
        if (menuoption == 4) {
            shareJPGFile();
        }
    }

    public void downloadPDF() {
        if (menuoption == 1) {
            Toast.makeText(this, "Download Complete", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        if (menuoption == 2) {
            openPdfFile();
        }
        if (menuoption == 4) {
            sharePdfFile();
        }
    }

    public void pdfToJpgConvertor() {
        PdfiumCore pdfiumCore = new PdfiumCore(InvoicePreviewActivity.this);
        int pageNum = 0;
        String catalogName = catalogDTO.getCatalogName();
        if (catalogDTO.getId() == 0) {
            catalogName = MyConstants.getInvoiceName();
        }
        Log.e(TAG, "catalogName: " + catalogName);

        String rootDirectory = MyConstants.getRootDirectory(getApplicationContext());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rootDirectory);
        stringBuilder.append(File.separator);
        stringBuilder.append(catalogName + "_invoice.pdf");
        this.pdfFileName = stringBuilder.toString();

        try {
            com.shockwave.pdfium.PdfDocument pdf = pdfiumCore.newDocument(
                    ParcelFileDescriptor.open(new File(pdfFileName), ParcelFileDescriptor.MODE_READ_WRITE)
            );

            pdfiumCore.openPage(pdf, pageNum);

            int width = pdfiumCore.getPageWidth(pdf, pageNum);
            int height = pdfiumCore.getPageHeight(pdf, pageNum);

            Bitmap cbitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            pdfiumCore.renderPageBitmap(pdf, cbitmap, pageNum, 0, 0, width, height);

            pdfiumCore.closeDocument(pdf);
            ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
            String parentpath = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
            new File(parentpath + "/PDF Reader").mkdirs();
            File outputFile = new File(parentpath + "/invoice", catalogName + "_invoice.jpg");
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            cbitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void openJPGFile() {
        String catalogName = catalogDTO.getCatalogName();
        if (catalogDTO.getId() == 0) {
            catalogName = MyConstants.getInvoiceName();
        }
        String rootDirectory = MyConstants.getRootDirectory(getApplicationContext());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rootDirectory);
        stringBuilder.append(File.separator);
        stringBuilder.append(catalogName + "_invoice.jpg");
        rootDirectory = stringBuilder.toString();
        Intent intent = new Intent("android.intent.action.VIEW");
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(getApplicationContext().getPackageName());
        stringBuilder2.append(".my.package.name.provider");
        intent.setDataAndType(FileProvider.getUriForFile(this, stringBuilder2.toString(), new File(rootDirectory)), "image/*");
        //  intent.setFlags(PropertyOptions.SEPARATE_NODE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Select Application."));
    }

    private void openPdfFile() {
        String catalogName = catalogDTO.getCatalogName();
        if (catalogDTO.getId() == 0) {
            catalogName = MyConstants.getInvoiceName();
        }
        Log.e(TAG, "catalogName: " + catalogName);
        String rootDirectory = MyConstants.getRootDirectory(getApplicationContext());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rootDirectory);
        stringBuilder.append(File.separator);
        stringBuilder.append(catalogName + "_invoice.pdf");
        rootDirectory = stringBuilder.toString();
        Intent intent = new Intent("android.intent.action.VIEW");
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(getApplicationContext().getPackageName());
        stringBuilder2.append(".my.package.name.provider");
        intent.setDataAndType(FileProvider.getUriForFile(this, stringBuilder2.toString(), new File(rootDirectory)), "application/pdf");
        //  intent.setFlags(PropertyOptions.SEPARATE_NODE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Select Application."));
    }

    public void shareJPGFile() {
        String catalogName = catalogDTO.getCatalogName();
        if (catalogDTO.getId() == 0) {
            catalogName = MyConstants.getInvoiceName();
        }
        Log.e(TAG, "catalogName: " + catalogName);
        String rootDirectory = MyConstants.getRootDirectory(getApplicationContext());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rootDirectory);
        stringBuilder.append(File.separator);
        stringBuilder.append(catalogName + "_invoice.jpg");
        rootDirectory = stringBuilder.toString();

        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/jpg");
        intent.putExtra("android.intent.extra.SUBJECT", this.catalogDTO.getCatalogName());
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(getApplicationContext().getPackageName());
        stringBuilder2.append(".my.package.name.provider");
        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, stringBuilder2.toString(), new File(rootDirectory)));
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Select Application."));

    }

    private void sharePdfFile() {
        String catalogName = catalogDTO.getCatalogName();
        if (catalogDTO.getId() == 0) {
            catalogName = MyConstants.getInvoiceName();
        }
        Log.e(TAG, "catalogName: " + catalogName);
        String rootDirectory = MyConstants.getRootDirectory(getApplicationContext());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rootDirectory);
        stringBuilder.append(File.separator);
        stringBuilder.append(catalogName + "_invoice.pdf");
        rootDirectory = stringBuilder.toString();

        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("message/rfc822");
        intent.putExtra("android.intent.extra.SUBJECT", this.catalogDTO.getCatalogName());
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(getApplicationContext().getPackageName());
        stringBuilder2.append(".my.package.name.provider");
        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, stringBuilder2.toString(), new File(rootDirectory)));
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Select Application."));
    }

    private void printPDF() {
        String catalogName = catalogDTO.getCatalogName();
        if (catalogDTO.getId() == 0) {
            catalogName = MyConstants.getInvoiceName();
        }
        Log.e(TAG, "catalogName: " + catalogName);
        if (VERSION.SDK_INT >= 19) {
            PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getString(R.string.app_name));
            stringBuilder.append(" Document");
            String stringBuilder2 = stringBuilder.toString();
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(MyConstants.getRootDirectory(getApplicationContext()));
            stringBuilder3.append(File.separator);
            stringBuilder3.append(catalogName + "_invoice.pdf");
            printManager.print(stringBuilder2, new MyPrintDocumentAdapter(this, stringBuilder3.toString()), null);
        }
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, InvoicePreviewActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void loadData() {
        this.businessDTO = LoadDatabase.getInstance().getBusinessInformation();
        loadInvoiceItems();
    }

    private void loadInvoiceItems() {
        this.itemAssociatedDTOS.clear();
        Collection invoiceItems = LoadDatabase.getInstance().getInvoiceItems(this.catalogDTO.getId());
        if (invoiceItems != null && invoiceItems.size() > 0) {
            this.itemAssociatedDTOS.addAll(invoiceItems);
        }
    }

    private void getIntentData() {
        this.catalogDTO = (CatalogDTO) getIntent().getSerializableExtra(MyConstants.CATALOG_DTO);
        this.settingsDTO = SettingsDTO.getSettingsDTO();
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Preview");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.pdfView = (PDFView) findViewById(R.id.pdfView);
        this.currency_sign = MyConstants.formatCurrency(this, this.settingsDTO.getCurrencyFormat());
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (i != 101) {
            super.onRequestPermissionsResult(i, strArr, iArr);
        } else if (iArr.length != 0 && iArr[0] == 0) {
            createPDF();
            viewPdf();
        }
    }

    private boolean checkString(String str) {
        return !TextUtils.isEmpty(str);
    }


    private void createPDF() {
        FileOutputStream fileOutputStream = null;
        Document document = null;
        Exception exception;
        Throwable th;

        //PdfPTable pdfPTable;

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setPercentage(this.widthParcentage);
        lineSeparator.setOffset(20.0f);
        String catalogName = catalogDTO.getCatalogName();
        if (catalogDTO.getId() == 0) {
            catalogName = MyConstants.getInvoiceName();
        }
        Log.e(TAG, "catalogName: " + catalogName);

        try {
            String rootDirectory = MyConstants.getRootDirectory(getApplicationContext());
            File fil = new File(rootDirectory);
            if (!fil.exists()) {
                fil.mkdirs();
                Log.e(TAG, "createPDF: mkdir 1 ");
            }
            Log.e(TAG, "createPDF: 1");
            StringBuilder stringBuilder = new StringBuilder();
            Log.e(TAG, "createPDF: 2");
            stringBuilder.append(rootDirectory);
            Log.e(TAG, "createPDF: 3");
            stringBuilder.append(File.separator);
            Log.e(TAG, "createPDF: 4");
            stringBuilder.append(catalogName + "_invoice.pdf");
            Log.e(TAG, "createPDF: 5");
            fileOutputStream = new FileOutputStream(new File(stringBuilder.toString()));
            Log.e(TAG, "createPDF: 6");
            Log.e(TAG, "createPDF: mkdir 2");
            document = new Document();
            try {
                PdfPTable pdfPTable;

                PdfPTable pdfPTable2;
                StringBuilder stringBuilder2;
                PdfPTable pdfPTable3;
                PdfPCell pdfPCell = null;
                PdfPCell pdfPCell2;
                StringBuilder stringBuilder3;
                document.setMargins(0.0f, 0.0f, TabSettings.DEFAULT_TAB_INTERVAL, TabSettings.DEFAULT_TAB_INTERVAL);
                PdfWriter.getInstance(document, fileOutputStream);
                document.open();
                textBaseFont = BaseFont.createFont("assets/nexa_regular.otf", BaseFont.IDENTITY_H, true);
                textFont = new Font(textBaseFont, 14.0f);
                int i = 0;
                if (businessDTO != null) {
                    pdfPTable = new PdfPTable(2);
                    pdfPTable.getDefaultCell().setBorder(0);
                    PdfPTable pdfPTable4 = new PdfPTable(1);
                    pdfPTable4.getDefaultCell().setBorder(0);
                    pdfPTable2 = new PdfPTable(1);
                    pdfPTable2.getDefaultCell().setBorder(0);
                    if (checkString(businessDTO.getName())) {
                        pdfPTable4.addCell(new PdfPCell(new Paragraph(businessDTO.getName(), titleFont))).setBorder(0);
                    }
                    if (checkString(businessDTO.getRegNo())) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Registration Number : ");
                        stringBuilder2.append(businessDTO.getRegNo());
                        pdfPTable4.addCell(new PdfPCell(new Paragraph(stringBuilder2.toString(), textFont))).setBorder(0);
                    }
                    if (checkString(businessDTO.getLine1())) {
                        pdfPTable4.addCell(new PdfPCell(new Paragraph(businessDTO.getLine1(), textFont))).setBorder(0);
                    }
                    if (checkString(businessDTO.getLine2())) {
                        pdfPTable4.addCell(new PdfPCell(new Paragraph(businessDTO.getLine2(), textFont))).setBorder(0);
                    }
                    if (checkString(businessDTO.getLine3())) {
                        pdfPTable4.addCell(new PdfPCell(new Paragraph(businessDTO.getLine3(), textFont))).setBorder(0);
                    }
                    if (checkString(businessDTO.getPhoneNo())) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Phone: ");
                        stringBuilder2.append(businessDTO.getPhoneNo());
                        pdfPTable4.addCell(new PdfPCell(new Paragraph(stringBuilder2.toString(), textFont))).setBorder(0);
                    }
                    if (checkString(businessDTO.getMobileNo())) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Mobile: ");
                        stringBuilder2.append(businessDTO.getMobileNo());
                        pdfPTable4.addCell(new PdfPCell(new Paragraph(stringBuilder2.toString(), textFont))).setBorder(0);
                    }
                    if (checkString(businessDTO.getFax())) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Fax: ");
                        stringBuilder2.append(businessDTO.getFax());
                        pdfPTable4.addCell(new PdfPCell(new Paragraph(stringBuilder2.toString(), textFont))).setBorder(0);
                    }
                    if (checkString(businessDTO.getEmail())) {
                        pdfPTable4.addCell(new PdfPCell(new Paragraph(businessDTO.getEmail(), textFont))).setBorder(0);
                    }
                    if (checkString(businessDTO.getWebsite())) {
                        pdfPTable4.addCell(new PdfPCell(new Paragraph(businessDTO.getWebsite(), textFont))).setBorder(0);
                    }
                    if (checkString(businessDTO.getLogo())) {
                        if (new File(businessDTO.getLogo()).exists()) {
                            Image instance = Image.getInstance(businessDTO.getLogo());
                            pdfPTable3 = new PdfPTable(2);
                            pdfPCell = new PdfPCell();
                            pdfPCell.setBorder(0);
                            pdfPCell2 = new PdfPCell(instance, true);
                            pdfPCell2.setBorder(0);
                            pdfPTable3.addCell(pdfPCell);
                            pdfPTable3.addCell(pdfPCell2);
                            pdfPTable2.addCell(pdfPTable3);
                        } else {
                            pdfPTable2.addCell(new PdfPCell()).setBorder(0);
                        }
                    }
                    pdfPTable.addCell(pdfPTable4);
                    pdfPTable.addCell(pdfPTable2);
                    pdfPTable.setWidthPercentage(widthParcentage);
                    pdfPTable.setSpacingAfter(40.0f);
                    document.add(pdfPTable);
                    document.add(lineSeparator);
                }

                pdfPTable = new PdfPTable(3);
                pdfPTable.getDefaultCell().setBorder(0);
                pdfPTable2 = new PdfPTable(1);
                pdfPTable2.getDefaultCell().setBorder(0);
                PdfPTable pdfPTable5 = new PdfPTable(1);
                pdfPTable5.getDefaultCell().setBorder(0);
                pdfPTable3 = new PdfPTable(1);
                pdfPTable3.getDefaultCell().setBorder(0);
                if (catalogDTO.getClientDTO().getId() > 0) {
                    ClientDTO clientDTO = catalogDTO.getClientDTO();
                    pdfPCell2 = new PdfPCell();
                    pdfPCell2.setHorizontalAlignment(0);
                    pdfPCell2.setBorder(0);
                    pdfPCell2.addElement(new Paragraph("Bill To", titleFont));
                    pdfPTable2.addCell(pdfPCell2);
                    PdfPCell pdfPCell3 = new PdfPCell(new Paragraph(clientDTO.getClientName()));
                    pdfPCell3.setHorizontalAlignment(0);
                    pdfPCell3.setBorder(0);
                    pdfPTable2.addCell(pdfPCell3);
                    if (checkString(clientDTO.getContactAdress())) {
                        pdfPCell3 = new PdfPCell(new Paragraph(clientDTO.getContactAdress()));
                        pdfPCell3.setHorizontalAlignment(0);
                        pdfPCell3.setBorder(0);
                        pdfPTable2.addCell(pdfPCell3);
                    }
                    if (checkString(clientDTO.getAddressLine1())) {
                        pdfPCell3 = new PdfPCell(new Paragraph(clientDTO.getAddressLine1()));
                        pdfPCell3.setHorizontalAlignment(0);
                        pdfPCell3.setBorder(0);
                        pdfPTable2.addCell(pdfPCell3);
                    }
                    if (checkString(clientDTO.getAddressLine2())) {
                        pdfPCell3 = new PdfPCell(new Paragraph(clientDTO.getAddressLine2()));
                        pdfPCell3.setHorizontalAlignment(0);
                        pdfPCell3.setBorder(0);
                        pdfPTable2.addCell(pdfPCell3);
                    }
                    if (checkString(clientDTO.getAddressLine3())) {
                        pdfPCell3 = new PdfPCell(new Paragraph(clientDTO.getAddressLine3()));
                        pdfPCell3.setHorizontalAlignment(0);
                        pdfPCell3.setBorder(0);
                        pdfPTable2.addCell(pdfPCell3);
                    }
                    if (checkString(clientDTO.getEmailAddress())) {
                        pdfPCell3 = new PdfPCell(new Paragraph(clientDTO.getEmailAddress()));
                        pdfPCell3.setHorizontalAlignment(0);
                        pdfPCell3.setBorder(0);
                        pdfPTable2.addCell(pdfPCell3);
                    }
                    if (checkString(clientDTO.getShippingAddress()) || checkString(clientDTO.getShippingLine1()) || checkString(clientDTO.getShippingLine2()) || checkString(clientDTO.getShippingLine3())) {
                        pdfPCell3 = new PdfPCell();
                        pdfPCell3.setHorizontalAlignment(0);
                        pdfPCell3.setBorder(0);
                        pdfPCell3.addElement(new Paragraph("Ship To", titleFont));
                        pdfPTable5.addCell(pdfPCell3);
                    }
                    if (checkString(clientDTO.getShippingAddress())) {
                        pdfPCell3 = new PdfPCell(new Paragraph(clientDTO.getShippingAddress(), textFont));
                        pdfPCell3.setHorizontalAlignment(0);
                        pdfPCell3.setBorder(0);
                        pdfPTable5.addCell(pdfPCell3);
                    }
                    if (checkString(clientDTO.getShippingLine1())) {
                        pdfPCell3 = new PdfPCell(new Paragraph(clientDTO.getShippingLine1(), textFont));
                        pdfPCell3.setHorizontalAlignment(0);
                        pdfPCell3.setBorder(0);
                        pdfPTable5.addCell(pdfPCell3);
                    }
                    if (checkString(clientDTO.getShippingLine2())) {
                        pdfPCell3 = new PdfPCell(new Paragraph(clientDTO.getShippingLine2(), textFont));
                        pdfPCell3.setHorizontalAlignment(0);
                        pdfPCell3.setBorder(0);
                        pdfPTable5.addCell(pdfPCell3);
                    }
                    if (checkString(clientDTO.getShippingLine3())) {
                        pdfPCell3 = new PdfPCell(new Paragraph(clientDTO.getShippingLine3(), textFont));
                        pdfPCell3.setHorizontalAlignment(0);
                        pdfPCell3.setBorder(0);
                        pdfPTable5.addCell(pdfPCell3);
                    }
                }
                String str = "Invoice    ";
                String catalogName1 = catalogDTO.getCatalogName();
                if (MyConstants.CATALOG_TYPE == 1) {
                    str = "Estimate    ";
                }
                if (catalogDTO.getId() == 0) {
                    catalogName1 = MyConstants.getInvoiceName();
                }
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append(str);
                stringBuilder4.append(catalogName1);
                PdfPCell pdfPCell4 = new PdfPCell(new Paragraph(stringBuilder4.toString(), titleFont));
                pdfPCell4.setHorizontalAlignment(2);
                pdfPCell4.setBorder(0);
                pdfPTable3.addCell(pdfPCell4);
                str = MyConstants.formatDate(InvoicePreviewActivity.this, Long.parseLong(catalogDTO.getCreationDate()), settingsDTO.getDateFormat());
                if (catalogDTO.getId() == 0) {
                    str = MyConstants.formatDate(InvoicePreviewActivity.this, Calendar.getInstance().getTimeInMillis(), settingsDTO.getDateFormat());
                }
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Date   ");
                stringBuilder2.append(str);
                PdfPCell pdfPCell5 = new PdfPCell(new Paragraph(stringBuilder2.toString(), textFont));
                pdfPCell5.setHorizontalAlignment(2);
                pdfPCell5.setBorder(0);
                pdfPTable3.addCell(pdfPCell5);
                if (catalogDTO.getTerms() != 0 || catalogDTO.getId() == 0) {
                    str = "Due   ";
                    if (catalogDTO.getTerms() > 1) {
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("Due   ");
                        stringBuilder3.append(MyConstants.formatDate(InvoicePreviewActivity.this, Long.parseLong(catalogDTO.getDueDate()), settingsDTO.getDateFormat()));
                        str = stringBuilder3.toString();
                    }
                    if (catalogDTO.getTerms() == 1 || catalogDTO.getId() == 0) {
                        str = "Terms   Due on receipt";
                    }
                    pdfPCell5 = new PdfPCell(new Paragraph(str, textFont));
                    pdfPCell5.setHorizontalAlignment(2);
                    pdfPCell5.setBorder(0);
                    pdfPTable3.addCell(pdfPCell5);
                }
                str = catalogDTO.getPoNumber();
                if (!(str == null || str.equals(""))) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("PO #   ");
                    stringBuilder2.append(str);
                    pdfPCell5 = new PdfPCell(new Paragraph(stringBuilder2.toString(), textFont));
                    pdfPCell5.setHorizontalAlignment(2);
                    pdfPCell5.setBorder(0);
                    pdfPTable3.addCell(pdfPCell5);
                }
                pdfPTable.addCell(pdfPTable2);
                pdfPTable.addCell(pdfPTable5);
                pdfPTable.addCell(pdfPTable3);
                pdfPTable.setWidthPercentage(widthParcentage);
                document.add(pdfPTable);
                if (catalogDTO.getInvoiceShippingDTO().getId() > 0) {
                    InvoiceShippingDTO invoiceShippingDTO = catalogDTO.getInvoiceShippingDTO();
                    if (invoiceShippingDTO != null) {
                        if (invoiceShippingDTO.getId() > 0) {
                            if (!invoiceShippingDTO.getShippingDate().equals("") || invoiceShippingDTO.getAmount() != 0.0d || !invoiceShippingDTO.getShipVia().equals("") || !invoiceShippingDTO.getTracking().equals("") || !invoiceShippingDTO.getFob().equals("")) {
                                PdfPTable pdfPTablegenerateShippingTable = generateShippingTable(invoiceShippingDTO);
                                pdfPTablegenerateShippingTable.setSpacingBefore(20.0f);
                                pdfPTablegenerateShippingTable.setSpacingAfter(25.0f);
                                pdfPTablegenerateShippingTable.setWidthPercentage(widthParcentage);
                                document.add(pdfPTablegenerateShippingTable);
                            }
                        }
                    }
                }
                if (itemAssociatedDTOS.size() > 0) {

                    //Log.e("dddd", "ddfdf");
                    String str2;

                    float width;
                    int i2;
                    Image instance2;
                    float f;
                    String additionalDetails;
                    PdfPCell pdfPCell6;
                    PdfPCell pdfPCell7;
                    PdfPTable pdfPTablegenerateItemsTable = generateItemsTable();
                    pdfPTablegenerateItemsTable.setSpacingAfter(35.0f);
                    pdfPTablegenerateItemsTable.setWidthPercentage(widthParcentage);
                    document.add(pdfPTablegenerateItemsTable);

                    lineSeparator.setOffset(10.0f);
                    document.add(lineSeparator);


                    Paragraph paragraphaddd = new Paragraph();
                    PdfPTable pdfPTabletotal = new PdfPTable(2);
                    pdfPTabletotal.setWidthPercentage(100.0f);

                    PdfPCell pdfPCellfirst = new PdfPCell();

                    PdfPTable pdfPTableddd = new PdfPTable(1);
                    pdfPTableddd.setWidths(new float[]{10.0f});
                    PdfPCell pdfPCell8 = new PdfPCell();
                    Paragraph paragraph2 = new Paragraph();
                    if (catalogDTO != null) {

                        Log.e("ddddd notr", "  " + catalogDTO.getNotes());
                        paragraph2.add(catalogDTO.getNotes());
                        paragraph2.setFont(textFont);
                        pdfPCell8.addElement(paragraph2);
                    }

                    pdfPCell8.setBorder(0);
                    pdfPCell8.setPaddingLeft(5.0f);
                    pdfPTableddd.addCell(pdfPCell8);

                    pdfPCellfirst.addElement(pdfPTableddd);
                    pdfPTabletotal.addCell(pdfPCellfirst);

                    ///document.add(pdfPTableddd);


                    PdfPTable pdfPTable6 = new PdfPTable(2);
                    pdfPTable6.setWidths(new float[]{3.0f, 3.0f});
                    //pdfPTable6.setHorizontalAlignment(Element.ALIGN_RIGHT);

                    double d = 0.0d;
                    PdfPCell pdfPCellsecond = new PdfPCell();

                    int i4 = 0;
                    for (int i3 = 0; i3 < 14; i3++) {

                        PdfPCell pdfPCellsub = new PdfPCell();
                        pdfPCellsub.setBorder(0);
                        PdfPTable pdfPTable7;
                        paragraph2.clear();
                        StringBuilder stringBuilder5;
                        switch (i3) {
                            case 0:
                                Log.e("suntotal", "suntotal");
                                pdfPTable7 = pdfPTable6;
                                paragraph2.add("Subtotal");

                                break;
                            case 1:
                                pdfPTable7 = pdfPTable6;
                                Log.e("currency_sign", "currency_sign");
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append(currency_sign);
                                //   stringBuilder3.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getSubTotalAmount())));

                                String s = String.valueOf(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getSubTotalAmount())));

                                if (s.contains(".")) {
                                    int dotPos = String.valueOf(s).lastIndexOf(".");
                                    String subStr = String.valueOf(s).substring(dotPos);
                                    if (subStr.length() <= 2) {
                                        stringBuilder3.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getSubTotalAmount())) + "0");
                                    } else {
                                        stringBuilder3.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getSubTotalAmount())));
                                    }
                                } else {
                                    stringBuilder3.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getSubTotalAmount())));
                                }

                                paragraph2.add(stringBuilder3.toString());

                                break;
                            case 2:
                                pdfPTable7 = pdfPTable6;
                                i4 = catalogDTO.getDiscountType();
                                if (i4 != 0) {
                                    if (i4 != 1) {
                                        str = "Discount";
                                        d = MyConstants.formatDecimal(Double.valueOf(catalogDTO.getDiscountAmount()));
                                        if (i4 == 2) {
                                            stringBuilder3 = new StringBuilder();
                                            stringBuilder3.append("Discount (");
                                            stringBuilder3.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getDiscount())));
                                            stringBuilder3.append("%)");
                                            str = stringBuilder3.toString();
                                        }
                                        paragraph2.add(str);

                                        break;
                                    }
                                    break;
                                }
                                break;
                            case 3:
                                pdfPTable7 = pdfPTable6;
                                if (i4 != 0) {
                                    if (i4 != 1) {
                                        stringBuilder3 = new StringBuilder();
                                        stringBuilder3.append(currency_sign);
                                        stringBuilder3.append(d);
                                        paragraph2.add(stringBuilder3.toString());

                                        break;
                                    }
                                    break;
                                }
                                break;
                            case 4:
                                pdfPTable7 = pdfPTable6;
                                i4 = catalogDTO.getTaxType();
                                if (i4 != 3) {
                                    str2 = "Tax";
                                    d = MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTaxAmount()));
                                    if (i4 == 0 || i4 == 1) {
                                        stringBuilder5 = new StringBuilder();
                                        stringBuilder5.append("Tax (");
                                        stringBuilder5.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTaxRate())));
                                        stringBuilder5.append("%)");
                                        str2 = stringBuilder5.toString();
                                    }
                                    paragraph2.add(str2);

                                    break;
                                }
                                break;
                            case 5:
                                pdfPTable7 = pdfPTable6;
                                if (i4 != 3) {
                                    StringBuilder stringBuilder6 = new StringBuilder();
                                    stringBuilder6.append(currency_sign);
                                    //  stringBuilder6.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTaxAmount())));

                                    String s1 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTaxAmount())));

                                    if (s1.contains(".")) {
                                        int dotPos = String.valueOf(s1).lastIndexOf(".");
                                        String subStr = String.valueOf(s1).substring(dotPos);
                                        if (subStr.length() <= 2) {
                                            stringBuilder6.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTaxAmount())) + "0");
                                        } else {
                                            stringBuilder6.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTaxAmount())));
                                        }
                                    } else {
                                        stringBuilder6.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTaxAmount())));
                                    }

                                    paragraph2.add(stringBuilder6.toString());

                                    break;
                                }
                                break;
                            case 6:
                                pdfPTable7 = pdfPTable6;
                                paragraph2.add("Shipping");
                                Log.e("Shipping", "Shipping");
                                pdfPCellsub.setBorder(2);
                                pdfPCellsub.setPaddingBottom(10.00f);


                                break;
                            case 7:
                                pdfPTable7 = pdfPTable6;
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append(currency_sign);
                                // stringBuilder3.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getInvoiceShippingDTO().getAmount())));

                                String s2 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getInvoiceShippingDTO().getAmount())));

                                if (s2.contains(".")) {
                                    int dotPos = String.valueOf(s2).lastIndexOf(".");
                                    String subStr = String.valueOf(s2).substring(dotPos);
                                    if (subStr.length() <= 2) {
                                        stringBuilder3.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getInvoiceShippingDTO().getAmount())) + "0");
                                    } else {
                                        stringBuilder3.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getInvoiceShippingDTO().getAmount())));
                                    }
                                } else {
                                    stringBuilder3.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getInvoiceShippingDTO().getAmount())));
                                }


                                paragraph2.add(stringBuilder3.toString());
                                pdfPCellsub.setBorder(2);
                                pdfPCellsub.setPaddingBottom(10.00f);


                                break;
                            case 8:
                                pdfPTable7 = pdfPTable6;
                                paragraph2.add("Total");

                                break;
                            case 9:
                                pdfPTable7 = pdfPTable6;
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append(currency_sign);
                                //    stringBuilder3.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTotalAmount())));

                                String s3 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTotalAmount())));

                                if (s3.contains(".")) {
                                    int dotPos = String.valueOf(s3).lastIndexOf(".");
                                    String subStr = String.valueOf(s3).substring(dotPos);
                                    if (subStr.length() <= 2) {
                                        stringBuilder3.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTotalAmount())) + "0");
                                    } else {
                                        stringBuilder3.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTotalAmount())));
                                    }
                                } else {
                                    stringBuilder3.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTotalAmount())));
                                }


                                paragraph2.add(stringBuilder3.toString());

                                break;
                            case 10:
                                pdfPTable7 = pdfPTable6;
                                paragraph2.add("Paid");

                                break;
                            case 11:
                                stringBuilder5 = new StringBuilder();
                                stringBuilder5.append(currency_sign);
                                pdfPTable7 = pdfPTable6;
                                //  stringBuilder5.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getPaidAmount())));

                                String s4 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getPaidAmount())));

                                if (s4.contains(".")) {
                                    int dotPos = String.valueOf(s4).lastIndexOf(".");
                                    String subStr = String.valueOf(s4).substring(dotPos);
                                    if (subStr.length() <= 2) {
                                        stringBuilder5.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getPaidAmount())) + "0");
                                    } else {
                                        stringBuilder5.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getPaidAmount())));
                                    }
                                } else {
                                    stringBuilder5.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getPaidAmount())));
                                }

                                paragraph2.add(stringBuilder5.toString());

                                break;
                            case 12:
                                paragraph2.add("Balance Due");

                                break;
                            case 13:
                                stringBuilder4 = new StringBuilder();
                                stringBuilder4.append(currency_sign);
                                //  stringBuilder4.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTotalAmount() - catalogDTO.getPaidAmount())));

                                String s5 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTotalAmount() - catalogDTO.getPaidAmount())));

                                if (s5.contains(".")) {
                                    int dotPos = String.valueOf(s5).lastIndexOf(".");
                                    String subStr = String.valueOf(s5).substring(dotPos);
                                    if (subStr.length() <= 2) {
                                        stringBuilder4.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTotalAmount() - catalogDTO.getPaidAmount())) + "0");
                                    } else {
                                        stringBuilder4.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTotalAmount() - catalogDTO.getPaidAmount())));
                                    }
                                } else {
                                    stringBuilder4.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTotalAmount() - catalogDTO.getPaidAmount())));
                                }

                                paragraph2.add(stringBuilder4.toString());

                                break;
                        }

                        pdfPCellsub.addElement(paragraph2);
                        pdfPTable6.addCell(pdfPCellsub);


                    }


                    pdfPCellsecond.addElement(pdfPTable6);
                    pdfPTabletotal.addCell(pdfPCellsecond);
                    pdfPTabletotal.setWidthPercentage(widthParcentage);
                    pdfPTabletotal.setSpacingAfter(20.0f);
                    paragraphaddd.add(pdfPTabletotal);
                    document.add(paragraphaddd);
//                        lineSeparator.setOffset(10.0f);
//                        document.add(lineSeparator);

//                        pdfPTable6.setWidthPercentage(widthParcentage);
//                        pdfPTable6.setSpacingAfter(20.0f);
//                        document.add(pdfPTable6);
//                        lineSeparator.setOffset(10.0f);
//                        document.add(lineSeparator);

                    Paragraph paragraphpdfpayment = new Paragraph();
                    PdfPCell firstcellofpayment = new PdfPCell();
                    PdfPCell secondcellofpayment = new PdfPCell();

                    PdfPTable pdfPTablepaymenyt = new PdfPTable(2);
                    pdfPTablepaymenyt.setWidths(new float[]{5.0f, 5.0f});
                    pdfPTablepaymenyt.setSpacingAfter(20.0f);
                    PdfPTable pdfPTable8 = new PdfPTable(1);

                    Paragraph paragraphpdf1 = new Paragraph();
                    paragraphpdf1.setFont(textFont);
                    for (int i5 = 0; i5 < 5; i5++) {
                        PdfPCell pdfPCell9 = new PdfPCell();
                        pdfPCell9.setBorder(0);
                        paragraphpdf1.clear();
                        paragraphpdf1.setFont(textFont);
                        StringBuilder stringBuilder7;
                        switch (i5) {
                            case 0:

                                if ((!TextUtils.isEmpty(businessDTO.getPaypalAddress())) || (!TextUtils.isEmpty(businessDTO.getBankInformation())) || (!TextUtils.isEmpty(businessDTO.getCheckInformation())) || (!TextUtils.isEmpty(businessDTO.getOtherPaymentInformation()))) {
                                    paragraphpdf1.setFont(titleFont);
                                    paragraphpdf1.add("Payment Instructions");


                                    break;
                                }
                                break;
                            case 1:
                                if (!TextUtils.isEmpty(businessDTO.getPaypalAddress())) {

                                    stringBuilder7 = new StringBuilder();
                                    stringBuilder7.append("Via PayPal\nSend payment to: ");
                                    stringBuilder7.append(businessDTO.getPaypalAddress());
                                    paragraphpdf1.add(stringBuilder7.toString());
                                    break;
                                }


                                break;
                            case 2:
                                if (!TextUtils.isEmpty(businessDTO.getBankInformation())) {

                                    stringBuilder7 = new StringBuilder();
                                    stringBuilder7.append("Bank Transfer\n");
                                    stringBuilder7.append(businessDTO.getBankInformation());
                                    paragraphpdf1.add(stringBuilder7.toString());
                                    break;
                                }
                                break;
                            case 3:
                                if (!TextUtils.isEmpty(businessDTO.getCheckInformation())) {
                                    stringBuilder7 = new StringBuilder();
                                    stringBuilder7.append("By cheque\nMake cheques payable to: ");
                                    stringBuilder7.append(businessDTO.getCheckInformation());
                                    paragraphpdf1.add(stringBuilder7.toString());
                                    break;
                                }
                                break;
                            case 4:
                                if (!TextUtils.isEmpty(businessDTO.getOtherPaymentInformation())) {
                                    stringBuilder7 = new StringBuilder();
                                    stringBuilder7.append("Other\n");
                                    stringBuilder7.append(businessDTO.getOtherPaymentInformation());
                                    paragraphpdf1.add(stringBuilder7.toString());
                                    break;
                                }
                                break;
                            default:
                                break;
                        }
                        pdfPCell9.addElement(paragraphpdf1);
                        pdfPTable8.addCell(pdfPCell9);
                    }


                    firstcellofpayment.addElement(pdfPTable8);
                    pdfPTablepaymenyt.addCell(firstcellofpayment).setBorder(0);
                    pdfPTablepaymenyt.setWidthPercentage(widthParcentage);
                    // pdfPTable9.addCell(new PdfPCell()).setBorder(0);
                    //    pdfPTable.addCell(new PdfPCell(pdfPTable8)).setBorder(0);
                    //  pdfPTable.addCell(new PdfPCell(pdfPTable9)).setBorder(0);

                    //document.add(pdfPTable);

                    PdfPTable pdfPTable9 = new PdfPTable(1);
                    if (catalogDTO.getSignedUrl() == null) {
                        if (catalogDTO.getSignedDate() == null) {
                            loadItemImages();
                            //pdfPTable = new PdfPTable(1);
                            Paragraph paragraph = new Paragraph();
                            paragraph.setFont(textFont);
                            width = document.getPageSize().getWidth() * 0.8f;
                            for (i2 = 0; i2 < imageDTOS.size(); i2++) {
                                if (new File(((ImageDTO) imageDTOS.get(i2)).getImageUrl()).exists()) {
                                    pdfPTable9.addCell(new PdfPCell()).setBorder(0);
                                } else {
                                    instance2 = Image.getInstance(((ImageDTO) imageDTOS.get(i2)).getImageUrl());
                                    f = 0.5f * width;
                                    instance2.scaleToFit(f, Math.abs((instance2.getWidth() - f) / instance2.getWidth()) * instance2.getHeight());
                                    instance2.setSpacingBefore(20.0f);
                                    pdfPCell4 = new PdfPCell(instance2);
                                    pdfPCell4.setHorizontalAlignment(0);
                                    pdfPCell4.setBorder(0);
                                    pdfPTable9.addCell(pdfPCell4).setBorder(0);
                                }
                                str2 = ((ImageDTO) imageDTOS.get(i2)).getDescription();
                                additionalDetails = ((ImageDTO) imageDTOS.get(i2)).getAdditionalDetails();
                                pdfPCell6 = new PdfPCell();
                                paragraph.clear();
                                paragraph.add(str2);
                                pdfPCell6.addElement(paragraph);
                                pdfPCell6.setBorder(0);
                                pdfPTable9.addCell(pdfPCell6);
                                pdfPCell7 = new PdfPCell();
                                paragraph.clear();
                                paragraph.add(additionalDetails);
                                pdfPCell7.addElement(paragraph);
                                pdfPCell7.setBorder(0);
                                pdfPTable9.addCell(pdfPCell7).setBorder(0);
                            }
                            pdfPTable9.setWidthPercentage(widthParcentage);
/*
                            secondcellofpayment.addElement(pdfPTable9);
                            pdfPTablepaymenyt.addCell(secondcellofpayment).setBorder(0);
                            paragraphpdfpayment.add(pdfPTablepaymenyt);
                            document.add(paragraphpdfpayment);*/
                        } else {


                        }
                    }
                    if (catalogDTO.getSignedUrl() != null) {
                        if (new File(catalogDTO.getSignedUrl()).exists()) {
                            pdfPCell7 = new PdfPCell(Image.getInstance(catalogDTO.getSignedUrl()), true);
                        } else {
                            pdfPCell7 = new PdfPCell();
                            pdfPCell7.setHorizontalAlignment(2);
                            pdfPCell7.setBorder(0);
                        }
                        pdfPTable9.addCell(pdfPCell7);
                    }
                    if (catalogDTO.getSignedDate() != null) {
                        pdfPCell8 = new PdfPCell(new Paragraph(MyConstants.formatDate(InvoicePreviewActivity.this, Long.parseLong(catalogDTO.getSignedDate()), settingsDTO.getDateFormat()), textFont));
                        pdfPCell8.setBorder(0);
                        pdfPCell8.setHorizontalAlignment(2);
                        pdfPTable9.addCell(pdfPCell8);
                    }
                    pdfPTable.addCell(new PdfPCell(pdfPTable8)).setBorder(0);
                    pdfPTable.addCell(new PdfPCell(pdfPTable9)).setBorder(0);
                    secondcellofpayment.addElement(pdfPTable9);
                    pdfPTablepaymenyt.addCell(secondcellofpayment).setBorder(0);
                    pdfPTablepaymenyt.setWidthPercentage(widthParcentage);
                    paragraphpdfpayment.add(pdfPTablepaymenyt);

                    document.add(paragraphpdfpayment);
                    loadItemImages();
                    PdfPTable pdfPTableimage = new PdfPTable(1);
                    Paragraph paragraphsss = new Paragraph();
                    paragraphsss.setFont(textFont);
                    width = document.getPageSize().getWidth() * 0.8f;
                    for (i2 = 0; i2 < imageDTOS.size(); i2++) {
                        if (new File(((ImageDTO) imageDTOS.get(i2)).getImageUrl()).exists()) {
                            Log.e("image url", " " + ((ImageDTO) imageDTOS.get(i2)).getImageUrl());
                            instance2 = Image.getInstance(((ImageDTO) imageDTOS.get(i2)).getImageUrl());
                            f = 0.5f * width;
                            instance2.scaleToFit(f, Math.abs((instance2.getWidth() - f) / instance2.getWidth()) * instance2.getHeight());
                            instance2.setSpacingBefore(20.0f);
                            pdfPCell4 = new PdfPCell(instance2);
                            pdfPCell4.setHorizontalAlignment(0);
                            pdfPCell4.setBorder(0);
                            pdfPTableimage.addCell(pdfPCell4);

                        } else {
                            pdfPTableimage.addCell(new PdfPCell()).setBorder(0);
                        }
                        str2 = ((ImageDTO) imageDTOS.get(i2)).getDescription();
                        additionalDetails = ((ImageDTO) imageDTOS.get(i2)).getAdditionalDetails();

                        // paragraphsss.clear();
                        // paragraphsss.add(str2);


                        // pdfPTableimage.addCell(pdfPCell6);
                        pdfPCell7 = new PdfPCell();
                        paragraphsss.clear();
                        paragraphsss.add(additionalDetails);
                        pdfPCell7.addElement(paragraphsss);
                        pdfPCell7.setBorder(0);
                        pdfPTableimage.addCell(pdfPCell7);
                    }
                    pdfPTableimage.setWidthPercentage(widthParcentage);
                    document.add(pdfPTableimage);
                }
                if (document != null) {
                    try {
                        document.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        return;
                    }
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e22) {


                Log.e("Exception", "========Naitik=============" + e22.toString());

                try {
                    e22.printStackTrace();
                    if (document != null) {
                        document.close();
                    }
                    if (fileOutputStream == null) {
                        fileOutputStream.close();
                    }
                } catch (Throwable th2) {


                    Log.e("Exception", "========Naitik2=============" + e22.toString());
                    th = th2;

                    if (document != null) {
                        try {
                            document.close();
                        } catch (Exception e222) {
                            e222.printStackTrace();
                            throw th;
                        }
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    throw th;
                }
            } catch (Throwable th22) {


                Log.e("Exception", "========Naitik3=============" + th22.toString());
                th = th22;
                if (document != null) {
                    document.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                throw th;
            }

        } catch (Exception e22222) {

            Log.e("Exception", "========Naitik4 =============" + e22222.toString());

            e22222.printStackTrace();
            if (document != null) {
                document.close();
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable th222) {
            th = th222;
            Log.e("Exception", "========Naitik5=============" + th222.toString());

            if (document != null) {
                document.close();
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                throw th;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private PdfPTable generateShippingTable(InvoiceShippingDTO invoiceShippingDTO) {
        int i;
        PdfPCell pdfPCell;
        PdfPTable pdfPTable = new PdfPTable(5);
        for (i = 0; i < 5; i++) {
            pdfPCell = new PdfPCell();
            pdfPCell.setBorder(0);
            pdfPCell.setBackgroundColor(BaseColor.GRAY);
            pdfPCell.setPadding(0.0f);
            pdfPCell.setMinimumHeight(25.0f);
            switch (i) {
                case 0:
                    pdfPCell.setHorizontalAlignment(0);
                    pdfPCell.setPaddingLeft(5.0f);
                    pdfPCell.addElement(new Paragraph("SHIP DATE", this.normalWhite));
                    break;
                case 1:
                    pdfPCell.setHorizontalAlignment(2);
                    pdfPCell.addElement(new Paragraph("SHIP AMOUNT", this.normalWhite));
                    break;
                case 2:
                    pdfPCell.setHorizontalAlignment(2);
                    pdfPCell.addElement(new Paragraph("SHIP VIA", this.normalWhite));
                    break;
                case 3:
                    pdfPCell.setHorizontalAlignment(2);
                    pdfPCell.addElement(new Paragraph("SHIP TRACKING #", this.normalWhite));
                    break;
                case 4:
                    pdfPCell.setHorizontalAlignment(2);
                    pdfPCell.addElement(new Paragraph("SHIP FOB", this.normalWhite));
                    break;
                default:
                    break;
            }
            pdfPTable.addCell(pdfPCell);
        }
        for (i = 0; i < 5; i++) {
            pdfPCell = new PdfPCell();
            pdfPCell.setBorder(0);
            switch (i) {
                case 0:
                    pdfPCell.setHorizontalAlignment(0);
                    pdfPCell.setPaddingLeft(5.0f);
                    pdfPCell.addElement(new Paragraph(invoiceShippingDTO.getShippingDate(), this.textFont));
                    break;
                case 1:
                    pdfPCell.setHorizontalAlignment(2);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(this.currency_sign);
                    // stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(invoiceShippingDTO.getAmount())));

                    String d = String.valueOf(MyConstants.formatDecimal(Double.valueOf(invoiceShippingDTO.getAmount())));
                    int dotPos = String.valueOf(d).lastIndexOf(".");
                    String subStr = String.valueOf(d).substring(dotPos);
                    if (d.contains(".")) {
                        if (subStr.length() <= 2) {
                            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(invoiceShippingDTO.getAmount())) + "0");
                        } else {
                            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(invoiceShippingDTO.getAmount())));
                        }
                    } else {
                        stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(invoiceShippingDTO.getAmount())));
                    }

                    pdfPCell.addElement(new Paragraph(stringBuilder.toString(), this.textFont));
                    break;
                case 2:
                    pdfPCell.setHorizontalAlignment(2);
                    pdfPCell.addElement(new Paragraph(invoiceShippingDTO.getShipVia(), this.textFont));
                    break;
                case 3:
                    pdfPCell.setHorizontalAlignment(2);
                    pdfPCell.addElement(new Paragraph(invoiceShippingDTO.getTracking(), this.textFont));
                    break;
                case 4:
                    pdfPCell.setHorizontalAlignment(2);
                    pdfPCell.addElement(new Paragraph(invoiceShippingDTO.getFob(), this.textFont));
                    break;
                default:
                    break;
            }
            pdfPTable.addCell(pdfPCell);
        }
        return pdfPTable;
    }


    private PdfPTable generateItemsTable() {

        int discountType = this.catalogDTO.getDiscountType();
        int taxType = this.catalogDTO.getTaxType();
        int i5 = (discountType == 1 && taxType == 2) ? 6 : (discountType == 1 || taxType == 2) ? 5 : 4;

        PdfPTable pdfPTable = new PdfPTable(i5);
        pdfPTable.setSpacingBefore(20.0f);

        if (i5 == 6) {
            try {
                pdfPTable.setWidths(new float[]{1.5f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f});
            } catch (DocumentException unused) {

                unused.printStackTrace();
            }
        } else if (i5 == 5) {
            try {
                pdfPTable.setWidths(new float[]{1.5f, 1.0f, 1.0f, 1.0f, 1.0f});
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        } else {
            try {
                pdfPTable.setWidths(new float[]{3.0f, 1.0f, 1.0f, 1.0f});
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }


        for (int i = 0; i < 6; i++) {
            PdfPCell pdfPCell = new PdfPCell();
            pdfPCell.setBorder(0);
            pdfPCell.setBackgroundColor(BaseColor.GRAY);
            pdfPCell.setPadding(0.0f);
            pdfPCell.setMinimumHeight(25.0f);
            switch (i) {
                case 0:
                    pdfPCell.setHorizontalAlignment(0);
                    pdfPCell.setPaddingLeft(5.0f);
                    pdfPCell.addElement(new Paragraph("DESCRIPTION", this.normalWhite));
                    break;
                case 1:
                    pdfPCell.setHorizontalAlignment(2);
                    pdfPCell.addElement(new Paragraph(Contract.Items_Associated.QTY, this.normalWhite));
                    break;
                case 2:
                    pdfPCell.setHorizontalAlignment(2);
                    pdfPCell.addElement(new Paragraph("RATE", this.normalWhite));
                    break;
                case 3:
                    if (discountType == 1) {
                        pdfPCell.setHorizontalAlignment(2);
                        pdfPCell.addElement(new Paragraph("DISCOUNT", this.normalWhite));
                        break;
                    } else {
                        continue;
                    }

                case 4:
                    pdfPCell.setHorizontalAlignment(2);
                    pdfPCell.addElement(new Paragraph("AMOUNT", this.normalWhite));
                    break;
                case 5:
                    if (taxType == 2) {
                        pdfPCell.setHorizontalAlignment(2);
                        pdfPCell.addElement(new Paragraph("TAX", this.normalWhite));
                        break;
                    } else {
                        continue;
                    }

                default:
                    break;


            }
            pdfPTable.addCell(pdfPCell);
        }


        ArrayList<ItemAssociatedDTO> arrayList = this.itemAssociatedDTOS;
        double d = 0.0d;
        int i7 = 0;
        double d2 = d;
        while (i7 < arrayList.size()) {


            Log.e("arraylist", "= " + arrayList.size());
            double unitCost = Double.valueOf(((ItemAssociatedDTO) arrayList.get(i7)).getUnitCost()) * ((ItemAssociatedDTO) arrayList.get(i7)).getQuantity();
            if (discountType == 1) {
                d2 = (((ItemAssociatedDTO) arrayList.get(i7)).getDiscount() / 100.0d) * unitCost;
                unitCost -= d2;
            }

            double d3 = d2;
            d2 = unitCost;
            if (taxType == 2) {
                d = (((ItemAssociatedDTO) arrayList.get(i7)).getTaxRate() / 100.0d) * d2;
            }


            String s = String.valueOf(MyConstants.formatDecimal(Double.valueOf(d2)));

            if (s.contains(".")) {
                int dotPos = String.valueOf(s).lastIndexOf(".");
                String subStr = String.valueOf(s).substring(dotPos);
                if (subStr.length() <= 2) {
                    ((ItemAssociatedDTO) arrayList.get(i7)).setTotalAmount(MyConstants.formatDecimal(Double.valueOf(d2)) + "0");
                } else {
                    ((ItemAssociatedDTO) arrayList.get(i7)).setTotalAmount(String.valueOf(MyConstants.formatDecimal(Double.valueOf(d2))));
                }
            } else {
                ((ItemAssociatedDTO) arrayList.get(i7)).setTotalAmount(String.valueOf(MyConstants.formatDecimal(Double.valueOf(d2))));
            }

            for (int i8 = 0; i8 < 6; i8++) {
                PdfPCell pdfPCells = new PdfPCell();
                pdfPCells.setBorder(0);
                ArrayList arrayList2;
                ArrayList arrayList3;

                StringBuilder stringBuilder;
                switch (i8) {
                    case 0:
                        arrayList2 = arrayList;

                        pdfPCells.setHorizontalAlignment(0);
                        pdfPCells.setPaddingLeft(5.0f);
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(((ItemAssociatedDTO) arrayList2.get(i7)).getItemName());
                        stringBuilder2.append("\n");
                        stringBuilder2.append(((ItemAssociatedDTO) arrayList2.get(i7)).getDescription());
                        Paragraph paragraph = new Paragraph(stringBuilder2.toString(), this.textFont);
                        paragraph.setLeading(0.0f, 1.0f);
                        pdfPCells.addElement(paragraph);
                        break;
                    case 1:
                        arrayList2 = arrayList;
                        pdfPCells.setHorizontalAlignment(2);
                        pdfPCells.addElement(new Paragraph(String.valueOf(((ItemAssociatedDTO) arrayList2.get(i7)).getQuantity()), this.textFont));
                        break;
                    case 2:
                        arrayList3 = arrayList;
                        pdfPCells.setHorizontalAlignment(2);
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(this.currency_sign);
                        arrayList2 = arrayList3;
                        //stringBuilder3.append(((ItemAssociatedDTO) arrayList2.get(i7)).getUnitCost());

                        String s1 = String.valueOf(((ItemAssociatedDTO) arrayList2.get(i7)).getUnitCost());

                        if (s1.contains(".")) {
                            int dotPos = String.valueOf(s1).lastIndexOf(".");
                            String subStr = String.valueOf(s1).substring(dotPos);
                            if (subStr.length() <= 2) {
                                stringBuilder3.append(((ItemAssociatedDTO) arrayList2.get(i7)).getUnitCost() + "0");
                            } else {
                                stringBuilder3.append(((ItemAssociatedDTO) arrayList2.get(i7)).getUnitCost());
                            }
                        } else {
                            stringBuilder3.append(((ItemAssociatedDTO) arrayList2.get(i7)).getUnitCost());
                        }

                        pdfPCells.addElement(new Paragraph(stringBuilder3.toString(), this.textFont));
                        break;
                    case 3:
                        if (discountType == 1) {
                            pdfPCells.setHorizontalAlignment(2);
                            StringBuilder stringBuilder4 = new StringBuilder();
                            stringBuilder4.append(this.currency_sign);
                            stringBuilder4.append(d3);
                            stringBuilder4.append(" (");
                            arrayList3 = arrayList;
                            stringBuilder4.append(((ItemAssociatedDTO) arrayList.get(i7)).getDiscount());
                            stringBuilder4.append("%)");
                            pdfPCells.addElement(new Paragraph(stringBuilder4.toString(), this.textFont));

//                            arrayList2 = arrayList3;
                            break;
                        }
//                        arrayList2 = arrayList;


                        continue;
                    case 4:
                        pdfPCells.setHorizontalAlignment(2);
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(this.currency_sign);
                        //   stringBuilder.append(((ItemAssociatedDTO) arrayList.get(i7)).getTotalAmount());
                        String s2 = String.valueOf(((ItemAssociatedDTO) arrayList.get(i7)).getTotalAmount());

                        if (s2.contains(".")) {
                            int dotPos = String.valueOf(s2).lastIndexOf(".");
                            String subStr = String.valueOf(s2).substring(dotPos);
                            if (subStr.length() <= 2) {
                                stringBuilder.append(((ItemAssociatedDTO) arrayList.get(i7)).getTotalAmount() + "0");
                            } else {
                                stringBuilder.append(((ItemAssociatedDTO) arrayList.get(i7)).getTotalAmount());
                            }
                        } else {
                            stringBuilder.append(((ItemAssociatedDTO) arrayList.get(i7)).getTotalAmount());
                        }

                        pdfPCells.addElement(new Paragraph(stringBuilder.toString(), this.textFont));
                        break;
                    case 5:
                        if (taxType == 2) {
                            pdfPCells.setHorizontalAlignment(2);
                            if (((ItemAssociatedDTO) arrayList.get(i7)).getTaxAble() != 0) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(this.currency_sign);
                                stringBuilder.append(d);
                                stringBuilder.append(" (");
                                stringBuilder.append(((ItemAssociatedDTO) arrayList.get(i7)).getTaxRate());
                                stringBuilder.append("%)");
                                pdfPCells.addElement(new Paragraph(stringBuilder.toString(), this.textFont));
                                break;
                            }
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(this.currency_sign);
                            stringBuilder.append("0");
                            pdfPCells.addElement(new Paragraph(stringBuilder.toString(), this.textFont));

                        }
//                        arrayList2 = arrayList;


                        continue;
                    default:
//                        arrayList2 = arrayList;

                        break;
                }

                pdfPTable.addCell(pdfPCells);
            }
            i7++;
            d2 = d3;


        }


        return pdfPTable;
    }


    private void loadItemImages() {
        this.imageDTOS.clear();
        Collection invoiceItemImages = LoadDatabase.getInstance().getInvoiceItemImages(this.catalogDTO.getId());
        if (invoiceItemImages != null && invoiceItemImages.size() > 0) {
            this.imageDTOS.addAll(invoiceItemImages);
        }
    }

    private void viewPdf() {
        String catalogName = catalogDTO.getCatalogName();
        if (catalogDTO.getId() == 0) {
            catalogName = MyConstants.getInvoiceName();
        }
        Log.e(TAG, "catalogName: " + catalogName);
        String rootDirectory = MyConstants.getRootDirectory(getApplicationContext());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rootDirectory);
        stringBuilder.append(File.separator);
        stringBuilder.append(catalogName + "_invoice.pdf");
        this.pdfFileName = stringBuilder.toString();
        this.pdfView.fromFile(new File(this.pdfFileName)).defaultPage(this.pageNumber.intValue()).enableSwipe(true).swipeHorizontal(false).onPageChange(this).enableAnnotationRendering(true).scrollHandle(new DefaultScrollHandle(this)).pageFitPolicy(FitPolicy.BOTH).spacing(10).load();
        this.pdfView.setMaxZoom(1.0f);
        this.pdfView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
    }

    public void onPageChanged(int i, int i2) {
        this.pageNumber = Integer.valueOf(i);
        setTitle(String.format("%s %s / %s", new Object[]{this.pdfFileName, Integer.valueOf(i + 1), Integer.valueOf(i2)}));
    }

    public void loadComplete(int i) {
        printBookmarksTree(this.pdfView.getTableOfContents(), "-");
    }

    public void printBookmarksTree(List<Bookmark> list, String str) {
        for (Bookmark bookmark : list) {
            if (bookmark.hasChildren()) {
                List children = bookmark.getChildren();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append("-");
                printBookmarksTree(children, stringBuilder.toString());
            }
        }
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
