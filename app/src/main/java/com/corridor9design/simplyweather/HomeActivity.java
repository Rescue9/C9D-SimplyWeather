package com.corridor9design.simplyweather;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import static com.corridor9design.simplyweather.location.CityFinder.getCityNameUsingNetwork;
import static com.corridor9design.simplyweather.location.CityFinder.setLongitudeLatitude;
import static com.corridor9design.simplyweather.network.InternetConnectivity.isInternetConnected;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.corridor9design.simplyweather.adapter.DaysAdapter;
import com.corridor9design.simplyweather.databinding.ActivityHomeBinding;
import com.corridor9design.simplyweather.location.LocationCord;
import com.corridor9design.simplyweather.preferences.AppPreferences;
import com.corridor9design.simplyweather.preferences.SettingsDialogFragment;
import com.corridor9design.simplyweather.update.UpdateUI;
import com.corridor9design.simplyweather.network.URL;
import com.corridor9design.simplyweather.utils.FadeAnimations;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private final int WEATHER_FORECAST_APP_UPDATE_REQ_CODE = 101;   // for app update
    private static final int PERMISSION_CODE = 1;                   // for user location permission
    private String name, updated_at, description, temperature, min_temperature, max_temperature, pressure, wind_speed, humidity;
    private int condition;
    private long update_time, sunset, sunrise;
    private String city = "";
    private final int REQUEST_CODE_EXTRA_INPUT = 101;
    private ActivityHomeBinding binding;
    private Button openSettingsButton;
    private AppPreferences prefs;
    private Boolean logging;
    private Boolean imperial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // binding
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        view.setVisibility(View.GONE);
        setContentView(view);
        FadeAnimations.fadeIn(view, 1000);
        
        prefs = new AppPreferences(this);

        // set navigation bar color
        setNavigationBarColor();

        //FIXME: will reimplement after functional code changes
        //check for new app update
        //checkUpdate();

        // set refresh color schemes
        setRefreshLayoutColor();

        // when user do search and refresh
        listeners();

        // load cached weather data so interface not empty on start
        loadCachedWeatherData();

        // getting data using internet connection
        //getDataUsingNetwork();
        logging = prefs.getBooleanPreference("log_info", false);
        Log.i("LogEnabled", Boolean.toString(logging));
        if (logging){
            //Log.i("BuildCfgAPIKey", BuildConfig.MY_API_KEY);
            Log.i("PrefsAPIKey", prefs.getStringPreference("api_key", "no prefs api key"));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void setNavigationBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.navBarColor));
        }
    }

    private void setUpDaysRecyclerView() {
        DaysAdapter daysAdapter = new DaysAdapter(this);
        binding.dayRv.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        binding.dayRv.setAdapter(daysAdapter);
        }

    @SuppressLint("ClickableViewAccessibility")
    private void listeners() {
        binding.layout.mainLayout.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard(view);
            return false;
        });
        binding.mainRefreshLayout.setOnRefreshListener(() -> {
            checkConnection();
            if (logging) Log.i("refresh", "Refresh Done.");
            binding.mainRefreshLayout.setRefreshing(false);  //for the next time
        });
        // Settings dialog launcher
        binding.btnOpenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySettings();
            }
        });
    }

    private void setRefreshLayoutColor() {
        binding.mainRefreshLayout.setProgressBackgroundColorSchemeColor(
                getResources().getColor(R.color.textColor)
        );
        binding.mainRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.navBarColor)
        );
    }
        
    private void getDataUsingNetwork() {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        //check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        } else {
            client.getLastLocation().addOnSuccessListener(location -> {
                setLongitudeLatitude(location);
                city = getCityNameUsingNetwork(this, location);
                getTodayWeatherInfo(city);
            });
        }
    }
    
    @SuppressLint("DefaultLocale")
    private void getTodayWeatherInfo(String name) {
        URL url = new URL(this, prefs.getStringPreference("api_key", "no prefs api key"), prefs.getBooleanPreference("imperial_units", false));
        Log.i("URL", url.getLink());
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url.getLink(), null, response -> {
            try {
                this.name = name;
                update_time = response.getJSONObject("current").getLong("dt");
                updated_at = new SimpleDateFormat("EEEE hh:mm a", Locale.ENGLISH).format(new Date(update_time * 1000));

                condition = response.getJSONArray("daily").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getInt("id");
                sunrise = response.getJSONArray("daily").getJSONObject(0).getLong("sunrise");
                sunset = response.getJSONArray("daily").getJSONObject(0).getLong("sunset");
                description = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("main");

                temperature = String.valueOf(Math.round(response.getJSONObject("current").getDouble("temp")));
                min_temperature = String.format("%.0f", response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getDouble("min"));
                max_temperature = String.format("%.0f", response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getDouble("max"));
                pressure = response.getJSONArray("daily").getJSONObject(0).getString("pressure");
                wind_speed = response.getJSONArray("daily").getJSONObject(0).getString("wind_speed");
                humidity = response.getJSONArray("daily").getJSONObject(0).getString("humidity");

                updateUI();
                saveWeatherDataLocally();
                hideProgressBar();
                setUpDaysRecyclerView();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, null);
        requestQueue.add(jsonObjectRequest);
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        String unitsTemp, unitsSpeed;

        if(prefs.getBooleanPreference("imperial_units", false)){
            unitsTemp = "°F";
            unitsSpeed = "mph";
        } else {
            unitsTemp = "°C";
            unitsSpeed = "kph";
        }

        binding.layout.nameTv.setText(name);
        updated_at = translate(updated_at);
        binding.layout.updatedAtTv.setText(updated_at);
        binding.layout.conditionIv.setImageResource(
                getResources().getIdentifier(
                        UpdateUI.getIconID(condition, update_time, sunrise, sunset),
                        "drawable",
                        getPackageName()
                ));
        binding.layout.conditionDescTv.setText(description);
        binding.layout.tempTv.setText(temperature + unitsTemp);
        binding.layout.minTempTv.setText(min_temperature + unitsTemp);
        binding.layout.maxTempTv.setText(max_temperature + unitsTemp);
        binding.layout.pressureTv.setText(pressure + " mb");
        binding.layout.windTv.setText(wind_speed + " " + unitsSpeed);
        binding.layout.humidityTv.setText(humidity + "%");
    }

    private String translate(String dayToTranslate) {
        String[] dayToTranslateSplit = dayToTranslate.split(" ");
        dayToTranslateSplit[0] = UpdateUI.TranslateDay(dayToTranslateSplit[0].trim(), getApplicationContext());
        return dayToTranslateSplit[0].concat(" " + dayToTranslateSplit[1]);
    }

    private void hideProgressBar() {
        binding.progress.setVisibility(View.GONE);
        binding.layout.mainLayout.setVisibility(View.VISIBLE);
    }

    private void hideMainLayout() {
        binding.progress.setVisibility(View.VISIBLE);
        binding.layout.mainLayout.setVisibility(View.GONE);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void checkConnection() {
        if (!isInternetConnected(this)) {
            hideMainLayout();
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        } else {
            hideProgressBar();
            getDataUsingNetwork();
        }
    }
    
    private void displaySettings() {
        SettingsDialogFragment dialog = new SettingsDialogFragment();
        dialog.show(getSupportFragmentManager(), "SettingsDialog");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                displaySettings();
                //getDataUsingNetwork();
            } else {
                Toast.makeText(this, "Permission Needed!", Toast.LENGTH_SHORT).show();
                finishActivity(0);
            }
        }
  
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    private void saveWeatherDataLocally() {
        getSharedPreferences("weatherData", MODE_PRIVATE).edit()
            .putString("name", name)
            .putString("updated_at", updated_at)
            .putString("description", description)
            .putString("temperature", temperature)
            .putString("min_temperature", min_temperature)
            .putString("max_temperature", max_temperature)
            .putString("pressure", pressure)
            .putString("wind_speed", wind_speed)
            .putString("humidity", humidity)
            .putInt("condition", condition)
            .putLong("update_time", update_time)
            .putLong("sunrise", sunrise)
            .putLong("sunset", sunset)
            .apply();
    }

    private void loadCachedWeatherData() {
        SharedPreferences prefs = getSharedPreferences("weatherData", MODE_PRIVATE);
        if (prefs.contains("name")) {
            name = prefs.getString("name", "");
            updated_at = prefs.getString("updated_at", "");
            description = prefs.getString("description", "");
            temperature = prefs.getString("temperature", "");
            min_temperature = prefs.getString("min_temperature", "");
            max_temperature = prefs.getString("max_temperature", "");
            pressure = prefs.getString("pressure", "");
            wind_speed = prefs.getString("wind_speed", "");
            humidity = prefs.getString("humidity", "");
            condition = prefs.getInt("condition", 0);
            update_time = prefs.getLong("update_time", 0);
            sunrise = prefs.getLong("sunrise", 0);
            sunset = prefs.getLong("sunset", 0);
    
            updateUI();  // Show cached data immediately
            hideProgressBar();
        }
    }

    // FIXME: will implement when code changes are functional
    /*private void checkUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(HomeActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, HomeActivity.this, WEATHER_FORECAST_APP_UPDATE_REQ_CODE);
                } catch (IntentSender.SendIntentException exception) {
                    Toast.makeText(this, "Update failed");
                }
            }
        });
    }*/

}