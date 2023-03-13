package com.example.invoicemaker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.example.invoicemaker.Database.Contract.Business_Information;
import com.example.invoicemaker.Database.Contract.Catalog;
import com.example.invoicemaker.Database.Contract.Catalog_Images;
import com.example.invoicemaker.Database.Contract.Clients;
import com.example.invoicemaker.Database.Contract.Items;
import com.example.invoicemaker.Database.Contract.Items_Associated;
import com.example.invoicemaker.Database.Contract.Payments;
import com.example.invoicemaker.Database.Contract.Settings;
import com.example.invoicemaker.Database.Contract.Shipping;
import com.example.invoicemaker.Dto.BusinessDTO;
import com.example.invoicemaker.Dto.CatalogDTO;
import com.example.invoicemaker.Dto.ClientDTO;
import com.example.invoicemaker.Dto.DiscountDTO;
import com.example.invoicemaker.Dto.ImageDTO;
import com.example.invoicemaker.Dto.InvoiceShippingDTO;
import com.example.invoicemaker.Dto.ItemAssociatedDTO;
import com.example.invoicemaker.Dto.ItemDTO;
import com.example.invoicemaker.Dto.PaymentDTO;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.Dto.SignedDTO;
import com.example.invoicemaker.Dto.TaxDTO;
import com.example.invoicemaker.utils.DataProcessor;
import com.example.invoicemaker.utils.MyConstants;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.util.ArrayList;

public class LoadDatabase extends DatabaseOpenClose {
    public static String TAG = "LoadDatabase";
    private static LoadDatabase mInstance;
    private SQLiteDatabase myDb;

    private LoadDatabase() {
    }

    public static synchronized LoadDatabase getInstance() {
        LoadDatabase loadDatabase;
        synchronized (LoadDatabase.class) {
            if (mInstance == null) {
                mInstance = new LoadDatabase();
            }
            loadDatabase = mInstance;
        }
        return loadDatabase;
    }

