package studio.sanguine.wallyschedule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class SettingsActivity extends Activity implements AdapterView.OnItemSelectedListener {
    final String todayOnlyPrefName = "isOnlyTodaySchedule";

    Context _context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        Button SettingsBackButton = (Button)findViewById(R.id.settingsBackButton);
        Switch todayScheduleSwitch = (Switch)findViewById(R.id.settingsStartTodaySchedSwitch);
        Switch lunchReminderSwitch = (Switch)findViewById(R.id.settingsLunchReminderSwitch);
        final TextView loginText = (TextView)findViewById(R.id.settingsUsernameEditText);
        final TextView passwordText = (TextView)findViewById(R.id.settingsPasswordEditText);
        TextView saveCredentialsTextView = (TextView)findViewById(R.id.settingsUpdateAccountTextView);
        TextView taxCalcSettingsTextView = (TextView)findViewById(R.id.settingsTaxCalcTextView);

        Spinner updateSpinner = (Spinner) findViewById(R.id.settingsUpdateFrequencySpinner);
        updateSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.updateFrequency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateSpinner.setAdapter(adapter);

        //read update frequency and put updateSpinner in correct position
        int updateFreq = Util.GetUpdateFrequency(this);
        switch (updateFreq){
            case 43200000:
                updateSpinner.setSelection(0);
                break;
            case 86400000:
                updateSpinner.setSelection(1);
                break;
            case 172800000:
                updateSpinner.setSelection(2);
                break;
            case 259200000:
                updateSpinner.setSelection(3);
                break;
            default:
                updateSpinner.setSelection(1);
                break;
        }

        taxCalcSettingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_context, TaxCalcConfig.class);
                startActivity(intent);
            }
        });


        SettingsBackButton.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));

        loginText.setText(Util.GetLogin(_context));
        passwordText.setText(Util.GetSavedPassword(_context));

        todayScheduleSwitch.setChecked(prefs.getBoolean(todayOnlyPrefName, false));

        todayScheduleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(todayOnlyPrefName, isChecked);
                editor.apply();
            }
        });

        SettingsBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        saveCredentialsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginText.getText().length() == 0 || passwordText.getText().length() == 0){
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(_context, "NOPE. Login or password cannot be empty. Try again.", duration);
                    toast.show();
                } else
                    Util.SaveCredentials(_context, loginText.getText().toString(), passwordText.getText().toString());
            }
        });
    }
    @Override
    public void onBackPressed(){
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        int updateFrequencyMillis;
        switch (pos){
            case 0:
                updateFrequencyMillis = 43200000;
                Log.w("update frequency", "12 hours");
                break;
            case 1:
                updateFrequencyMillis = 86400000;
                Log.w("update frequency", "24 hours");
                break;
            case 2:
                updateFrequencyMillis = 172800000;
                Log.w("update frequency", "48 hours");
                break;
            case 3:
                updateFrequencyMillis = 259200000;
                Log.w("update frequency", "72 hours");
                break;
            default:
                updateFrequencyMillis = 86400000;
                Log.w("update frequency", "default value (24 hours) assigned");
                break;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("updateFrequency", updateFrequencyMillis);
        editor.apply();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
