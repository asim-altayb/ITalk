package com.hackathon.light.italk;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Emad Abbas on 7/14/2018.
 */

/*
an
 */

public class AutofitRecyclerView extends RecyclerView {
    private GridLayoutManager manager;
    private int coulmnWidth = -1;

    public AutofitRecyclerView(Context context) {
        super(context);
        init(context,null);
    }


    public AutofitRecyclerView(Context context,AttributeSet attrs) {
        super(context,attrs);
        init(context,attrs);
    }


    public AutofitRecyclerView(Context context,AttributeSet set,int defStyle) {
        super(context,set,defStyle);
        init(context,null);
    }

    private void init(Context context, AttributeSet set) {
        if (set != null){
            int[] attrArray = {
                    android.R.attr.columnWidth
            };
            TypedArray array = context.obtainStyledAttributes(set,attrArray);
            coulmnWidth = array.getDimensionPixelSize(0,-1);
            array.recycle();
        }
        manager = new GridLayoutManager(getContext(),1);
        setLayoutManager(manager);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (coulmnWidth>0){
            int spanCount = Math.max(1,getMeasuredWidth()/coulmnWidth);
            manager.setSpanCount(spanCount);
        }
    }
}
