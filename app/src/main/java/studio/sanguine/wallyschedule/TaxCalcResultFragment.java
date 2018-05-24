package studio.sanguine.wallyschedule;


import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TaxCalcResultFragment extends DialogFragment {
    static double _subtotal, _tax, _total;

    public TaxCalcResultFragment() {
        // Required empty public constructor
    }

    public static TaxCalcResultFragment newInstance(double subtotal, double tax, double total) {
        TaxCalcResultFragment fragment = new TaxCalcResultFragment();
        _subtotal = subtotal;
        _tax = tax;
        _total = total;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tax_calc_result, container, false);
        Button closeButton = (Button)view.findViewById(R.id.taxcalc_RF_closeButton);
        TextView subtotalText = (TextView)view.findViewById(R.id.taxcalc_RF_subtotalTextView);
        TextView taxesText = (TextView)view.findViewById(R.id.taxcalc_RF_taxesTextView);
        TextView totalText = (TextView)view.findViewById(R.id.taxcalc_RF_totalTextView);

        String subtotal = String.format("$%.2f", _subtotal);
        String taxes = String.format("$%.2f", _tax);
        String total = String.format("$%.2f", _total);

        subtotalText.setText(subtotal);
        taxesText.setText(taxes);
        totalText.setText(total);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }

}
