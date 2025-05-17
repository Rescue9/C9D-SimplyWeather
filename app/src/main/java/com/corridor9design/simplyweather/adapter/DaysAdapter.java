package com.corridor9design.simplyweather.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.corridor9design.simplyweather.R;
import com.corridor9design.simplyweather.preferences.AppPreferences;
import com.corridor9design.simplyweather.update.UpdateUI;
import com.corridor9design.simplyweather.network.URL;
import com.corridor9design.simplyweather.utils.FadeAnimations;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.prefs.Preferences;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.DayViewHolder> {
    private final Context context;
    private AppPreferences prefs;

    public DaysAdapter(Context context) {
        this.context = context;
        prefs = new AppPreferences(this.context);
    }

    private String updated_at, min, max, pressure, wind_speed, humidity;
    private int condition;
    private long update_time, sunset, sunrise;

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.day_item_layout, parent, false);
        view.setVisibility(View.GONE);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        getDailyWeatherInfo(position + 1, holder);
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    @SuppressLint("DefaultLocale")
    private void getDailyWeatherInfo(int i, DayViewHolder holder) {
        URL url = new URL(context, prefs.getStringPreference("api_key", "no prefs api key"), prefs.getBooleanPreference("imperial_units", false));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url.getLink(), null, response -> {
            try {
                update_time = response.getJSONObject("current").getLong("dt");
                updated_at = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date((update_time * 1000) + (i * 864_000_00L)));   // i=0

                condition = response.getJSONArray("daily").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getInt("id");
                sunrise = response.getJSONArray("daily").getJSONObject(i).getLong("sunrise");
                sunset = response.getJSONArray("daily").getJSONObject(i).getLong("sunset");

                min = String.format("%.0f", response.getJSONArray("daily").getJSONObject(i).getJSONObject("temp").getDouble("min"));
                max = String.format("%.0f", response.getJSONArray("daily").getJSONObject(i).getJSONObject("temp").getDouble("max"));
                pressure = response.getJSONArray("daily").getJSONObject(i).getString("pressure");
                wind_speed = response.getJSONArray("daily").getJSONObject(i).getString("wind_speed");
                humidity = response.getJSONArray("daily").getJSONObject(i).getString("humidity");

                updateUI(holder);
                hideProgressBar(holder);
                FadeAnimations.fadeIn(holder.itemView, 750);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, null);
        requestQueue.add(jsonObjectRequest);
        Log.i("json_req", "Day " + i);
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(DayViewHolder holder) {
        String unitsTemp, unitsSpeed;

        if(prefs.getBooleanPreference("imperial_units", false)){
            unitsTemp = "°F";
            unitsSpeed = "mph";
        } else {
            unitsTemp = "°C";
            unitsSpeed = "kph";
        }
        String day = UpdateUI.TranslateDay(updated_at, context);
        holder.dTime.setText(day);
        holder.temp_min.setText(min + unitsTemp);
        holder.temp_max.setText(max + unitsTemp);
        holder.pressure.setText(pressure + " mb");
        holder.wind.setText(wind_speed + " " + unitsSpeed);
        holder.humidity.setText(humidity + "%");
        holder.icon.setImageResource(
                context.getResources().getIdentifier(
                        UpdateUI.getIconID(condition, update_time, sunrise, sunset),
                        "drawable",
                        context.getPackageName()
                ));
    }

    private void hideProgressBar(DayViewHolder holder) {
        holder.progress.setVisibility(View.GONE);
        holder.layout.setVisibility(View.VISIBLE);
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        SpinKitView progress;
        RelativeLayout layout;
        TextView dTime, temp_min, temp_max, pressure, wind, humidity;
        ImageView icon;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            progress = itemView.findViewById(R.id.day_progress_bar);
            layout = itemView.findViewById(R.id.day_relative_layout);
            dTime = itemView.findViewById(R.id.day_time);
            temp_min = itemView.findViewById(R.id.day_min_temp);
            temp_max = itemView.findViewById(R.id.day_max_temp);
            pressure = itemView.findViewById(R.id.day_pressure);
            wind = itemView.findViewById(R.id.day_wind);
            humidity = itemView.findViewById(R.id.day_humidity);
            icon = itemView.findViewById(R.id.day_icon);
        }
    }
}
