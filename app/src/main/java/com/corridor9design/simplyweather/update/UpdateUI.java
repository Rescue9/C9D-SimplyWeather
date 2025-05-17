package com.corridor9design.simplyweather.update;

import android.content.Context;

import com.corridor9design.simplyweather.R;

public class UpdateUI {

    public static String getIconID(int condition, long update_time, long sunrise, long sunset) {
        if(condition == 200) return "thunderstorm with light rain";
        else if(condition == 201) return "thunderstorm with rain";
        else if(condition == 202) return "thunderstorm with heavy rain";
        else if(condition == 210) return "light thunderstorm";
        else if(condition == 211) return "thunderstorm";
        else if(condition == 212) return "heavy thunderstorm";
        else if(condition == 221) return "ragged thunderstorm";
        else if(condition == 230) return "thunderstorm with light drizzle";
        else if(condition == 231) return "thunderstorm with drizzle";
        else if(condition == 232) return "thunderstorm with heavy drizzle";
        else if(condition == 300) return "light intensity drizzle";
        else if(condition == 301) return "drizzle";
        else if(condition == 302) return "heavy intensity drizzle";
        else if(condition == 310) return "light intensity drizzle rain";
        else if(condition == 311) return "drizzle rain";
        else if(condition == 312) return "heavy intensity drizzle rain";
        else if(condition == 313) return "shower rain and drizzle";
        else if(condition == 314) return "heavy shower rain and drizzle";
        else if(condition == 321) return "shower drizzle";
        else if(condition == 500) return "light rain";
        else if(condition == 501) return "moderate rain";
        else if(condition == 502) return "heavy intensity rain";
        else if(condition == 503) return "very heavy rain";
        else if(condition == 504) return "extreme rain";
        else if(condition == 511) return "freezing rain";
        else if(condition == 520) return "light intensity shower rain";
        else if(condition == 521) return "shower rain";
        else if(condition == 522) return "heavy intensity shower rain";
        else if(condition == 531) return "ragged shower rain";
        else if(condition == 600) return "light snow";
        else if(condition == 601) return "snow";
        else if(condition == 602) return "heavy snow";
        else if(condition == 611) return "sleet";
        else if(condition == 612) return "light shower sleet";
        else if(condition == 613) return "shower sleet";
        else if(condition == 615) return "light rain and snow";
        else if(condition == 616) return "rain and snow";
        else if(condition == 620) return "light shower snow";
        else if(condition == 621) return "shower snow";
        else if(condition == 622) return "heavy shower snow";
        else if(condition == 701) return "mist";
        else if(condition == 711) return "smoke";
        else if(condition == 721) return "haze";
        else if(condition == 731) return "sand/dust whirls";
        else if(condition == 741) return "fog";
        else if(condition == 751) return "sand";
        else if(condition == 761) return "dust";
        else if(condition == 762) return "volcanic ash";
        else if(condition == 771) return "squalls";
        else if(condition == 781) return "tornado";
        else if(condition == 801) return "few clouds: 11-25%";
        else if(condition == 802) return "scattered clouds: 25-50%";
        else if(condition == 803) return "broken clouds: 51-84%";
        else if(condition == 804) return "overcast clouds: 85-100%";
        else if (condition == 800) {
            if (update_time >= sunrise && update_time <= sunset)
                return "clear_day";
            else
                return "clear_night";
        } else if (condition == 801) {
            if (update_time >= sunrise && update_time <= sunset)
                return "few_clouds_day";
            else
                return "few_clouds_night";
        } else if (condition == 802)
            return "scattered_clouds";
        else if (condition == 803 || condition == 804)
            return "broken_clouds";
        return null;
    }

    /** Builds the full OpenWeatherMap icon URL, e.g. "10d" → ".../10d@2x.png". */
    public static String getIconById(String id) {
        return (id == null || id.isEmpty())
                ? "" // or throw IllegalArgumentException
                : "https://openweathermap.org/img/wn/" + id + "@2x.png";
    }

    public static String TranslateDay(String dayToBeTranslated, Context context) {
        switch (dayToBeTranslated.trim()) {
            case "Monday":
                return context.getResources().getString(R.string.monday);
            case "Tuesday":
                return context.getResources().getString(R.string.tuesday);
            case "Wednesday":
                return context.getResources().getString(R.string.wednesday);
            case "Thursday":
                return context.getResources().getString(R.string.thursday);
            case "Friday":
                return context.getResources().getString(R.string.friday);
            case "Saturday":
                return context.getResources().getString(R.string.saturday);
            case "Sunday":
                return context.getResources().getString(R.string.sunday);
        }
        return dayToBeTranslated;
    }
}
