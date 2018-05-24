package studio.sanguine.wallyschedule;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mafia on 11/24/2017.
 */

public class TaxOptionAdapter extends RecyclerView.Adapter<TaxOptionAdapter.TaxOptionHolder>{

    private List<TaxOption> taxOptions;

    public class TaxOptionHolder extends RecyclerView.ViewHolder{
        //define and set up textviews
        TextView taxNameText, taxRateText, replaceMainText;

        public TaxOptionHolder(View view){
            super(view);
            taxNameText = (TextView)view.findViewById(R.id.taxConfigSingle_taxNameTextView);
            taxRateText = (TextView)view.findViewById(R.id.taxConfigSingle_taxRateTextView);
            replaceMainText = (TextView)view.findViewById(R.id.taxConfigSingle_replaceMainTextView);
        }
    }

    public TaxOptionAdapter(List<TaxOption> values){
        taxOptions = values;
    }

    public int getItemCount(){
        return taxOptions.size();
    }

    public TaxOptionAdapter.TaxOptionHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.taxconfig_single, parent, false);
        return new TaxOptionAdapter.TaxOptionHolder(itemView);
    }

    public void onBindViewHolder(TaxOptionAdapter.TaxOptionHolder holder, int position){
        TaxOption item = taxOptions.get(position);
        holder.taxNameText.setText(item.getName());
        holder.taxRateText.setText(String.valueOf(item.getTaxRate()) + (item.isPercent() ? "%" : "$"));
        holder.replaceMainText.setText(String.valueOf(item.isReplaceMainTax()));
    }
}
