package com.example.suraj.spiderappdevtask3;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Forecast extends AppCompatActivity {

    String JSON_STRING;
    TextView Temp1, Temp2, Temp3, Temp4, Temp5;
    TextView Min1, Min2, Min3, Min4, Min5;
    TextView Max1, Max2, Max3, Max4, Max5;
    RelativeLayout forecastlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        forecastlayout = (RelativeLayout) findViewById(R.id.forecastlayout);

        Temp1 = (TextView) findViewById(R.id.temp1);
        Temp2 = (TextView) findViewById(R.id.temp2);
        Temp3 = (TextView) findViewById(R.id.temp3);
        Temp4 = (TextView) findViewById(R.id.temp4);
        Temp5 = (TextView) findViewById(R.id.temp5);

        Min1 = (TextView) findViewById(R.id.min1);
        Min2 = (TextView) findViewById(R.id.min2);
        Min3 = (TextView) findViewById(R.id.min3);
        Min4 = (TextView) findViewById(R.id.min4);
        Min5 = (TextView) findViewById(R.id.min5);

        Max1 = (TextView) findViewById(R.id.max1);
        Max2 = (TextView) findViewById(R.id.max2);
        Max3 = (TextView) findViewById(R.id.max3);
        Max4 = (TextView) findViewById(R.id.max4);
        Max5 = (TextView) findViewById(R.id.max5);

        new GetForecast().execute();

    }

    public class GetForecast extends AsyncTask<Object, Object, String> {

        String apikey= "bedbde363311b4653455b03af96f72ed";
        String json_url = "http://api.openweathermap.org/data/2.5/forecast?q=";

        @Override
        protected void onPreExecute() {
            json_url = json_url + MainActivity.cityName + "," + MainActivity.countryId + "&appid=" + apikey;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                while ((JSON_STRING=bufferedReader.readLine())!=null)
                {
                    stringBuilder.append(JSON_STRING+"\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                String s = stringBuilder.toString().trim();

                return s;
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s!=null) {
                ArrayList <String> temps = new ArrayList<>(), mins = new ArrayList<>(), maxs = new ArrayList<>(), dts = new ArrayList<>();

                try {
                    JSONObject parentObject = new JSONObject(s);
                    JSONArray forecast = parentObject.getJSONArray("list");

                    for (int i=0,j=0; i<=39; j++)
                    {
                        JSONObject weatherObject = forecast.getJSONObject(i);
                        dts.add(j,weatherObject.get("dt_txt").toString());
                        JSONObject mainObject = weatherObject.getJSONObject("main");
                        String temp = (mainObject.get("temp").toString()); temps.add(j,temp);
                        mins.add(j,mainObject.get("temp_min").toString());
                        maxs.add(j,mainObject.get("temp_max").toString());
                        i = i+8;
                    }

                    Temp1.setText(dts.get(0).substring(0,10) + "\nTemperature: " + (int)(Float.parseFloat(temps.get(0))-273.15) + "°C");
                    Min1.setText("Min: " + (int)(Float.parseFloat(mins.get(0))-273.15)+ "°C");
                    Max1.setText("Max: " + (int)(Float.parseFloat(maxs.get(0))-273.15)+ "°C");

                    Temp2.setText(dts.get(1).substring(0,10) +"\nTemperature: " + (int)(Float.parseFloat(temps.get(1))-273.15) + "°C");
                    Min2.setText("Min: " + (int)(Float.parseFloat(mins.get(1))-273.15)+ "°C");
                    Max2.setText("Max: " + (int)(Float.parseFloat(maxs.get(1))-273.15)+ "°C");

                    Temp3.setText(dts.get(2).substring(0,10) +"\nTemperature: " + (int)(Float.parseFloat(temps.get(2))-273.15) + "°C");
                    Min3.setText("Min: " + (int)(Float.parseFloat(mins.get(2))-273.15)+ "°C");
                    Max3.setText("Max: " + (int)(Float.parseFloat(maxs.get(2))-273.15)+ "°C");

                    Temp4.setText(dts.get(3).substring(0,10) +"\nTemperature: " + (int)(Float.parseFloat(temps.get(3))-273.15) + "°C");
                    Min4.setText("Min: " + (int)(Float.parseFloat(mins.get(3))-273.15)+ "°C");
                    Max4.setText("Max: " + (int)(Float.parseFloat(maxs.get(3))-273.15)+ "°C");

                    Temp5.setText(dts.get(4).substring(0,10) +"\nTemperature: " + (int)(Float.parseFloat(temps.get(4))-273.15) + "°C");
                    Min5.setText("Min: " + (int)(Float.parseFloat(mins.get(4))-273.15)+ "°C");
                    Max5.setText("Max: " + (int)(Float.parseFloat(maxs.get(4))-273.15)+ "°C");

                    forecastlayout.setVisibility(View.VISIBLE);




                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            else
                Toast.makeText(getApplicationContext(),"City not available",Toast.LENGTH_LONG).show();
        }
    }
}
