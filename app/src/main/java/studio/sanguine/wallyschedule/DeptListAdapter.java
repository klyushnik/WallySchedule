package studio.sanguine.wallyschedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mafia on 11/21/2017.
 */

public class DeptListAdapter extends ArrayAdapter<DeptListItem>{

    public DeptListAdapter(Context context, ArrayList<DeptListItem> values){
        super(context, 0, values);
    }

    public View getView(int index, View view, ViewGroup viewGroup){
        DeptListItem item = getItem(index);

        view = LayoutInflater.from(getContext()).inflate(R.layout.dept_list_item, viewGroup, false);

        TextView idText = (TextView)view.findViewById(R.id.deptList_idTextView);
        TextView nameText = (TextView)view.findViewById(R.id.deptList_nameTextView);

        idText.setText(item.getDeptNumber());
        nameText.setText(item.getDeptName());

        return view;
    }
}
