package com.bixi.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bixi.R;

public class LocationView extends LinearLayout {

    public LocationView(final Context context) {
        super(context);
    }

    public LocationView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public LocationView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTitle(final String title) {
        final TextView textView = (TextView) findViewById(R.id.location_title);
        textView.setText(title);
    }

    public void setSubtitle(final String subtitle) {
        final TextView textView = (TextView) findViewById(R.id.location_subtitle);
        textView.setText(subtitle);
    }
}
