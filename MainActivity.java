package com.example.juiceboxbot.y3840030mobileandroid;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager s_mgr;
    private Sensor s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        s_mgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        s = s_mgr.getDefaultSensor(Sensor.TYPE_LIGHT);
        s_mgr.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
    }


    public void getNews(View v) {
        //new getNewsFacts().execute("820dc531-f4bc-4ae6-8a66-e1e8013b47d0");

        //URL url = new URL("http://eventregistry.org/api/v1/event/getEvents/");
        //HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//https://newsapi.org/v2/top-headlines?country=us&apiKey=034c261d12784990b41d4ef4f235ed1d
        new downloadNews().execute("https://newsapi.org/v2/top-headlines" + "country=us&" + "apiKey=034c261d12784990b41d4ef4f235ed1d");

        //sourceLocationUri  array[string]
    //includeEventLocation  boolean
        // includeStoryLocation boolean

        //Conncct api from web to local.
        //820dc531-f4bc-4ae6-8a66-e1e8013b47d0
        //
    }

    public void setNews(String new_news) {
        TextView tv = (TextView) findViewById(R.id.textView2);
        tv.setText(new_news);
    }

    public void onAccuracyChanged(Sensor sensor, int value) {

    }

    public void onSensorChanged(SensorEvent event) {
        ((TextView)findViewById(R.id.textView)).setText("SENSOR DATE:\n" + String.valueOf(event.values[0]));

        if(event.values[0] >= s_mgr.LIGHT_FULLMOON) {
            //set Image to show up
            findViewById(R.id.imageView).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.imageView).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        s_mgr.unregisterListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class downloadNews extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            URL url;

            try {
                url = new URL(urls[0]);
            }
            catch (MalformedURLException e) {
                return "";
            }

            StringBuilder sb = new StringBuilder();

            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = bf.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                bf.close();
                connection.getInputStream().close();

                return(sb.toString());
            } catch (IOException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.length()==0) {
                setNews("ERROR :(");
                return;
            }

            String news;
            try {
                JSONObject json = new JSONObject(result);
                news = json.getString("news");
            }
            catch(JSONException e) {
                news = e.getLocalizedMessage();
            }

            setNews(news);
        }
    }
}
