package com.edgar.yurihouse.Items;

public class AuthorItem {

    private String authorName, authorUrl;

    public AuthorItem(String authorName, String authorUrl) {
        this.authorName = authorName;
        this.authorUrl = authorUrl;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

}
