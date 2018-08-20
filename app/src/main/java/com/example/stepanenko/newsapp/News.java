package com.example.stepanenko.newsapp;

public class News {
    private String title;
    private String date;
    private String url;
    private String author;

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public News(String authorBase, String dateBase, String titleBase, String urlBase) {
        author = authorBase;
        date = dateBase;
        title = titleBase;
        url = urlBase;

    }
}
