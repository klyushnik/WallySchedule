package studio.sanguine.wallyschedule;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by mafia on 11/2/2017.
 */

public class DrawerMenuItem {
    String icon = "";
    String text = "";
    public DrawerMenuItem(int position, Context context){
        switch (position){
            case 0:
                text = context.getString(R.string.drawer_taxCalculator);
                icon = context.getString(R.string.fa_calculator);
                break;
            case 1:
                text = "Department List";
                icon = context.getString(R.string.fa_list);
                break;
            case 2:
                text = context.getString(R.string.drawer_callIn);
                icon = context.getString(R.string.fa_ambulance);
                break;
            case 3:
                text = context.getString(R.string.drawer_settings);
                icon = context.getString(R.string.fa_gear);
                break;
            case 4:
                text = context.getString(R.string.drawer_about);
                icon = context.getString(R.string.fa_star);
                break;
            default:
                text = "Unknown";
                icon = context.getString(R.string.fa_gear);
                break;
        }
    }
}
