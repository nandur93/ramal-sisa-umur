package com.nandur.ramalumur;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Locale;
import java.util.Objects;

import static com.nandur.ramalumur.MainActivity.textBirthDay;
import static com.nandur.ramalumur.MainActivity.usia;

@SuppressWarnings({"WeakerAccess", "CanBeFinal"})
public class MyDatePickerFragment extends DialogFragment {

    private Calendar newDate;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar c = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            c = Calendar.getInstance();
        }
        int year = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            year = c.get(Calendar.YEAR);
        }
        int month = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            month = c.get(Calendar.MONTH);
        }
        int day = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        return new DatePickerDialog(Objects.requireNonNull(getActivity()), R.style.CustomDatePickerDialogTheme, dateSetListener, year, month, day);
    }


    private DateFormat dateFormatter;
    private DatePickerDialog.OnDateSetListener dateSetListener =
            (view, year, month, day) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                }
                newDate = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    newDate = Calendar.getInstance();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    newDate.set(year, month, day);
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    String dateStr = dateFormatter.format(newDate.getTime());
                    textBirthDay.setText(dateStr);
                }

                Calendar today = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    today = Calendar.getInstance();
                }

                int age = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    age = today.get(Calendar.YEAR) - newDate.get(Calendar.YEAR);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (today.get(Calendar.DAY_OF_YEAR) < newDate.get(Calendar.DAY_OF_YEAR)){
                        age--;
                    }
                }

                Integer ageInt = age;
                String ageS = ageInt.toString();
                usia.setText(ageS);
            };
}
