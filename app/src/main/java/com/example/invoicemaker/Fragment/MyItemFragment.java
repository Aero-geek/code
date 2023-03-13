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
import androidx.core.view.InputDeviceCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.invoicemaker.Activity.MyItemActivity;
import com.example.invoicemaker.Adapter.ItemAdapter;
import com.example.invoicemaker.Adapter.ItemAdapter.ItemHolder;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.ItemDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.utils.RecyclerItemTouchHelper;
import com.example.invoicemaker.utils.RecyclerItemTouchHelper.RecyclerItemTouchHelperListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.Snackbar.Callback;

import java.util.ArrayList;

public class MyItemFragment extends Fragment implements OnClickListener, RecyclerItemTouchHelperListener {
    public String TAG = "MyItemFragment";
    private FloatingActionButton add_item;
    private boolean deleteItemFlag = false;
    private ItemAdapter itemAdapter;
    private ArrayList<ItemDTO> itemDTOS;
    private ArrayList<ItemDTO> items;
    private Activity mActivity;
    private RecyclerView my_items_rv;
    private TextView no_item_message;
    private SearchView searchView;
    private View view;

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        if (this.view == null) {
            this.view = layoutInflater.inflate(R.layout.my_item_fragment_layout, viewGroup, false);
            this.itemDTOS = new ArrayList();
            initLayout();
        }
        return this.view;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.my_item_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    public void onResume() {
        super.onResume();
        updateItemsFromDatabase();
        loadData();
    }

    private void loadData() {
        this.itemDTOS.clear();
        if (this.items != null) {
            this.itemDTOS.addAll(this.items);
        }
        if (this.itemAdapter != null) {
            this.itemAdapter.notifyDataSetChanged();
        }
        if (this.itemDTOS.size() == 0) {
            this.no_item_message.setVisibility(View.VISIBLE);
        }
    }

    private void updateItemsFromDatabase() {
        this.items = LoadDatabase.getInstance().getMyItems();
    }

    private void initLayout() {

        this.add_item = (FloatingActionButton) this.view.findViewById(R.id.add_item);
        this.add_item.setOnClickListener(this);
        this.no_item_message = (TextView) this.view.findViewById(R.id.no_item_message);
        this.my_items_rv = (RecyclerView) this.view.findViewById(R.id.my_items_rv);
        this.my_items_rv.setLayoutManager(new LinearLayoutManager(this.mActivity, 1, false));
        this.my_items_rv.setHasFixedSize(true);
        this.itemAdapter = new ItemAdapter(this.mActivity, this.itemDTOS, 0, false);
        this.my_items_rv.setAdapter(this.itemAdapter);
        new ItemTouchHelper(new RecyclerItemTouchHelper(0, 4, this)).attachToRecyclerView(this.my_items_rv);
        this.searchView = (SearchView) this.view.findViewById(R.id.my_item_searchview);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        // searchIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.ic_search));
        this.searchView.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                return false;
            }

            public boolean onQueryTextChange(String str) {
                MyItemFragment.this.loadData();
                MyItemFragment.this.itemAdapter.filter(str, MyItemFragment.this.itemDTOS);
                return false;
            }
        });
    }

    public void onClick(View view) {
        if (view.getId() == R.id.add_item) {
            MyItemActivity.start(view.getContext(), null);
        }
    }

    public void onSwiped(ViewHolder viewHolder, int i, int i2) {
        if (viewHolder instanceof ItemHolder) {
            String itemName = ((ItemDTO) this.itemDTOS.get(viewHolder.getAdapterPosition())).getItemName();
            final ItemDTO itemDTO = (ItemDTO) this.itemDTOS.get(viewHolder.getAdapterPosition());
            final int adapterPosition = viewHolder.getAdapterPosition();
            this.itemAdapter.removeItem(viewHolder.getAdapterPosition());
            this.items.remove(itemDTO);
            this.deleteItemFlag = true;
            View view = getView();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(itemName);
            stringBuilder.append(" removed from my items!");
            Snackbar make = Snackbar.make(view, stringBuilder.toString(), 0);
            make.setAction((CharSequence) "UNDO", new OnClickListener() {
                public void onClick(View view) {
                    MyItemFragment.this.itemAdapter.restoreItem(itemDTO, adapterPosition);
                    MyItemFragment.this.items.add(adapterPosition, itemDTO);
                    MyItemFragment.this.deleteItemFlag = false;
                }
            });
            make.addCallback(new Callback() {
                public void onShown(Snackbar snackbar) {
                }

                public void onDismissed(Snackbar snackbar, int i) {
                    if (MyItemFragment.this.deleteItemFlag) {
                        LoadDatabase.getInstance().deleteMyItem(itemDTO.getId());
                        MyItemFragment.this.updateItemsFromDatabase();
                    }
                }
            });
            make.setActionTextColor((int) InputDeviceCompat.SOURCE_ANY);
            make.show();
        }
    }
}
