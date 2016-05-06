package com.rujian.consumemanager.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import com.rujian.consumemanager.R;

/**
 * Created by zhrjian on 2015/11/16.
 */
public class CalendarFragment extends Fragment {
    private CalendarView cv_calendar;
    private ChooseDateListener chooseDateListener;
    private Button btn_fragment_OK;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        chooseDateListener = (ChooseDateListener)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar,null);
        cv_calendar = (CalendarView)v.findViewById(R.id.cv_calendar);
        cv_calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                chooseDateListener.onSelectDate(year, month, dayOfMonth);
            }
        });
        btn_fragment_OK = (Button)v.findViewById(R.id.btn_fragment_OK);
        btn_fragment_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDateListener.onClickOK();
            }
        });
        return v;
    }
    public interface ChooseDateListener{
        void onSelectDate(int year,int month,int dayOfMonth);
        void onClickOK();
    }
}
