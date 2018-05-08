package com.edgar.yurihouse.Items;

import java.util.ArrayList;

public class ChapterItem {

    private String title;
    private ArrayList<DataItem> data;

    public class DataItem {

        private int id, comic_id, chapter_order, chaptertype, sort;
        private String chapter_name, title;

        public DataItem(int id, int comic_id, int chapter_order, int chaptertype, int sort, String chapter_name, String title) {
            this.id = id;
            this.comic_id = comic_id;
            this.chapter_order = chapter_order;
            this.chaptertype = chaptertype;
            this.sort = sort;
            this.chapter_name = chapter_name;
            this.title = title;
        }

        public int getId() {
            return id;
        }

        public int getComic_id() {
            return comic_id;
        }

        public int getChapter_order() {
            return chapter_order;
        }

        public int getChaptertype() {
            return chaptertype;
        }

        public int getSort() {
            return sort;
        }

        public String getChapter_name() {
            return chapter_name;
        }

        public String getTitle() {
            return title;
        }

    }

    public ChapterItem(String title, ArrayList<DataItem> data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<DataItem> getData() {
        return data;
    }

}
