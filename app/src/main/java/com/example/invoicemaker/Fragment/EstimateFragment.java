package com.example.invoicemaker.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.example.invoicemaker.Activity.InvoiceDetailsActivity;
import com.example.invoicemaker.Adapter.PagerAdapter;
import com.example.invoicemaker.R;
import com.example.invoicemaker.utils.DataProcessor;
import com.google.android.material.tabs.TabLayout;

public class EstimateFragment extends Fragment implements OnClickListener {
    private String TAG = "EstimateFragment";
    private Activity mActivity;
    private PagerAdapter pagerAdapter;
    private SearchView searchView;
    private TabLayout tabLayout;
    private View view;
    private ViewPager viewPager;

    private void initLayout() {
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        if (this.view == null) {
            this.view = layoutInflater.inflate(R.layout.invoice_fragment_layout, viewGroup, false);
            initLayout();
            setUpTabLayout(bundle);
        }
        return this.view;
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("tabsCount", this.pagerAdapter.getCount());
        bundle.putStringArray("titles", (String[]) this.pagerAdapter.getTitles().toArray(new String[0]));
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu);
        MenuItem findItem = menu.findItem(R.id.action_search);
        if (findItem != null) {
            this.searchView = (SearchView) findItem.getActionView();
            EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(getResources().getColor(R.color.white));
            searchEditText.setHintTextColor(getResources().getColor(R.color.white));
            ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
            this.searchView.setQueryHint("Client name");
            this.searchView.setOnQueryTextListener(new OnQueryTextListener() {
                public boolean onQueryTextSubmit(String str) {
                    return true;
                }

                public boolean onQueryTextChange(String str) {
                    DataProcessor.getInstance().notifyListeners(str, 2001);
                    return false;
                }
            });
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    private void setUpTabLayout(Bundle bundle) {
        this.view.findViewById(R.id.add_item).setOnClickListener(this);
        this.viewPager = (ViewPager) this.view.findViewById(R.id.viewPager);
        this.pagerAdapter = new PagerAdapter(getChildFragmentManager(), this.mActivity);
        if (bundle == null) {
            this.pagerAdapter.addFragment(new AlIEstimateFragment(), getResources().getString(R.string.all_text), getResources().getDrawable(R.drawable.ic_estimates));
            this.pagerAdapter.addFragment(new OpenEstimateFragment(), getResources().getString(R.string.open_text), getResources().getDrawable(R.drawable.ic_estimates));
            this.pagerAdapter.addFragment(new ClosedEstimateFragment(), getResources().getString(R.string.closed_text), getResources().getDrawable(R.drawable.ic_estimates));
        } else {
            Integer valueOf = Integer.valueOf(bundle.getInt("tabsCount"));
            String[] stringArray = bundle.getStringArray("titles");
            for (int i = 0; i < valueOf.intValue(); i++) {
                this.pagerAdapter.addFragment(getFragment(i, bundle), stringArray[i], null);
            }
        }
        this.viewPager.setAdapter(this.pagerAdapter);
        this.viewPager.setOffscreenPageLimit(3);
        this.viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
            }

            public void onPageSelected(int i) {
            }
        });
        this.tabLayout = (TabLayout) this.view.findViewById(R.id.tabLayout);
        this.tabLayout.setTabGravity(0);
        this.tabLayout.setupWithViewPager(this.viewPager);
        int betweenSpace = 10;
        ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);

        for (int i = 0; i < slidingTabStrip.getChildCount(); i++) {
            View v = slidingTabStrip.getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.leftMargin = betweenSpace;
            params.rightMargin = betweenSpace;

        }
    }

    private Fragment getFragment(int i, Bundle bundle) {
        return bundle == null ? this.pagerAdapter.getItem(i) : getChildFragmentManager().findFragmentByTag(getFragmentTag(i));
    }

    private String getFragmentTag(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("android:switcher:2131296670:");
        stringBuilder.append(i);
        return stringBuilder.toString();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.add_item) {
            InvoiceDetailsActivity.start(view.getContext(), null);
        }
    }
}
