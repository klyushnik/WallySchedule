package studio.sanguine.wallyschedule;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mafia on 8/25/2017.
 */

public class TaxItemAdapter extends RecyclerView.Adapter<TaxItemAdapter.TaxItemHolder>{

    private List<TaxItem> taxes;

    public class TaxItemHolder extends RecyclerView.ViewHolder{
        //define and set up textviews
        TextView priceText, quantityText, taxOptionText, discountText;

        public TaxItemHolder(View view){
            super(view);
            priceText = (TextView)view.findViewById(R.id.taxitem_priceTextView);
            quantityText = (TextView)view.findViewById(R.id.taxitem_countTextView);
            taxOptionText = (TextView)view.findViewById(R.id.taxitem_taxOptionTextView);
            discountText = (TextView)view.findViewById(R.id.taxitem_discountTextView);
        }
    }

    public TaxItemAdapter(List<TaxItem> values){
        taxes = values;
    }

    public int getItemCount(){
        return taxes.size();
    }

    public TaxItemHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.taxcalc_item_single, parent, false);
        return new TaxItemHolder(itemView);
    }

    public void onBindViewHolder(TaxItemHolder holder, int position){
        TaxItem item = taxes.get(position);
        holder.priceText.setText(String.format("$%.2f", item.get_price()));
        holder.taxOptionText.setText(item.get_taxOption());
        holder.quantityText.setText(String.valueOf(item.get_quantity()));
        if(item.is_discountPercent())
            holder.discountText.setText("Discount: " + String.valueOf(item.get_discount()) + "%");
        else
            holder.discountText.setText("Discount: " + String.format("$%.2f", item.get_discount()));
    }
}