    public void viewData() {
        openDb();
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_ALL_FROM_CLIENTS, null);
        rawQuery.moveToFirst();
        for (int i = 0; i < rawQuery.getCount(); i++) {
            for (int i2 = 1; i2 < rawQuery.getColumnCount(); i2++) {
            }
            rawQuery.moveToNext();
        }
        closeDB();
    }

    public SettingsDTO getSettings() {
        openDb();
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_ALL_FROM_SETTINGS, null);
        SettingsDTO settingsDTO = new SettingsDTO();
        while (rawQuery.moveToNext()) {
            settingsDTO.setId(rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id")));
            settingsDTO.setCurrencyFormat(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Settings.CURRENCY)));
            settingsDTO.setDateFormat(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Settings.DATE_FORMAT)));
        }
        rawQuery.close();
        closeDB();
        return settingsDTO;
    }

    public int updateSettings(SettingsDTO settingsDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Settings.CURRENCY, Integer.valueOf(settingsDTO.getCurrencyFormat()));
        contentValues.put(Settings.DATE_FORMAT, Integer.valueOf(settingsDTO.getDateFormat()));
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(settingsDTO.getId());
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Settings.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        SettingsDTO.setSettingsDTO(settingsDTO);
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) settingsDTO), 321);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update;
    }

    public BusinessDTO getBusinessInformation() {
        openDb();
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_ALL_FROM_BUSINESS_INFORMATION, null);
        BusinessDTO businessDTO = new BusinessDTO();
        while (rawQuery.moveToNext()) {
            businessDTO.setId(rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id")));
            businessDTO.setName(rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME")));
            businessDTO.setRegNo(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Business_Information.REGISTRATION_NUMBER)));
            businessDTO.setLine1(rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_1")));
            businessDTO.setLine2(rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_2")));
            businessDTO.setLine3(rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_3")));
            businessDTO.setPhoneNo(rawQuery.getString(rawQuery.getColumnIndexOrThrow("PHONE")));
            businessDTO.setMobileNo(rawQuery.getString(rawQuery.getColumnIndexOrThrow("MOBILE")));
            businessDTO.setFax(rawQuery.getString(rawQuery.getColumnIndexOrThrow("FAX")));
            businessDTO.setEmail(rawQuery.getString(rawQuery.getColumnIndexOrThrow("EMAIL")));
            businessDTO.setWebsite(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Business_Information.WEBSITE)));
            businessDTO.setPaypalAddress(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Business_Information.PAYPAL_ADDRESS)));
            businessDTO.setCheckInformation(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Business_Information.CHEQUES_INFORMATION)));
            businessDTO.setBankInformation(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Business_Information.BANK_INFORMATION)));
            businessDTO.setOtherPaymentInformation(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Business_Information.OTHER_PAYMENT_INFORMATION)));
            businessDTO.setLogo(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Business_Information.LOGO_URL)));
        }
        rawQuery.close();
        closeDB();
        return businessDTO;
    }

    public long saveBusinessInformation(BusinessDTO businessDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", businessDTO.getName());
        contentValues.put(Business_Information.REGISTRATION_NUMBER, businessDTO.getRegNo());
        contentValues.put("ADDRESS_LINE_1", businessDTO.getLine1());
        contentValues.put("ADDRESS_LINE_2", businessDTO.getLine2());
        contentValues.put("ADDRESS_LINE_3", businessDTO.getLine3());
        contentValues.put("PHONE", businessDTO.getPhoneNo());
        contentValues.put("MOBILE", businessDTO.getMobileNo());
        contentValues.put("FAX", businessDTO.getFax());
        contentValues.put("EMAIL", businessDTO.getEmail());
        contentValues.put(Business_Information.WEBSITE, businessDTO.getWebsite());
        contentValues.put(Business_Information.PAYPAL_ADDRESS, businessDTO.getPaypalAddress());
        contentValues.put(Business_Information.CHEQUES_INFORMATION, businessDTO.getCheckInformation());
        contentValues.put(Business_Information.BANK_INFORMATION, businessDTO.getBankInformation());
        contentValues.put(Business_Information.OTHER_PAYMENT_INFORMATION, businessDTO.getOtherPaymentInformation());
        contentValues.put(Business_Information.LOGO_URL, businessDTO.getLogo());
        long insert = this.myDb.insert(Business_Information.TABLE_NAME, null, contentValues);
        businessDTO.setId(insert);
        closeDB();
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) businessDTO), MyConstants.ACTION_BUSINESS_INFORMATION_ADDED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return insert;
    }

    public int updateBusinessInformation(BusinessDTO businessDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", businessDTO.getName());
        contentValues.put(Business_Information.REGISTRATION_NUMBER, businessDTO.getRegNo());
        contentValues.put("ADDRESS_LINE_1", businessDTO.getLine1());
        contentValues.put("ADDRESS_LINE_2", businessDTO.getLine2());
        contentValues.put("ADDRESS_LINE_3", businessDTO.getLine3());
        contentValues.put("PHONE", businessDTO.getPhoneNo());
        contentValues.put("MOBILE", businessDTO.getMobileNo());
        contentValues.put("FAX", businessDTO.getFax());
        contentValues.put("EMAIL", businessDTO.getEmail());
        contentValues.put(Business_Information.WEBSITE, businessDTO.getWebsite());
        contentValues.put(Business_Information.PAYPAL_ADDRESS, businessDTO.getPaypalAddress());
        contentValues.put(Business_Information.CHEQUES_INFORMATION, businessDTO.getCheckInformation());
        contentValues.put(Business_Information.BANK_INFORMATION, businessDTO.getBankInformation());
        contentValues.put(Business_Information.OTHER_PAYMENT_INFORMATION, businessDTO.getOtherPaymentInformation());
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(businessDTO.getId());
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Business_Information.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) businessDTO), MyConstants.ACTION_BUSINESS_INFORMATION_UPDATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update;
    }

    public int updateBusinessLogo(long j, String str) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Business_Information.LOGO_URL, str);
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Business_Information.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        return update;
    }

    public long saveMyItem(ItemDTO itemDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", itemDTO.getItemName());
        contentValues.put("UNIT_COST", Double.valueOf(itemDTO.getUnitCost()));
        contentValues.put("DESCRIPTION", itemDTO.getItemDescription());
        contentValues.put("TAXABLE", Integer.valueOf(itemDTO.getTexable()));
        long insert = this.myDb.insert(Items.TABLE_NAME, null, contentValues);
        closeDB();
        return insert;
    }

    public ArrayList<ItemDTO> getMyItems() {
        openDb();
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_ALL_FROM_ITEMS, null);
        ArrayList<ItemDTO> arrayList = new ArrayList();
        while (rawQuery.moveToNext()) {
            ItemDTO itemDTO = new ItemDTO();
            itemDTO.setId(rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id")));
            itemDTO.setItemName(rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME")));
            itemDTO.setUnitCost(String.valueOf(rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("UNIT_COST"))));
            itemDTO.setItemDescription(rawQuery.getString(rawQuery.getColumnIndexOrThrow("DESCRIPTION")));
            itemDTO.setTexable(rawQuery.getInt(rawQuery.getColumnIndexOrThrow("TAXABLE")));
            arrayList.add(itemDTO);
        }
        rawQuery.close();
        closeDB();
        return arrayList;
    }

    public void updateMyItem(ItemDTO itemDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", itemDTO.getItemName());
        contentValues.put("UNIT_COST", Double.valueOf(itemDTO.getUnitCost()));
        contentValues.put("DESCRIPTION", itemDTO.getItemDescription());
        contentValues.put("TAXABLE", Integer.valueOf(itemDTO.getTexable()));
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(itemDTO.getId());
        strArr[0] = stringBuilder.toString();
        this.myDb.update(Items.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
    }

    public void deleteMyItem(long j) {
        openDb();
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        this.myDb.delete(Items.TABLE_NAME, "_id = ?", strArr);
        closeDB();
    }

    public long saveInvoice(CatalogDTO catalogDTO) {


        catalogDTO.setType(MyConstants.CATALOG_TYPE);
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Catalog.CLIENTS_ID, Long.valueOf(catalogDTO.getClientDTO().getId()));
        contentValues.put(Catalog.TYPE, Integer.valueOf(catalogDTO.getType()));
        contentValues.put(Catalog.DISCOUNT_TYPE, Integer.valueOf(catalogDTO.getDiscountType()));
        contentValues.put("DISCOUNT", Double.valueOf(catalogDTO.getDiscount()));
        contentValues.put(Catalog.TAX_TYPE, Integer.valueOf(catalogDTO.getTaxType()));
        contentValues.put(Catalog.TAX_LABEL, catalogDTO.getTaxLabel());
        contentValues.put("TAX_RATE", Double.valueOf(catalogDTO.getTaxRate()));
        contentValues.put("NAME", catalogDTO.getCatalogName());
        contentValues.put(Catalog.CREATED_AT, catalogDTO.getCreationDate());
        contentValues.put(Catalog.TERMS, Integer.valueOf(catalogDTO.getTerms()));
        contentValues.put(Catalog.DUE_DATE, catalogDTO.getDueDate());
        contentValues.put(Catalog.PO_NUMBER, catalogDTO.getPoNumber());
        contentValues.put(Catalog.PAID, Double.valueOf(catalogDTO.getPaidAmount()));
        contentValues.put(Catalog.TOTAL_AMOUNT, Double.valueOf(catalogDTO.getTotalAmount()));
        contentValues.put(Catalog.SIGNED_DATE, catalogDTO.getSignedDate());
        contentValues.put("NOTES", catalogDTO.getNotes());
        contentValues.put(Catalog.PAID_STATUS, Integer.valueOf(catalogDTO.getPaidStatus()));
        contentValues.put(Catalog.ESTIMATE_STATUS, Integer.valueOf(catalogDTO.getEstimateStatus()));
        contentValues.put(Catalog.TYPE, Integer.valueOf(catalogDTO.getType()));
        long insert = this.myDb.insert(Catalog.TABLE_NAME, null, contentValues);
        catalogDTO.setId(insert);
        catalogDTO.setClientDTO(getSingleClient(catalogDTO.getClientDTO().getId()));
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) catalogDTO), 2001);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return insert;
    }

    public CatalogDTO getSingleCatalog(long j) {
        openDb();
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_SINGLE_CATALOG, strArr);
        CatalogDTO catalogDTO = new CatalogDTO();
        while (rawQuery.moveToNext()) {
            catalogDTO.setId(rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id")));
            catalogDTO.setType(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.TYPE)));
            catalogDTO.setDiscountType(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.DISCOUNT_TYPE)));
            catalogDTO.setDiscount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("DISCOUNT")));
            catalogDTO.setTaxType(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.TAX_TYPE)));
            catalogDTO.setTaxLabel(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.TAX_LABEL)));
            catalogDTO.setTaxRate((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("TAX_RATE")));
            catalogDTO.setCatalogName(rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME")));
            catalogDTO.setCreationDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.CREATED_AT)));
            catalogDTO.setTerms(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.TERMS)));
            catalogDTO.setDueDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.DUE_DATE)));
            catalogDTO.setPoNumber(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.PO_NUMBER)));
            catalogDTO.setPaidAmount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Catalog.PAID)));
            catalogDTO.setPaidStatus(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.PAID_STATUS)));
            catalogDTO.setEstimateStatus(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.ESTIMATE_STATUS)));
            catalogDTO.setTotalAmount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Catalog.TOTAL_AMOUNT)));
            catalogDTO.setSignedDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.SIGNED_DATE)));
            catalogDTO.setSignedUrl(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.SIGNED_URL)));
            catalogDTO.setNotes(rawQuery.getString(rawQuery.getColumnIndexOrThrow("NOTES")));
            ClientDTO singleClient = getSingleClient((long) rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.CLIENTS_ID)));
            if (singleClient.getId() > 0) {
                catalogDTO.setClientDTO(singleClient);
            }
            catalogDTO.setInvoiceShippingDTO(getInvoiceShipping(catalogDTO.getId()));
        }
        rawQuery.close();
        closeDB();
        return catalogDTO;
    }

    public ArrayList<CatalogDTO> getAllInvoices() {
        return getFromCatalogs(Query.SELECT_INVOICES_FROM_CATALOG);
    }

    public ArrayList<CatalogDTO> getAllEstimates() {
        return getFromCatalogs(Query.SELECT_ESTIMATES_FROM_CATALOG);
    }

    public ArrayList<CatalogDTO> getFromCatalogs(String str) {
        openDb();
        Cursor rawQuery = this.myDb.rawQuery(str, null);
        ArrayList<CatalogDTO> arrayList = new ArrayList();
        while (rawQuery.moveToNext()) {
            CatalogDTO catalogDTO = new CatalogDTO();
            catalogDTO.setId(rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id")));
            catalogDTO.setType(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.TYPE)));
            catalogDTO.setCatalogName(rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME")));
            catalogDTO.setCreationDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.CREATED_AT)));
            catalogDTO.setTerms(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.TERMS)));
            catalogDTO.setDueDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.DUE_DATE)));
            catalogDTO.setPoNumber(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.PO_NUMBER)));
            catalogDTO.setDiscountType(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.DISCOUNT_TYPE)));
            catalogDTO.setDiscount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("DISCOUNT")));
            catalogDTO.setTaxType(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.TAX_TYPE)));
            catalogDTO.setTaxLabel(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.TAX_LABEL)));
            catalogDTO.setTaxRate((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("TAX_RATE")));
            catalogDTO.setPaidAmount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Catalog.PAID)));
            catalogDTO.setPaidStatus(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.PAID_STATUS)));
            catalogDTO.setEstimateStatus(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.ESTIMATE_STATUS)));
            catalogDTO.setTotalAmount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Catalog.TOTAL_AMOUNT)));
            catalogDTO.setSignedDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.SIGNED_DATE)));
            catalogDTO.setSignedUrl(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.SIGNED_URL)));
            catalogDTO.setNotes(rawQuery.getString(rawQuery.getColumnIndexOrThrow("NOTES")));
            ClientDTO singleClient = getSingleClient((long) rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.CLIENTS_ID)));
            if (singleClient.getId() > 0) {
                catalogDTO.setClientDTO(singleClient);
            }
            catalogDTO.setInvoiceShippingDTO(getInvoiceShipping(catalogDTO.getId()));
            arrayList.add(catalogDTO);
        }
        rawQuery.close();
        closeDB();
        return arrayList;
    }

    public int updateInvoice(CatalogDTO catalogDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Catalog.TYPE, Integer.valueOf(catalogDTO.getType()));
        contentValues.put(Catalog.DISCOUNT_TYPE, Integer.valueOf(catalogDTO.getDiscountType()));
        contentValues.put(Catalog.TAX_TYPE, Integer.valueOf(catalogDTO.getTaxType()));
        contentValues.put(Catalog.TAX_LABEL, catalogDTO.getTaxLabel());
        contentValues.put("TAX_RATE", Double.valueOf(catalogDTO.getTaxRate()));
        contentValues.put("NAME", catalogDTO.getCatalogName());
        contentValues.put(Catalog.CREATED_AT, catalogDTO.getCreationDate());
        contentValues.put(Catalog.TERMS, Integer.valueOf(catalogDTO.getTerms()));
        contentValues.put(Catalog.DUE_DATE, catalogDTO.getDueDate());
        contentValues.put(Catalog.PO_NUMBER, catalogDTO.getPoNumber());
        contentValues.put("DISCOUNT", Double.valueOf(catalogDTO.getDiscount()));
        contentValues.put(Catalog.PAID, Double.valueOf(catalogDTO.getPaidAmount()));
        contentValues.put(Catalog.TOTAL_AMOUNT, Double.valueOf(catalogDTO.getTotalAmount()));
        contentValues.put(Catalog.SIGNED_DATE, catalogDTO.getSignedDate());
        contentValues.put("NOTES", catalogDTO.getNotes());
        contentValues.put(Catalog.ESTIMATE_STATUS, Integer.valueOf(catalogDTO.getEstimateStatus()));
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(catalogDTO.getId());
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Catalog.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) catalogDTO), MyConstants.ACTION_INVOICE_UPDATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update;
    }

    public int updateInvoiceTax(TaxDTO taxDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Catalog.TAX_TYPE, Integer.valueOf(taxDTO.getTaxType()));
        contentValues.put(Catalog.TAX_LABEL, taxDTO.getTaxLabel());
        contentValues.put("TAX_RATE", Double.valueOf(taxDTO.getTaxRate()));
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(taxDTO.getCatalogId());
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Catalog.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) taxDTO), 103);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update;
    }

    public void deleteInvoice(long j) {
        openDb();
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        this.myDb.delete(Catalog.TABLE_NAME, "_id = ?", strArr);
        closeDB();
        CatalogDTO catalogDTO = new CatalogDTO();
        catalogDTO.setId(j);
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson(catalogDTO), MyConstants.ACTION_INVOICE_DELETED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<CatalogDTO> getInvoiceByClientName(String str) {
        openDb();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM CATALOG c join CLIENTS cl on c.CLIENTS_ID = cl._id WHERE cl.NAME like '%");
        stringBuilder.append(str);
        stringBuilder.append("%'");
        Cursor rawQuery = this.myDb.rawQuery(stringBuilder.toString(), null);
        ArrayList<CatalogDTO> arrayList = new ArrayList();
        while (rawQuery.moveToNext()) {
            CatalogDTO catalogDTO = new CatalogDTO();
            catalogDTO.setId(rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id")));
            catalogDTO.setCatalogName(rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME")));
            catalogDTO.setCreationDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.CREATED_AT)));
            catalogDTO.setTerms(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.TERMS)));
            catalogDTO.setDueDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.DUE_DATE)));
            catalogDTO.setPoNumber(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.PO_NUMBER)));
            catalogDTO.setTotalAmount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Catalog.TOTAL_AMOUNT)));
            ClientDTO singleClient = getSingleClient((long) rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.CLIENTS_ID)));
            if (singleClient.getId() > 0) {
                catalogDTO.setClientDTO(singleClient);
            } else {
                catalogDTO.getClientDTO().setClientName("No client");
                catalogDTO.getClientDTO().setId(-1);
            }
            arrayList.add(catalogDTO);
        }
        rawQuery.close();
        closeDB();
        return arrayList;
    }

    public InvoiceShippingDTO getInvoiceShipping(long j) {
        openDb();
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_INVOICE_SHIPPING, strArr);
        InvoiceShippingDTO invoiceShippingDTO = new InvoiceShippingDTO();
        while (rawQuery.moveToNext()) {
            invoiceShippingDTO.setId(rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id")));
            invoiceShippingDTO.setAmount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("AMOUNT")));
            invoiceShippingDTO.setShippingDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Shipping.SHIPPING_DATE)));
            invoiceShippingDTO.setShipVia(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Shipping.SHIP_VIA)));
            invoiceShippingDTO.setTracking(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Shipping.TRACKING)));
            invoiceShippingDTO.setFob(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Shipping.FOB)));
        }
        rawQuery.close();
        closeDB();
        return invoiceShippingDTO;
    }

    public long saveInvoiceImage(ImageDTO imageDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put("CATALOG_ID", Long.valueOf(imageDTO.getCatalogId()));
        contentValues.put(Catalog_Images.IMAGE_URL, imageDTO.getImageUrl());
        contentValues.put("DESCRIPTION", imageDTO.getDescription());
        contentValues.put(Catalog_Images.ADDITIONAL_DETAILS, imageDTO.getAdditionalDetails());
        long insert = this.myDb.insert(Catalog_Images.TABLE_NAME, null, contentValues);
        imageDTO.setId(insert);
        closeDB();
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) imageDTO), 3004);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return insert;
    }

    public int deleteInvoiceImage(ImageDTO imageDTO) {
        openDb();
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(imageDTO.getId());
        strArr[0] = stringBuilder.toString();
        int delete = this.myDb.delete(Catalog_Images.TABLE_NAME, "_id = ?", strArr);
        closeDB();
        MyConstants.deleteFile(imageDTO.getImageUrl());
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) imageDTO), 3006);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delete;
    }

    public int updateInvoiceImage(String str, ImageDTO imageDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Catalog_Images.IMAGE_URL, imageDTO.getImageUrl());
        contentValues.put("DESCRIPTION", imageDTO.getDescription());
        contentValues.put(Catalog_Images.ADDITIONAL_DETAILS, imageDTO.getAdditionalDetails());
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(imageDTO.getId());
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Catalog_Images.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        MyConstants.deleteFile(str);
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) imageDTO), 3005);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update;
    }

    public ArrayList<ImageDTO> getInvoiceItemImages(long j) {
        openDb();
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_CATALOG_IMAGES, strArr);
        ArrayList<ImageDTO> arrayList = new ArrayList();
        while (rawQuery.moveToNext()) {
            ImageDTO imageDTO = new ImageDTO();
            imageDTO.setId(rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id")));
            imageDTO.setCatalogId(rawQuery.getLong(rawQuery.getColumnIndexOrThrow("CATALOG_ID")));
            imageDTO.setImageUrl(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog_Images.IMAGE_URL)));
            imageDTO.setDescription(rawQuery.getString(rawQuery.getColumnIndexOrThrow("DESCRIPTION")));
            imageDTO.setAdditionalDetails(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog_Images.ADDITIONAL_DETAILS)));
            arrayList.add(imageDTO);
        }
        rawQuery.close();
        closeDB();
        return arrayList;
    }

    public ArrayList<ItemAssociatedDTO> getInvoiceItems(long j) {
        openDb();
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_INVOICE_ITEMS_ASSOCIATED, strArr);
        ArrayList<ItemAssociatedDTO> arrayList = new ArrayList();
        while (rawQuery.moveToNext()) {
            ItemAssociatedDTO itemAssociatedDTO = new ItemAssociatedDTO();
            itemAssociatedDTO.setId(rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id")));
            itemAssociatedDTO.setItemName(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Items_Associated.ITEM_NAME)));
            itemAssociatedDTO.setDescription(rawQuery.getString(rawQuery.getColumnIndexOrThrow("DESCRIPTION")));

            itemAssociatedDTO.setUnitCost(String.valueOf(MyConstants.formatDecimal(Double.valueOf(rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("UNIT_COST"))))));
            itemAssociatedDTO.setTaxAble(rawQuery.getInt(rawQuery.getColumnIndexOrThrow("TAXABLE")));
            itemAssociatedDTO.setCatalogId(j);
            itemAssociatedDTO.setQuantity((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Items_Associated.QTY)));
            itemAssociatedDTO.setTaxRate((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("TAX_RATE")));
            itemAssociatedDTO.setDiscount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("DISCOUNT")));
            arrayList.add(itemAssociatedDTO);
        }
        rawQuery.close();
        closeDB();
        return arrayList;
    }

    public long saveInvoiceItem(ItemAssociatedDTO itemAssociatedDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put("CATALOG_ID", Long.valueOf(itemAssociatedDTO.getCatalogId()));
        contentValues.put(Items_Associated.ITEM_NAME, itemAssociatedDTO.getItemName());
        contentValues.put("DESCRIPTION", itemAssociatedDTO.getDescription());


        contentValues.put("UNIT_COST", Double.valueOf(MyConstants.formatDecimal(Double.valueOf(itemAssociatedDTO.getUnitCost()))));
        contentValues.put("TAXABLE", Integer.valueOf(itemAssociatedDTO.getTaxAble()));
        contentValues.put(Items_Associated.QTY, Double.valueOf(itemAssociatedDTO.getQuantity()));
        contentValues.put("TAX_RATE", Double.valueOf(itemAssociatedDTO.getTaxRate()));
        contentValues.put("DISCOUNT", Double.valueOf(itemAssociatedDTO.getDiscount()));
        long insert = this.myDb.insert(Items_Associated.TABLE_NAME, null, contentValues);
        itemAssociatedDTO.setId(insert);
        closeDB();
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) itemAssociatedDTO), 3001);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return insert;
    }

    public int updateInvoiceAmount(long j, double d) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Catalog.TOTAL_AMOUNT, Double.valueOf(d));
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Catalog.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        return update;
    }


    public int deleteInvoiceItem(long j) {
        openDb();
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        int delete = this.myDb.delete(Items_Associated.TABLE_NAME, "_id = ?", strArr);
        closeDB();


        ItemAssociatedDTO itemAssociatedDTO = new ItemAssociatedDTO();
        itemAssociatedDTO.setId(j);

        Log.e(TAG, "deleteInvoiceItem: " + itemAssociatedDTO.getUnitCost());

        if (itemAssociatedDTO.getUnitCost() != null) {

            double quantity = itemAssociatedDTO.getQuantity() * Double.valueOf(itemAssociatedDTO.getUnitCost());


            itemAssociatedDTO.setTotalAmount(String.valueOf(Double.parseDouble(String.valueOf(MyConstants.formatDecimal(quantity)))));
            updateInvoiceAmount(itemAssociatedDTO.getCatalogId(), quantity);


        }

        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson(itemAssociatedDTO), 3003);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return delete;
    }

    public int updateInvoiceItem(ItemAssociatedDTO itemAssociatedDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Items_Associated.ITEM_NAME, itemAssociatedDTO.getItemName());
        contentValues.put("DESCRIPTION", itemAssociatedDTO.getDescription());


        contentValues.put("UNIT_COST", MyConstants.formatDecimal(Double.valueOf(itemAssociatedDTO.getUnitCost())));
        contentValues.put("TAXABLE", Integer.valueOf(itemAssociatedDTO.getTaxAble()));
        contentValues.put(Items_Associated.QTY, Double.valueOf(itemAssociatedDTO.getQuantity()));
        contentValues.put("TAX_RATE", Double.valueOf(itemAssociatedDTO.getTaxRate()));
        contentValues.put("DISCOUNT", Double.valueOf(itemAssociatedDTO.getDiscount()));
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(itemAssociatedDTO.getId());
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Items_Associated.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) itemAssociatedDTO), 3002);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update;
    }

    public CatalogDTO calculateInvoiceBalance(long j) {
        double d;
        double d2;
        double d3;
        long j2 = j;
        CatalogDTO singleCatalog = getSingleCatalog(j);
        openDb();
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j2);
        strArr[0] = stringBuilder.toString();
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_INVOICE_ITEMS_ASSOCIATED, strArr);
        double d4 = 0.0d;
        double d5 = 0.0d;
        double d6 = 0.0d;
        double d7 = 0.0d;
        while (rawQuery.moveToNext()) {
            d = d6;
            d6 = (double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("DISCOUNT"));
            d2 = ((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("UNIT_COST"))) * ((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Items_Associated.QTY)));
            if (singleCatalog.getDiscountType() == 1) {
                d6 = (d6 * d2) * 0.01d;
                d2 -= d6;
                d4 += d6;
            }
            d5 += d2;
            if (rawQuery.getInt(rawQuery.getColumnIndexOrThrow("TAXABLE")) == 1) {
                d6 = d + ((((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("TAX_RATE"))) * d2) * 0.01d);
                d7 += d2;
            } else {
                d6 = d;
            }
            j2 = j;
        }
        d = d6;
        rawQuery.close();
        closeDB();
        switch (singleCatalog.getDiscountType()) {
            case 1:
                d2 = d4;
                d3 = d5;
                break;
            case 2:
                d2 = (singleCatalog.getDiscount() * d5) * 0.01d;
                d3 = d5 - d2;
                break;
            case 3:
                d2 = singleCatalog.getDiscount();
                d3 = d5 - d2;
                break;
            default:
                d3 = d5;
                d2 = 0.0d;
                break;
        }
        d3 += singleCatalog.getInvoiceShippingDTO().getAmount();
        switch (singleCatalog.getTaxType()) {
            case 0:
                d4 = ((d7 - d2) * singleCatalog.getTaxRate()) * 0.01d;
                d3 += d4;
                break;
            case 1:
                d4 = ((d7 - d2) * singleCatalog.getTaxRate()) * 0.01d;
                d3 -= d4;
                break;
            case 2:
                d3 += d;
                d4 = d;
                break;
            default:
                d4 = 0.0d;
                break;
        }
        singleCatalog.setSubTotalAmount(d5);
        singleCatalog.setDiscountAmount(d2);
        singleCatalog.setTaxAmount(d4);
        singleCatalog.setTotalAmount(d3);
        rawQuery.close();
        closeDB();
        long j3 = j;
        updateInvoiceTotal(j3, d3);
        d3 = MyConstants.formatDecimal(Double.valueOf(singleCatalog.getTotalAmount())) - MyConstants.formatDecimal(Double.valueOf(singleCatalog.getPaidAmount()));
        d2 = 0.0d;
        if (d3 == 0.0d) {
            if (singleCatalog.getTotalAmount() == 0.0d || singleCatalog.getPaidStatus() == 2) {
                d2 = 0.0d;
            } else {
                singleCatalog.setPaidStatus(2);
                updateInvoicePaidStatus(j3, singleCatalog.getPaidStatus());
                DataProcessor.getInstance().notifyListeners(new Gson().toJson(singleCatalog), 101);
                return singleCatalog;
            }
        }
        if (!(d3 == d2 || singleCatalog.getPaidStatus() == 1)) {
            singleCatalog.setPaidStatus(1);
            updateInvoicePaidStatus(j3, singleCatalog.getPaidStatus());
        }
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson(singleCatalog), 101);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return singleCatalog;
    }

    private int updateInvoicePaidStatus(long j, int i) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Catalog.PAID_STATUS, Integer.valueOf(i));
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Catalog.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        return update;
    }

    public int updateInvoiceTotal(long j, double d) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Catalog.TOTAL_AMOUNT, Double.valueOf(d));
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Catalog.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        return update;
    }

    public long addPayment(PaymentDTO paymentDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put("CATALOG_ID", Long.valueOf(paymentDTO.getCatalogId()));
        contentValues.put("AMOUNT", Double.valueOf(paymentDTO.getPaidAmount()));
        contentValues.put(Payments.DATE, paymentDTO.getPaymentDate());
        contentValues.put(Payments.METHOD, paymentDTO.getPaymentMethod());
        contentValues.put("NOTES", paymentDTO.getPaymentNotes());
        long insert = this.myDb.insert(Payments.TABLE_NAME, null, contentValues);
        closeDB();
        paymentDTO.setId(insert);
        updateInvoicePayment(paymentDTO.getCatalogId());
        notifyListenersForPaymentUpdate(paymentDTO);
        return insert;
    }

    public long updatePayment(PaymentDTO paymentDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put("CATALOG_ID", Long.valueOf(paymentDTO.getCatalogId()));
        contentValues.put("AMOUNT", Double.valueOf(paymentDTO.getPaidAmount()));
        contentValues.put(Payments.DATE, paymentDTO.getPaymentDate());
        contentValues.put(Payments.METHOD, paymentDTO.getPaymentMethod());
        contentValues.put("NOTES", paymentDTO.getPaymentNotes());
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(paymentDTO.getId());
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Payments.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        updateInvoicePayment(paymentDTO.getCatalogId());
        notifyListenersForPaymentUpdate(paymentDTO);
        return (long) update;
    }

    public void deletePayment(PaymentDTO paymentDTO) {
        openDb();
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(paymentDTO.getId());
        strArr[0] = stringBuilder.toString();
        this.myDb.delete(Payments.TABLE_NAME, "_id = ?", strArr);
        closeDB();
        updateInvoicePayment(paymentDTO.getCatalogId());
        notifyListenersForPaymentUpdate(null);
    }

    public void notifyListenersForPaymentUpdate(PaymentDTO paymentDTO) {
        try {
            Gson gson = new Gson();
            String str = null;
            if (paymentDTO != null) {
                str = gson.toJson(Double.valueOf(paymentDTO.getPaidAmount()));
            }
            DataProcessor.getInstance().notifyListeners(str, 104);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PaymentDTO> getPayments(long j) {
        openDb();
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_INVOICE_PAYMENTS, strArr);
        ArrayList<PaymentDTO> arrayList = new ArrayList();
        while (rawQuery.moveToNext()) {
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setId(rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id")));
            paymentDTO.setCatalogId(j);
            paymentDTO.setPaidAmount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("AMOUNT")));
            paymentDTO.setPaymentDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Payments.DATE)));
            paymentDTO.setPaymentMethod(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Payments.METHOD)));
            paymentDTO.setPaymentNotes(rawQuery.getString(rawQuery.getColumnIndexOrThrow("NOTES")));
            arrayList.add(paymentDTO);
        }
        rawQuery.close();
        closeDB();
        return arrayList;
    }

    public String getLastPaymentDate(long j) {
        ArrayList payments = getPayments(j);
        return ((PaymentDTO) payments.get(payments.size() - 1)).getPaymentDate();
    }

    public PaymentDTO getSinglePayment(long j) {
        openDb();
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        Cursor query = this.myDb.query(Payments.TABLE_NAME, null, "_id=?", strArr, null, null, null);
        PaymentDTO paymentDTO = new PaymentDTO();
        while (query.moveToNext()) {
            paymentDTO.setId(query.getLong(query.getColumnIndexOrThrow("_id")));
            paymentDTO.setCatalogId(query.getLong(query.getColumnIndexOrThrow("CATALOG_ID")));
            paymentDTO.setPaidAmount((double) query.getFloat(query.getColumnIndexOrThrow("AMOUNT")));
            paymentDTO.setPaymentDate(query.getString(query.getColumnIndexOrThrow(Payments.DATE)));
            paymentDTO.setPaymentMethod(query.getString(query.getColumnIndexOrThrow(Payments.METHOD)));
            paymentDTO.setPaymentNotes(query.getString(query.getColumnIndexOrThrow("NOTES")));
        }
        query.close();
        closeDB();
        return paymentDTO;
    }

    public int updateInvoicePayment(long j) {
        ArrayList payments = getPayments(j);
        double d = 0.0d;
        if (payments != null && payments.size() > 0) {
            double d2 = 0.0d;
            for (int i = 0; i < payments.size(); i++) {
                d2 += ((PaymentDTO) payments.get(i)).getPaidAmount();
            }
            d = d2;
        }
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Catalog.PAID, Double.valueOf(d));
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Catalog.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        return update;
    }

    public int updateInvoiceSignature(SignedDTO signedDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Catalog.SIGNED_DATE, signedDTO.getSignedDate());
        contentValues.put(Catalog.SIGNED_URL, signedDTO.getSignedUrl());
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(signedDTO.getCatalogId());
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Catalog.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) signedDTO), 105);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update;
    }

    public int updateInvoiceDiscount(DiscountDTO discountDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Catalog.DISCOUNT_TYPE, Integer.valueOf(discountDTO.getDiscountType()));
        contentValues.put("DISCOUNT", Double.valueOf(discountDTO.getDiscountAmount()));
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(discountDTO.getCatalogId());
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Catalog.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) discountDTO), 102);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update;
    }

    public int updateInvoiceNotes(long j, String str) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NOTES", str);
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Catalog.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        return update;
    }

    public long saveInvoiceShipping(InvoiceShippingDTO invoiceShippingDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put("CATALOG_ID", Long.valueOf(invoiceShippingDTO.getCatalogId()));
        contentValues.put("AMOUNT", Double.valueOf(invoiceShippingDTO.getAmount()));
        contentValues.put(Shipping.SHIPPING_DATE, invoiceShippingDTO.getShippingDate());
        contentValues.put(Shipping.SHIP_VIA, invoiceShippingDTO.getShipVia());
        contentValues.put(Shipping.TRACKING, invoiceShippingDTO.getTracking());
        contentValues.put(Shipping.FOB, invoiceShippingDTO.getFob());
        long insert = this.myDb.insert(Shipping.TABLE_NAME, null, contentValues);
        invoiceShippingDTO.setId(insert);
        closeDB();
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) invoiceShippingDTO), MyConstants.ACTION_INVOICE_SHIPPING_ADDED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return insert;
    }

    public int updateInvoiceShipping(InvoiceShippingDTO invoiceShippingDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put("AMOUNT", Double.valueOf(invoiceShippingDTO.getAmount()));
        contentValues.put(Shipping.SHIPPING_DATE, invoiceShippingDTO.getShippingDate());
        contentValues.put(Shipping.SHIP_VIA, invoiceShippingDTO.getShipVia());
        contentValues.put(Shipping.TRACKING, invoiceShippingDTO.getTracking());
        contentValues.put(Shipping.FOB, invoiceShippingDTO.getFob());
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(invoiceShippingDTO.getId());
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Shipping.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) invoiceShippingDTO), MyConstants.ACTION_INVOICE_SHIPPING_UPDATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update;
    }

    public void deleteInvoiceShipping(long j) {
        openDb();
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        this.myDb.delete(Shipping.TABLE_NAME, "CATALOG_ID = ?", strArr);
        closeDB();
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson(Long.valueOf(j)), MyConstants.ACTION_INVOICE_SHIPPING_DELETED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<CatalogDTO> getUnPaidInvoices(int i) {
        openDb();
        String[] strArr = new String[2];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(i);
        strArr[0] = stringBuilder.toString();
        strArr[1] = "0";
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_PAID_FROM_CATALOG, strArr);
        ArrayList<CatalogDTO> arrayList = new ArrayList();
        while (rawQuery.moveToNext()) {
            CatalogDTO catalogDTO = new CatalogDTO();
            catalogDTO.setId(rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id")));
            catalogDTO.setType(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.TYPE)));
            catalogDTO.setCatalogName(rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME")));
            catalogDTO.setCreationDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.CREATED_AT)));
            catalogDTO.setTerms(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.TERMS)));
            catalogDTO.setDueDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.DUE_DATE)));
            catalogDTO.setPoNumber(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.PO_NUMBER)));
            catalogDTO.setDiscountType(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.DISCOUNT_TYPE)));
            catalogDTO.setDiscount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("DISCOUNT")));
            catalogDTO.setTaxType(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.TAX_TYPE)));
            catalogDTO.setTaxLabel(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.TAX_LABEL)));
            catalogDTO.setTaxRate((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("TAX_RATE")));
            catalogDTO.setPaidAmount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Catalog.PAID)));
            catalogDTO.setPaidStatus(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.PAID_STATUS)));
            catalogDTO.setEstimateStatus(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.ESTIMATE_STATUS)));
            catalogDTO.setTotalAmount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Catalog.TOTAL_AMOUNT)));
            catalogDTO.setSignedDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.SIGNED_DATE)));
            catalogDTO.setSignedUrl(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.SIGNED_URL)));
            catalogDTO.setNotes(rawQuery.getString(rawQuery.getColumnIndexOrThrow("NOTES")));
            ClientDTO singleClient = getSingleClient((long) rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.CLIENTS_ID)));
            if (singleClient.getId() > 0) {
                catalogDTO.setClientDTO(singleClient);
            }
            catalogDTO.setInvoiceShippingDTO(getInvoiceShipping(catalogDTO.getId()));
            arrayList.add(catalogDTO);
        }
        rawQuery.close();
        closeDB();
        return arrayList;
    }

    public ArrayList<CatalogDTO> getCatalogsByClient(long j, int i, int i2) {
        openDb();
        String[] strArr = new String[3];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(i);
        strArr[0] = stringBuilder.toString();
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("");
        stringBuilder2.append(i2);
        strArr[1] = stringBuilder2.toString();
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("");
        stringBuilder2.append(j);
        strArr[2] = stringBuilder2.toString();
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_CATALOGS_BY_CLIENT, strArr);
        ArrayList<CatalogDTO> arrayList = new ArrayList();
        while (rawQuery.moveToNext()) {
            CatalogDTO catalogDTO = new CatalogDTO();
            catalogDTO.setId(rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id")));
            catalogDTO.setType(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.TYPE)));
            catalogDTO.setCatalogName(rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME")));
            catalogDTO.setCreationDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.CREATED_AT)));
            catalogDTO.setTerms(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.TERMS)));
            catalogDTO.setDueDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.DUE_DATE)));
            catalogDTO.setPoNumber(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.PO_NUMBER)));
            catalogDTO.setDiscountType(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.DISCOUNT_TYPE)));
            catalogDTO.setDiscount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("DISCOUNT")));
            catalogDTO.setTaxType(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.TAX_TYPE)));
            catalogDTO.setTaxLabel(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.TAX_LABEL)));
            catalogDTO.setTaxRate((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("TAX_RATE")));
            catalogDTO.setPaidAmount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Catalog.PAID)));
            catalogDTO.setPaidStatus(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.PAID_STATUS)));
            catalogDTO.setEstimateStatus(rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.ESTIMATE_STATUS)));
            catalogDTO.setTotalAmount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Catalog.TOTAL_AMOUNT)));
            catalogDTO.setSignedDate(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.SIGNED_DATE)));
            catalogDTO.setSignedUrl(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.SIGNED_URL)));
            catalogDTO.setNotes(rawQuery.getString(rawQuery.getColumnIndexOrThrow("NOTES")));
            ClientDTO singleClient = getSingleClient((long) rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Catalog.CLIENTS_ID)));
            if (singleClient.getId() > 0) {
                catalogDTO.setClientDTO(singleClient);
            }
            catalogDTO.setInvoiceShippingDTO(getInvoiceShipping(catalogDTO.getId()));
            arrayList.add(catalogDTO);
        }
        rawQuery.close();
        closeDB();
        return arrayList;
    }

    public ArrayList<String> getInvoiceHistories() {
        ArrayList<String> arrayList = new ArrayList();
        try {
            openDb();
            Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_CREATION_DATE_FROM_INVOICES, null);
            while (rawQuery.moveToNext()) {
                arrayList.add(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Catalog.CREATED_AT)));
            }
            rawQuery.close();
            closeDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public void updateInvoiceClient(ClientDTO clientDTO) {
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) clientDTO), MyConstants.ACTION_INVOICE_CLIENT_UPDATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int addInvoiceClient(ClientDTO clientDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Catalog.CLIENTS_ID, Long.valueOf(clientDTO.getId()));
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(clientDTO.getCatalogId());
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Catalog.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson((Object) clientDTO), MyConstants.ACTION_INVOICE_CLIENT_ADDED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update;
    }

    public int deleteInvoiceClient(long j, long j2) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Catalog.CLIENTS_ID, Long.valueOf(j2));
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Catalog.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        try {
            DataProcessor.getInstance().notifyListeners(new Gson().toJson(Long.valueOf(j)), MyConstants.ACTION_INVOICE_CLIENT_DELETED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update;
    }

    public void deleteClient(long j) {
        openDb();
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        this.myDb.delete(Clients.TABLE_NAME, "_id = ?", strArr);
        closeDB();
    }

    public long saveClient(ClientDTO clientDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", clientDTO.getClientName());
        contentValues.put("EMAIL", clientDTO.getEmailAddress());
        contentValues.put("MOBILE", clientDTO.getMobileNo());
        contentValues.put("PHONE", clientDTO.getPhoneNo());
        contentValues.put("FAX", clientDTO.getFaxNo());
        contentValues.put(Clients.ADDRESS_CONTACT, clientDTO.getContactAdress());
        contentValues.put("ADDRESS_LINE_1", clientDTO.getAddressLine1());
        contentValues.put("ADDRESS_LINE_2", clientDTO.getAddressLine2());
        contentValues.put("ADDRESS_LINE_3", clientDTO.getAddressLine3());
        contentValues.put(Clients.SHIPPING_ADDRESS_NAME, clientDTO.getShippingAddress());
        contentValues.put(Clients.SHIPPING_ADDRESS_LINE_1, clientDTO.getShippingLine1());
        contentValues.put(Clients.SHIPPING_ADDRESS_LINE_2, clientDTO.getShippingLine2());
        contentValues.put(Clients.SHIPPING_ADDRESS_LINE_3, clientDTO.getShippingLine3());
        long insert = this.myDb.insert(Clients.TABLE_NAME, null, contentValues);
        closeDB();
        return insert;
    }

    public int updateClient(ClientDTO clientDTO) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", clientDTO.getClientName());
        contentValues.put("EMAIL", clientDTO.getEmailAddress());
        contentValues.put("MOBILE", clientDTO.getMobileNo());
        contentValues.put("PHONE", clientDTO.getPhoneNo());
        contentValues.put("FAX", clientDTO.getFaxNo());
        contentValues.put(Clients.ADDRESS_CONTACT, clientDTO.getContactAdress());
        contentValues.put("ADDRESS_LINE_1", clientDTO.getAddressLine1());
        contentValues.put("ADDRESS_LINE_2", clientDTO.getAddressLine2());
        contentValues.put("ADDRESS_LINE_3", clientDTO.getAddressLine3());
        contentValues.put(Clients.SHIPPING_ADDRESS_NAME, clientDTO.getShippingAddress());
        contentValues.put(Clients.SHIPPING_ADDRESS_LINE_1, clientDTO.getShippingLine1());
        contentValues.put(Clients.SHIPPING_ADDRESS_LINE_2, clientDTO.getShippingLine2());
        contentValues.put(Clients.SHIPPING_ADDRESS_LINE_3, clientDTO.getShippingLine3());
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(clientDTO.getId());
        strArr[0] = stringBuilder.toString();
        int update = this.myDb.update(Clients.TABLE_NAME, contentValues, "_id = ?", strArr);
        closeDB();
        return update;
    }

    public ClientDTO getSingleClient(long j) {
        openDb();
        String[] strArr = new String[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(j);
        strArr[0] = stringBuilder.toString();
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_SINGLE_CLIENT, strArr);
        ClientDTO clientDTO = new ClientDTO();
        while (rawQuery.moveToNext()) {
            clientDTO.setId(rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id")));
            clientDTO.setClientName(rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME")));
            clientDTO.setEmailAddress(rawQuery.getString(rawQuery.getColumnIndexOrThrow("EMAIL")));
            clientDTO.setMobileNo(rawQuery.getString(rawQuery.getColumnIndexOrThrow("MOBILE")));
            clientDTO.setPhoneNo(rawQuery.getString(rawQuery.getColumnIndexOrThrow("PHONE")));
            clientDTO.setFaxNo(rawQuery.getString(rawQuery.getColumnIndexOrThrow("FAX")));
            clientDTO.setContactAdress(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Clients.ADDRESS_CONTACT)));
            clientDTO.setAddressLine1(rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_1")));
            clientDTO.setAddressLine2(rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_2")));
            clientDTO.setAddressLine3(rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_3")));
            clientDTO.setShippingAddress(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Clients.SHIPPING_ADDRESS_NAME)));
            clientDTO.setShippingLine1(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Clients.SHIPPING_ADDRESS_LINE_1)));
            clientDTO.setShippingLine2(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Clients.SHIPPING_ADDRESS_LINE_2)));
            clientDTO.setShippingLine3(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Clients.SHIPPING_ADDRESS_LINE_3)));
        }
        rawQuery.close();
        closeDB();
        return clientDTO;
    }

    public ArrayList<ClientDTO> getClientList() {
        openDb();
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_ALL_FROM_CLIENTS, null);
        ArrayList<ClientDTO> arrayList = new ArrayList();
        while (rawQuery.moveToNext()) {
            ClientDTO clientDTO = new ClientDTO();
            clientDTO.setId(rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id")));
            clientDTO.setClientName(rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME")));
            clientDTO.setEmailAddress(rawQuery.getString(rawQuery.getColumnIndexOrThrow("EMAIL")));
            clientDTO.setMobileNo(rawQuery.getString(rawQuery.getColumnIndexOrThrow("MOBILE")));
            clientDTO.setPhoneNo(rawQuery.getString(rawQuery.getColumnIndexOrThrow("PHONE")));
            clientDTO.setFaxNo(rawQuery.getString(rawQuery.getColumnIndexOrThrow("FAX")));
            clientDTO.setContactAdress(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Clients.ADDRESS_CONTACT)));
            clientDTO.setAddressLine1(rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_1")));
            clientDTO.setAddressLine2(rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_2")));
            clientDTO.setAddressLine3(rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_3")));
            clientDTO.setShippingAddress(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Clients.SHIPPING_ADDRESS_NAME)));
            clientDTO.setShippingLine1(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Clients.SHIPPING_ADDRESS_LINE_1)));
            clientDTO.setShippingLine2(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Clients.SHIPPING_ADDRESS_LINE_2)));
            clientDTO.setShippingLine3(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Clients.SHIPPING_ADDRESS_LINE_3)));
            getInvoiceByClient(clientDTO);
            getEstimateByClient(clientDTO);
            arrayList.add(clientDTO);
        }
        rawQuery.close();
        closeDB();
        return arrayList;
    }

    public ClientDTO getInvoiceByClient(ClientDTO clientDTO) {
        String[] strArr = new String[2];
        strArr[0] = "0";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(clientDTO.getId());
        strArr[1] = stringBuilder.toString();
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_CATALOG_BY_CLIENT, strArr);
        while (rawQuery.moveToNext()) {
            clientDTO.setTotalAmount((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Catalog.TOTAL_AMOUNT)));
            clientDTO.setDueAmount(clientDTO.getTotalAmount() - ((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Catalog.PAID))));
            clientDTO.setTotalInvoice(rawQuery.getInt(rawQuery.getColumnIndexOrThrow("TOTAL_CATALOG")));
        }
        rawQuery.close();
        return clientDTO;
    }

    public ClientDTO getEstimateByClient(ClientDTO clientDTO) {
        String[] strArr = new String[2];
        strArr[0] = "1";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(clientDTO.getId());
        strArr[1] = stringBuilder.toString();
        Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_CATALOG_BY_CLIENT, strArr);
        while (rawQuery.moveToNext()) {
            clientDTO.setTotalAmountEstimate((double) rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Catalog.TOTAL_AMOUNT)));
            clientDTO.setTotalEstimate(rawQuery.getInt(rawQuery.getColumnIndexOrThrow("TOTAL_CATALOG")));
        }
        rawQuery.close();
        return clientDTO;
    }

    public String getBusinessName() {
        String str = "";
        try {
            openDb();
            Cursor rawQuery = this.myDb.rawQuery(Query.SELECT_ALL_FROM_BUSINESS_INFORMATION, null);
            while (rawQuery.moveToNext()) {
                str = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME"));
            }
            rawQuery.close();
            closeDB();
        } catch (Exception unused) {
            return str;
        }
        return str;
    }

    public long createDuplicate(CatalogDTO catalogDTO, Context applicationContext) {
        long id = catalogDTO.getId();
        long saveInvoice = saveInvoice(catalogDTO);
        catalogDTO.setId(saveInvoice);
        if (saveInvoice > 0) {
            String stringBuilder;
            ArrayList invoiceItems = getInstance().getInvoiceItems(id);
            int i = 0;
            for (int i2 = 0; i2 < invoiceItems.size(); i2++) {
                ((ItemAssociatedDTO) invoiceItems.get(i2)).setCatalogId(saveInvoice);
                saveInvoiceItem((ItemAssociatedDTO) invoiceItems.get(i2));
            }
            InvoiceShippingDTO invoiceShipping = getInvoiceShipping(id);
            if (invoiceShipping.getId() > 0) {
                invoiceShipping.setCatalogId(saveInvoice);
                saveInvoiceShipping(invoiceShipping);
            }
            ArrayList payments = getPayments(id);
            for (int i3 = 0; i3 < payments.size(); i3++) {
                ((PaymentDTO) payments.get(i3)).setCatalogId(saveInvoice);
                addPayment((PaymentDTO) payments.get(i3));
            }
            String signedUrl = catalogDTO.getSignedUrl();
            if (!TextUtils.isEmpty(signedUrl)) {
                File file = new File(signedUrl);
                if (file.exists()) {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(MyConstants.getRootDirectory(applicationContext));
                    stringBuilder2.append(File.separator);
                    stringBuilder2.append("signature");
                    String stringBuilder3 = stringBuilder2.toString();
                    StringBuilder stringBuilder4 = new StringBuilder();
                    stringBuilder4.append(stringBuilder3);
                    stringBuilder4.append(File.separator);
                    stringBuilder4.append("Signature_");
                    stringBuilder4.append(timestamp);
                    stringBuilder4.append(".jpg");
                    stringBuilder = stringBuilder4.toString();
                    try {
                        replicateFile(file, new File(stringBuilder));
                        catalogDTO.setSignedUrl(stringBuilder);
                        SignedDTO signedDTO = new SignedDTO();
                        signedDTO.setCatalogId(saveInvoice);
                        signedDTO.setSignedDate(catalogDTO.getSignedDate());
                        signedDTO.setSignedUrl(stringBuilder);
                        updateInvoiceSignature(signedDTO);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            ArrayList invoiceItemImages = getInvoiceItemImages(id);
            while (i < invoiceItemImages.size()) {
                ((ImageDTO) invoiceItemImages.get(i)).setCatalogId(saveInvoice);
                String imageUrl = ((ImageDTO) invoiceItemImages.get(i)).getImageUrl();
                if (!TextUtils.isEmpty(imageUrl)) {
                    File file2 = new File(imageUrl);
                    if (file2.exists()) {
                        Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
                        stringBuilder = MyConstants.getRootDirectory(applicationContext);
                        StringBuilder stringBuilder5 = new StringBuilder();
                        stringBuilder5.append(stringBuilder);
                        stringBuilder5.append(File.separator);
                        stringBuilder5.append(timestamp2);
                        stringBuilder5.append(".jpg");
                        String stringBuilder6 = stringBuilder5.toString();
                        try {
                            replicateFile(file2, new File(stringBuilder6));
                            ((ImageDTO) invoiceItemImages.get(i)).setImageUrl(stringBuilder6);
                            saveInvoiceImage((ImageDTO) invoiceItemImages.get(i));
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
                i++;
            }
        }
        return saveInvoice;
    }

    private void replicateFile(File file, File file2) throws IOException {
        FileChannel channel = new FileInputStream(file).getChannel();
        FileChannel channel2 = new FileOutputStream(file2).getChannel();
        if (!(channel2 == null || channel == null)) {
            channel2.transferFrom(channel, 0, channel.size());
        }
        if (channel != null) {
            channel.close();
        }
        if (channel2 != null) {
            channel2.close();
        }
    }

    private void replicateFile(String str, Context context) throws IOException {
        if (!TextUtils.isEmpty(str)) {
            File file = new File(str);
            if (file.exists()) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(MyConstants.getRootDirectory(context));
                stringBuilder.append(File.separator);
                stringBuilder.append("signature");
                String stringBuilder2 = stringBuilder.toString();
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(stringBuilder2);
                stringBuilder3.append(File.separator);
                stringBuilder3.append("Signature_");
                stringBuilder3.append(timestamp);
                stringBuilder3.append(".jpg");
                File file2 = new File(stringBuilder3.toString());
                FileChannel channel = new FileInputStream(file).getChannel();
                FileChannel channel2 = new FileOutputStream(file2).getChannel();
                if (!(channel2 == null || channel == null)) {
                    channel2.transferFrom(channel, 0, channel.size());
                }
                if (channel != null) {
                    channel.close();
                }
                if (channel2 != null) {
                    channel2.close();
                }
            }
        }
    }

    public void truncateDB(Context context) {
        openDb();
        this.myDb.delete(Settings.TABLE_NAME, null, null);
        this.myDb.delete(Items_Associated.TABLE_NAME, null, null);
        this.myDb.delete(Items.TABLE_NAME, null, null);
        this.myDb.delete(Shipping.TABLE_NAME, null, null);
        this.myDb.delete(Payments.TABLE_NAME, null, null);
        this.myDb.delete(Catalog_Images.TABLE_NAME, null, null);
        this.myDb.delete(Clients.TABLE_NAME, null, null);
        this.myDb.delete(Business_Information.TABLE_NAME, null, null);
        this.myDb.delete(Catalog.TABLE_NAME, null, null);
        new DatabaseHelper(context).populateSettingsTable(this.myDb);
        closeDB();
    }

    public void openDb() {
        if (this.myDb == null) {
            this.myDb = DatabaseManager.getInstance().openDatabase();
        }
    }

    public void closeDB() {
        if (this.myDb != null) {
            DatabaseManager.getInstance().closeDatabase();
            this.myDb = null;
        }
    }
}


