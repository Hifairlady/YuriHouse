package com.edgar.yurihouse.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.edgar.yurihouse.Items.SearchResultItem;
import com.edgar.yurihouse.Utils.HtmlParser;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SearchController {

    private ArrayList<SearchResultItem> searchResultItems = null;

    public SearchController() {}

    private String quoteText, backgroundUrl;

    private HtmlParser.DataFromDocument quoteData = new HtmlParser.DataFromDocument() {
        @Override
        public void setData(Document document) {
            quoteText = document.selectFirst("#LC1").text();
            backgroundUrl = document.selectFirst("#LC2").text();
        }
    };

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public String getQuoteText(Context context) {
        storeQuoteText(context, quoteText);
        return quoteText;
    }

    public void setupSearchUI(String urlString, Handler handler) {

        HtmlParser htmlParser = new HtmlParser(urlString, handler);
        htmlParser.setDataFromDocument(quoteData);
        htmlParser.start();

    }

    private HtmlParser.DataFromDocument searchData = new HtmlParser.DataFromDocument() {
        @Override
        public void setData(Document document) {
            searchResultItems = null;
            Elements scriptElements = document.select("script");
            for (Element scriptElement : scriptElements) {
                String scriptString = scriptElement.html();
                if (scriptString.contains("var serchArry=")) {
                    String[] childStrings = scriptString.split("\n");
                    for (int i = 0; i < childStrings.length; i++) {
                        if (childStrings[i].contains("var serchArry=")) {
                            try {
                                int startPos = childStrings[i].indexOf("[{");
                                int endPos = childStrings[i].indexOf("}]") + 2;
                                String jsonString = childStrings[i].substring(startPos, endPos);
                                Type listType = new TypeToken<ArrayList<SearchResultItem>>() {}.getType();
                                searchResultItems = new GsonBuilder().create().fromJson(jsonString, listType);
                            } catch (StringIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    };

    public ArrayList<SearchResultItem> getSearchResultItems() {
        if (searchResultItems == null) { searchResultItems = new ArrayList<>(); }
        return searchResultItems;
    }

    public void setupSearchData(String urlString, Handler handler) {
        HtmlParser htmlParser = new HtmlParser(urlString, handler);
        htmlParser.setDataFromDocument(searchData);
        htmlParser.start();
    }

    public void storeQuoteText(Context context, String quoteKeyString) {
        SharedPreferences quoteLocalStorage = context.getSharedPreferences("quoteText", MODE_PRIVATE);
        SharedPreferences.Editor editor = quoteLocalStorage.edit();
        editor.putString("QUOTE DEFAULT", quoteText);
        editor.putString(quoteKeyString, quoteText);
        editor.apply();
    }

    public String getQuoteByKey(Context context, String quoteKeyString) {
        SharedPreferences quoteLocalStorage = context.getSharedPreferences("quoteText", MODE_PRIVATE);
        return quoteLocalStorage.getString(quoteKeyString, null);
    }
}
