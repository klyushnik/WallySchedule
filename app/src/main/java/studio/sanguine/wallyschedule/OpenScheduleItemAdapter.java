package studio.sanguine.wallyschedule;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mafia on 8/25/2017.
 */

public class OpenScheduleItemAdapter extends ArrayAdapter<OpenScheduleItem>{

    public OpenScheduleItemAdapter(Context context, ArrayList<OpenScheduleItem> values){
        super(context, 0, values);
    }


    public View getView(int i, View view, ViewGroup viewGroup){
        OpenScheduleItem item = getItem(i);

        if (view == null) view = LayoutInflater.from(getContext()).inflate(R.layout.open_shift_cell, viewGroup, false);

        TextView jobDescTextView = (TextView) view.findViewById(R.id.openShiftPosition);
        TextView shiftTimeTextView = (TextView) view.findViewById(R.id.openShiftTime);

        jobDescTextView.setText(item.get_jobDescription());
        shiftTimeTextView.setText(item.get_shiftTime());

        return view;
    }
}
