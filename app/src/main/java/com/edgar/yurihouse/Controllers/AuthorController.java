package com.edgar.yurihouse.Controllers;

import android.os.Handler;

import com.edgar.yurihouse.Items.MangaItem;
import com.edgar.yurihouse.Utils.HtmlParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class AuthorController {

    private ArrayList<MangaItem> mangaItems = null;

    public AuthorController() {}

    private HtmlParser.DataFromDocument mangasData = new HtmlParser.DataFromDocument() {
        @Override
        public void setData(Document document) {
            String nameString, statusString, coverUrlString, authorsString, queryInfoUrl;

            Element rootElement = document.selectFirst("#tagBox");
            Elements coverElements = rootElement.children();

            mangaItems = new ArrayList<>();
            for (Element coverElement : coverElements) {
                Element imgElement = coverElement.getElementsByClass("ImgA autoHeight").first();
                queryInfoUrl = imgElement.absUrl("href");
                nameString = imgElement.attr("title");
                coverUrlString = imgElement.child(0).attr("src");
                authorsString = coverElement.selectFirst("span.info").text();

                int startPos = authorsString.indexOf("作者") + 3;
                authorsString = authorsString.substring(startPos);
                statusString = "连载中";

                MangaItem mangaItem = new MangaItem(nameString, statusString,
                        coverUrlString, authorsString, queryInfoUrl);
                mangaItems.add(mangaItem);
            }

        }
    };

    public void setupMangaList(String urlString, Handler handler) {
        HtmlParser htmlParser = new HtmlParser(urlString, handler);
        htmlParser.setDataFromDocument(mangasData);
        htmlParser.start();
    }

    public ArrayList<MangaItem> getMangaItems() {
        return mangaItems;
    }
}
