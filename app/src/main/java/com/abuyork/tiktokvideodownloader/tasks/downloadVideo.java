package com.abuyork.tiktokvideodownloader.tasks;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.abuyork.tiktokvideodownloader.utils.iUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.abuyork.tiktokvideodownloader.utils.Constants.DOWNLOADING_MSG;
import static com.abuyork.tiktokvideodownloader.utils.Constants.TiktokApi;
import static com.abuyork.tiktokvideodownloader.utils.Constants.WEB_DISABLE;
import static com.abuyork.tiktokvideodownloader.utils.Constants.WENT_WRONG;

public class downloadVideo {

    public static Context Mcontext;
    public static ProgressDialog pd;
    public static Dialog dialog;
    static String SessionID, Title;
    static int error=1;
    public static SharedPreferences prefs;

    public static Boolean fromService;


    public static void Start(final Context context , String url , Boolean service ){

        Mcontext=context;
        fromService = service;

//SessionID=title;
        if(!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        if(!fromService) {
            pd = new ProgressDialog(context);
            pd.setMessage(DOWNLOADING_MSG);
            pd.setCancelable(false);
            pd.show();
        }
        if(url.contains("tiktok.com")){

            new GetTikTokVideo().execute(url);
        } else if (url.contains("facebook.com")){

//String[] Furl = url.split("/");
// url = Furl[Furl.length-1];
//iUtils.ShowToast(Mcontext,Furl[Furl.length-1]);
            new GetFacebookVideo().execute(url);
        }else if (url.contains("instagram.com")){

            new GetInstagramVideo().execute(url);
        }else{
            if(!fromService) {
                pd.dismiss();

                iUtils.ShowToast(Mcontext,WEB_DISABLE);
            }
        }

//iUtils.ShowToast(Mcontext,url);
//iUtils.ShowToast(Mcontext,SessionID);


        prefs = Mcontext.getSharedPreferences("AppConfig", MODE_PRIVATE);
    }


    private static class GetTikTokVideo extends AsyncTask<String, Void, Document> {
        Document doc;

        @Override
        protected Document doInBackground(String... urls) {
            try {
                doc = Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: Error");
            }
            return doc;
        }

        protected void onPostExecute(Document result) {
// pd.dismiss();
// Log.d("GetResult", );
            try {
                String URL = result.select("link[rel=\"canonical\"]").last().attr("href");

                if(!URL.equals("") && URL.contains("video/")){
                    URL =URL.split("video/")[1];
                    Title = result.title();
// iUtils.ShowToast(Mcontext,URL);
                    new DownloadTikTokVideo().execute(URL);
                }else{
                    if(!fromService) {

                        pd.dismiss();
                    }
                    iUtils.ShowToast(Mcontext,WENT_WRONG);
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
                if(!fromService) {

                    pd.dismiss();
                }
                iUtils.ShowToast(Mcontext,WENT_WRONG);
            }


        }
    }
    private static class GetFacebookVideo extends AsyncTask<String, Void, Document> {
        Document doc;

        @Override
        protected Document doInBackground(String... urls) {
            try {

//doc = Jsoup.connect(FacebookApi).data("v",urls[0]).get();
                doc = Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: Error");
                iUtils.ShowToast(Mcontext,WENT_WRONG);

            }
            return doc;

        }

        protected void onPostExecute(Document result) {
            if(!fromService) {

                pd.dismiss();
            }
// Log.d("GetResult", );
            try {
                String URL = result.select("meta[property=\"og:video\"]").last().attr("content");
                Title = result.title();
// iUtils.ShowToast(Mcontext,URL);
                new downloadFile().Downloading(Mcontext,URL,Title,".mp4");
            } catch (NullPointerException e)
            {
                e.printStackTrace();
                iUtils.ShowToast(Mcontext,WENT_WRONG);
            }
// new DownloadTikTokVideo().execute(URL);

        }
    }

    private static class GetInstagramVideo extends AsyncTask<String, Void, Document> {
        Document doc;

        @Override
        protected Document doInBackground(String... urls) {
            try {
                doc = Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: Error");
            }
            return doc;

        }

        protected void onPostExecute(Document result) {
            if(!fromService) {

                pd.dismiss();}
// Log.d("GetResult", );
            try {
                String URL = result.select("meta[property=\"og:video\"]").last().attr("content");
                Title = result.title();
//iUtils.ShowToast(Mcontext, URL);

                new downloadFile().Downloading(Mcontext, URL, Title, ".mp4");
            }catch (NullPointerException e)
            {
                e.printStackTrace();
                iUtils.ShowToast(Mcontext,WENT_WRONG);
            }
        }
    }
    private static class DownloadTikTokVideo extends AsyncTask<String, Void, Document> {
        Document doc;

        @Override
        protected Document doInBackground(String... urls) {
            try {
                Map<String, String> Headers = new HashMap<String, String>();
                Headers.put("Cookie","1");
                Headers.put("User-Agent","1");
                Headers.put("Accept","application/json");
                Headers.put("Host","api2-16-h2.musical.ly");
                Headers.put("Connection","keep-alive");
                doc = Jsoup.connect(TiktokApi).data("aweme_id",urls[0]).ignoreContentType(true).headers(Headers).get();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: Error");
                iUtils.ShowToast(Mcontext,WENT_WRONG);

            }
            return doc;

        }

        protected void onPostExecute(Document result) {
            if(!fromService) {

                pd.dismiss();
            }
            String URL = result.body().toString().replace("<body>","").replace("</body>","");
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(URL);
                String URLs = jsonObject.getJSONObject("aweme_detail").getJSONObject("video").getJSONObject("play_addr").getJSONArray("url_list").getString(0);

                new downloadFile().Downloading(Mcontext,URLs,Title,".mp4");
// iUtils.ShowToast(Mcontext,URLs);

            }catch (JSONException err){
                Log.d("Error", err.toString());
                iUtils.ShowToast(Mcontext,WENT_WRONG);
            }


        }
    }
}
