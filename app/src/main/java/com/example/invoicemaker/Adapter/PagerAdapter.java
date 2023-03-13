package com.example.invoicemaker.Adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private final ArrayList<Fragment> fragmentList = new ArrayList();
    private final ArrayList<Drawable> fragmentTitleIcon = new ArrayList();
    private final ArrayList<String> fragmentTitleList = new ArrayList();
    private Activity mActivity;

    public PagerAdapter(FragmentManager fragmentManager, Activity activity) {
        super(fragmentManager);
        this.mActivity = activity;
    }

    public Fragment getItem(int i) {
        return (Fragment) this.fragmentList.get(i);
    }

    public void addFragment(Fragment fragment, String str, Drawable drawable) {
        this.fragmentList.add(fragment);
        this.fragmentTitleList.add(str);
        this.fragmentTitleIcon.add(drawable);
    }

    public ArrayList<String> getTitles() {
        return this.fragmentTitleList;
    }

    public int getCount() {
        return this.fragmentList.size();
    }

    public CharSequence getPageTitle(int i) {
        //Drawable drawable = (Drawable) this.fragmentTitleIcon.get(i);
//        if (drawable == null) {
//            return (CharSequence) this.fragmentTitleList.get(i);
//        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((String) this.fragmentTitleList.get(i));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder.toString());
//        try {
//            drawable.setBounds(5, 5, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//            spannableStringBuilder.setSpan(new ImageSpan(drawable, 0), 0, 1, 33);
//        } catch (Exception unused) {
//            return spannableStringBuilder;
//        }
        return spannableStringBuilder;
    }
}
