package com.example.stepanenko.newsapp;

public class News {
    private String title;
    private String date;
    private String url;
    private String author;
    private String section;

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

    public String getSection() {
        return section;
    }

    public News(String authorBase, String sectionBase, String dateBase, String titleBase, String urlBase) {
        author = authorBase;
        section = sectionBase;
        date = dateBase;
        title = titleBase;
        url = urlBase;

    }
}
