package studio.sanguine.wallyschedule;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class TaxCalcActivity extends Activity implements AdapterView.OnItemSelectedListener{
    EditText itemPriceText;
    EditText discountText;
    EditText quantityText;
    Spinner discountSpinner;
    Spinner taxOptionSpinner;
    Button addItemButton;
    Button calculateButton;
    Button clearButton;
    TextView settingsTextView;

    RecyclerView taxItemsRecyclerView;
    List<TaxItem> taxItemList;
    TaxItemAdapter adapter;

    JSONArray jsonArray;
    List<TaxOption> taxes;
    int selectedTax = 0;
    boolean isDiscountPercent = true;
    Context _context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax_calc);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        itemPriceText = (EditText) findViewById(R.id.taxcalc_priceEditText);
        discountText = (EditText) findViewById(R.id.taxcalc_discountEditText);
        quantityText = (EditText) findViewById(R.id.taxcalc_quantityEditText);
        discountSpinner = (Spinner)findViewById(R.id.taxcalc_discountSpinner);
        taxOptionSpinner = (Spinner)findViewById(R.id.taxcalc_taxOptionSpinner);
        addItemButton = (Button)findViewById(R.id.taxcalc_addItemButton);
        calculateButton = (Button)findViewById(R.id.taxcalc_calculateButton);
        clearButton = (Button)findViewById(R.id.taxcalc_clearButton);
        taxItemsRecyclerView = (RecyclerView)findViewById(R.id.taxcalc_recyclerView);
        settingsTextView = (TextView)findViewById(R.id.taxcalc_settingsTextView);

        taxes = new ArrayList<TaxOption>();
        PopulateTaxOptions();

        taxItemList = new ArrayList<TaxItem>();
        adapter = new TaxItemAdapter(taxItemList);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        taxItemsRecyclerView.setLayoutManager(manager);
        //taxItemsRecyclerView.setHasFixedSize(true);
        taxItemsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        taxItemsRecyclerView.setAdapter(adapter);

        taxOptionSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<TaxOption> taxOptionSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, taxes);
        taxOptionSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_popup);
        taxOptionSpinner.setAdapter(taxOptionSpinnerAdapter);

        //discount spinner
        discountSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> discountSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.discountOption, R.layout.spinner_item);
        discountSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_popup);
        discountSpinner.setAdapter(discountSpinnerAdapter);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cleanup(true);
            }
        });

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTaxItemToMainList();
                //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calculate();
            }
        });

        settingsTextView.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));

        settingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_context, TaxCalcConfig.class);
                startActivityForResult(intent, 1500);
            }
        });


        //swipe to delete
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            Drawable background = new ColorDrawable(Color.parseColor("#ffccbc"));

            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                    // move item in `fromPos` to `toPos` in adapter.
                return true;// true if moved, false otherwise
            }

            @Override
            public void onChildDraw(Canvas c,
                                    RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder,
                                    float dX,
                                    float dY,
                                    int actionState,
                                    boolean isCurrentlyActive){
                View itemView = viewHolder.itemView;
                background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                taxItemList.remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(taxItemsRecyclerView);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if (requestCode == 1500){
            if(resultCode == RESULT_OK){
                //tax list updated, RESULT_CANCELLED - tax list still the same
                Cleanup(true);
                PopulateTaxOptions();
            }
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.taxcalc_discountSpinner)
        {
            isDiscountPercent = pos % 2 == 0;
            Log.w("isPercent", String.valueOf(isDiscountPercent));
        }
        else if(spinner.getId() == R.id.taxcalc_taxOptionSpinner)
        {
            Log.w("taxes", taxes.get(pos).toString());
            selectedTax = pos;
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    void AddTaxItemToMainList(){
        TaxItem item;
        double price, taxRate, discount;
        boolean taxPercent, replaceMainTax, discountPercent;
        int quantity;
        String taxOption;

        price = itemPriceText.length() > 0 ? Double.parseDouble(itemPriceText.getText().toString()) : 0;
        taxRate = taxes.get(selectedTax).getTaxRate();
        discount = discountText.length() > 0 ? Double.parseDouble(discountText.getText().toString()) : 0;
        taxPercent = taxes.get(selectedTax).isPercent();
        replaceMainTax = taxes.get(selectedTax).isReplaceMainTax();
        discountPercent = isDiscountPercent;
        quantity = quantityText.length() > 0 ? Integer.parseInt(quantityText.getText().toString()) : 1;
        taxOption = taxes.get(selectedTax).toString();

        item = new TaxItem(price, taxRate, taxPercent, replaceMainTax, quantity, discount, discountPercent, taxOption);
        taxItemList.add(item);
        adapter.notifyDataSetChanged();
        Cleanup(false);
    }

    void PopulateTaxOptions(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            //read saved taxes json into a list of tax options
            //read saved taxes json into a list of tax names to use with the spinner
            taxes.clear();
            jsonArray = new JSONArray(preferences.getString("taxes", null));
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject o = jsonArray.getJSONObject(i);
                TaxOption taxOption = new TaxOption(o.getString("name"), o.getDouble("taxRate"),
                        o.getBoolean("isPercent"), o.getBoolean("replaceMainTax"));
                taxes.add(taxOption);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void Calculate(){
        double subtotal = 0, tax = 0, total = 0;
        Boolean invalidTaxes = false;
        String invalidTaxesMsg = "Discount can't be bigger than item price. Resetting some items' discount to 0.";
        if (taxItemList.size() > 0){

            for (TaxItem t : taxItemList) {
                double _subt, _tax;
                //discount
                if(t.is_discountPercent()){
                    if(t.get_discount() < 100)
                        _subt = (t.get_price() - (t.get_price() * t.get_discount() / 100));
                    else{
                        _subt = t.get_price();
                        invalidTaxes = true;
                    }
                }else{
                    if(t.get_discount() < t.get_price())
                        _subt = (t.get_price() - t.get_discount());
                    else{
                        _subt = t.get_price();
                        invalidTaxes = true;
                    }

                }
                //tax
                //taxes[0] is the regular tax, taxes[1] is tax exempt, taxes[2]-taxes[infinity] are user defined. It is known.
                if (t.is_replaceMainTax()){
                    if(t.is_isPercent())
                        _tax = _subt * t.get_taxRate() / 100;
                    else
                        _tax = t.get_taxRate();
                }else {
                    if(t.is_isPercent())
                        _tax = (_subt * t.get_taxRate() / 100) + (_subt * taxes.get(0).getTaxRate() / 100);
                    else
                        _tax = t.get_taxRate() + (_subt * taxes.get(0).getTaxRate() / 100);
                }
                //total
                subtotal += _subt * t.get_quantity();
                tax += _tax * t.get_quantity();
                total += (_subt + _tax) * t.get_quantity();
            }
        }
        if(invalidTaxes){
            Toast.makeText(this, invalidTaxesMsg, Toast.LENGTH_LONG).show();
        }
        FragmentManager fragmentManager = getFragmentManager();
        TaxCalcResultFragment fragment = TaxCalcResultFragment.newInstance(subtotal,tax,total);
        fragment.show(fragmentManager, "none");
    }

    void Cleanup(Boolean all){
        if (all){
            taxItemList.clear();
            adapter.notifyDataSetChanged();
        }
        itemPriceText.setText("");
        discountText.setText("");
        discountSpinner.setSelection(0);
        taxOptionSpinner.setSelection(0);
        quantityText.setText("");
        itemPriceText.requestFocus();
    }



}
