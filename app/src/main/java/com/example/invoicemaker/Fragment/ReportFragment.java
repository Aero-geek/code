package com.example.invoicemaker.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.invoicemaker.Adapter.ClientReportAdapter;
import com.example.invoicemaker.Adapter.ReportAdapter;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.CatalogDTO;
import com.example.invoicemaker.Dto.ClientDTO;
import com.example.invoicemaker.Dto.ClientReportDTO;
import com.example.invoicemaker.Dto.MonthlyReportDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.utils.NonScrollListView;
import com.example.invoicemaker.utils.SegmentedRadioGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ReportFragment extends Fragment implements OnCheckedChangeListener {
    public static final long CLIENT_ITEM_TITLE = -999;
    public static final long NO_CLIENT_ASSIGNED = -1;
    ClientReportAdapter clientReportAdapter;
    ArrayList<MonthlyReportDTO> listData;
    ArrayList<ClientReportDTO> listDataClient;
    NonScrollListView listReport;
    ReportAdapter reportAdapter;
    RadioButton rBtn1, rBtn2;
    private String TAG = "ReportFragment";
    private TextView clientsNumber;
    private LinearLayout list_title;
    private Activity mActivity;
    private SegmentedRadioGroup radioGroup;
    private String[] spinnerData;
    private View view;
    private TreeMap<Integer, ArrayList<ClientReportDTO>> yearlyClientReport;
    private TreeMap<Integer, ArrayList<MonthlyReportDTO>> yearlyReport;
    private Spinner yearly_report_spinner;

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        if (this.view == null) {
            this.view = layoutInflater.inflate(R.layout.report_fragment_layout, viewGroup, false);
            setUpData();
            setUpPaidListData();
            initLayout();
            setUpSpinner();
        }
        return this.view;
    }

    private void initLayout() {
        this.yearly_report_spinner = (Spinner) this.view.findViewById(R.id.yearly_report);
        this.list_title = (LinearLayout) this.view.findViewById(R.id.list_title);
        this.clientsNumber = (TextView) this.view.findViewById(R.id.clients_number);
        this.listReport = (NonScrollListView) this.view.findViewById(R.id.list_report);
        this.reportAdapter = new ReportAdapter(this.mActivity, this.listData);
        this.listReport.setAdapter(this.reportAdapter);
        rBtn1 = view.findViewById(R.id.radio_btn_paid);
        rBtn2 = view.findViewById(R.id.radio_btn_clients);
        if (this.listReport.getAdapter().getCount() <= 0) {
            this.list_title.setVisibility(View.GONE);
        }
        this.radioGroup = (SegmentedRadioGroup) this.view.findViewById(R.id.radio_group);
        this.radioGroup.setOnCheckedChangeListener(this);
    }

    private void setUpData() {
        ArrayList unPaidInvoices = LoadDatabase.getInstance().getUnPaidInvoices(2);
        this.yearlyReport = new TreeMap(Collections.reverseOrder());
        this.yearlyClientReport = new TreeMap(Collections.reverseOrder());
        Calendar instance = Calendar.getInstance();
        int i = 0;
        while (i < unPaidInvoices.size()) {
            int i2;
            int i3;
            CatalogDTO catalogDTO = (CatalogDTO) unPaidInvoices.get(i);
            instance.setTimeInMillis(Long.parseLong(catalogDTO.getCreationDate()));
            ArrayList arrayList = (ArrayList) this.yearlyReport.get(Integer.valueOf(instance.get(Calendar.YEAR)));
            ArrayList arrayList2 = (ArrayList) this.yearlyClientReport.get(Integer.valueOf(instance.get(1)));
            if (arrayList == null) {
                arrayList = new ArrayList();
                for (int i4 = 0; i4 < 12; i4++) {
                    arrayList.add(new MonthlyReportDTO());
                }
            }
            if (arrayList2 == null) {
                arrayList2 = new ArrayList();
            }
            MonthlyReportDTO monthlyReportDTO = (MonthlyReportDTO) arrayList.get(instance.get(Calendar.MONTH));
            monthlyReportDTO.setTotalInvoices(monthlyReportDTO.getTotalInvoices() + 1);
            ClientDTO clientDTO = catalogDTO.getClientDTO();
            if (clientDTO == null || clientDTO.getId() <= 0) {
                i2 = 0;
            } else {
                monthlyReportDTO.setClients(clientDTO.getId());
                for (i2 = 0; i2 < arrayList2.size(); i2++) {
                    if (((ClientReportDTO) arrayList2.get(i2)).getId() == clientDTO.getId()) {
                        ((ClientReportDTO) arrayList2.get(i2)).setInvoices(((ClientReportDTO) arrayList2.get(i2)).getInvoices() + 1);
                        ((ClientReportDTO) arrayList2.get(i2)).setPaidAmount(((ClientReportDTO) arrayList2.get(i2)).getPaidAmount() + catalogDTO.getTotalAmount());
                        i2 = 1;
                        break;
                    }
                }
                i2 = 0;
                if (i2 == 0) {
                    ClientReportDTO clientReportDTO = new ClientReportDTO();
                    clientReportDTO.setId(clientDTO.getId());
                    clientReportDTO.setName(clientDTO.getClientName());
                    clientReportDTO.setInvoices(1);
                    clientReportDTO.setPaidAmount(catalogDTO.getTotalAmount());
                    arrayList2.add(clientReportDTO);
                    i2 = 1;
                }
            }
            if (i2 == 0) {
                for (int i5 = 0; i5 < arrayList2.size(); i5++) {
                    if (((ClientReportDTO) arrayList2.get(i5)).getId() == -1) {
                        ((ClientReportDTO) arrayList2.get(i5)).setInvoices(((ClientReportDTO) arrayList2.get(i5)).getInvoices() + 1);
                        i3 = i;
                        ((ClientReportDTO) arrayList2.get(i5)).setPaidAmount(((ClientReportDTO) arrayList2.get(i5)).getPaidAmount() + catalogDTO.getTotalAmount());
                        i2 = 1;
                        break;
                    }
                    i3 = i;
                }
                i3 = i;
                if (i2 == 0) {
                    ClientReportDTO clientReportDTO2 = new ClientReportDTO();
                    clientReportDTO2.setId(-1);
                    clientReportDTO2.setName("(None)");
                    clientReportDTO2.setInvoices(1);
                    clientReportDTO2.setPaidAmount(catalogDTO.getTotalAmount());
                    arrayList2.add(clientReportDTO2);
                }
            } else {
                i3 = i;
            }
            monthlyReportDTO.setTotalPaidAmount(monthlyReportDTO.getTotalPaidAmount() + catalogDTO.getTotalAmount());
            this.yearlyReport.put(Integer.valueOf(instance.get(1)), arrayList);
            this.yearlyClientReport.put(Integer.valueOf(instance.get(1)), arrayList2);
            i = i3 + 1;
        }
    }

    private void setUpPaidListData() {
        this.listData = new ArrayList();
        for (Entry entry : this.yearlyReport.entrySet()) {
            ArrayList<MonthlyReportDTO> arrayList = (ArrayList<MonthlyReportDTO>) entry.getValue();
            MonthlyReportDTO monthlyReportDTO = new MonthlyReportDTO();
            monthlyReportDTO.setYearOrMonth(((Integer) entry.getKey()).intValue());
            int i = 0;
            double d = 0.0d;
            int i2 = 0;
            int i3 = i2;
            while (i < arrayList.size()) {
                i2 += ((MonthlyReportDTO) arrayList.get(i)).getTotalInvoices();
                i3 = (int) (((long) i3) + ((MonthlyReportDTO) arrayList.get(i)).getTotalClients());
                d += ((MonthlyReportDTO) arrayList.get(i)).getTotalPaidAmount();
                ((MonthlyReportDTO) arrayList.get(i)).setYearOrMonth(i);
                i++;
            }
            monthlyReportDTO.setTotalInvoices(i2);
            monthlyReportDTO.setTotalClientsPerYear(i3);
            monthlyReportDTO.setTotalPaidAmount(d);
            this.listData.add(monthlyReportDTO);
            for (i = arrayList.size() - 1; i >= 0; i--) {
                this.listData.add(arrayList.get(i));
            }
        }
    }

    private void setUpClientList() {
        this.listDataClient = new ArrayList();
        for (Entry entry : this.yearlyClientReport.entrySet()) {
            ArrayList arrayList = (ArrayList) entry.getValue();
            ClientReportDTO clientReportDTO = new ClientReportDTO();
            clientReportDTO.setId(-999);
            clientReportDTO.setName(String.valueOf(entry.getKey()));
            int i = 0;
            double d = 0.0d;
            int i2 = 0;
            while (i < arrayList.size()) {
                i2 += ((ClientReportDTO) arrayList.get(i)).getInvoices();
                d += ((ClientReportDTO) arrayList.get(i)).getPaidAmount();
                i++;
            }
            clientReportDTO.setInvoices(i2);
            clientReportDTO.setPaidAmount(d);
            this.listDataClient.add(clientReportDTO);
            this.listDataClient.addAll(arrayList);
        }
    }

    private void setUpSpinner() {
        int i = 1;
        this.spinnerData = new String[(this.yearlyReport.size() + 1)];
        this.spinnerData[0] = "ALL";
        for (Entry entry : this.yearlyReport.entrySet()) {
            String[] strArr = this.spinnerData;
            int i2 = i + 1;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(entry.getKey());
            stringBuilder.append("");
            strArr[i] = stringBuilder.toString();
            i = i2;
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this.mActivity, 17367050, this.spinnerData) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(16);
                ((TextView) v).setTextColor(
                        getResources().getColorStateList(R.color.white)
                );

                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                v.setBackgroundColor(getResources().getColor(R.color.white));
                //   v.getBackground().setAlpha(0);

                ((TextView) v).setTextColor(
                        getResources().getColor(R.color.newAppColor)
                );


                return v;
            }
        };
        arrayAdapter.setDropDownViewResource(17367049);
        this.yearly_report_spinner.setAdapter(arrayAdapter);
        this.yearly_report_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                ReportFragment.this.filterList(i, adapterView.getItemAtPosition(i).toString());
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void filterList(int i, String str) {
        setUpData();
        Iterator it;
        if (this.radioGroup.getCheckedRadioButtonId() == R.id.radio_btn_paid) {
            rBtn1.setBackgroundResource(R.drawable.selected_tab);
            rBtn1.setTextColor(getResources().getColor(R.color.white));
            rBtn2.setTextColor(getResources().getColor(R.color.newAppColor));
            rBtn2.setBackgroundResource(R.drawable.non_selected_tab);

            this.clientsNumber.setVisibility(View.VISIBLE);
            if (i != 0) {
                it = this.yearlyReport.entrySet().iterator();
                while (it.hasNext()) {
                    if (((Integer) ((Entry) it.next()).getKey()).intValue() != Integer.parseInt(str)) {
                        it.remove();
                    }
                }
            }
            setUpPaidListData();
            this.reportAdapter = new ReportAdapter(this.mActivity, this.listData);
            this.listReport.setAdapter(this.reportAdapter);
        } else if (this.radioGroup.getCheckedRadioButtonId() == R.id.radio_btn_clients) {
            rBtn1.setBackgroundResource(R.drawable.non_selected_tab);
            rBtn1.setTextColor(getResources().getColor(R.color.newAppColor));
            rBtn2.setTextColor(getResources().getColor(R.color.white));
            rBtn2.setBackgroundResource(R.drawable.selected_tab);

            this.clientsNumber.setVisibility(View.INVISIBLE);
            if (i != 0) {
                it = this.yearlyClientReport.entrySet().iterator();
                while (it.hasNext()) {
                    if (((Integer) ((Entry) it.next()).getKey()).intValue() != Integer.parseInt(str)) {
                        it.remove();
                    }
                }
            }
            setUpClientList();
            this.clientReportAdapter = new ClientReportAdapter(this.mActivity, this.listDataClient);
            this.listReport.setAdapter(this.clientReportAdapter);
        }
        if (this.listReport.getAdapter().getCount() <= 0) {
            this.list_title.setVisibility(View.GONE);
        } else {
            this.list_title.setVisibility(View.VISIBLE);
        }
    }

    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        int selectedItemPosition = this.yearly_report_spinner.getSelectedItemPosition();

        filterList(selectedItemPosition, this.yearly_report_spinner.getItemAtPosition(selectedItemPosition).toString());
    }
}
