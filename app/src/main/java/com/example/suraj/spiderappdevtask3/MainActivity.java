package com.example.suraj.spiderappdevtask3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

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

public class MainActivity extends AppCompatActivity {

    EditText cityname;
    TextView city,main,description,temp,min,max,pressure,humidity;
    String JSON_STRING;
    public static String countryId, cityName;
    String pic = null;
    Bitmap bitmap;
    ImageView icon;
    LinearLayout weatherlayout;
    Button forecastbutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityname = (EditText) findViewById(R.id.citytext);
        city = (TextView) findViewById(R.id.city);
        main = (TextView) findViewById(R.id.main);
        description = (TextView) findViewById(R.id.description);
        temp = (TextView) findViewById(R.id.temp);
        min = (TextView) findViewById(R.id.min);
        max = (TextView) findViewById(R.id.max);
        pressure = (TextView) findViewById(R.id.pressure);
        humidity = (TextView) findViewById(R.id.humidity);
        icon = (ImageView) findViewById(R.id.icon);
        weatherlayout = (LinearLayout) findViewById(R.id.weatherlayout);
        forecastbutton = (Button) findViewById(R.id.forecast);


    }

    public void autocomplete (View v) {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                cityname.setText(place.getName());
                cityname.setSelection(cityname.getText().length());
                countryId = null;
                weatherlayout.setVisibility(View.INVISIBLE);
                new GetData().execute();

                //Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                //Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void search (View v) {
        countryId = null;
        weatherlayout.setVisibility(View.INVISIBLE);
        new GetData().execute();

    }

    public void forecast (View v) {
        if (countryId!=null) {
            Intent i = new Intent(this, Forecast.class);
            startActivity(i);
        }

    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }


    public class GetData extends AsyncTask<Object, Object, String> {

        String apikey= "bedbde363311b4653455b03af96f72ed";
        String json_url = "http://api.openweathermap.org/data/2.5/weather?q=";

        @Override
        protected void onPreExecute() {
            cityName = cityname.getText().toString().toLowerCase();
            json_url = json_url + cityName + "&appid=" + apikey;
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

                JSONObject parentObject = new JSONObject(s);
                countryId = parentObject.get("id").toString();
                JSONArray weather = parentObject.getJSONArray("weather");
                JSONObject weatherObject = weather.getJSONObject(0);
                pic = weatherObject.get("icon").toString();

                bitmap = getBitmapFromURL("http://openweathermap.org/img/w/" + pic + ".png");

                return s;
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            cityname.setText("");
            cityname.clearFocus();
            View view = getCurrentFocus();
            /*if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
           */
            if (s!=null) {
                icon.setImageBitmap(bitmap);
                try {
                    JSONObject parentObject = new JSONObject(s);
                    JSONArray weather = parentObject.getJSONArray("weather");
                    JSONObject weatherObject = weather.getJSONObject(0);
                    JSONObject mainObject = (JSONObject) parentObject.get("main");

                    city.setText(parentObject.get("name").toString());
                    main.setText(weatherObject.get("main").toString());
                    description.setText(weatherObject.get("description").toString());
                    temp.setText("Temperature: " + (int)(Float.parseFloat(mainObject.get("temp").toString()) - 273.15) + "°C");
                    min.setText("Minimum: " + (int)(Float.parseFloat(mainObject.get("temp_min").toString())-273.15)+ "°C");
                    max.setText("Maximum: " + (int)(Float.parseFloat(mainObject.get("temp_max").toString())-273.150)+ "°C");
                    pressure.setText("Pressure: " + mainObject.get("pressure").toString()+" hPa");
                    humidity.setText("Humidity: " + mainObject.get("humidity").toString()+"%");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                weatherlayout.setVisibility(View.VISIBLE);
                forecastbutton.setVisibility(View.VISIBLE);
            }

            else {
                Toast.makeText(getApplicationContext(), "City not available", Toast.LENGTH_LONG).show();
                forecastbutton.setVisibility(View.INVISIBLE);
            }
        }
    }
}
