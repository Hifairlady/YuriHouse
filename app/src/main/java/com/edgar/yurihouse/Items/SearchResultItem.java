package com.edgar.yurihouse.Items;

public class SearchResultItem {
//
//"id": 9949,
//"name": "一拳超人",
//"comic_py": "yiquanchaoren",
//"alias_name": "一击男,one punch man,秃头披风侠",
//"authors": "村田雄介/ONE",
//"types": "冒险/欢乐向/格斗",
//"zone": "日本",
//"status": "连载中",
//"last_update_chapter_name": "第131话",
//"last_update_chapter_id": 71879,
//"hot_hits": 272490231,
//"last_updatetime": 1523864271,
//"description": "主人公埼玉原本是一名整日奔波于求职的普通人。3年前的一天偶然遇到了要对淘气少年下杀手的异变螃蟹人后，回忆起年少年时“想要成为英雄”的梦想，最终拼尽全力救下了淘气少年。之后通过拼命锻炼，埼玉终于脱胎换骨获得了最强的力量，但同时失去了头发成了光头。在独自做了一段时间英雄后，正式加入英雄协会，与众多英雄一起开始了对抗各种怪人以及恶势力的生活……",
//"cover": "webpic/1/onepunchmanfengmianl.jpg"

    public static final int TYPE_NORMAL = 0, TYPE_HEADER = 1, TYPE_FOOTER = 2;
    private int itemType = 0;

    private int id, last_update_chapter_id, hot_hits;
    private long last_updatetime;
    private String name, comic_py, alias_name, authors, types, zone, status, last_update_chapter_name,
            description, cover;

    public SearchResultItem(int id, int last_update_chapter_id, int hot_hits, long last_updatetime, String name, String comic_py, String alias_name, String authors, String types, String zone, String status, String last_update_chapter_name, String description, String cover) {
        this.id = id;
        this.last_update_chapter_id = last_update_chapter_id;
        this.hot_hits = hot_hits;
        this.last_updatetime = last_updatetime;
        this.name = name;
        this.comic_py = comic_py;
        this.alias_name = alias_name;
        this.authors = authors;
        this.types = types;
        this.zone = zone;
        this.status = status;
        this.last_update_chapter_name = last_update_chapter_name;
        this.description = description;
        this.cover = cover;
    }

    public int getItemType() {
        return itemType;
    }

    public SearchResultItem(int itemType) {
        this.itemType = itemType;
    }

    public int getId() {
        return id;
    }

    public int getLast_update_chapter_id() {
        return last_update_chapter_id;
    }

    public int getHot_hits() {
        return hot_hits;
    }

    public long getLast_updatetime() {
        return last_updatetime;
    }

    public String getName() {
        return name;
    }

    public String getComic_py() {
        return comic_py;
    }

    public String getAlias_name() {
        return alias_name;
    }

    public String getAuthors() {
        return authors;
    }

    public String getTypes() {
        return types;
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

    public String getDescription() {
        return description;
    }

    public String getCover() {
        return cover;
    }
}
