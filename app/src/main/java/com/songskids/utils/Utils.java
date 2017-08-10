package com.songskids.utils;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.songskids.database.Database;
import com.songskids.models.Songs;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static void getData(final Database dbManager, final OnDatabaseListenner listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (dbManager != null) {
                        List<Songs> datas = dbManager.getSongs();
                        if (listener != null) {
                            listener.onResult(datas);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    public static void convertUrlYoutube(final String url, final OnConvertListener listener) {
        final ArrayList<String> urls = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;
                String responseString = null;
                try {
                    response = httpclient.execute(new HttpGet(url));
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        responseString = out.toString();
                        out.close();
                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (IOException e) {
                    //TODO Handle problems..
                }
                Document doc = Jsoup.parse(responseString);
                Element div = doc.select("div[id=dl").first();
                if (div != null) {
                    urls.clear();
                    for (int i = 0; i < div.childNodeSize(); i++) {
                        Element link = div.getElementsByIndexEquals(i).select("a[class=l]").first();
                        if (link != null && link.text().equals("» Download MP4 «") && urls.size() < 1) {
                            String url = link.attr("href");
                            urls.add(url);
                        }
                    }
                    if (urls.size() != 0) {
                        String url = urls.get(0);
                        if (listener != null) {
                            listener.onResultConvert(url);
                        }
                    }
                }
            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    public static void setupUI(View view, final Activity activity) {
        if (!(view instanceof EditText) || view.isFocusable() == false) {

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    // TODO Auto-generated method stub
                    try {
                        hideSoftKeyboard(activity);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    return false;
                }


            });
        }
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView, activity);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void showKeyboard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (v.requestFocus()) {
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public interface OnDatabaseListenner {
        void onResult(List<Songs> result);
    }

    public interface OnConvertListener {
        void onResultConvert(String urlVideo);
    }
}
