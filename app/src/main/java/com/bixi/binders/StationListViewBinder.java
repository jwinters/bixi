package com.bixi.binders;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.adapters.ViewBinder;

public class StationListViewBinder implements ViewBinder {

	@Override
	public boolean setViewValue(final View view, final Cursor cursor, final Binding binding) {
		return setDefaultValue((TextView) view, cursor, binding);
	}

	private static boolean setDefaultValue(final TextView textView, final Cursor cursor, final Binding binding) {
		final String columnName = binding.getColumnName();
		final int columnIndex = binding.getColumnIndex();
		final String text = cursor.getString(columnIndex);
		textView.setText(String.format("%s : %s", columnName, text));
		return true;
	}
}