package com.edgar.yurihouse.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;

import com.edgar.yurihouse.Items.MangaItem;
import com.edgar.yurihouse.Utils.JsonGetter;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainController {

    public MainController() {}

    public static final String[] storeStringKeys = {
            "curStatusString", "curRegionString", "curOrderString"
    };

    public static final String[] storeIntegerKeys = {
            "curStatusCode", "curRegionCode", "curOrderCode"
    };

    //curFlag[12, 0, 0, 0, 0, 0] (position 0: yuri class; 1: unknown; 2: status; 3: region; 4: sortType; 5: pageNum)
//    public static final String DEFAULT_JSON_URL = "https://m.dmzj.com/classify/12-0-0-0-0-0.json";

    private static final int[] REGIONS_CODE = {0, 1, 2, 3, 4, 5, 6};
    private static final int[] STATUS_CODE = {0, 1, 2};
    private static final int[] ORDER_CODE = {0, 0, 1};

    public String getSortUrl(int statusCode, int regionCode, int orderCode, int pageNum) {
        String jsonUrl = "https://m.dmzj.com/classify/";
        jsonUrl = jsonUrl + String.valueOf(12) + "-0-" + String.valueOf(STATUS_CODE[statusCode]) +
                "-" + REGIONS_CODE[regionCode] + "-" + String.valueOf(ORDER_CODE[orderCode]) +
                "-" + String.valueOf(pageNum) + ".json";
        return jsonUrl;
    }

    public void storeFilterNames(Context context, String[] datas, String[] keys) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FILTER_SETTING",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < datas.length; i++) {
            editor.putString(keys[i], datas[i]);
        }
        editor.apply();
    }

    public void storeFilterIndices(Context context, int[] datas, String[] keys) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FILTER_SETTING",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < datas.length; i++) {
            editor.putInt(keys[i], datas[i]);
        }
        editor.apply();
    }

    public String getStoredNames(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FILTER_SETTING",
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public int getStoredIndices(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FILTER_SETTING",
                Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public void clearSharedPreference(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FILTER_SETTING",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Toast.makeText(context, "Clear Data!!", Toast.LENGTH_SHORT).show();
        editor.clear();
        editor.apply();
    }

    public void refreshData(String urlString, Handler handler) {

        JsonGetter jsonGetter = new JsonGetter(urlString, handler);
        jsonGetter.start();

    }

    public void loadMoreData(String urlString, Handler handler) {

        JsonGetter jsonGetter = new JsonGetter(urlString, handler);
        jsonGetter.start();

    }

    public ArrayList<MangaItem> getDataFromJson(String jsonString) {

        Type listType = new TypeToken<ArrayList<MangaItem>>() {}.getType();
        ArrayList<MangaItem> items = new GsonBuilder().create().fromJson(jsonString, listType);

        if (items == null || items.size() == 0) {
            return null;
        }
        return items;

    }

}
