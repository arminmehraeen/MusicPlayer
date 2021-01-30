package com.armin.mehraein.musicplayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadTask extends AsyncTask<String, Integer, String> {

    Context context ;
    public static ProgressDialog mprogress ;

    public DownloadTask(Context cont) {
        this.context = cont;
        mprogress = new ProgressDialog(context);
        mprogress.setMessage("لطفا صبر کنید");
        mprogress.setIndeterminate(true);
        mprogress.setCancelable(true);
        mprogress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mprogress.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mprogress.setIndeterminate(false);
        mprogress.setMax(100);
        mprogress.setProgress(values[0]);
    }

    @Override
    protected String doInBackground(String... strings) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            int filelen = connection.getContentLength();
            input = connection.getInputStream();
            filechache();
            output = new FileOutputStream(android.os.Environment.getExternalStorageDirectory() + "/MyDownloadMusic/"
                    + strings[1] + ".mp3");
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                if (filelen > 0) publishProgress((int) (total * 100 / filelen));
                output.write(data, 0, count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) output.close();
                if (input != null) input.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) connection.disconnect();
        }
        return null;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mprogress.dismiss();
        if (s != null) {
            Toast.makeText(context, "خطای حین دانلود", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "دانلود با موفقیت به اتمام رسید", Toast.LENGTH_LONG).show();
        }
    }

    private void filechache() {
        if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File chachefile = new File(android.os.Environment.getExternalStorageDirectory(), "MyDownloadMusic");

            if (!chachefile.exists()) {
                chachefile.mkdirs();
            }


        }


    }
}


