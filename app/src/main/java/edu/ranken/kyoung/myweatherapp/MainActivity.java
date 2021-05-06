package edu.ranken.kyoung.myweatherapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    // Declare program constants
    final String CN = "City Name ";
    final String CBE = "Cannot Be Empty";
    final String WNF = "Was Not Found By API";
    final String UPF = "Unexpected Program Failure";

    // Declare instance variables
    EditText etCity;
    TextView tvResults;
    Button   btnGetTemperature;
    Button   btnClear;

    // Create APICall interface object
    APICall apiCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get references to widgets
        etCity = findViewById(R.id.etCity);
        tvResults = findViewById(R.id.tvResults);
        btnGetTemperature = findViewById(R.id.btnGetTemperature);
        btnClear = findViewById(R.id.btnClear);

        // Set focus to city editText when program begins
        etCity.requestFocus();

        // Create Retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(ServerInformation.BASE_URL)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

        // Associate Retrofit Object with APICall
        apiCall = retrofit.create(APICall.class);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCity.setText("");
                tvResults.setText("");
                etCity.requestFocus();
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void showSoftKeyboard(View view){
                if(view.requestFocus()){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
                }
            }

            public void hideSoftKeyboard(View view){
                InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });



        btnGetTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            // Code that executes when the "Get Temp" button is clicked
            public void onClick(View v) {
                // Get weather information. The city is from
                // whatever the user entered into etCity and
                // the key is from openweathermap.org

                try{
                    if (etCity.getText().toString().trim().equals(""))
                    {
                        throw new IllegalArgumentException();
                    }

                    Call<WeatherInformation> call = apiCall.getWeatherData(
                            etCity.getText().toString(),
                            ServerInformation.API_KEY);

                    // Callback function
                    call.enqueue(new Callback<WeatherInformation>() {
                        @Override
                        public void onResponse(Call<WeatherInformation> call, Response<WeatherInformation> response) {
                            // If city not found in API database, a 404 error should
                            // be returned. Show an associated toast and return.
                            if (response.code() == 404) {
                                Toast.makeText(getApplicationContext(), CN + WNF, Toast.LENGTH_LONG).show();
                                etCity.setText("");
                                etCity.requestFocus();
                                return;
                            }

                            // A valid city was entered
                            WeatherInformation weatherInformation = response.body();
                            double theTemperature = weatherInformation.getMainData().getTemperature();

                            String theText = "The Temperature in\n";
                            // Convert temp from Kelvin to Fahrenheit
                            theTemperature = (theTemperature - 273.15) * 1.8 + 32;
                            theText += etCity.getText().toString() +
                                    " is " + Math.round(theTemperature) + "\u00B0";
                            tvResults.setText((theText));
                        }

                        @Override
                        public void onFailure(Call<WeatherInformation> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), UPF, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                catch(IllegalArgumentException iae){
                    Toast.makeText(getApplicationContext(), CN + CBE, Toast.LENGTH_LONG).show();
                    return;
                }


            }
        });
    }
}