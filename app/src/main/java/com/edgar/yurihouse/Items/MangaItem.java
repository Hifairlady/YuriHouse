package com.edgar.yurihouse.Items;

public class MangaItem {

//    "id": 42435,
//    "name": "利维兹家的家庭教师",
//    "zone": "日本",
//    "status": "连载中",
//    "last_update_chapter_name": "第07话下",
//    "last_update_chapter_id": 73597,
//    "last_updatetime": 1517220131,
//    "hidden": 0,
//    "cover": "webpic/13/liweizijiadejiatingjiaoshi.jpg",
//    "first_letter": "l",
//    "comic_py": "liweicijiadejiatingjiaoshi",
//    "authors": "山座",
//    "types": "冒险/魔法/百合",
//    "readergroup": "青年漫画",
//    "copyright": 0,
//    "hot_hits": 2419184,
//    "app_click_count": 2420238,
//    "num": "4839422"


    public static final int TYPE_NORMAL = 0, TYPE_HEADER = 1, TYPE_FOOTER = 2;
    private int mangaItemType = 0;

    private int id;
    private String name, zone, status, last_update_chapter_name, last_update_chapter_id,
            last_updatetime, hidden, cover, first_letter, comic_py, authors, types, readergroup,
            copyright, hot_hits, app_click_count, num;

    private String queryInfoUrl = null;

    public MangaItem(int id, String name, String zone, String status, String last_update_chapter_name, String last_update_chapter_id, String last_updatetime, String hidden, String cover, String first_letter, String comic_py, String authors, String types, String readergroup, String copyright, String hot_hits, String app_click_count, String num) {
        this.id = id;
        this.name = name;
        this.zone = zone;
        this.status = status;
        this.last_update_chapter_name = last_update_chapter_name;
        this.last_update_chapter_id = last_update_chapter_id;
        this.last_updatetime = last_updatetime;
        this.hidden = hidden;
        this.cover = cover;
        this.first_letter = first_letter;
        this.comic_py = comic_py;
        this.authors = authors;
        this.types = types;
        this.readergroup = readergroup;
        this.copyright = copyright;
        this.hot_hits = hot_hits;
        this.app_click_count = app_click_count;
        this.num = num;
    }

    public MangaItem(String name, String status, String cover, String authors, String queryInfoUrl) {
        this.name = name;
        this.status = status;
        this.cover = cover;
        this.authors = authors;
        this.queryInfoUrl = queryInfoUrl;
    }

    public MangaItem(int mangaItemType) {
        this.mangaItemType = mangaItemType;
    }

    public int getMangaItemType() {
        return mangaItemType;
    }

    public void setMangaItemType(int mangaItemType) {
        this.mangaItemType = mangaItemType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getZone() {
        return zone;
    }

    public String getStatus() {
        return status;
    }

    public String getLast_update_chapter_name() {
        return last_update_chapter_name;
    }

    public String getLast_update_chapter_id() {
        return last_update_chapter_id;
    }

    public String getLast_updatetime() {
        return last_updatetime;
    }

    public String getHidden() {
        return hidden;
    }

    public String getCover() {
        return cover;
    }

    public String getFirst_letter() {
        return first_letter;
    }

    public String getComic_py() {
        return comic_py;
    }

    public String getAuthors() {
        return authors;
    }

    public String getTypes() {
        return types;
    }

    public String getReadergroup() {
        return readergroup;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getHot_hits() {
        return hot_hits;
    }

    public String getApp_click_count() {
        return app_click_count;
    }

    public String getNum() {
        return num;
    }

    public String getQueryInfoUrl() {
        return queryInfoUrl;
    }
}
