package com.bixi.binders;

import android.database.Cursor;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bixi.R;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.adapters.ViewBinder;

public class StationListViewBinder implements ViewBinder {

	@Override
	public boolean setViewValue(final View view, final Cursor cursor, final Binding binding) {
        if (view.getId() == R.id.station_geofenced) {
            return setCheckBoxValue((CheckBox) view, cursor, binding);
        } else {
            return setDefaultValue((TextView) view, cursor, binding);
        }
	}

    private boolean setCheckBoxValue(final CheckBox view, final Cursor cursor, final Binding binding) {
        final CheckBox checkBox = (CheckBox) view;
        final int columnIndex = binding.getColumnIndex();
        final int checked = cursor.getInt(columnIndex);
        checkBox.setChecked(checked == 1);
        return true;
    }

    private static boolean setDefaultValue(final TextView textView, final Cursor cursor, final Binding binding) {
		final int columnIndex = binding.getColumnIndex();
		final String text = cursor.getString(columnIndex);
		textView.setText(String.format("%s", text));
		return true;
	}
}