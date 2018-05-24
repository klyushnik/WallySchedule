package studio.sanguine.wallyschedule;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mafia on 7/25/2017.
 */

public class RowAdapter extends ArrayAdapter<ScheduleCellSingle> {

    private String todaysTimeCode;

    //constructor
    public RowAdapter(Context context, ArrayList<ScheduleCellSingle> values, String _todaysTimeCode){
        super(context, 0, values);
        todaysTimeCode = _todaysTimeCode;
    }

    //override getView
    public View getView(int index, View view, ViewGroup viewGroup){
        ScheduleCellSingle item = getItem(index);

        //check if we need to inflate the scheduled view or not scheduled view
        boolean isNotScheduled = item.isNotScheduledCell;

        //inflate the view, then set the values
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.schedule_cell_single, viewGroup, false);
        }

        //define layout view elements

            TextView dayOfMonthText = (TextView) view.findViewById(R.id.dayOfMonthTextView);
            TextView dayOfWeekText = (TextView) view.findViewById(R.id.weekNumberTextView);
            TextView jobDescText = (TextView) view.findViewById(R.id.jobDescriptionTextView);
            TextView mealText = (TextView) view.findViewById(R.id.youreOffTextView);
            TextView shiftTimeText = (TextView) view.findViewById(R.id.shiftTimeTextView);
            TextView openShiftText = (TextView) view.findViewById(R.id.openShiftTextView);
            RelativeLayout cellBackground = (RelativeLayout) view.findViewById(R.id.cellBackground);

            //insert values from ScheduleCellSingle item into the textviews
            dayOfMonthText.setText(item.getDayOfMonth());
            dayOfWeekText.setText(item.getDayOfWeek());
            jobDescText.setText(isNotScheduled ? "" : item.getJobDescription());
            mealText.setText(isNotScheduled ? getContext().getString(R.string.widget_no_shift) : item.getMealTime());
            shiftTimeText.setText(isNotScheduled ? "" : item.getShiftTime());

            if (item.isOpenShift) {
                openShiftText.setVisibility(View.VISIBLE);
            } else {
                openShiftText.setVisibility(View.INVISIBLE);
            }

            if (item.getDateCode().equals(todaysTimeCode)) {
                cellBackground.setBackgroundColor(Color.parseColor("#FFBBDEFB"));
            } else {
                cellBackground.setBackgroundColor(Color.WHITE);
            }

            if (item.getDayOfWeek().equals("Saturday") || item.getDayOfWeek().equals("Sunday")) {
                dayOfMonthText.setTextColor(Color.parseColor("#FFFF7043"));
            } else {
                dayOfMonthText.setTextColor(Color.parseColor("#FF757575"));
            }

            if (Integer.parseInt(item.getDateCode()) < Integer.parseInt(todaysTimeCode)) {
                dayOfWeekText.setBackgroundColor(Color.GRAY);
            } else {
                dayOfWeekText.setBackgroundColor(Color.parseColor("#FFFF7043"));
            }


        return view;
    }
}
