package com.edgar.yurihouse.Controllers;

import android.os.Handler;

import com.edgar.yurihouse.Utils.HtmlParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReaderController {

    private String jsonString;

    public ReaderController() {}

    public void setupImageUrl(String urlString, Handler handler) {

        HtmlParser htmlParser = new HtmlParser(urlString, handler);
        htmlParser.setDataFromDocument(imageData);
        htmlParser.start();

    }

    private HtmlParser.DataFromDocument imageData = new HtmlParser.DataFromDocument() {
        @Override
        public void setData(Document document) {

            Elements scriptElements = document.select("script");
            for (Element scriptElement : scriptElements) {
                String scriptString = scriptElement.html();
                if (scriptString.contains("initData") && scriptString.contains("page_url")) {
                    String[] strings = scriptString.split("\n");
                    for (int i = 0; i < strings.length; i++) {
                        String str = strings[i];
                        if (str.contains("initData") && str.contains("page_url")) {
                            int startPos = str.indexOf("{");
                            int endPos = str.indexOf("}") + 1;
                            jsonString = str.substring(startPos, endPos);
                            break;

                        }
                    }
                    break;
                }
            }
        }
    };

    public String getJsonString() {
        return jsonString;
    }

    public String getDateString(long lastUpdateTime) {
        long times = lastUpdateTime * 1000;
        Date date = new Date(times);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(date);
    }

    public String getDateString() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(date);
    }
}
