package com.uiSearch.presentation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uisearch.presentation.R;

public class CountryCodesAdapter extends BaseAdapter {

    private String[] mCountries;
    private Context mContext;

    public CountryCodesAdapter(Context cxt) {
        super();
        mContext = cxt;
        mCountries = mContext.getResources().getStringArray(R.array.CountryCodes);
    }

    @Override
    public int getCount() {
        return mCountries.length;
    }

    @Override
    public Object getItem(int index) {
        return mCountries[index];
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int index, View recycleView, ViewGroup viewGroup) {
        TextView view;
        if (recycleView == null) {
            view = new TextView(mContext);
            view.setPadding(30, 10, 10, 10);
        } else {
            view = (TextView) recycleView;
        }
        String selectedStr=mCountries[index];
        String[] strArr=selectedStr.split(",");
        view.setText(strArr[1]+"("+strArr[0].trim()+")");
        return view;
    }
}
