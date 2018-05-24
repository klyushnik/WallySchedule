package studio.sanguine.wallyschedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mafia on 9/5/2017.
 */

public final class Util {
    //This is a utility class designed to make the main code look less ugly.
    //Contains: OpenSavedHtml, GetTodaysTimeCode, toSentenceCase, SaveDownloadedSchedule,
    //          SaveCredentials, GetLogin, GetSavedPassword, GetSavedTimestamp,
    //          GetDownloadLock, SetDownloadLock, GetUpdateFrequency
    //these are needed for reading and saving preferences
    private static final String MY_FILE = "Schedule.save";
    private static final String timeStampName = "downloadTimeStamp";
    private static final String passVal = "VTNWd1pYSlRaV055WlhSUVlYTnpkMjl5WkE9PQ==";
    public static final String loginVal = "userName";
    private static final String DownloadLockValue = "DOWNLOAD_LOCK";

    private Util(){}

    public static String OpenSavedHtml(Context context){
        //Open the downloaded schedule from file if it has been less than an hour since previous update
        //We don't want the app to update every time the user opens it, because
        // a)it drains battery, b)it's taking like 10 seconds to get the schedule, and c)there could be no service or problems with WM1 servers.
        String result = "";
        try {
            File file = new File(context.getFilesDir(), MY_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            StringBuilder sb = new StringBuilder();
            String mLine = reader.readLine();
            while (mLine !=null){
                sb.append(mLine);
                mLine = reader.readLine();
            }
            result = sb.toString();

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static String GetTodaysTimecode(boolean openShiftFormat){
        String todaysTimeCode;
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat(openShiftFormat ? "yyyy-MM-dd" : "yyyyMMdd");
        todaysTimeCode = dateFormat.format(now);
        return todaysTimeCode;
    }

    public static String toSentenceCase(String input){
        //I wanted to do it in couple of lines but then I realized some people might have more than
        //just first & last name. I blame globalization.
        String[] strArray = input.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }
        return  builder.toString();
    }

    public static void SaveDownloadedSchedule(Context context, String scheduleHtml){
        FileOutputStream outputStream = null;
        try {
                //put timestamp into SharedPreferences
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong(timeStampName, System.currentTimeMillis());
                editor.apply();
                //write the file
                outputStream = context.openFileOutput(MY_FILE, Context.MODE_PRIVATE);
                outputStream.write(scheduleHtml.getBytes());
                outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Validation/'Nothing to Show' error. Try again later!", Toast.LENGTH_LONG).show();
        }
    }

    public static  boolean ValidateSchedule(Context context, String scheduleHtml){
        boolean retValue = true;
        Document document = Jsoup.parse(scheduleHtml);
        String validationString = document.select("div.weekDate").text();
        if(validationString == null || validationString == "")
            retValue = false;
        return retValue;
    }

    public static void SaveCredentials(Context context, String login, String password){
        //TODO: replace with better encryption algorithm
        String tmp = Base64.encodeToString(password.trim().getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(passVal, tmp);
        editor.putString(loginVal, login);
        editor.apply();
    }

    public static String GetSavedPassword(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String encryptedPass = preferences.getString(passVal, "");
        byte[] bytes = encryptedPass.getBytes();
        byte[] pass = Base64.decode(bytes, Base64.DEFAULT);
        String p = new String(pass, StandardCharsets.UTF_8);
        return p;
    }
    public static String GetLogin(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String login = preferences.getString(loginVal, "");
        return login;
    }
    public static long GetSavedTimeStamp(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        long savedTimeStamp = preferences.getLong(timeStampName, -1);
        return savedTimeStamp;
    }

    public static boolean GetDownloadLock(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(DownloadLockValue, false);
    }

    public static void SetDownloadLock(boolean isLocked, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(DownloadLockValue, isLocked);
        editor.apply();
    }

    public static int GetUpdateFrequency(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int frequency = preferences.getInt("updateFrequency", 86400000);
        return frequency;
    }

}
