package studio.sanguine.wallyschedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mafia on 11/2/2017.
 */

public class DrawerListViewAdapter extends ArrayAdapter<DrawerMenuItem> {
    public DrawerListViewAdapter(Context context, ArrayList<DrawerMenuItem> values){
        super(context, 0, values);
    }

    public View getView(int index, View view, ViewGroup viewGroup){
        DrawerMenuItem item = getItem(index);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.drawer_listview_item, viewGroup, false);
        }
        TextView label = (TextView)view.findViewById(R.id.drawerListViewText);
        TextView icon = (TextView)view.findViewById(R.id.drawerListViewIcon);
        icon.setTypeface(FontManager.getTypeface(getContext(), FontManager.FONTAWESOME));

        label.setText(item.text);
        icon.setText(item.icon);

        return view;
    }
}
