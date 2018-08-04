package com.example.marynahorshkalova.weatherapp;

import android.content.Context;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText cityNameEditText;
    TextView resultTextView;

    public void buttonClicked(View view) {

        // hide keyboard
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(cityNameEditText.getWindowToken(), 0);

        try {
            // in case cityName has two parts (spaces %20)
            String encodedCityName = URLEncoder.encode(cityNameEditText.getText().toString(), "UTF-8");

            TaskDownloader task = new TaskDownloader();

            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=9313a9a6c23abec4b3a9c725a4eb27d4");


        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT);

        } catch (Exception e) {

            resultTextView.setText("Could not find weather");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityNameEditText = findViewById(R.id.cityNameEditText);
        resultTextView = findViewById(R.id.resultTextView);

    }


    public class TaskDownloader extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection connection = null;


            try {

                url = new URL(urls[0]);

                connection = (HttpURLConnection) url.openConnection();

                InputStream in = connection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (Exception e) {

                e.printStackTrace();

                return "Failed!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                String message = "";

                // convert JSONObject to String
                JSONObject jsonObject = new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather Content", weatherInfo);

                JSONArray jsonArray = new JSONArray(weatherInfo);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonPart = jsonArray.getJSONObject(i);

                    String main;
                    String description;

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if (main != "" && description != "") {

                        message += main + ": " + description + "\r\n";
                    }

                    Log.i("Main", jsonPart.getString("main"));
                    Log.i("Description", jsonPart.getString("description"));
                }

                if (message != "") {

                    resultTextView.setText(message);

                } else {

                    resultTextView.setText("Could not find weather");
                }

            } catch (JSONException e) {

                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT);
            }
            catch (StringIndexOutOfBoundsException e) {

                resultTextView.setText("Could not find weather");
            }
        }
    }
}
