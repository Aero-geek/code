package com.example.invoicemaker.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.invoicemaker.Activity.AddClientActivity;
import com.example.invoicemaker.Activity.AddItemActivity;
import com.example.invoicemaker.Activity.AddPhotoActivity;
import com.example.invoicemaker.Activity.DiscountActivity;
import com.example.invoicemaker.Activity.InvoiceInfoActivity;
import com.example.invoicemaker.Activity.InvoicePreviewActivity;
import com.example.invoicemaker.Activity.PaymentActivity;
import com.example.invoicemaker.Activity.PaymentOptionActivity;
import com.example.invoicemaker.Activity.SearchClientActivity;
import com.example.invoicemaker.Activity.ShippingInfoActivity;
import com.example.invoicemaker.Activity.SignatureActivity;
import com.example.invoicemaker.Activity.TaxActivity;
import com.example.invoicemaker.Adapter.ItemAssociatedAdapter;
import com.example.invoicemaker.Adapter.ItemImageAdapter;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.BusinessDTO;
import com.example.invoicemaker.Dto.CatalogDTO;
import com.example.invoicemaker.Dto.ClientDTO;
import com.example.invoicemaker.Dto.DiscountDTO;
import com.example.invoicemaker.Dto.ImageDTO;
import com.example.invoicemaker.Dto.ItemAssociatedDTO;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.Dto.SignedDTO;
import com.example.invoicemaker.Dto.TaxDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.DataProcessor;
import com.example.invoicemaker.utils.ModelChangeListener;
import com.example.invoicemaker.utils.MyConstants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.itextpdf.text.pdf.security.SecurityConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class EditInvoiceFragment extends Fragment implements OnClickListener, ModelChangeListener {
    public String TAG = "EditInvoiceFragment";
    ImageView bank_details_edit_img;
    LinearLayout signLay;
    ImageView addClientImg;
    private BusinessDTO businessDTO;
    private TextView business_name;
    private CatalogDTO catalogDTO;
    private TextView client_name;
    private TextView creation_date;
    private String currency_sign;
    private TextView discount_text;
    private TextView due_amount;
    private TextView due_info;
    private ImageDTO imageDTO;
    private ArrayList<ImageDTO> imageDTOS;
    private TextView invoice_discount;
    private RecyclerView invoice_items_rv;
    private TextView invoice_name;
    private EditText invoice_notes;
    private ItemAssociatedAdapter itemAssociatedAdapter;
    private ItemAssociatedDTO itemAssociatedDTO;
    private ArrayList<ItemAssociatedDTO> itemAssociatedDTOS;
    private ItemImageAdapter itemImageAdapter;
    private RecyclerView item_images_rv;
    private Activity mActivity;
    private NestedScrollView nestedScrollView;
    private TextView paid_amount;
    private TextView payment_option;
    private TextView payment_option_text;
    private FloatingActionButton preview_fab;
    private SettingsDTO settingsDTO;
    private TextView shipping_amount;
    private TextView signed_date;
    private TextView subtotal_amount;
    private TextView tax_amount;
    private TextView tax_text;
    private TextView total_amount;
    private TextView total_cost;
    private TextView unit_cost_quantity;
    private View view;

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        if (this.view == null) {
            this.view = layoutInflater.inflate(R.layout.edit_invoice_layout, viewGroup, false);
            DataProcessor.getInstance().addChangeListener(this);
            this.itemAssociatedDTOS = new ArrayList();
            this.imageDTOS = new ArrayList();
            getIntentData();
            this.businessDTO = LoadDatabase.getInstance().getBusinessInformation();
            this.settingsDTO = SettingsDTO.getSettingsDTO();


            initLayout();
            if (this.catalogDTO.getId() > 0) {
                new AsyncCaller().execute(new Void[0]);
            }
            CharSequence makeBusinessString = makeBusinessString();
            if (makeBusinessString.length() > 0) {
                TextView textView = this.payment_option_text;

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.mActivity.getResources().getString(R.string.payment_options));
                stringBuilder.append(":");
                textView.setText(stringBuilder.toString());
                bank_details_edit_img.setVisibility(View.GONE);
                this.payment_option.setText(makeBusinessString);
                this.payment_option.setVisibility(View.VISIBLE);
            } else {
                bank_details_edit_img.setVisibility(View.VISIBLE);
                this.payment_option.setText("");
                this.payment_option.setVisibility(View.GONE);
            }
            if (this.businessDTO != null) {
                this.business_name.setText("");
            } else {
                this.business_name.setText(this.mActivity.getResources().getString(R.string.business_info_message));
            }
        }
        return this.view;
    }

    private void getIntentData() {
        this.catalogDTO = (CatalogDTO) getArguments().getSerializable(MyConstants.CATALOG_DTO);
        if (this.catalogDTO == null) {
            this.catalogDTO = new CatalogDTO();
        }
    }

    public String makeBusinessString() {

        String r0 = "";

        if (!TextUtils.isEmpty(businessDTO.getBankInformation())) {

            StringBuilder businifo0 = new StringBuilder();
            businifo0.append(r0);
            businifo0.append("Bank transfer, ");
            r0 = businifo0.toString();

            return r0;

        }

        if (!TextUtils.isEmpty(businessDTO.getPaypalAddress())) {

            StringBuilder businifo1 = new StringBuilder();
            businifo1.append(r0);
            businifo1.append("PayPal, ");
            r0 = businifo1.toString();

            return r0;

        }

        if (!TextUtils.isEmpty(businessDTO.getCheckInformation())) {

            StringBuilder businifo3 = new StringBuilder();
            businifo3.append(r0);
            businifo3.append("Check, ");
            r0 = businifo3.toString();

            return r0;

        }

        if (!TextUtils.isEmpty(businessDTO.getOtherPaymentInformation())) {

            StringBuilder businifo4 = new StringBuilder();
            businifo4.append(r0);
            businifo4.append("Other");
            r0 = businifo4.toString();


            return r0;
        }


        String r1 = ", $";
        String r2 = "";


        r1 = r0.replaceAll(r1, r2);


        return r1;
    }

    private void loadInvoiceItems() {
        this.itemAssociatedDTOS.clear();
        Collection invoiceItems = LoadDatabase.getInstance().getInvoiceItems(this.catalogDTO.getId());
        if (invoiceItems != null && invoiceItems.size() > 0) {
            this.itemAssociatedDTOS.addAll(invoiceItems);
        }
    }

    private void loadItemImages() {
        this.imageDTOS.clear();
        Collection invoiceItemImages = LoadDatabase.getInstance().getInvoiceItemImages(this.catalogDTO.getId());
        if (invoiceItemImages != null && invoiceItemImages.size() > 0) {
            this.imageDTOS.addAll(invoiceItemImages);
        }
    }

    private void initLayout() {
        this.invoice_name = (TextView) this.view.findViewById(R.id.invoice_name);
        this.due_info = (TextView) this.view.findViewById(R.id.due_info);
        this.business_name = (TextView) this.view.findViewById(R.id.business_name);
        this.creation_date = (TextView) this.view.findViewById(R.id.creation_date);
        addClientImg = view.findViewById(R.id.addClientImg);
        this.client_name = (TextView) this.view.findViewById(R.id.client_name);
        this.invoice_discount = (TextView) this.view.findViewById(R.id.invoice_discount);
        this.shipping_amount = (TextView) this.view.findViewById(R.id.shipping_amount);
        this.tax_amount = (TextView) this.view.findViewById(R.id.tax_amount);
        this.total_amount = (TextView) this.view.findViewById(R.id.total_amount);
        this.paid_amount = (TextView) this.view.findViewById(R.id.paid_amount);
        this.due_amount = (TextView) this.view.findViewById(R.id.due_amount);
        this.signed_date = (TextView) this.view.findViewById(R.id.signed_date);
        this.invoice_notes = (EditText) this.view.findViewById(R.id.invoice_notes);
        this.discount_text = (TextView) this.view.findViewById(R.id.discount_text);
        this.subtotal_amount = (TextView) this.view.findViewById(R.id.subtotal_amount);
        this.tax_text = (TextView) this.view.findViewById(R.id.tax_text);
        this.payment_option_text = (TextView) this.view.findViewById(R.id.payment_option_text);
        this.payment_option = (TextView) this.view.findViewById(R.id.payment_option);
        bank_details_edit_img = view.findViewById(R.id.bank_details_edit_img);
        this.unit_cost_quantity = (TextView) this.view.findViewById(R.id.unit_cost_quantity);
        this.total_cost = (TextView) this.view.findViewById(R.id.total_cost);
        this.currency_sign = MyConstants.formatCurrency(this.mActivity, this.settingsDTO.getCurrencyFormat());
        signLay = view.findViewById(R.id.signed_Lay);
        TextView textView = this.unit_cost_quantity;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1 * ");
        stringBuilder.append(this.currency_sign);
        stringBuilder.append("0.00");
        textView.setText(stringBuilder.toString());
        textView = this.total_cost;
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.currency_sign);
        stringBuilder.append("0.00");
        textView.setText(stringBuilder.toString());
        if (this.catalogDTO.getId() > 0) {
            updateInvoiceInfo();
        } else {
            addInvoiceInfo();
        }
        if (MyConstants.CATALOG_TYPE == 1) {
            this.due_info.setVisibility(View.GONE);
        }
        signLay.setOnClickListener(this);
        this.view.findViewById(R.id.discount_layout).setOnClickListener(this);
        this.view.findViewById(R.id.add_photo_layout).setOnClickListener(this);
        this.view.findViewById(R.id.invoice_info_layout).setOnClickListener(this);
        this.view.findViewById(R.id.preview_fab).setOnClickListener(this);
        this.view.findViewById(R.id.client_layout).setOnClickListener(this);
        this.view.findViewById(R.id.add_item_layout).setOnClickListener(this);
        this.view.findViewById(R.id.shipping_layout).setOnClickListener(this);
        this.view.findViewById(R.id.tax_layout).setOnClickListener(this);
        this.view.findViewById(R.id.paid_layout).setOnClickListener(this);
        this.view.findViewById(R.id.signed_date).setOnClickListener(this);
        this.view.findViewById(R.id.payment_option_layout).setOnClickListener(this);
        this.nestedScrollView = (NestedScrollView) this.view.findViewById(R.id.nested_scrollview);
        this.nestedScrollView.setSmoothScrollingEnabled(true);
        this.invoice_items_rv = (RecyclerView) this.view.findViewById(R.id.invoice_items_rv);
        this.invoice_items_rv.setHasFixedSize(true);
        this.invoice_items_rv.setNestedScrollingEnabled(false);
        this.invoice_items_rv.setLayoutManager(new LinearLayoutManager(this.mActivity, RecyclerView.VERTICAL, false));
        this.itemAssociatedAdapter = new ItemAssociatedAdapter(this.mActivity, this.itemAssociatedDTOS, this.catalogDTO.getDiscountType(), this.catalogDTO.getTaxType());
        this.invoice_items_rv.setAdapter(this.itemAssociatedAdapter);
        this.item_images_rv = (RecyclerView) this.view.findViewById(R.id.item_images_rv);
        this.item_images_rv.setHasFixedSize(true);
        this.item_images_rv.setNestedScrollingEnabled(false);
        this.item_images_rv.setLayoutManager(new LinearLayoutManager(this.mActivity, RecyclerView.VERTICAL, false));
        this.itemImageAdapter = new ItemImageAdapter(this.mActivity, this.imageDTOS);
        this.item_images_rv.setAdapter(this.itemImageAdapter);
        if (MyConstants.CATALOG_TYPE == 1) {
            this.view.findViewById(R.id.paid_layout).setVisibility(View.GONE);
        }
        this.invoice_notes.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                long id = EditInvoiceFragment.this.catalogDTO.getId();
                if (id == 0) {
                    id = MyConstants.createNewInvoice();
                }
                catalogDTO.setNotes(EditInvoiceFragment.this.invoice_notes.getText().toString().trim());
                LoadDatabase.getInstance().updateInvoiceNotes(id, EditInvoiceFragment.this.invoice_notes.getText().toString().trim());
            }
        });
    }

    private void updateInvoiceInfo() {
        this.invoice_name.setText(this.catalogDTO.getCatalogName());
        if (this.catalogDTO.getPaidStatus() != 2) {
            switch (this.catalogDTO.getTerms()) {
                case 0:
                    this.due_info.setText("");
                    this.due_info.setTextColor(this.mActivity.getResources().getColor(R.color.white));
                    break;
                case 1:
                    this.due_info.setText("Due on receipt");
                    this.due_info.setTextColor(this.mActivity.getResources().getColor(R.color.white));
                    break;
                default:
                    Date date = new Date();
                    Date date2 = new Date();
                    date2.setTime(Long.parseLong(this.catalogDTO.getDueDate()));
                    double ceil = Math.ceil(((double) (date2.getTime() - date.getTime())) / 8.64E7d);
                    if (ceil <= 0.0d) {
                        if (ceil != 0.0d) {
                            this.due_info.setText("Overdue");
                            this.due_info.setTextColor(this.mActivity.getResources().getColor(R.color.delete_color_bg));
                            break;
                        }
                        this.due_info.setText("Due on receipt");
                        this.due_info.setTextColor(this.mActivity.getResources().getColor(R.color.white));
                        break;
                    }
                    TextView textView = this.due_info;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Due in ");
                    stringBuilder.append((int) ceil);
                    stringBuilder.append(" Days");
                    textView.setText(stringBuilder.toString());
                    this.due_info.setTextColor(this.mActivity.getResources().getColor(R.color.white));
                    break;
            }
        }
        this.due_info.setText("Paid");
        this.due_info.setTextColor(this.mActivity.getResources().getColor(R.color.green));
        this.creation_date.setText(MyConstants.formatDate(this.mActivity, Long.parseLong(this.catalogDTO.getCreationDate()), this.settingsDTO.getDateFormat()));
        if (this.catalogDTO.getClientDTO().getId() > 0) {
            addClientImg.setVisibility(View.GONE);
            this.client_name.setText(this.catalogDTO.getClientDTO().getClientName());
            this.client_name.setTextColor(this.mActivity.getResources().getColor(R.color.white));
            return;
        }
        addClientImg.setVisibility(View.VISIBLE);
        // this.client_name.setTextColor(this.mActivity.getResources().getColor(R.color.white));
        // this.client_name.setText("Client");
    }

    private void addInvoiceInfo() {
        this.invoice_name.setText(MyConstants.getInvoiceName());
        this.business_name.setText(this.mActivity.getResources().getString(R.string.business_info_message));
        if (MyConstants.CATALOG_TYPE == 1) {
            this.due_info.setVisibility(View.INVISIBLE);
        } else {
            this.due_info.setText(this.mActivity.getResources().getString(R.string.due_on_receipt));
        }
        this.creation_date.setText(MyConstants.formatDate(this.mActivity, Calendar.getInstance().getTimeInMillis(), this.settingsDTO.getDateFormat()));
        addClientImg.setVisibility(View.VISIBLE);
        //this.client_name.setText("Client");
        this.signed_date.setText(SecurityConstants.Signature);
    }

    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.add_item_layout:
                AddItemActivity.start(this.mActivity, null, this.catalogDTO.getId(), this.catalogDTO.getDiscountType(), this.catalogDTO.getTaxType());
                break;
            case R.id.add_photo_layout:
                AddPhotoActivity.start(this.mActivity, null, this.catalogDTO.getId());
                break;
            case R.id.client_layout:
                if ((this.catalogDTO.getClientDTO() != null && this.catalogDTO.getClientDTO().getId() > 0) || LoadDatabase.getInstance().getClientList().size() <= 0) {
                    AddClientActivity.start(this.mActivity, this.catalogDTO.getClientDTO(), this.catalogDTO.getId(), true);
                    break;
                } else {
                    SearchClientActivity.start(this.mActivity, this.catalogDTO.getId(), true);
                    break;
                }
            case R.id.discount_layout:
                DiscountActivity.start(view.getContext(), this.catalogDTO.getId(), this.catalogDTO.getDiscountType(), this.catalogDTO.getDiscountAmount(), this.catalogDTO.getSubTotalAmount());
                break;
            case R.id.invoice_info_layout:
                InvoiceInfoActivity.start(view.getContext(), this.catalogDTO);
                break;
            case R.id.paid_layout:
                PaymentActivity.start(view.getContext(), this.catalogDTO.getId(), this.catalogDTO.getPaidAmount(), this.catalogDTO.getTotalAmount());
                break;
            case R.id.payment_option_layout:
                PaymentOptionActivity.start(view.getContext(), this.businessDTO, this.catalogDTO.getId());
                break;
            case R.id.preview_fab:
                AdsProvider.provider.showInterstitial(getActivity(), () -> {
                    InvoicePreviewActivity.start(view.getContext(), this.catalogDTO);
                });
                break;
            case R.id.shipping_layout:
                if (this.catalogDTO.getClientDTO().getId() != 0) {
                    ShippingInfoActivity.start(this.mActivity, this.catalogDTO.getInvoiceShippingDTO(), this.catalogDTO.getId());
                    break;
                } else {
                    Toast.makeText(this.mActivity, "Please choose a client first", Toast.LENGTH_SHORT).show();
                    return;
                }
            case R.id.signed_Lay:
            case R.id.signed_date:
                SignatureActivity.start(view.getContext(), this.catalogDTO.getId(), this.catalogDTO.getSignedDate(), this.catalogDTO.getSignedUrl());
                break;
            case R.id.tax_layout:
                TaxActivity.start(view.getContext(), this.catalogDTO.getId(), this.catalogDTO.getTaxType(), this.catalogDTO.getTaxLabel(), this.catalogDTO.getTaxRate());
                break;
        }
    }

    public void onReceiveModelChange(String str, int i) {
        if (!MyConstants.createDuplicateEntry) {
            try {
                Gson gson = new Gson();
                switch (i) {
                    case 3001:
                    case 3002:
                    case 3003:
                        this.itemAssociatedDTO = (ItemAssociatedDTO) gson.fromJson(str, ItemAssociatedDTO.class);
                        LoadDatabase.getInstance().calculateInvoiceBalance(this.catalogDTO.getId());
                        updateInvoiceInfo();
                        break;
                    case 3004:
                    case 3005:
                    case 3006:
                        this.imageDTO = (ImageDTO) gson.fromJson(str, ImageDTO.class);
                        break;
                    default:
                        break;
                }
                switch (i) {
                    case 101:
                        CatalogDTO catalogDTO = (CatalogDTO) gson.fromJson(str, CatalogDTO.class);
                        this.catalogDTO.setSubTotalAmount(catalogDTO.getSubTotalAmount());
                        this.catalogDTO.setDiscountType(catalogDTO.getDiscountType());
                        this.catalogDTO.setDiscountAmount(catalogDTO.getDiscountAmount());
                        this.catalogDTO.setTaxType(catalogDTO.getTaxType());
                        this.catalogDTO.setTaxLabel(catalogDTO.getTaxLabel());
                        this.catalogDTO.setTaxRate(catalogDTO.getTaxRate());
                        this.catalogDTO.setTaxAmount(catalogDTO.getTaxAmount());
                        this.catalogDTO.setInvoiceShippingDTO(catalogDTO.getInvoiceShippingDTO());
                        this.catalogDTO.setTotalAmount(catalogDTO.getTotalAmount());
                        this.catalogDTO.setPaidAmount(catalogDTO.getPaidAmount());
                        this.catalogDTO.setPaidStatus(catalogDTO.getPaidStatus());
                        this.catalogDTO.setNotes(catalogDTO.getNotes());
                        updateInvoiceBalance();
                        break;
                    case 102:
                        DiscountDTO discountDTO = (DiscountDTO) gson.fromJson(str, DiscountDTO.class);
                        if (this.itemAssociatedAdapter != null) {
                            this.itemAssociatedAdapter.updateDiscount(discountDTO.getDiscountType());
                            this.itemAssociatedAdapter.notifyDataSetChanged();
                        }
                        if (discountDTO.getDiscountType() == 2) {
                            this.catalogDTO.setDiscount(discountDTO.getDiscountAmount());
                        }
                        LoadDatabase.getInstance().calculateInvoiceBalance(this.catalogDTO.getId());
                        updateInvoiceInfo();
                        break;
                    case 103:
                        TaxDTO taxDTO = (TaxDTO) gson.fromJson(str, TaxDTO.class);
                        if (this.itemAssociatedAdapter != null) {
                            this.itemAssociatedAdapter.updateTax(taxDTO.getTaxType());
                        }
                        LoadDatabase.getInstance().calculateInvoiceBalance(this.catalogDTO.getId());
                        updateInvoiceInfo();
                        break;
                    case 104:
                        LoadDatabase.getInstance().calculateInvoiceBalance(this.catalogDTO.getId());
                        updateInvoiceInfo();
                        break;
                    case 105:
                        SignedDTO signedDTO = (SignedDTO) gson.fromJson(str, SignedDTO.class);
                        this.catalogDTO.setSignedDate(signedDTO.getSignedDate());
                        this.catalogDTO.setSignedUrl(signedDTO.getSignedUrl());
                        if (TextUtils.isEmpty(this.catalogDTO.getSignedDate())) {
                            this.signed_date.setText(SecurityConstants.Signature);
                            break;
                        }
                        TextView strss = this.signed_date;
                        StringBuilder iss = new StringBuilder();
                        iss.append("Signed ");
                        iss.append(MyConstants.formatDate(this.mActivity, Long.parseLong(this.catalogDTO.getSignedDate()), this.settingsDTO.getDateFormat()));
                        strss.setText(iss.toString());
                        break;
                    default:
                        switch (i) {
                            case 2001:
                            case MyConstants.ACTION_INVOICE_UPDATED /*2002*/:
                                this.catalogDTO = (CatalogDTO) gson.fromJson(str, CatalogDTO.class);
                                updateInvoiceInfo();
                                break;
                            default:
                                int i2 = 0;
                                switch (i) {
                                    case 3001:
                                        this.itemAssociatedDTOS.add(this.itemAssociatedDTO);
                                        this.itemAssociatedAdapter.notifyDataSetChanged();
                                        if (this.invoice_items_rv.getVisibility() != View.VISIBLE) {
                                            this.invoice_items_rv.setVisibility(View.VISIBLE);
                                            break;
                                        }
                                        break;
                                    case 3002:
                                        while (i2 < this.itemAssociatedDTOS.size()) {
                                            if (((ItemAssociatedDTO) this.itemAssociatedDTOS.get(i2)).getId() == this.itemAssociatedDTO.getId()) {
                                                ItemAssociatedDTO itemAssociatedDTO = (ItemAssociatedDTO) this.itemAssociatedDTOS.get(i2);
                                                itemAssociatedDTO.setItemName(this.itemAssociatedDTO.getItemName());
                                                itemAssociatedDTO.setDescription(this.itemAssociatedDTO.getDescription());

                                                itemAssociatedDTO.setUnitCost(String.valueOf(MyConstants.formatDecimal(Double.valueOf(itemAssociatedDTO.getUnitCost()))));
                                                itemAssociatedDTO.setTaxAble(this.itemAssociatedDTO.getTaxAble());
                                                itemAssociatedDTO.setQuantity(this.itemAssociatedDTO.getQuantity());
                                                itemAssociatedDTO.setTaxRate(this.itemAssociatedDTO.getTaxRate());
                                                itemAssociatedDTO.setDiscount(this.itemAssociatedDTO.getDiscount());
                                                itemAssociatedDTO.setTotalAmount(String.valueOf(MyConstants.formatDecimal(Double.valueOf(itemAssociatedDTO.getTotalAmount()))));
                                                this.itemAssociatedAdapter.notifyItemChanged(i2);
                                                break;
                                            }
                                            i2++;
                                        }
                                        break;
                                    case 3003:
                                        while (i2 < this.itemAssociatedDTOS.size()) {
                                            if (((ItemAssociatedDTO) this.itemAssociatedDTOS.get(i2)).getId() == this.itemAssociatedDTO.getId()) {
                                                this.itemAssociatedDTOS.remove(i2);
                                                this.itemAssociatedAdapter.notifyItemRemoved(i2);
                                                if (this.itemAssociatedDTOS.size() == 0) {
                                                    this.invoice_items_rv.setVisibility(View.GONE);
                                                    break;
                                                }
                                            }
                                            i2++;
                                        }
                                        break;
                                    case 3004:
                                        this.imageDTOS.add(this.imageDTO);
                                        this.itemImageAdapter.notifyDataSetChanged();
                                        if (this.item_images_rv.getVisibility() != View.VISIBLE) {
                                            this.item_images_rv.setVisibility(View.VISIBLE);
                                            break;
                                        }
                                        break;
                                    case 3005:
                                        while (i2 < this.imageDTOS.size()) {
                                            if (((ImageDTO) this.imageDTOS.get(i2)).getId() == this.imageDTO.getId()) {
                                                ImageDTO imageDTO = (ImageDTO) this.imageDTOS.get(i2);
                                                imageDTO.setImageUrl(this.imageDTO.getImageUrl());
                                                imageDTO.setDescription(this.imageDTO.getDescription());
                                                imageDTO.setAdditionalDetails(this.imageDTO.getAdditionalDetails());
                                                this.itemImageAdapter.notifyItemChanged(i2);
                                                break;
                                            }
                                            i2++;
                                        }
                                        break;
                                    case 3006:
                                        while (i2 < this.imageDTOS.size()) {
                                            if (((ImageDTO) this.imageDTOS.get(i2)).getId() == this.imageDTO.getId()) {
                                                this.imageDTOS.remove(i2);
                                                this.itemImageAdapter.notifyItemRemoved(i2);
                                                if (this.imageDTOS.size() == 0) {
                                                    this.item_images_rv.setVisibility(View.GONE);
                                                    break;
                                                }
                                            }
                                            i2++;
                                        }
                                        break;
                                    default:
                                        switch (i) {
                                            case MyConstants.ACTION_INVOICE_CLIENT_ADDED /*4001*/:
                                            case MyConstants.ACTION_INVOICE_CLIENT_UPDATED /*4002*/:
                                                this.catalogDTO.setClientDTO((ClientDTO) gson.fromJson(str, ClientDTO.class));
                                                addClientImg.setVisibility(View.GONE);
                                                this.client_name.setText(this.catalogDTO.getClientDTO().getClientName());
                                                this.client_name.setTextColor(this.mActivity.getResources().getColor(R.color.white));
                                                break;
                                            case MyConstants.ACTION_INVOICE_CLIENT_DELETED /*4003*/:
                                                this.catalogDTO.getClientDTO().setId(0);
                                                addClientImg.setVisibility(View.VISIBLE);
                                                //this.client_name.setText("No Client");
                                                // this.client_name.setTextColor(this.mActivity.getResources().getColor(R.color.white));
                                                break;
                                            default:
                                                switch (i) {
                                                    case MyConstants.ACTION_INVOICE_SHIPPING_ADDED /*5001*/:
                                                    case MyConstants.ACTION_INVOICE_SHIPPING_UPDATED /*5002*/:
                                                    case MyConstants.ACTION_INVOICE_SHIPPING_DELETED /*5003*/:
                                                        LoadDatabase.getInstance().calculateInvoiceBalance(this.catalogDTO.getId());
                                                        updateInvoiceInfo();
                                                        break;
                                                    default:
                                                        switch (i) {
                                                            case MyConstants.ACTION_BUSINESS_INFORMATION_ADDED /*10001*/:
                                                            case MyConstants.ACTION_BUSINESS_INFORMATION_UPDATED /*10002*/:
                                                                BusinessDTO businessDTO = (BusinessDTO) gson.fromJson(str, BusinessDTO.class);
                                                                this.businessDTO.setPaypalAddress(businessDTO.getPaypalAddress());
                                                                this.businessDTO.setCheckInformation(businessDTO.getCheckInformation());
                                                                this.businessDTO.setBankInformation(businessDTO.getBankInformation());
                                                                this.businessDTO.setOtherPaymentInformation(businessDTO.getOtherPaymentInformation());
                                                                str = makeBusinessString();
                                                                if (str.length() <= 0) {
                                                                    bank_details_edit_img.setVisibility(View.VISIBLE);
                                                                    this.payment_option.setText("");
                                                                    this.payment_option.setVisibility(View.GONE);
                                                                    break;
                                                                }
                                                                TextView iaaa = this.payment_option_text;
                                                                StringBuilder stringBuilder = new StringBuilder();
                                                                stringBuilder.append(this.mActivity.getResources().getString(R.string.payment_options));
                                                                stringBuilder.append(":");
                                                                iaaa.setText(stringBuilder.toString());
                                                                bank_details_edit_img.setVisibility(View.GONE);
                                                                this.payment_option.setText(str);
                                                                this.payment_option.setVisibility(View.VISIBLE);

                                                                break;
                                                            default:
                                                                break;
                                                        }
                                                }
                                        }
                                }
                        }
                }
            } catch (Exception str2) {
                str2.printStackTrace();
            }
        }
    }

    private void updateInvoiceBalance() {
        StringBuilder stringBuilder;
        TextView textView = this.shipping_amount;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        String d = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getInvoiceShippingDTO().getAmount())));
        if (d.contains(".")) {
            int dotPos = String.valueOf(d).lastIndexOf(".");
            String subStr = String.valueOf(d).substring(dotPos);
            if (subStr.length() <= 2) {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getInvoiceShippingDTO().getAmount())) + "0");
            } else {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getInvoiceShippingDTO().getAmount())));
            }
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getInvoiceShippingDTO().getAmount())));

        }

        textView.setText(stringBuilder2.toString());
        textView = this.tax_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        String d1 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));
        if (d1.contains(".")) {
            int dotPos1 = String.valueOf(d1).lastIndexOf(".");
            String subStr1 = String.valueOf(d1).substring(dotPos1);
            if (subStr1.length() <= 2) {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())) + "0");
            } else {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));
            }
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));
        }

        textView.setText(stringBuilder2.toString());
        textView = this.total_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        //  stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount())));

        String d2 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount())));
        if (d2.contains(".")) {

            int dotPos2 = String.valueOf(d2).lastIndexOf(".");
            String subStr2 = String.valueOf(d2).substring(dotPos2);
            if (subStr2.length() <= 2) {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount())) + "0");
            } else {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount())));
            }
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount())));
        }

        textView.setText(stringBuilder2.toString());
        textView = this.paid_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        //  stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));

        String d3 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));
        if (d3.contains(".")) {
            int dotPos3 = String.valueOf(d3).lastIndexOf(".");
            String subStr3 = String.valueOf(d3).substring(dotPos3);
            if (subStr3.length() <= 2) {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())) + "0");
            } else {
            }
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));

        }

        textView.setText(stringBuilder2.toString());
        if (TextUtils.isEmpty(this.catalogDTO.getSignedDate())) {
            this.signed_date.setText(SecurityConstants.Signature);
        } else {
            textView = this.signed_date;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Signed ");
            stringBuilder2.append(MyConstants.formatDate(this.mActivity, Long.parseLong(this.catalogDTO.getSignedDate()), this.settingsDTO.getDateFormat()));
            textView.setText(stringBuilder2.toString());
        }
        this.invoice_notes.setText(this.catalogDTO.getNotes());
        textView = this.subtotal_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        //  stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getSubTotalAmount())));

        String d4 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getSubTotalAmount())));
        if (d4.contains(".")) {
            int dotPos4 = String.valueOf(d4).lastIndexOf(".");
            String subStr4 = String.valueOf(d4).substring(dotPos4);
            if (subStr4.length() <= 2) {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getSubTotalAmount())) + "0");
            } else {
            }
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getSubTotalAmount())));

        }

        Log.e(TAG, "updateInvoiceBalance 1: " + String.valueOf(Double.valueOf(this.catalogDTO.getSubTotalAmount())));
        textView.setText(stringBuilder2.toString());
        if (this.catalogDTO.getDiscountType() == 2) {
            textView = this.discount_text;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Discount (");
            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getDiscount())));
            stringBuilder.append("%)");
            textView.setText(stringBuilder.toString());
        } else {
            this.discount_text.setText("Discount");
        }
        textView = this.invoice_discount;
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.currency_sign);
        String d5 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getDiscountAmount())));
        if (d5.contains(".")) {


            int dotPos5 = String.valueOf(d5).lastIndexOf(".");
            String subStr5 = String.valueOf(d5).substring(dotPos5);
            if (subStr5.length() <= 2) {
                stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getDiscountAmount())) + "0");
            } else {
                stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getDiscountAmount())));
            }
        } else {
            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getDiscountAmount())));

        }
        textView.setText(stringBuilder.toString());
        if (this.catalogDTO.getTaxType() == 3 || this.catalogDTO.getTaxType() == 2) {
            this.tax_text.setText("Tax");
        } else {
            textView = this.tax_text;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Tax (");

            String d6 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxRate())));
            if (d6.contains(".")) {
                int dotPos6 = String.valueOf(d6).lastIndexOf(".");
                String subStr6 = String.valueOf(d6).substring(dotPos6);
                if (subStr6.length() <= 2) {
                    stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxRate())) + "0");
                } else {
                    stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxRate())));
                }
            } else {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxRate())));

            }

            // stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxRate())));
            stringBuilder2.append("%)");
            textView.setText(stringBuilder2.toString());
        }
        textView = this.tax_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);

        String d6 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));
        if (d6.contains(".")) {

            int dotPos6 = String.valueOf(d6).lastIndexOf(".");
            String subStr6 = String.valueOf(d6).substring(dotPos6);
            if (subStr6.length() <= 2) {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())) + "0");
            } else {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));
            }
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));

        }

        // stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));
        textView.setText(stringBuilder2.toString());
        textView = this.paid_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);

        String d7 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));
        if (d7.contains(".")) {

            int dotPos7 = String.valueOf(d7).lastIndexOf(".");
            String subStr7 = String.valueOf(d7).substring(dotPos7);
            if (subStr7.length() <= 2) {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())) + "0");
            } else {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));
            }
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));

        }

        // stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));
        textView.setText(stringBuilder2.toString());
        textView = this.due_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);

        String d8 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount() - this.catalogDTO.getPaidAmount())));
        if (d8.contains(".")) {

            int dotPos8 = String.valueOf(d8).lastIndexOf(".");
            String subStr8 = String.valueOf(d8).substring(dotPos8);
            if (subStr8.length() <= 2) {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount() - this.catalogDTO.getPaidAmount())) + "0");
            } else {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount() - this.catalogDTO.getPaidAmount())));
            }
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount() - this.catalogDTO.getPaidAmount())));

        }
        //   stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount() - this.catalogDTO.getPaidAmount())));
        textView.setText(stringBuilder2.toString());
    }

    public void onDestroy() {
        super.onDestroy();
        DataProcessor.getInstance().removeChangeListener(this);
    }
    /*{
        StringBuilder stringBuilder;
        TextView textView = this.shipping_amount;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        String d = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getInvoiceShippingDTO().getAmount())));
        if (d.contains(".")) {
            int dotPos = String.valueOf(d).lastIndexOf(".");
            String subStr = String.valueOf(d).substring(dotPos);
            if (subStr.length() <= 2) {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getInvoiceShippingDTO().getAmount())) + "0");
            } else {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getInvoiceShippingDTO().getAmount())));
            }
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getInvoiceShippingDTO().getAmount())));
        }

        textView.setText(stringBuilder2.toString());
        textView = this.tax_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        String d1 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));
        if (d1.contains(".")) {

        int dotPos1 = String.valueOf(d1).lastIndexOf(".");
        String subStr1 = String.valueOf(d1).substring(dotPos1);
        if (subStr1.length() <= 2) {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())) + "0");
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));
        }
    } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));
    }
        textView.setText(stringBuilder2.toString());
        textView = this.total_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        //  stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount())));

        String d2 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount())));
        if (d2.contains(".")) {

        int dotPos2 = String.valueOf(d2).lastIndexOf(".");
        String subStr2 = String.valueOf(d2).substring(dotPos2);
        if (subStr2.length() <= 2) {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount())) + "0");
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount())));
        }
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount())));
        }

        textView.setText(stringBuilder2.toString());
        textView = this.paid_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        //  stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));

        String d3 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));
        if (d3.contains(".")) {
        int dotPos3 = String.valueOf(d3).lastIndexOf(".");
        String subStr3 = String.valueOf(d3).substring(dotPos3);
        if (subStr3.length() <= 2) {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())) + "0");
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));
        }
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));
        }

        textView.setText(stringBuilder2.toString());
        if (TextUtils.isEmpty(this.catalogDTO.getSignedDate())) {
            this.signed_date.setText(SecurityConstants.Signature);
        } else {
            textView = this.signed_date;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Signed ");
            stringBuilder2.append(MyConstants.formatDate(this.mActivity, Long.parseLong(this.catalogDTO.getSignedDate()), this.settingsDTO.getDateFormat()));
            textView.setText(stringBuilder2.toString());
        }
        this.invoice_notes.setText(this.catalogDTO.getNotes());
        textView = this.subtotal_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        //  stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getSubTotalAmount())));

        String d4 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getSubTotalAmount())));
        if (d4.contains(".")) {
        int dotPos4 = String.valueOf(d4).lastIndexOf(".");
        String subStr4 = String.valueOf(d4).substring(dotPos4);
        if (subStr4.length() <= 2) {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getSubTotalAmount())) + "0");
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getSubTotalAmount())));
        }} else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getSubTotalAmount())));
    }

        Log.e(TAG, "updateInvoiceBalance 1: " + String.valueOf(Double.valueOf(this.catalogDTO.getSubTotalAmount())));
        textView.setText(stringBuilder2.toString());
        if (this.catalogDTO.getDiscountType() == 2) {
            textView = this.discount_text;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Discount (");
            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getDiscount())));
            stringBuilder.append("%)");
            textView.setText(stringBuilder.toString());
        } else {
            this.discount_text.setText("Discount");
        }
        textView = this.invoice_discount;
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.currency_sign);
        String d5 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getDiscountAmount())));
        if (d5.contains(".")) {

        int dotPos5 = String.valueOf(d5).lastIndexOf(".");
        String subStr5 = String.valueOf(d5).substring(dotPos5);
        if (subStr5.length() <= 2) {
            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getDiscountAmount())) + "0");
        } else {
            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getDiscountAmount())));
        }  } else {
            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getDiscountAmount())));
    }
        textView.setText(stringBuilder.toString());
        if (this.catalogDTO.getTaxType() == 3 || this.catalogDTO.getTaxType() == 2) {
            this.tax_text.setText("Tax");
        } else {
            textView = this.tax_text;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Tax (");

            String d6 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxRate())));
            if (d6.contains(".")) {

            int dotPos6 = String.valueOf(d6).lastIndexOf(".");
            String subStr6 = String.valueOf(d6).substring(dotPos6);
            if (subStr6.length() <= 2) {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxRate())) + "0");
            } else {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxRate())));
            }
        } else {
                stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxRate())));
        }
            // stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxRate())));
            stringBuilder2.append("%)");
            textView.setText(stringBuilder2.toString());
        }
        textView = this.tax_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);

        String d6 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));
        if (d6.contains(".")) {

        int dotPos6 = String.valueOf(d6).lastIndexOf(".");
        String subStr6 = String.valueOf(d6).substring(dotPos6);
        if (subStr6.length() <= 2) {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())) + "0");
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));
        } } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));
    }

        // stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));
        textView.setText(stringBuilder2.toString());
        textView = this.paid_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);

        String d7 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));
        if (d7.contains(".")) {
        int dotPos7 = String.valueOf(d7).lastIndexOf(".");
        String subStr7 = String.valueOf(d7).substring(dotPos7);
        if (subStr7.length() <= 2) {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())) + "0");
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));
        }  } else {
        stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));
    }

        // stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));
        textView.setText(stringBuilder2.toString());
        textView = this.due_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);

        String d8 = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount() - this.catalogDTO.getPaidAmount())));
        if (d8.contains(".")) {
        int dotPos8 = String.valueOf(d8).lastIndexOf(".");
        String subStr8 = String.valueOf(d8).substring(dotPos8);
        if (subStr8.length() <= 2) {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount() - this.catalogDTO.getPaidAmount())) + "0");
        } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount() - this.catalogDTO.getPaidAmount())));
        }  } else {
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount() - this.catalogDTO.getPaidAmount())));

    }
        //   stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount() - this.catalogDTO.getPaidAmount())));
        textView.setText(stringBuilder2.toString());
    }*/
    /*{
        StringBuilder stringBuilder;
        TextView textView = this.shipping_amount;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getInvoiceShippingDTO().getAmount())));
        textView.setText(stringBuilder2.toString());
        textView = this.tax_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));
        textView.setText(stringBuilder2.toString());
        textView = this.total_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount())));
        textView.setText(stringBuilder2.toString());
        textView = this.paid_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));
        textView.setText(stringBuilder2.toString());
        if (TextUtils.isEmpty(this.catalogDTO.getSignedDate())) {
            this.signed_date.setText(SecurityConstants.Signature);
        } else {
            textView = this.signed_date;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Signed ");
            stringBuilder2.append(MyConstants.formatDate(this.mActivity, Long.parseLong(this.catalogDTO.getSignedDate()), this.settingsDTO.getDateFormat()));
            textView.setText(stringBuilder2.toString());
        }
        this.invoice_notes.setText(this.catalogDTO.getNotes());
        textView = this.subtotal_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getSubTotalAmount())));
        textView.setText(stringBuilder2.toString());
        if (this.catalogDTO.getDiscountType() == 2) {
            textView = this.discount_text;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Discount (");
            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getDiscount())));
            stringBuilder.append("%)");
            textView.setText(stringBuilder.toString());
        } else {
            this.discount_text.setText("Discount");
        }
        textView = this.invoice_discount;
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.currency_sign);
        stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getDiscountAmount())));
        textView.setText(stringBuilder.toString());
        if (this.catalogDTO.getTaxType() == 3 || this.catalogDTO.getTaxType() == 2) {
            this.tax_text.setText("Tax");
        } else {
            textView = this.tax_text;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Tax (");
            stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxRate())));
            stringBuilder2.append("%)");
            textView.setText(stringBuilder2.toString());
        }
        textView = this.tax_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTaxAmount())));
        textView.setText(stringBuilder2.toString());
        textView = this.paid_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getPaidAmount())));
        textView.setText(stringBuilder2.toString());
        textView = this.due_amount;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.currency_sign);
        stringBuilder2.append(MyConstants.formatDecimal(Double.valueOf(this.catalogDTO.getTotalAmount() - this.catalogDTO.getPaidAmount())));
        textView.setText(stringBuilder2.toString());
    }*/

    private class AsyncCaller extends AsyncTask<Void, Void, Boolean> {
        private AsyncCaller() {
        }


        protected Boolean doInBackground(Void... voidArr) {
            try {
                EditInvoiceFragment.this.loadItemImages();
                EditInvoiceFragment.this.loadInvoiceItems();
                return Boolean.valueOf(true);
            } catch (Exception unused) {
                return Boolean.valueOf(false);
            }
        }

        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if (bool.booleanValue()) {
                if (EditInvoiceFragment.this.itemAssociatedDTOS.size() > 0) {
                    EditInvoiceFragment.this.invoice_items_rv.setVisibility(View.VISIBLE);
                    EditInvoiceFragment.this.itemAssociatedAdapter.notifyDataSetChanged();
                }
                if (EditInvoiceFragment.this.imageDTOS.size() > 0) {
                    EditInvoiceFragment.this.item_images_rv.setVisibility(View.VISIBLE);
                    EditInvoiceFragment.this.itemImageAdapter.notifyDataSetChanged();
                }
                LoadDatabase.getInstance().calculateInvoiceBalance(EditInvoiceFragment.this.catalogDTO.getId());
                EditInvoiceFragment.this.updateInvoiceInfo();
            }
        }
    }
}
