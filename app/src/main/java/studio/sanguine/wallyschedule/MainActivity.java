//WallySchedule Main activity.
//If you wish to test this app, keep in mind that Walmart One servers are DOWN every day 7-9 PM Pacific time. 6-9:30-ish on Sundays.
package studio.sanguine.wallyschedule;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;

import java.text.DateFormat;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class MainActivity extends Activity {
    final String todayOnlyPrefName = "isOnlyTodaySchedule";

    ListView mainListView;
    ArrayList<ScheduleCellSingle> scheduleList;
    RowAdapter adapter;
    String todaysDate = Util.GetTodaysTimecode(false);
    String localHtml;
    ProgressDialog progressDialog;
    TextView empName;
    TextView empWin;
    Button refreshButton;
    Button menuButton;
    DrawerLayout drawerLayout;
    ListView drawerListView;
    DrawerListViewAdapter _drawerListViewAdapter;
    ArrayList<DrawerMenuItem> drawerMenuItems;
    long savedTimeStamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menuButton = (Button)findViewById(R.id.menuButton);
        refreshButton = (Button)findViewById(R.id.refreshButton);
        empName = (TextView)findViewById(R.id.drawerEmployeeName);
        empWin = (TextView)findViewById(R.id.drawerEmployeeWIN);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayoutMain);
        drawerMenuItems = new ArrayList<DrawerMenuItem>();
        _drawerListViewAdapter = new DrawerListViewAdapter(this, drawerMenuItems);
        drawerListView = (ListView)findViewById(R.id.drawerItemsListView);
        drawerListView.setAdapter(_drawerListViewAdapter);

        //We do not use drawables for icons because a) they take space,
        // b) if I want to replace one of them, I need to replace several at once,
        // and c) a font looks good on any resolution.
        menuButton.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));
        refreshButton.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));

        scheduleList = new ArrayList<ScheduleCellSingle>();
        adapter = new RowAdapter(this, scheduleList, todaysDate);
        mainListView = (ListView)findViewById(R.id.main_ScheduleListView);
        mainListView.setAdapter(adapter);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progress_message));
        progressDialog.setTitle(getString(R.string.progress_title));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Start();

    }

    @Override
    public void onResume(){
        super.onResume();
        //Register LocalBroadcastReceiver to call PopulateListView upon update
        LocalBroadcastManager.getInstance(this).registerReceiver(successMessageReceiver, new IntentFilter("WALLY_DOWNLOAD_FINISHED"));
        LocalBroadcastManager.getInstance(this).registerReceiver(failMessageReceiver, new IntentFilter("WALLY_DOWNLOAD_FAILED"));
    }

    @Override
    public void onPause(){
        super.onPause();
        //unregister LocalBroadcastReceiver to save battery
        LocalBroadcastManager.getInstance(this).unregisterReceiver(successMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(failMessageReceiver);
    }

    //create our BroadcastReceivers
    private BroadcastReceiver successMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PopulateListView();
        }
    };

    private BroadcastReceiver failMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast toast = Toast.makeText(context, intent.getStringExtra("errorMessage"), Toast.LENGTH_LONG);
            toast.show();
            PopulateListView();
        }
    };

    void SetUpInteractiveElements(){
        //add OnClickListeners to elements
        //This function is called from Start() and from onActivityResult from FirstLaunchActivity
        final Context context = this;

        //add items to drawer
        if(_drawerListViewAdapter.getCount() < 1) {
            for (int i = 0; i < 5; i++) {
                DrawerMenuItem item = new DrawerMenuItem(i, this);
                _drawerListViewAdapter.add(item);
            }
            _drawerListViewAdapter.notifyDataSetChanged();

            drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (_drawerListViewAdapter.getCount() > 0) {
                        Intent intent;
                        switch (i) {
                            case 0: //taxcalc
                                drawerLayout.closeDrawer(Gravity.LEFT, false);
                                intent = new Intent(context, TaxCalcActivity.class);
                                startActivityForResult(intent, 3000);
                                break;
                            case 1: //report an absence
                                drawerLayout.closeDrawer(Gravity.LEFT, false);
                                FragmentManager deptListManager = getFragmentManager();
                                DeptListFragment deptListFragment = DeptListFragment.newInstance();
                                deptListFragment.show(deptListManager, "");
                                break;
                            case 2: //report an absence
                                drawerLayout.closeDrawer(Gravity.LEFT, false);
                                FragmentManager reportAbsenceManager = getFragmentManager();
                                ReportAbsenceFragment reportAbsenceFragment = ReportAbsenceFragment.newInstance();
                                reportAbsenceFragment.show(reportAbsenceManager, "");
                                break;
                            case 3: //app settings
                                drawerLayout.closeDrawer(Gravity.LEFT, false);
                                intent = new Intent(context, SettingsActivity.class);
                                startActivityForResult(intent, 2000);
                                break;
                            case 4: //about
                                drawerLayout.closeDrawer(Gravity.LEFT, false);
                                FragmentManager fragmentManager = getFragmentManager();
                                AboutWallyScheduleFragment fragment = AboutWallyScheduleFragment.newInstance();
                                fragment.show(fragmentManager, "");
                                break;
                            default:
                                Toast.makeText(context, "Unknown error in drawerLayout!", Toast.LENGTH_SHORT).show();
                                break;
                        }

                    }
                }
            });
        }

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //open and close drawer
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(Gravity.LEFT))
                    drawerLayout.openDrawer(Gravity.LEFT);
                else
                    drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //download and refresh schedule
                if(drawerLayout.isDrawerOpen(Gravity.LEFT))
                    drawerLayout.closeDrawer(Gravity.LEFT);
                adapter.clear();
                progressDialog.show();
                NetUtil.StartDownload(context);
            }
        });

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int i, long l){
                //show open shifts
                if(adapter.getCount() > 0){
                    if (adapter.getItem(i).isOpenShift) {
                        String date = DateFormat.getDateInstance().format(savedTimeStamp);
                        Document doc = Jsoup.parse(localHtml);
                        String parsedOpenScheduleList = doc.select("div." + adapter.getItem(i).getOpenScheduleTimeCode() + " ").toString();
                        FragmentManager fragmentManager = getFragmentManager();
                        OpenScheduleFragment fragment = OpenScheduleFragment.newInstance(parsedOpenScheduleList, date);
                        fragment.show(fragmentManager, "");
                    }
                }
            }
        });
    }

    void Start(){
        //Check if the downloaded schedule is at least a day old before downloading a new one, otherwise use the saved one.
        //This is to save the battery and avoid waiting ~10 seconds every time you open the app.
        long currentTimeStamp = System.currentTimeMillis();
        savedTimeStamp = Util.GetSavedTimeStamp(this);
        SetUpInteractiveElements();
        if (savedTimeStamp == -1){
            //We need to launch the startup activity so that user can log in.
            Intent intent = new Intent(this, FirstLaunchActivity.class);
            startActivityForResult(intent, 1000);
            return;
        }

        localHtml = Util.OpenSavedHtml(this);

        if (currentTimeStamp >= savedTimeStamp + Util.GetUpdateFrequency(this)){
        //if (currentTimeStamp >= savedTimeStamp + 120000){ //2 minutes, use for testing
            //show a progress dialog and start downloading the schedule
            if(!Util.GetDownloadLock(this)){
                progressDialog.show();
                NetUtil.StartDownload(this);
            }else {
                Toast.makeText(this, "Another download is in progress", Toast.LENGTH_LONG).show();
                PopulateListView();
            }

        } else {
            PopulateListView();
        }


    }

    @Override
    public void onBackPressed(){
        if (drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.closeDrawer(Gravity.LEFT);
        else
            super.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        //PopulateListView if first launch was a success, else exit app
        if(requestCode == 1000){
            if(resultCode == RESULT_OK){
                //load localhtml and add onclicklisteners after first launch completes
                localHtml = Util.OpenSavedHtml(this);
                SetUpInteractiveElements();
                PopulateListView();
            } else {
                finish();
            }
        }
        if(requestCode == 2000){
            if(resultCode == RESULT_OK) {
                //refresh after exiting the settings activity
                PopulateListView();
            }
        }
    }


    //#################### FILE AND TEXT OPERATIONS ###################################
    public void PopulateListView(){
        //Now here's the fun part. mySchedule has a HUGE ASS parser that heavily relies on positions of tags and whatnot in the original aspx page.
        //Then, through an asteroid field of unnecessary classes, it gets assembled into the main view, where the text gets squished on any Android >=6.0.
        //We're gonna use Jsoup to keep things simple.

        String scheduleHtml = Util.OpenSavedHtml(this);
            try{
                //load raw html into Document
                Document document = Jsoup.parse(scheduleHtml);

                adapter.clear();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                boolean displayFromTodayOnly = preferences.getBoolean(todayOnlyPrefName, false);

                //get only relevant data from document, in our case <tr dataDate="yyyyMMdd"> pattern
                //Elements extends ArrayList<Element>
                Elements elements = document.select("tr[dataDate]");
                //Elements walmartWeekInfo = document.select("div[id^=pnlWeek]");

                //Log.w("%%%%%_TEST_STRING", document.select("div.weekDate").text());
                //Log.w("%%%%%_TEST_STRING", String.valueOf(walmartWeekInfo.size()));

                int position = 0;
                boolean isFinalPosition = false;

                //Let's display the employee's name and WIN in the app drawer while Document is still in the memory
                String name = document.select("table.footer").select("h2").text().trim().toLowerCase();
                name = Util.toSentenceCase(name);
                String win = document.select("div.noPrint").toString();
                win = win.substring(win.indexOf("WIN"), win.indexOf("WIN") + 14);
                empName.setText(name);
                empWin.setText(win);
                float hours = 0.0f;
                for (Element element : elements){
                    //set up strings and plug in info from element
                    String dataDate = element.attr("datadate");

                    if(displayFromTodayOnly){
                        //skip loading element if user chose to load the part of schedule starting today
                        if (Integer.parseInt(todaysDate) > Integer.parseInt(dataDate)) continue;
                    }

                    String dayOfWeek = element.select("div.weekDate").text();
                    String notSched = element.select("span.notCurrentSched").text();
                    String dataKey = element.select(notSched.length() == 0 ? "span.todaySched" : "span.notCurrentSched").select("a").attr("class");
                    boolean isOpenSched = dataKey.equals("unfilledAvail");

                    //construct new ScheduleCellCingle and add them to the list
                    if (notSched.length() != 0){
                        ScheduleCellSingle item = new ScheduleCellSingle(this,dataDate,dayOfWeek, isOpenSched);
                        adapter.add(item);
                        adapter.notifyDataSetChanged();
                    } else{
                        String shiftTime = element.select("span.schedTime > b").text();
                        String lunchTime = element.select("span.schedTime").select("span > b").toString();
                        if (lunchTime.length() != 0)
                            lunchTime = lunchTime.substring(lunchTime.indexOf("</b>\n<b>") + 8, lunchTime.length() - 4);
                        else
                            lunchTime = ""; //needs to be initialized at least, ScheduleCellCingle will replace it with -none-
                        String jobDesc = element.select("span.schedTime").toString();
                        if (jobDesc.length() != 0) jobDesc = jobDesc.substring(jobDesc.indexOf("&nbsp;") + 6, jobDesc.indexOf("<br>"));

                        ScheduleCellSingle item = new ScheduleCellSingle(this,dataDate,dayOfWeek,shiftTime,lunchTime,jobDesc, isOpenSched);
                        adapter.add(item);
                        adapter.notifyDataSetChanged();
                    } //end if notsched.length !=0
                    //see if it's today, then stop checking
                    if (!isFinalPosition){
                        if (Integer.parseInt(todaysDate) == Integer.parseInt(dataDate)) {
                            isFinalPosition = true;
                        }
                        else position++;
                    } //end if
                } //end for

                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                //smooth scroll to today's schedule

                if(!displayFromTodayOnly)
                    mainListView.smoothScrollToPosition(position + 2 <= adapter.getCount() - 1 ? position + 2 : position);
                else
                    mainListView.smoothScrollToPosition(0);



            } catch (Exception e){
                Toast.makeText(this, "Error parsing the schedule! Please refresh.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                //PopulateListView();
            }

    }

}

