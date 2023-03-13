package com.example.invoicemaker.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.print.PrintManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.invoicemaker.Adapter.MyPrintDocumentAdapter;
import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.BusinessDTO;
import com.example.invoicemaker.Dto.CatalogDTO;
import com.example.invoicemaker.Dto.ClientDTO;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ClientStatementPreviewActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {
    public String TAG = "ClientStatementPreviewActivity";
    Font normalWhite = new Font(FontFamily.HELVETICA, 12.0f, 0, BaseColor.WHITE);
    Integer pageNumber = Integer.valueOf(0);
    String pdfFileName;
    PDFView pdfView;
    BaseFont textBaseFont;
    Font textFont = new Font(FontFamily.TIMES_ROMAN, 14.0f);
    Font titleFont = new Font(FontFamily.TIMES_ROMAN, 18.0f, 1, BaseColor.BLACK);
    private BusinessDTO businessDTO;
    private ArrayList<CatalogDTO> catalogs;
    private ClientDTO clientDTO;
    private String currency_sign;
    private SettingsDTO settingsDTO;
    private Toolbar toolbar;
    private float widthParcentage = 90.0f;

    public static void start(Context context, ClientDTO clientDTO) {
        Intent intent = new Intent(context, ClientStatementPreviewActivity.class);
        intent.putExtra(MyConstants.CLIENT_DTO, clientDTO);
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
        try {
            if (AppCore.isNetworkAvailable(ClientStatementPreviewActivity.this)) {
                addBanner();
            }
            getIntentData();
            initLayout();
            loadData();
            if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                createPDF();
                viewPdf();
                return;
            }
            requestPermission(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preview_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.open_in) {
            openPdfFile();
            return true;
        } else if (itemId == R.id.print) {
            printPDF();
            return true;
        } else if (itemId != R.id.share) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            sharePdfFile();
            return true;
        }
    }

    private void openPdfFile() {
        String rootDirectory = MyConstants.getRootDirectory(getApplicationContext());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rootDirectory);
        stringBuilder.append(File.separator);
        stringBuilder.append("Client_Report.pdf");
        rootDirectory = stringBuilder.toString();
        Intent intent = new Intent("android.intent.action.VIEW");
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(getApplicationContext().getPackageName());
        stringBuilder2.append(".my.package.name.provider");
        intent.setDataAndType(FileProvider.getUriForFile(this, stringBuilder2.toString(), new File(rootDirectory)), "application/pdf");
        // intent.setFlags(PropertyOptions.SEPARATE_NODE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Select Application."));
    }

    private void sharePdfFile() {
        String rootDirectory = MyConstants.getRootDirectory(getApplicationContext());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rootDirectory);
        stringBuilder.append(File.separator);
        stringBuilder.append("Client_Report.pdf");
        rootDirectory = stringBuilder.toString();
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("message/rfc822");
        intent.putExtra("android.intent.extra.SUBJECT", this.clientDTO.getClientName());
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(getApplicationContext().getPackageName());
        stringBuilder2.append(".my.package.name.provider");
        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, stringBuilder2.toString(), new File(rootDirectory)));
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Select Application."));
    }

    private void printPDF() {
        if (VERSION.SDK_INT >= 19) {
            PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getString(R.string.app_name));
            stringBuilder.append(" Document");
            String stringBuilder2 = stringBuilder.toString();
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(MyConstants.getRootDirectory(getApplicationContext()));
            stringBuilder3.append(File.separator);
            stringBuilder3.append("Client_Report.pdf");
            printManager.print(stringBuilder2, new MyPrintDocumentAdapter(this, stringBuilder3.toString()), null);
        }
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, ClientStatementPreviewActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void loadData() {
        this.businessDTO = LoadDatabase.getInstance().getBusinessInformation();
        try {
            loadInvoiceItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadInvoiceItems() {
        this.catalogs = LoadDatabase.getInstance().getCatalogsByClient(this.clientDTO.getId(), 1, 0);
    }

    private void getIntentData() {
        this.clientDTO = (ClientDTO) getIntent().getSerializableExtra(MyConstants.CLIENT_DTO);
        this.settingsDTO = SettingsDTO.getSettingsDTO();
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Client Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.pdfView = (PDFView) findViewById(R.id.pdfView);
        this.currency_sign = MyConstants.formatCurrency(this, this.settingsDTO.getCurrencyFormat());
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (i != 101) {
            super.onRequestPermissionsResult(i, strArr, iArr);
        } else if (iArr.length != 0 && iArr[0] == 0) {
            createPDF();
        }
    }

    private boolean checkString(String str) {
        return !TextUtils.isEmpty(str);
    }

    private void createPDF() {
        Document document;
        Exception exception;
        Throwable th;
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setPercentage(this.widthParcentage);
        lineSeparator.setOffset(20.0f);
        Document document2 = null;
        FileOutputStream fileOutputStream = null;
        try {
            String rootDirectory = MyConstants.getRootDirectory(getApplicationContext());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(rootDirectory);
            stringBuilder.append(File.separator);
            stringBuilder.append("Client_Report.pdf");
            fileOutputStream = new FileOutputStream(new File(stringBuilder.toString()));
            try {
                document = new Document();
                try {
                    PdfPTable pdfPTable;
                    PdfPTable pdfPTable2;
                    StringBuilder stringBuilder2;
                    PdfPCell pdfPCell;
                    document.setMargins(0.0f, 0.0f, TabSettings.DEFAULT_TAB_INTERVAL, TabSettings.DEFAULT_TAB_INTERVAL);
                    PdfWriter.getInstance(document, fileOutputStream);
                    document.open();
                    textBaseFont = BaseFont.createFont("assets/nexa_regular.otf", BaseFont.IDENTITY_H, true);
                    textFont = new Font(textBaseFont, 14.0f);
                    int i = 2;
                    if (businessDTO != null) {
                        pdfPTable = new PdfPTable(2);
                        pdfPTable.getDefaultCell().setBorder(0);
                        pdfPTable2 = new PdfPTable(1);
                        pdfPTable2.getDefaultCell().setBorder(0);
                        PdfPTable pdfPTable3 = new PdfPTable(1);
                        pdfPTable3.getDefaultCell().setBorder(0);
                        if (checkString(businessDTO.getName())) {
                            pdfPTable2.addCell(new PdfPCell(new Paragraph(businessDTO.getName(), titleFont))).setBorder(0);
                        }
                        if (checkString(businessDTO.getRegNo())) {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Registration Number : ");
                            stringBuilder2.append(businessDTO.getRegNo());
                            pdfPTable2.addCell(new PdfPCell(new Paragraph(stringBuilder2.toString(), textFont))).setBorder(0);
                        }
                        if (checkString(businessDTO.getLine1())) {
                            pdfPTable2.addCell(new PdfPCell(new Paragraph(businessDTO.getLine1(), textFont))).setBorder(0);
                        }
                        if (checkString(businessDTO.getLine2())) {
                            pdfPTable2.addCell(new PdfPCell(new Paragraph(businessDTO.getLine2(), textFont))).setBorder(0);
                        }
                        if (checkString(businessDTO.getLine3())) {
                            pdfPTable2.addCell(new PdfPCell(new Paragraph(businessDTO.getLine3(), textFont))).setBorder(0);
                        }
                        if (checkString(businessDTO.getPhoneNo())) {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Phone: ");
                            stringBuilder2.append(businessDTO.getPhoneNo());
                            pdfPTable2.addCell(new PdfPCell(new Paragraph(stringBuilder2.toString(), textFont))).setBorder(0);
                        }
                        if (checkString(businessDTO.getMobileNo())) {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Mobile: ");
                            stringBuilder2.append(businessDTO.getMobileNo());
                            pdfPTable2.addCell(new PdfPCell(new Paragraph(stringBuilder2.toString(), textFont))).setBorder(0);
                        }
                        if (checkString(businessDTO.getFax())) {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Fax: ");
                            stringBuilder2.append(businessDTO.getFax());
                            pdfPTable2.addCell(new PdfPCell(new Paragraph(stringBuilder2.toString(), textFont))).setBorder(0);
                        }
                        if (checkString(businessDTO.getEmail())) {
                            pdfPTable2.addCell(new PdfPCell(new Paragraph(businessDTO.getEmail(), textFont))).setBorder(0);
                        }
                        if (checkString(businessDTO.getWebsite())) {
                            pdfPTable2.addCell(new PdfPCell(new Paragraph(businessDTO.getWebsite(), textFont))).setBorder(0);
                        }
                        if (checkString(businessDTO.getLogo())) {
                            Image instance = Image.getInstance(businessDTO.getLogo());
                            PdfPTable pdfPTable4 = new PdfPTable(2);
                            PdfPCell pdfPCell2 = new PdfPCell();
                            pdfPCell2.setBorder(0);
                            PdfPCell pdfPCell3 = new PdfPCell(instance, true);
                            pdfPCell3.setBorder(0);
                            pdfPTable4.addCell(pdfPCell2);
                            pdfPTable4.addCell(pdfPCell3);
                            pdfPTable3.addCell(pdfPTable4);
                        }
                        pdfPTable.addCell(pdfPTable2);
                        pdfPTable.addCell(pdfPTable3);
                        pdfPTable.setWidthPercentage(widthParcentage);
                        pdfPTable.setSpacingAfter(40.0f);
                        document.add(pdfPTable);
                        document.add(lineSeparator);
                    }
                    pdfPTable = new PdfPTable(2);
                    pdfPTable.getDefaultCell().setBorder(0);
                    PdfPTable pdfPTable5 = new PdfPTable(1);
                    pdfPTable5.getDefaultCell().setBorder(0);
                    pdfPTable2 = new PdfPTable(1);
                    pdfPTable2.getDefaultCell().setBorder(0);
                    PdfPCell pdfPCell4 = new PdfPCell(new Paragraph("Client", titleFont));
                    pdfPCell4.setHorizontalAlignment(0);
                    pdfPCell4.setBorder(0);
                    pdfPTable5.addCell(pdfPCell4);
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("");
                    stringBuilder2.append(clientDTO.getClientName());
                    pdfPCell4 = new PdfPCell(new Paragraph(stringBuilder2.toString(), textFont));
                    pdfPCell4.setHorizontalAlignment(0);
                    pdfPCell4.setBorder(0);
                    pdfPTable5.addCell(pdfPCell4);
                    pdfPCell4 = new PdfPCell(new Paragraph("Statement of Account", titleFont));
                    pdfPCell4.setHorizontalAlignment(2);
                    pdfPCell4.setBorder(0);
                    pdfPTable2.addCell(pdfPCell4);
                    String formatDate = MyConstants.formatDate(ClientStatementPreviewActivity.this, Calendar.getInstance().getTimeInMillis(), settingsDTO.getDateFormat());
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("Date   ");
                    stringBuilder3.append(formatDate);
                    PdfPCell pdfPCell5 = new PdfPCell(new Paragraph(stringBuilder3.toString(), textFont));
                    pdfPCell5.setHorizontalAlignment(2);
                    pdfPCell5.setBorder(0);
                    pdfPTable2.addCell(pdfPCell5);
                    pdfPTable.addCell(pdfPTable5);
                    pdfPTable.addCell(pdfPTable2);
                    pdfPTable.setWidthPercentage(widthParcentage);
                    document.add(pdfPTable);
                    pdfPTable = generateInvoiceTable();
                    pdfPTable.setSpacingBefore(20.0f);
                    if (catalogs.size() > 0) {
                        pdfPTable.setSpacingAfter(50.0f);
                    } else {
                        pdfPTable.setSpacingAfter(20.0f);
                    }
                    pdfPTable.setWidthPercentage(widthParcentage);
                    document.add(pdfPTable);
                    if (catalogs.size() > 0) {
                        document.add(lineSeparator);
                    }
                    PdfPTable lineSeparatorpdf1 = new PdfPTable(2);
                    lineSeparatorpdf1.setWidths(new float[]{7.0f, 3.0f});
                    lineSeparatorpdf1.setSpacingAfter(20.0f);
                    PdfPTable pdfPTable6 = new PdfPTable(1);
                    PdfPTable pdfPTable7 = new PdfPTable(2);
                    pdfPTable7.setWidths(new float[]{1.5f, 1.5f});
                    int i2 = 0;
                    while (i2 < i) {
                        pdfPCell = new PdfPCell();
                        pdfPCell.setBorder(0);
                        pdfPCell.setPadding(0.0f);
                        pdfPCell.setMinimumHeight(25.0f);
                        switch (i2) {
                            case 0:
                                pdfPCell.setHorizontalAlignment(0);
                                pdfPCell.setPaddingLeft(5.0f);
                                pdfPCell.addElement(new Paragraph("TOTAL", textFont));
                                break;
                            case 1:
                                pdfPCell.setHorizontalAlignment(i);
                                double d = 0.0d;
                                for (int i3 = 0; i3 < catalogs.size(); i3++) {
                                    d += ((CatalogDTO) catalogs.get(i3)).getTotalAmount() - ((CatalogDTO) catalogs.get(i3)).getPaidAmount();
                                }
                                StringBuilder stringBuilder4 = new StringBuilder();
                                stringBuilder4.append(MyConstants.formatCurrency(ClientStatementPreviewActivity.this, settingsDTO.getCurrencyFormat()));
                                stringBuilder4.append(MyConstants.formatDecimal(Double.valueOf(d)));
                                pdfPCell.addElement(new Paragraph(stringBuilder4.toString(), textFont));
                                break;
                            default:
                                break;
                        }
                        pdfPTable7.addCell(pdfPCell);
                        i2++;
                        i = 2;
                    }
                    Paragraph paragraph = new Paragraph();
                    paragraph.setFont(textFont);
                    for (i = 0; i < 5; i++) {
                        pdfPCell = new PdfPCell();
                        pdfPCell.setBorder(0);
                        paragraph.clear();
                        paragraph.setFont(textFont);
                        StringBuilder stringBuilder5;
                        switch (i) {
                            case 0:
                                paragraph.setFont(titleFont);
                                paragraph.add("Payment Instructions");
                                break;
                            case 1:
                                stringBuilder5 = new StringBuilder();
                                stringBuilder5.append("Via PayPal\nSend payment to: ");
                                stringBuilder5.append(businessDTO.getPaypalAddress());
                                paragraph.add(stringBuilder5.toString());
                                break;
                            case 2:
                                stringBuilder5 = new StringBuilder();
                                stringBuilder5.append("Bank Transfer\n");
                                stringBuilder5.append(businessDTO.getBankInformation());
                                paragraph.add(stringBuilder5.toString());
                                break;
                            case 3:
                                stringBuilder5 = new StringBuilder();
                                stringBuilder5.append("By cheque\nMake cheques payable to: ");
                                stringBuilder5.append(businessDTO.getCheckInformation());
                                paragraph.add(stringBuilder5.toString());
                                break;
                            case 4:
                                stringBuilder5 = new StringBuilder();
                                stringBuilder5.append("Other\n");
                                stringBuilder5.append(businessDTO.getOtherPaymentInformation());
                                paragraph.add(stringBuilder5.toString());
                                break;
                            default:
                                break;
                        }
                        pdfPCell.addElement(paragraph);
                        pdfPTable6.addCell(pdfPCell);
                    }
                    lineSeparatorpdf1.addCell(new PdfPCell(pdfPTable6)).setBorder(0);
                    lineSeparatorpdf1.addCell(new PdfPCell(pdfPTable7)).setBorder(0);
                    lineSeparatorpdf1.setWidthPercentage(widthParcentage);
                    document.add(lineSeparatorpdf1);
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
                    exception = e22;
                    document2 = document;
                    try {
                        exception.printStackTrace();
                        if (document2 != null) {
                            document2.close();
                        }
                        if (fileOutputStream == null) {
                            fileOutputStream.close();
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        document = document2;
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
                    th = th22;
                    if (document != null) {
                        document.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    throw th;
                }
            } catch (Exception e2222) {
                exception = e2222;
                exception.printStackTrace();
                if (document2 != null) {
                    document2.close();
                }
                if (fileOutputStream == null) {
                    fileOutputStream.close();
                }
            }
        } catch (Exception e22222) {
            exception = e22222;
            fileOutputStream = null;
            exception.printStackTrace();
            if (document2 != null) {
                document2.close();
            }
            if (fileOutputStream == null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable th222) {
            th = th222;
            document = null;

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

    private PdfPTable generateInvoiceTable() {
        int i;
        PdfPTable pdfPTable = new PdfPTable(2);
        try {
            pdfPTable.setWidths(new float[]{8.0f, 1.5f});
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        for (i = 0; i < 2; i++) {
            PdfPCell pdfPCell = new PdfPCell();
            pdfPCell.setBorder(0);
            pdfPCell.setBackgroundColor(BaseColor.GRAY);
            pdfPCell.setPadding(0.0f);
            pdfPCell.setMinimumHeight(25.0f);
            switch (i) {
                case 0:
                    pdfPCell.setHorizontalAlignment(0);
                    pdfPCell.setPaddingLeft(5.0f);
                    pdfPCell.addElement(new Paragraph("INVOICE", this.normalWhite));
                    break;
                case 1:
                    pdfPCell.setHorizontalAlignment(2);
                    pdfPCell.addElement(new Paragraph("OWING", this.normalWhite));
                    break;
                default:
                    break;
            }
            pdfPTable.addCell(pdfPCell);
        }
        for (i = 0; i < this.catalogs.size(); i++) {
            double totalAmount = ((CatalogDTO) this.catalogs.get(i)).getTotalAmount() - ((CatalogDTO) this.catalogs.get(i)).getPaidAmount();
            for (int i2 = 0; i2 < 2; i2++) {
                PdfPCell pdfPCell2 = new PdfPCell();
                pdfPCell2.setBorder(0);
                StringBuilder stringBuilder;
                switch (i2) {
                    case 0:
                        pdfPCell2.setHorizontalAlignment(0);
                        pdfPCell2.setPaddingLeft(5.0f);
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(MyConstants.formatDate(this, Long.parseLong(((CatalogDTO) this.catalogs.get(i)).getCreationDate()), this.settingsDTO.getDateFormat()));
                        stringBuilder2.append(" - ");
                        stringBuilder2.append(((CatalogDTO) this.catalogs.get(i)).getCatalogName());
                        String stringBuilder3 = stringBuilder2.toString();
                        if (((CatalogDTO) this.catalogs.get(i)).getPaidAmount() > 0.0d) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(stringBuilder3);
                            stringBuilder.append("\n");
                            stringBuilder.append(MyConstants.formatCurrency(this, this.settingsDTO.getCurrencyFormat()));
                            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(((CatalogDTO) this.catalogs.get(i)).getTotalAmount())));
                            stringBuilder.append(" Total");
                            stringBuilder3 = stringBuilder.toString();
                        }
                        Paragraph paragraph = new Paragraph(stringBuilder3, this.textFont);
                        paragraph.setLeading(0.0f, 1.0f);
                        pdfPCell2.addElement(paragraph);
                        break;
                    case 1:
                        pdfPCell2.setHorizontalAlignment(2);
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(MyConstants.formatCurrency(this, this.settingsDTO.getCurrencyFormat()));
                        stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(totalAmount)));
                        pdfPCell2.addElement(new Paragraph(stringBuilder.toString(), this.textFont));
                        break;
                    default:
                        break;
                }
                pdfPTable.addCell(pdfPCell2);
            }
        }
        return pdfPTable;
    }

    private void viewPdf() {
        String rootDirectory = MyConstants.getRootDirectory(getApplicationContext());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rootDirectory);
        stringBuilder.append(File.separator);
        stringBuilder.append("Client_Report.pdf");
        this.pdfFileName = stringBuilder.toString();
        this.pdfView.fromFile(new File(this.pdfFileName)).defaultPage(this.pageNumber.intValue()).enableSwipe(true).swipeHorizontal(false).onPageChange(this).enableAnnotationRendering(true).scrollHandle(new DefaultScrollHandle(this)).pageFitPolicy(FitPolicy.BOTH).spacing(10).load();
        this.pdfView.setMaxZoom(1.0f);
        this.pdfView.setBackgroundColor(ContextCompat.getColor(this, R.color.rng_gray_lite));
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
