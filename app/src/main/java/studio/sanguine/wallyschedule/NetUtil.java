package studio.sanguine.wallyschedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by mafia on 9/14/2017.
 */

public class NetUtil {
    private static final String[] ErrorMessages = {
            "Connection Error",
            "Credentials or Login Error",
            "WalmartOne Server Time Out",
            "Another download is in progress!"
    };
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static String loginHtml = "";
    private static String EVENTVALIDATION = "";
    private static String VIEWSTATE = "";
    private static String scheduleHtml = "";
    private static Context _context;


    private NetUtil(){}

    public static void StartDownload (Context context){
        //set up context and start download
        //set up a global lock so that only one instance of the downloader can run at any time
        //otherwise, it will throw a login error
        _context = context;
        if(Util.GetDownloadLock(_context)){
            //if not locked, place lock, otherwise throw LOCKED error and return
            Locked("download locked", 3);
            Util.SetDownloadLock(false, _context);
        }else {
            Util.SetDownloadLock(true, _context);
            GetLoginSessionInfo();
        }
    }

    private static void GetLoginSessionInfo(){
        //Step 1 of 3 - We get the login page so that we can extract __EVENTVALIDATION and __VIEWSTATE.
        //reset client because it throws a CircularRedirectException otherwise
        client = null;
        client = new AsyncHttpClient();

        Document document;

        client.get("https://authn.walmartone.com/login.aspx", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                loginHtml = new String(response);
                Document document = Jsoup.parse(loginHtml);
                EVENTVALIDATION = document.select("[id=__EVENTVALIDATION").attr("value");
                VIEWSTATE = document.select("[id=__VIEWSTATE").attr("value");

                Login();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                e.printStackTrace();
                //Fail(errorResponse.toString(), 0);
                Fail("connection error", 0);
            }

        });
    }

    private static void Login(){
        //Step 2 of 3 - Log in. We generate a cookie (header) and RequestParam with login info,
        // then get the login page once again, so that it gives us the redirect script.

        Log.w("2eventvalidation", EVENTVALIDATION);
        Log.w("2viewstate", VIEWSTATE);

        HashMap<String, String> paramMap = new HashMap<String, String>();

        client.addHeader("Accept", "text/html");
        client.addHeader("User-Agent", "Apache-HttpClient/4.1.2 (java 1.5)");
        client.addHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        client.addHeader("auth_mode", "basic");

        paramMap.put("uxAuthMode", "BASIC");
        paramMap.put("uxOrigUrl", null);
        paramMap.put("uxOverrideUri", "false");
        paramMap.put("__EVENTVALIDATION", EVENTVALIDATION);
        paramMap.put("__VIEWSTATE", VIEWSTATE);
        paramMap.put("uxUserName", Util.GetLogin(_context));
        paramMap.put("uxPassword", Util.GetSavedPassword(_context));
        paramMap.put("SubmitCreds", "Login");

        RequestParams params = new RequestParams(paramMap);

        client.get("https://authn.walmartone.com/login.aspx", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                scheduleHtml = new String(response);
                Log.w("__HTTP_RESPONSE__", scheduleHtml);
                Log.w("___HTTP_RESP_LENGTH", String.valueOf(scheduleHtml.length()));
                if(scheduleHtml.length() > 950){
                    Fail("wrong credentials", 1);
                    return;
                } else
                    GetSchedule();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                e.printStackTrace();
                Fail("other login error", 1);
            }
        });
    }

    static void GetSchedule(){
        //Step 3 of 3 - We get the schedule itself and call PopulateListView to load it into mainListView.

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("uxAuthMode", "BASIC");
        paramMap.put("uxOrigUrl", null);
        paramMap.put("uxOverrideUri", "false");
        paramMap.put("SubmitCreds", "Login");

        RequestParams params = new RequestParams(paramMap);

        client.get("https://login.walmartone.com/onlineschedule/schedule/FullSchedule.aspx", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                scheduleHtml = new String(response);
                Finish(scheduleHtml);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("####__SERVER_TIMEOUT", "Server time out");
                Fail("servertimeout", 2);
            }

        });
    }
    static void Finish(String downloadedFile){
        if(Util.ValidateSchedule(_context, scheduleHtml)) {
            Util.SaveDownloadedSchedule(_context, scheduleHtml);
            Intent intent = new Intent("WALLY_DOWNLOAD_FINISHED");
            LocalBroadcastManager.getInstance(_context).sendBroadcast(intent);
            Util.SetDownloadLock(false, _context);
            Log.w("######_DOWNLOAD_FINISH", "Download has finished!");
        }
        else
            Fail("Validation failed", 2);
    }
    //Fail and Locked do the same thing, and the reason both of them are there is NOT to interrupt
    //another download. Both will bypass the update process, but
    //Fail will refresh the view (since either Finish or Fail will get called at the end),
    //and Locked will only display a message.
    static void Fail(String failOutput, int errorCode){
        Intent intent = new Intent("WALLY_DOWNLOAD_FAILED");
        intent.putExtra("errorCode", errorCode);
        intent.putExtra("errorMessage", ErrorMessages[errorCode]);
        LocalBroadcastManager.getInstance(_context).sendBroadcast(intent);
        Util.SetDownloadLock(false, _context);
        Log.e("######_DOWNLOAD_FAILED", "Download failed: " + failOutput);
    }

    static void Locked(String failOutput, int errorCode){
        //Util.SetDownloadLock(false, _context);
        Toast.makeText(_context, ErrorMessages[errorCode], Toast.LENGTH_LONG).show();
        Log.e("######_DOWNLOAD_FAILED", "Download failed: " + failOutput);
    }
}
