package br.org.meiaentrada.validadorcie.components;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Locale;


public class DateInputMask implements TextWatcher {

    private String current = "";
    private String ddmmyyyy = "DDMMYYYY";
    private Calendar calendar = Calendar.getInstance();
    private EditText input;

    public DateInputMask(EditText input) {

        this.input = input;
        this.input.addTextChangedListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (s.toString().equals(current))
            return;

        String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
        String cleanC = current.replaceAll("[^\\d.]|\\.", "");

        int cl = clean.length();
        int sel = cl;
        for (int i = 2; i <= cl && i < 6; i += 2) {
            sel++;
        }

        if (clean.equals(cleanC))
            sel--;

        if (clean.length() < 8)
            clean = clean + ddmmyyyy.substring(clean.length());
        else {

            int day = Integer.parseInt(clean.substring(0, 2));
            int mon = Integer.parseInt(clean.substring(2, 4));
            int year = Integer.parseInt(clean.substring(4, 8));

            mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
            calendar.set(Calendar.MONTH, mon - 1);
            year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
            calendar.set(Calendar.YEAR, year);

            day = (day > calendar.getActualMaximum(Calendar.DATE)) ? calendar.getActualMaximum(Calendar.DATE) : day;
            clean = String.format("%02d%02d%02d", day, mon, year);

        }

        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                clean.substring(2, 4),
                clean.substring(4, 8));

        sel = sel < 0 ? 0 : sel;
        current = clean;
        input.setText(current);
        input.setSelection(sel < current.length() ? sel : current.length());

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
