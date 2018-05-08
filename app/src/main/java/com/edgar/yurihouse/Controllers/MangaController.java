package com.edgar.yurihouse.Controllers;

import android.os.Handler;
import android.util.Log;

import com.edgar.yurihouse.Items.AuthorItem;
import com.edgar.yurihouse.Items.ChapterItem;
import com.edgar.yurihouse.Items.CommentQueryArg;
import com.edgar.yurihouse.Items.MangaItem;
import com.edgar.yurihouse.Utils.HtmlParser;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MangaController {

    private static final String TAG = "====================" + MangaController.class.getSimpleName();

    private String introString, relatedAuthorName;
    private ArrayList<ChapterItem> chapterItems = new ArrayList<>();
    private ArrayList<MangaItem> mangaItems1 = null;
    private ArrayList<MangaItem> mangaItems2 = null;

    private String timeString, relatedUrl, coverUrl, typeString, statusString;

    private String obj_id, authoruid, is_Original, comment_type, dt;
    private CommentQueryArg commentQueryArg;
    private ArrayList<AuthorItem> authorItems = null;

    public MangaController(){ }

    public void setupChaptersList(String urlString, Handler handler) {

        HtmlParser htmlParser = new HtmlParser(urlString, handler);
        htmlParser.setDataFromDocument(chapterData);
        htmlParser.start();

    }

    private HtmlParser.DataFromDocument chapterData = new HtmlParser.DataFromDocument() {
        @Override
        public void setData(Document document) {
            introString = document.getElementsByClass("txtDesc autoHeight").first().text();

            Elements scriptElements = document.select("script");
            String jsonString = null;

            for (Element eachScriptElement : scriptElements) {

                String scriptString = eachScriptElement.html();
                String[] outputStrings = scriptString.split("\n");

                for (int i = 0; i < outputStrings.length; i++) {
                    String str = outputStrings[i];
                    if (str.contains("initIntroData")) {
                        int startPos = str.indexOf("initIntroData(") + 14;
                        int endPos = str.lastIndexOf(")");
                        jsonString = str.substring(startPos, endPos);
                    }
                }
            }

            if (jsonString != null) {
                Type listType = new TypeToken<ArrayList<ChapterItem>>() {}.getType();
                chapterItems = new GsonBuilder().create().fromJson(jsonString, listType);
            }
        }

    };

    public ArrayList<ChapterItem> getChapterItems() {
        return chapterItems;
    }

    public String getIntroString() {
        return introString;
    }

    public void setupRelated(String urlString, Handler handler) {

        Log.d(TAG, "setupRelated: " + urlString);
        HtmlParser htmlParser = new HtmlParser(urlString, handler);
        htmlParser.setDataFromDocument(relatedData);
        htmlParser.start();

    }

    public ArrayList<MangaItem> getMangaItems1() {
        return mangaItems1;
    }

    public ArrayList<MangaItem> getMangaItems2() {
        return mangaItems2;
    }

    public String getRelatedAuthorName() {
        return relatedAuthorName;
    }

    private HtmlParser.DataFromDocument relatedData = new HtmlParser.DataFromDocument() {
        @Override
        public void setData(Document document) {

            Elements rootElements = document.select("div.imgBox");
            relatedAuthorName = rootElements.get(0).child(0).child(0).text();

            if (relatedAuthorName == null) {
                return;
            }
            mangaItems1 = new ArrayList<>();
            mangaItems2 = new ArrayList<>();

            int index = 0;
            for (Element rootElement : rootElements) {

                Element listElement = rootElement.getElementsByClass("col_3_1").first();
                Elements listItemElements = listElement.children();
                for (Element listItem : listItemElements) {
                    String coverInfoUrl = listItem.getElementsByClass("ImgA autoHeight")
                            .first().absUrl("href");
                    String coverUrlString = listItem.selectFirst("img").attr("src");
                    Element em = listItem.selectFirst("em");
                    String coverStatusString = (em == null) ? "连载中" : "已完结";
                    String coverAuthorString = listItem.getElementsByClass("info").first().text();

                    int startPos = coverAuthorString.indexOf(":") + 1;
                    coverAuthorString = coverAuthorString.substring(startPos);

                    String coverTitleString = listItem.getElementsByClass("txtA").first().text();
                    MangaItem mangaItem = new MangaItem(coverTitleString,
                            coverStatusString, coverUrlString, coverAuthorString, coverInfoUrl);
                    if (index == 0) {
                        mangaItems1.add(mangaItem);
                    } else if (index == 1) {
                        mangaItems2.add(mangaItem);
                    }
                }
                index++;
            }

        }
    };



    public void setupMangaInfos(String urlString, Handler handler) {

        HtmlParser htmlParser = new HtmlParser(urlString, handler);
        htmlParser.setDataFromDocument(detailInfosData);
        htmlParser.start();

    }

    private HtmlParser.DataFromDocument detailInfosData = new HtmlParser.DataFromDocument() {
        @Override
        public void setData(Document document) {

            Elements scriptElements = document.select("script");

            for (Element scriptElement : scriptElements) {

                String scriptString = scriptElement.html();

                if (scriptString.contains("obj_id") && scriptString.contains("comment_type")) {

                    String[] stringArray = scriptString.split("\n");

                    for (int mIndex = 0; mIndex < stringArray.length; mIndex++) {

                        String str = stringArray[mIndex];
                        if (str.contains("obj_id")) {
                            int startPos = str.indexOf("\"");
                            startPos = (startPos != -1) ? startPos : str.indexOf("'");
                            int endPos = str.lastIndexOf("\"");
                            endPos = (endPos != -1) ? endPos : str.lastIndexOf("'");
                            obj_id = str.substring(startPos+1, endPos);
                        }

                        if (str.contains("authoruid")) {
                            int startPos = str.indexOf("\"");
                            startPos = (startPos != -1) ? startPos : str.indexOf("'");
                            int endPos = str.lastIndexOf("\"");
                            endPos = (endPos != -1) ? endPos : str.lastIndexOf("'");
                            authoruid = str.substring(startPos+1, endPos);
                        }

                        if (str.contains("is_Original")) {
                            int startPos = str.indexOf("\"");
                            startPos = (startPos != -1) ? startPos : str.indexOf("'");
                            int endPos = str.lastIndexOf("\"");
                            endPos = (endPos != -1) ? endPos : str.lastIndexOf("'");
                            is_Original = str.substring(startPos+1, endPos);
                        }

                        if (str.contains("comment_type")) {
                            int startPos = str.indexOf("\"");
                            startPos = (startPos != -1) ? startPos : str.indexOf("'");
                            int endPos = str.lastIndexOf("\"");
                            endPos = (endPos != -1) ? endPos : str.lastIndexOf("'");
                            comment_type = str.substring(startPos+1, endPos);
                        }

                        if (str.contains("dt")) {
                            int startPos = str.indexOf("\"");
                            startPos = (startPos != -1) ? startPos : str.indexOf("'");
                            int endPos = str.lastIndexOf("\"");
                            endPos = (endPos != -1) ? endPos : str.lastIndexOf("'");
                            dt = str.substring(startPos+1, endPos);
                        }

                    }
                    commentQueryArg = new CommentQueryArg(obj_id, authoruid, is_Original, comment_type, dt);
                }

            }

            timeString = document.selectFirst("span.date").text();
            relatedUrl = document.selectFirst("li.last").child(0).absUrl("href");

            coverUrl = document.selectFirst("#Cover").getElementsByTag("img").first()
                    .attr("src");
            Element infoElement = document.selectFirst("div.sub_r");

            Elements allInfoElements = infoElement.getElementsByClass("txtItme");
            Element authorRootElement = allInfoElements.get(0);

            typeString = "";
            Elements typeStringElements = allInfoElements.get(1).getElementsByClass("pd");
            for (Element typeStringEle : typeStringElements) {
                typeString += typeStringEle.text() + "  ";
            }

            statusString = "";
            Elements statusStringElements = allInfoElements.get(2).getElementsByClass("pd");
            for (Element statusStringEle : statusStringElements) {
                statusString += statusStringEle.text() + "  ";
            }

            Elements authorsElements = authorRootElement.getElementsByClass("pd introName");
            authorItems = new ArrayList<>();
            for (Element authorChild : authorsElements) {
                String authorName = authorChild.text();
                String authorUrl = authorChild.absUrl("href");
                AuthorItem authorItem = new AuthorItem(authorName, authorUrl);
                authorItems.add(authorItem);
            }
        }
    };

    public CommentQueryArg getCommentQueryArg() {
        return commentQueryArg;
    }

    public String getTimeString() {
        return timeString;
    }

    public String getRelatedUrl() {
        return relatedUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getTypeString() {
        return typeString;
    }

    public String getStatusString() {
        return statusString;
    }

    public ArrayList<AuthorItem> getAuthorItems() {
        return authorItems;
    }
}
