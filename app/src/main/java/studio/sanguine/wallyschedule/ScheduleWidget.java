package studio.sanguine.wallyschedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

//TODO: add WIDGET_REFRESH intent that calls CallUpdate and use it in MidnightAlarm instead of WIDGET_UPDATE_TEXT

public class ScheduleWidget extends AppWidgetProvider {
    public static final String WIDGET_UPDATE_TEXT = "studio.sanguine.wallyschedule.WALLY_MIDNIGHT_UPDATE";
    public static final String WIDGET_MAIN_ACTIVITY_TEXT = "studio.sanguine.wallyschedule.WALLY_LAUNCH_MAIN_ACTIVITY";
    public static final String WIDGET_TAX_CALC_TEXT = "studio.sanguine.wallyschedule.WALLY_LAUNCH_TAX_CALC";
    public static final String WIDGET_REFRESH_TEXT = "studio.sanguine.wallyschedule.WALLY_REFRESH_WIDGET";

    static String shiftTimeToday = "";
    static String shiftMealToday = "";
    static String shiftRoleToday = "";
    static String shiftTimeTomorrow = "";
    static String shiftMealTomorrow = "";
    static String shiftRoleTomorrow = "";
    static String updateInProgressText = "Updating...";
    //static boolean doUpdate = false;
    //static boolean skipUpdate = false;
    //static boolean updatingText = false;
    static boolean isFirstLaunch = true; //used to be static but it wouldn't work when phone restarts, will not be static for now...
    static String walmartWeek = "";
    static String todayLabel = "";
    static String tomorrowLabel = "";


    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wally_widget);

        views.setTextViewText(R.id.widgetTodayTime, shiftTimeToday);
        views.setTextViewText(R.id.widgetTodayMeal, shiftMealToday);
        views.setTextViewText(R.id.widgetTodayRole, shiftRoleToday);
        views.setTextViewText(R.id.widgetTomorrowTime, shiftTimeTomorrow);
        views.setTextViewText(R.id.widgetTomorrowMeal, shiftMealTomorrow);
        views.setTextViewText(R.id.widgetTomorrowRole, shiftRoleTomorrow);
        views.setTextViewText(R.id.widgetTitle, "Week " + walmartWeek + ", Upd: " + DateFormat.getDateInstance().format(Util.GetSavedTimeStamp(context)));

        views.setTextViewText(R.id.widget_todayLabel, "TODAY " + todayLabel);
        views.setTextViewText(R.id.widget_tomorrowLabel, "TOMORROW " + tomorrowLabel);

        Log.w("widget", "widget updated");

        Intent intentFS = new Intent(WIDGET_MAIN_ACTIVITY_TEXT);
        Intent updateWidgetIntent = new Intent(WIDGET_UPDATE_TEXT);
        Intent taxCalcIntent = new Intent(WIDGET_TAX_CALC_TEXT);
        PendingIntent launchMainActivity = PendingIntent.getBroadcast(context, 0, intentFS, 0);
        PendingIntent updateWidget = PendingIntent.getBroadcast(context, 0, updateWidgetIntent, 0);
        PendingIntent launchTaxCalc = PendingIntent.getBroadcast(context, 0, taxCalcIntent, 0);
        views.setOnClickPendingIntent(R.id.widgetFullScheduleTextView, launchMainActivity);
        views.setOnClickPendingIntent(R.id.widgetTitle, updateWidget);
        views.setOnClickPendingIntent(R.id.widgetTaxCalcText, launchTaxCalc);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //boolean isFirstLaunch = true;
        long savedTimeStamp = Util.GetSavedTimeStamp(context);
        //long currentTimeStamp = System.currentTimeMillis();

        if(isFirstLaunch){
            //when a phone restarts, onUpdate is called but not onEnabled, so we need to set up our receivers here.
            Util.SetDownloadLock(false, context);
            LocalBroadcastManager.getInstance(context).registerReceiver(successMessageReceiver, new IntentFilter("WALLY_DOWNLOAD_FINISHED"));
            LocalBroadcastManager.getInstance(context).registerReceiver(failMessageReceiver, new IntentFilter("WALLY_DOWNLOAD_FAILED"));
            Log.w("widget", "LocalBroadcastManager registration OK!");
            Log.w("widget", "first launch = " + String.valueOf(isFirstLaunch));
            isFirstLaunch = false;
        }

        if (savedTimeStamp == -1){
            //if there is no data, open main activity so that user logs in and downloads the initial schedule
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        }
        /*if ((currentTimeStamp >= savedTimeStamp + Util.GetUpdateFrequency(context) && !skipUpdate) || doUpdate){ //24 * 60 * 60 * 1000
            //DEPRECATED. Widget fucks up when trying to update in Doze mode.
            //
            //doUpdate forces manual update when refresh button is clicked
            //skipUpdate == true when broadcastreceiver reports failure; to avoid infinite loop
            //call getInfoDetails to fill out the details before widget gets updated
            updatingText = true;
            getInfoDetails(context);
            Util.SetDownloadLock(false, context);
            NetUtil.StartDownload(context);
        }*/ else {
            //just refresh the widget; also called when broadcastreceiver reports success
            //this block will also execute at 00:00:01 every day
            getInfoDetails(context);
        }
        SetMidnightAlarm(context);

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        //set up midnight alarm
        SetMidnightAlarm(context);
        Log.w("widget", "widget created");
    }

    void SetMidnightAlarm(Context context){
        Intent intent = new Intent(WIDGET_REFRESH_TEXT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        LocalBroadcastManager.getInstance(context).unregisterReceiver(successMessageReceiver);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(failMessageReceiver);
    }

    private BroadcastReceiver successMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Download Finished", Toast.LENGTH_SHORT).show();
            CallUpdate(context);
        }
    };

    private BroadcastReceiver failMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show();
            CallUpdate(context);
        }
    };

    void CallUpdate(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), ScheduleWidget.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent){
        //set alarm and onUpdate
        if(intent.getAction().equals(WIDGET_UPDATE_TEXT)) {
            Toast.makeText(context, "Starting download", Toast.LENGTH_SHORT).show();
            NetUtil.StartDownload(context);
        }
        if(intent.getAction().equals(WIDGET_MAIN_ACTIVITY_TEXT)){
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }
        if(intent.getAction().equals(WIDGET_TAX_CALC_TEXT)){
            Intent mainActivityIntent = new Intent(context, TaxCalcActivity.class);
            mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }
        if(intent.getAction().equals(WIDGET_REFRESH_TEXT)){
            CallUpdate(context);
        }
        super.onReceive(context, intent);
    }

    static void getInfoDetails(Context context){
        String scheduleHtml = Util.OpenSavedHtml(context);
        Document document = Jsoup.parse(scheduleHtml);

        String currentWeek = scheduleHtml.substring(scheduleHtml.indexOf("var currentWeek = ") + 18);
        currentWeek = currentWeek.substring(0, currentWeek.indexOf(";"));

        walmartWeek = currentWeek;

        //get today and tomorrow date
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        DateFormat labelDateFormat = new SimpleDateFormat("EEE dd");
        String todayAsString = dateFormat.format(today);
        String tomorrowAsString = dateFormat.format(tomorrow);
        String labelTodayAsString = labelDateFormat.format(today);
        String labelTomorrowAsString = labelDateFormat.format(tomorrow);

        todayLabel = labelTodayAsString;
        tomorrowLabel = labelTomorrowAsString;


        String todaySelect = document.select("tr[dataDate="+todayAsString+"]").toString();
        String tomorrowSelect = document.select("tr[dataDate="+tomorrowAsString+"]").toString();
        Log.w("&&&&&&&&____datadate", todaySelect);
        Log.w("&&&&&&&&____datadate", tomorrowSelect);


        Document todayDoc = Jsoup.parse(todaySelect);
        Document tomorrowDoc = Jsoup.parse(tomorrowSelect);


        String todayNotSched = todayDoc.select("span.notCurrentSched").text();
        String tomorrowNotSched = tomorrowDoc.select("span.notCurrentSched").text();

        //if the selection's length != 0 then we assume you're off for the day, else parse the document and load stuff into our vars
        if(todayNotSched.length() != 0){
            shiftTimeToday = "You're off!";
            shiftMealToday = "";
            shiftRoleToday = "";
        } else {
            String todayShiftTime = todayDoc.select("span.schedTime > b").text();
            String todayLunchTime = todayDoc.select("span.schedTime").select("span > b").toString();
            if (todayLunchTime.length() != 0)
                todayLunchTime = todayLunchTime.substring(todayLunchTime.indexOf("</b>\n<b>") + 8, todayLunchTime.length() - 4);
            else
                todayLunchTime = "";
            String todayJobRole = todayDoc.select("span.schedTime").toString();
            if (todayJobRole.length() != 0) todayJobRole = todayJobRole.substring(todayJobRole.indexOf("&nbsp;") + 6, todayJobRole.indexOf("<br>"));
            shiftTimeToday = todayShiftTime;
            shiftMealToday = todayLunchTime;
            shiftRoleToday = todayJobRole;
        }

        if(tomorrowNotSched.length() != 0){
            shiftTimeTomorrow = "You're off!";
            shiftMealTomorrow = "";
            shiftRoleTomorrow = "";
        } else {
            String tomorrowShiftTime = tomorrowDoc.select("span.schedTime > b").text();
            String tomorrowLunchTime = tomorrowDoc.select("span.schedTime").select("span > b").toString();
            if (tomorrowLunchTime.length() != 0)
                tomorrowLunchTime = tomorrowLunchTime.substring(tomorrowLunchTime.indexOf("</b>\n<b>") + 8, tomorrowLunchTime.length() - 4);
            else
                tomorrowLunchTime = "";
            String tomorrowJobRole = tomorrowDoc.select("span.schedTime").toString();
            if (tomorrowJobRole.length() != 0) tomorrowJobRole = tomorrowJobRole.substring(tomorrowJobRole.indexOf("&nbsp;") + 6, tomorrowJobRole.indexOf("<br>"));
            shiftTimeTomorrow = tomorrowShiftTime;
            shiftMealTomorrow = tomorrowLunchTime;
            shiftRoleTomorrow = tomorrowJobRole;
        }



        /*if(tomorrowNotSched.length() != 0){
            shiftTimeTomorrow = "You're off!";
            shiftMealTomorrow = "";
            shiftRoleTomorrow = "";
        } else {
            String tomorrowShiftTime = tomorrowTime.substring(tomorrowTime.indexOf("Time\">") + 6, tomorrowTime.indexOf("</span>"));
            String tomorrowLunchTime;
            try {
                tomorrowLunchTime = tomorrowTime.substring(tomorrowTime.indexOf("/span>"));
                tomorrowLunchTime = tomorrowLunchTime.substring(tomorrowLunchTime.indexOf("Time\">") + 6, tomorrowLunchTime.indexOf("</span>"));
            } catch (Exception e){
                tomorrowLunchTime = ""; //if shift is too short for a lunch break
            }
            String tomorrowJobRole = document.select("div." + tomorrowAsString + " ").select("table.unfilledHeader").select("td.unfilledDetails").toString();
            tomorrowJobRole = tomorrowJobRole.substring(tomorrowJobRole.indexOf("/span") + 6, tomorrowJobRole.indexOf("<div>")).trim();
            shiftTimeTomorrow = tomorrowShiftTime;
            shiftMealTomorrow = tomorrowLunchTime;
            shiftRoleTomorrow = tomorrowJobRole;
        }*/

    }
}

