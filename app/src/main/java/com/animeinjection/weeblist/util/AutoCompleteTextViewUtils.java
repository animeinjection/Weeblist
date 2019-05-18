package com.animeinjection.weeblist.util;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;


public class AutoCompleteTextViewUtils {
  // Can't be constructed
  private AutoCompleteTextViewUtils() {}

  public static <E extends Enum<E>> ArrayAdapter<E> setupAdapterForEnum(Context context, AutoCompleteTextView textView, Class<E> enumClass, E initialValue) {
    if (initialValue != null) {
      textView.setText(initialValue.toString());
    }
    ArrayAdapter<E> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, enumClass.getEnumConstants());
    textView.setAdapter(adapter);
    textView.setListSelection(adapter.getPosition(initialValue));
    return adapter;
  }
}
