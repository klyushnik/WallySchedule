package studio.sanguine.wallyschedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class FirstLaunchActivity extends Activity {

    EditText loginText;
    EditText passwordText;
    TextView nextTextView;
    TextView noAccountTextView;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_1_fragment);
        loginText = (EditText)findViewById(R.id.welcomeUserNameEditText);
        passwordText = (EditText)findViewById(R.id.welcomeUserPasswordEditText);
        noAccountTextView = (TextView)findViewById(R.id.welcome1NoAccountTextView);
        nextTextView = (TextView)findViewById(R.id.welcome1NextTextView);
        final Context _context = getApplicationContext();

        LocalBroadcastManager.getInstance(this).registerReceiver(successMessageReceiver, new IntentFilter("WALLY_DOWNLOAD_FINISHED"));
        LocalBroadcastManager.getInstance(this).registerReceiver(failMessageReceiver, new IntentFilter("WALLY_DOWNLOAD_FAILED"));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progress_message));
        progressDialog.setTitle(getString(R.string.progress_title));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        nextTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginText.getText().length() == 0 || passwordText.getText().length() == 0){
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(_context, "NOPE. Login or password cannot be empty. Try again.", duration);
                    toast.show();
                    return;
                }
                Util.SaveCredentials(_context, loginText.getText().toString(), passwordText.getText().toString());
                progressDialog.show();
                NetUtil.StartDownload(_context);
            }
        });

        noAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://authn.walmartone.com/login.aspx";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        //set some default settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("updateFrequency", 86400000); //24 hours update frequency
        editor.putBoolean("isOnlyTodaySchedule", false); //start with cut off schedule : false
        editor.apply();

        GenerateDefaultTaxCalcSettings();
    }

    //create our BroadcastReceivers
    private BroadcastReceiver successMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO: Show FirstLaunchSuccessFragment
            //Save schedule, save time stamp, set RESULT_OK, exit the activity
            Success();
        }
    };

    private BroadcastReceiver failMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //show FirstLaunchErrorFragment and do not proceed
            Fail(intent.getIntExtra("errorCode", -1));
        }
    };

    void Success(){
        if(progressDialog.isShowing()) progressDialog.dismiss();
        setResult(RESULT_OK);
        finish();
    }
    void Fail(int errorCode){
        if(progressDialog.isShowing()) progressDialog.dismiss();

        String errorMessage;
        switch (errorCode){
            case 0:
                errorMessage = getString(R.string.welcome_error_connection);
                break;
            case 1:
                errorMessage = getString(R.string.welcome_error_credentials);
                break;
            case 2:
                errorMessage = getString(R.string.welcome_error_timeout);
                break;
            default:
                errorMessage = "Unknown error. If you are seeing this, contact the developer.";
                break;
        }
        try {
            if (!isFinishing()) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(getString(R.string.welcome_error_header));
                alertDialog.setMessage(errorMessage);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
            }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Unknown error. If you are seeing this, contact the developer.", Toast.LENGTH_LONG).show();
        }
        /*FirstLaunchErrorFragment fragment = FirstLaunchErrorFragment.newInstance(errorCode);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(fragment, "error_fragment");
        transaction.commitAllowingStateLoss();*/ //This crashes the app under unknown circumstances, cant figure out exactly what the fuck
        /*FragmentManager fragmentManager = getFragmentManager();
        FirstLaunchErrorFragment fragment = FirstLaunchErrorFragment.newInstance(errorCode);
        fragment.show(fragmentManager, "");*/ //This crashes the app if user minimizes the app while the fragment is showing or about to show
    }


    void GenerateDefaultTaxCalcSettings(){
        //This will generate the Tax Exempt option and a couple of testing values, and save them to
        // sharedprefs for TaxCalc testing
        JSONArray jsonArray = new JSONArray();
        JSONObject mainTax = new JSONObject();
        JSONObject taxExempt = new JSONObject();
        JSONObject exampleTax = new JSONObject();

        try {
            //main tax
            mainTax.put("name", "Regular Tax");
            mainTax.put("taxRate", 8.0);
            mainTax.put("isPercent", true);
            mainTax.put("replaceMainTax", true);

            //tax exempt
            taxExempt.put("name", "Tax Exempt");
            taxExempt.put("taxRate", 0.0);
            taxExempt.put("isPercent", true);
            taxExempt.put("replaceMainTax", true);

            exampleTax.put("name", "Example Tax");
            exampleTax.put("taxRate", 14.0);
            exampleTax.put("isPercent", false);
            exampleTax.put("replaceMainTax", false);

            jsonArray.put(mainTax);
            jsonArray.put(taxExempt);
            jsonArray.put(exampleTax);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.i("json", String.valueOf(jsonArray));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("taxCalcFirstLaunch", true);
        editor.putString("taxes", String.valueOf(jsonArray));
        editor.apply();
    }

}
