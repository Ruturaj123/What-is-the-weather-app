package com.example.ruturaj.whatistheweather;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return  result;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Check the city name!", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

                try {
                    String message = "";
                    JSONObject jsonObject = new JSONObject(result);
                    String weatherInfo = jsonObject.getString("weather");
                    String mainInfo = jsonObject.getString("main");
                    Log.i("Weather", weatherInfo);
                    Log.i("Main", mainInfo);
                    JSONArray arr1 = new JSONArray(weatherInfo);

                    for (int j = 0; j < arr1.length(); j++) {
                        String main = "";
                        String description = "";
                        JSONObject jsonPart = arr1.getJSONObject(j);
                        main = jsonPart.getString("main");
                        description = jsonPart.getString("description");

                        if (main != "" && description != "") {
                            message += main + ": " + description + "\r\n";
                        }
                    }

                    double temp;
                    int pressure;
                    JSONObject jsonObject1 = jsonObject.getJSONObject("main");
                    temp = jsonObject1.getDouble("temp");
                    pressure = jsonObject1.getInt("pressure");
                    temp = round(temp - 273.15);

                    if (temp != 0.0 && pressure != 0) {
                        message += "Temperature: " + Double.toString(temp) + "°C" + "\r\n" + "Pressure: " + Double.toString(pressure) + "mm" + "\r\n";
                    }

                    if (message != "") {
                        resultTextView.setText(message);
                        resultTextView.setTextColor(Color.YELLOW);
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter a city!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
                }
        }
    }


    public void findWeather(View view){
        Log.i("City", cityName.getText().toString());

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=f28e62b65a3fa9ba3a30b4a713e273a1");
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
        }
    }
}
