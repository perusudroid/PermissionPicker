package com.perusudroid.mypermissionpicker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.perusudroid.mypermissionpicker.R;

import java.util.Arrays;
import java.util.List;

public class ArrayAdapterWithIcon
        extends ArrayAdapter<String> {
    private List<Integer> images;
    private List<String> titles;

    public ArrayAdapterWithIcon(Context paramContext, List<String> paramList, List<Integer> paramList1) {
        super(paramContext, R.layout.inflater_adapter, paramList);
        this.images = paramList1;
    }

    public ArrayAdapterWithIcon(Context paramContext, String[] paramArrayOfString, Integer[] paramArrayOfInteger) {
        super(paramContext, R.layout.inflater_adapter, paramArrayOfString);
        this.images = Arrays.asList(paramArrayOfInteger);
        this.titles = Arrays.asList(paramArrayOfString);
    }

    @NonNull
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {

        String title = titles.get(paramInt);

        View localView = paramView;

        if(localView == null){
           localView = LayoutInflater.from(getContext()).inflate(R.layout.inflater_adapter, null);
        }

        TextView localTextView = localView.findViewById(R.id.tvTxt);
        localTextView.setText(title);
        localTextView.setCompoundDrawablesWithIntrinsicBounds(this.images.get(paramInt).intValue(), 0, 0, 0);
        localTextView.setCompoundDrawablePadding((int) TypedValue.applyDimension(1, 12.0F, getContext().getResources().getDisplayMetrics()));
        return localView;
    }
}