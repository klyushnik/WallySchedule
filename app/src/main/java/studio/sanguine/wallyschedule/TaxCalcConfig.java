package studio.sanguine.wallyschedule;

import android.app.Activity;
import android.content.Context;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaxCalcConfig extends Activity {
    //load taxes from json from sharedpreferences and add them to list
    //add or remove any taxes - list
    //when saving taxes, generate json and replace the one in sharedpreferences
    //taxcalc activity will re-load the tax list and do a clean state

    List<TaxOption> taxes;
    List<TaxOption> defaultTaxes; //main tax and tax exempt

    EditText taxNameText, taxRateText, mainTaxText;
    RadioButton dollarsRadioButton, percentRadioButton;
    CheckBox overrideMainCheckbox;
    RecyclerView taxOptionRecyclerView;
    Button addButton, saveButton, cancelButton;
    TaxOptionAdapter taxOptionAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tax_calc_config);
        taxes = new ArrayList<TaxOption>();
        defaultTaxes = new ArrayList<TaxOption>();
        PopulateTaxOptions();

        mainTaxText = (EditText)findViewById(R.id.taxConfig_mainTaxEditText);
        taxNameText = (EditText)findViewById(R.id.taxConfig_taxNameEditText);
        taxRateText = (EditText)findViewById(R.id.taxConfig_taxRateEditText);
        dollarsRadioButton = (RadioButton)findViewById(R.id.taxConfig_dollarsRadioButton);
        percentRadioButton = (RadioButton)findViewById(R.id.taxConfig_percentRadioButton);
        overrideMainCheckbox = (CheckBox)findViewById(R.id.taxConfig_replaceCheckBox);
        taxOptionRecyclerView = (RecyclerView)findViewById(R.id.taxConfig_taxesRecyclerView);
        addButton = (Button)findViewById(R.id.taxConfig_addUpdateButton);
        saveButton = (Button)findViewById(R.id.taxConfig_saveButton);
        cancelButton = (Button)findViewById(R.id.taxConfig_cancelButton);

        taxOptionAdapter = new TaxOptionAdapter(taxes);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        taxOptionRecyclerView.setLayoutManager(manager);
        taxOptionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        taxOptionRecyclerView.setAdapter(taxOptionAdapter);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveTaxes();
                setResult(RESULT_OK);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTaxToList();
                taxNameText.setText("");
                taxRateText.setText("");
                percentRadioButton.setChecked(true);
                dollarsRadioButton.setChecked(false);
                overrideMainCheckbox.setChecked(false);
                taxRateText.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });


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
                taxes.remove(viewHolder.getAdapterPosition());
                taxOptionAdapter.notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(taxOptionRecyclerView);

        mainTaxText.setText(String.valueOf(defaultTaxes.get(0).getTaxRate()));
    }

    void PopulateTaxOptions(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            //read saved taxes json into a list of tax options
            //read saved taxes json into a list of tax options to use with the recyclerview
            taxes.clear();
            JSONArray jsonArray = new JSONArray(preferences.getString("taxes", null));
            for (int i = 0; i < 2; i++){ //default taxes, saved first, loaded first
                JSONObject o = jsonArray.getJSONObject(i);
                TaxOption taxOption = new TaxOption(o.getString("name"), o.getDouble("taxRate"),
                        o.getBoolean("isPercent"), o.getBoolean("replaceMainTax"));
                defaultTaxes.add(taxOption);
            }
            for (int i = 2; i < jsonArray.length(); i++){
                JSONObject o = jsonArray.getJSONObject(i);
                TaxOption taxOption = new TaxOption(o.getString("name"), o.getDouble("taxRate"),
                        o.getBoolean("isPercent"), o.getBoolean("replaceMainTax"));
                taxes.add(taxOption);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void SaveTaxes(){
        //This will generate the Tax Exempt option and a couple of testing values, and save them to
        // sharedprefs for TaxCalc testing
        JSONArray jsonArray = new JSONArray();

        defaultTaxes.get(0).setTaxRate(Double.parseDouble(mainTaxText.getText().toString()));

        try {
            for(TaxOption o : defaultTaxes){
                JSONObject j = new JSONObject();
                j.put("name", o.getName());
                j.put("taxRate", o.getTaxRate());
                j.put("isPercent", true);
                j.put("replaceMainTax", true);
                jsonArray.put(j);
            }
            for(TaxOption o : taxes){
                JSONObject j = new JSONObject();
                j.put("name", o.getName());
                j.put("taxRate", o.getTaxRate());
                j.put("isPercent", o.isPercent());
                j.put("replaceMainTax", o.isReplaceMainTax());
                jsonArray.put(j);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.i("json", String.valueOf(jsonArray));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("taxes", String.valueOf(jsonArray));
        editor.apply();
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    void AddTaxToList(){
        String name;
        Double rate;
        Boolean isPercent, replaceMain;
        name = taxNameText.length() > 0 ? taxNameText.getText().toString() : "(No Name)";
        rate = taxRateText.length() > 0 ? Double.parseDouble(taxRateText.getText().toString()) : 0;
        isPercent = percentRadioButton.isChecked();
        replaceMain = overrideMainCheckbox.isChecked();
        TaxOption taxOption = new TaxOption(name, rate, isPercent, replaceMain);
        taxes.add(taxOption);
        taxOptionAdapter.notifyDataSetChanged();

    }

}
