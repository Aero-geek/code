package com.example.invoicemaker.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.invoicemaker.Activity.AddClientActivity;
import com.example.invoicemaker.Adapter.ClientAdapter;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.ClientDTO;
import com.example.invoicemaker.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ClientFragment extends Fragment implements OnClickListener {
    private FloatingActionButton add_item;
    private ClientAdapter clientAdapter;
    private ArrayList<ClientDTO> clientDTOS;
    private ArrayList<ClientDTO> clients;
    private RecyclerView clients_rv;
    private Activity mActivity;
    private TextView no_client_message;
    private SearchView search_view;
    private View view;

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        if (this.view == null) {
            this.view = layoutInflater.inflate(R.layout.client_fragment_layout, viewGroup, false);
            this.clientDTOS = new ArrayList();
            initLayout();
        }
        return this.view;
    }

    public void onResume() {
        super.onResume();
        updateInvoicesFromDatabase();
        loadData();
    }

    private void loadData() {
        this.clientDTOS.clear();
        this.clientDTOS.addAll(this.clients);
        if (this.clientAdapter != null) {
            this.clientAdapter.notifyDataSetChanged();
        }
        if (this.clientDTOS.size() == 0) {
            this.no_client_message.setVisibility(0);
        }
    }

    private void updateInvoicesFromDatabase() {
        this.clients = LoadDatabase.getInstance().getClientList();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.my_item_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    private void initLayout() {
        this.add_item = (FloatingActionButton) this.view.findViewById(R.id.add_item);
        this.add_item.setOnClickListener(this);
        this.no_client_message = (TextView) this.view.findViewById(R.id.no_client_message);
        this.clients_rv = (RecyclerView) this.view.findViewById(R.id.clients_rv);
        this.clients_rv.setLayoutManager(new LinearLayoutManager(this.mActivity, 1, false));
        this.clients_rv.setHasFixedSize(true);
        this.clientAdapter = new ClientAdapter(this.mActivity, this.clientDTOS, 0, false);
        this.clients_rv.setAdapter(this.clientAdapter);
        this.search_view = (SearchView) this.view.findViewById(R.id.search_view);
        EditText searchEditText = search_view.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));
        ImageView searchIcon = search_view.findViewById(androidx.appcompat.R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_search_white_24dp));
        this.search_view.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                return false;
            }

            public boolean onQueryTextChange(String str) {
                ClientFragment.this.loadData();
                ClientFragment.this.clientAdapter.filter(str, ClientFragment.this.clientDTOS);
                return false;
            }
        });
    }

    public void onClick(View view) {
        if (view.getId() == R.id.add_item) {
            AddClientActivity.start(view.getContext(), new ClientDTO(), 0, false);
        }
    }
}
