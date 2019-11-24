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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.juiceboxbot.y3840030mobileandroid.Function;


/**
 * Created by juiceboxbot on 15/11/2019.
 */

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager s_mgr;
    private Sensor s;
    public String NEWS_SOURCE;
    public String API_KEY = "820dc531-f4bc-4ae6-8a66-e1e8013b47d0";
    public  Function func;

    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
    static final String KEY_AUTHOR = "author";
    static final String KEY_TITLE = "title";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_URL = "url";
    static final String KEY_URLTOIMAGE = "urlToImage";
    static final String KEY_PUBLISHEDAT = "publishedAt";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        s_mgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        s = s_mgr.getDefaultSensor(Sensor.TYPE_LIGHT);
        s_mgr.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);


        if (func.isNetworkAvailable(getApplicationContext())) {
            downloadNews newsTask = new downloadNews();
            newsTask.execute();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }


    public String doInBackground(View v) {
        //new getNewsFacts().execute("820dc531-f4bc-4ae6-8a66-e1e8013b47d0");

        //URL url = new URL("http://eventregistry.org/api/v1/event/getEvents/");
        //HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//https://newsapi.org/v2/top-headlines?country=us&apiKey=034c261d12784990b41d4ef4f235ed1d
        new downloadNews().execute("https://newsapi.org/v2/top-headlines?" +
                                    "country=uk&" +
                                    "apiKey=034c261d12784990b41d4ef4f235ed1d");

        String xml = func.excuteGet("https://newsapi.org/v2/articles?source=" + NEWS_SOURCE + "&sourtBy=top&apiKey=" + API_KEY);
        return xml;

        //new downloadNews().execute("https://newsapi.org/v2/top-headlines?country=us&apiKey=034c261d12784990b41d4ef4f235ed1d");
        //sourceLocationUri  array[string]

        //Conncct api from web to local.
        //
        //
    }

    public void setNews(String new_news) {
        TextView tv = (TextView) findViewById(R.id.textView2);
        tv.setText(new_news);

/*   ListNewsAdapter adapter = new ListNewsAdapter(MainActivity.this, dataList);
        listNews.setAdapter(adapter);

        listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(MainActivity.this, DetailsActivity.class);
                i.putExtra("url", dataList.get(+position).get(KEY_URL));
                startActivity(i);
*/
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
            if(result.length()== 0) { //Checking if the string comes back empty or not
                setNews("ERROR :(");
                return;
            }

            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray jsonArray = jsonResponse.optJSONArray("articles");
            }
            catch(JSONException e) {
                Toast.makeText(getApplicationContext(), "Unexpected error", Toast.LENGTH_SHORT).show();
            }

            //setNews(news);
        }
    }
}
