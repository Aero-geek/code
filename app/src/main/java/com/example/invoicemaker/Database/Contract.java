package com.example.invoicemaker.Database;

import android.provider.BaseColumns;

public class Contract {

    public static abstract class Business_Information implements BaseColumns {
        public static final String BANK_INFORMATION = "BANK_INFORMATION";
        public static final String CHEQUES_INFORMATION = "CHEQUES_INFORMATION";
        public static final String LOGO_URL = "LOGO_URL";
        public static final String OTHER_PAYMENT_INFORMATION = "OTHER_PAYMENT_INFORMATION";
        public static final String PAYPAL_ADDRESS = "PAYPAL_ADDRESS";
        public static final String QTY_RATE = "QTY_RATE";
        public static final String REGISTRATION_NUMBER = "REGISTRATION_NUMBER";
        public static final String TABLE_NAME = "BUSINESS_INFORMATION";
        public static final String WEBSITE = "WEBSITE";
    }

    public static abstract class Catalog implements BaseColumns {
        public static final String CLIENTS_ID = "CLIENTS_ID";
        public static final String CREATED_AT = "CREATED_AT";
        public static final String DISCOUNT_TYPE = "DISCOUNT_TYPE";
        public static final String DUE_DATE = "DUE_DATE";
        public static final String ESTIMATE_STATUS = "ESTIMATE_STATUS";
        public static final String PAID = "PAID";
        public static final String PAID_STATUS = "PAID_STATUS";
        public static final String PO_NUMBER = "PO_NUMBER";
        public static final String SIGNED_DATE = "SIGNED_DATE";
        public static final String SIGNED_URL = "SIGNED_URL";
        public static final String TABLE_NAME = "CATALOG";
        public static final String TAX_LABEL = "TAX_LABEL";
        public static final String TAX_TYPE = "TAX_TYPE";
        public static final String TERMS = "TERMS";
        public static final String TOTAL_AMOUNT = "TOTAL_AMOUNT";
        public static final String TYPE = "TYPE";
    }

    public static abstract class Catalog_Images implements BaseColumns {
        public static final String ADDITIONAL_DETAILS = "ADDITIONAL_DETAILS";
        public static final String IMAGE_URL = "IMAGE_URL";
        public static final String TABLE_NAME = "CATALOG_IMAGES";
    }

    public static abstract class Clients implements BaseColumns {
        public static final String ADDRESS_CONTACT = "ADDRESS_CONTACT";
        public static final String SHIPPING_ADDRESS_LINE_1 = "SHIPPING_ADDRESS_LINE_1";
        public static final String SHIPPING_ADDRESS_LINE_2 = "SHIPPING_ADDRESS_LINE_2";
        public static final String SHIPPING_ADDRESS_LINE_3 = "SHIPPING_ADDRESS_LINE_3";
        public static final String SHIPPING_ADDRESS_NAME = "SHIPPING_ADDRESS_NAME";
        public static final String TABLE_NAME = "CLIENTS";
    }

    public static abstract class Items implements BaseColumns {
        public static final String TABLE_NAME = "ITEMS";
    }

    public static abstract class Items_Associated implements BaseColumns {
        public static final String ITEM_NAME = "ITEM_NAME";
        public static final String QTY = "QTY";
        public static final String TABLE_NAME = "ITEMS_ASSOCIATED";
    }

    public static abstract class Payments implements BaseColumns {
        public static final String DATE = "DATE";
        public static final String METHOD = "METHOD";
        public static final String TABLE_NAME = "PAYMENTS";
    }

    public static abstract class Settings implements BaseColumns {
        public static final String CURRENCY = "CURRENCY";
        public static final String DATE_FORMAT = "DATE_FORMAT";
        public static final String TABLE_NAME = "SETTINGS";
    }

    public static abstract class Shipping implements BaseColumns {
        public static final String FOB = "FOB";
        public static final String SHIPPING_DATE = "SHIPPING_DATE";
        public static final String SHIP_VIA = "SHIP_VIA";
        public static final String TABLE_NAME = "SHIPPING";
        public static final String TRACKING = "TRACKING";
    }
}
