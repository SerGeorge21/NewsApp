package com.example.android.newsappstage1;

public class Story {

    private String title;
    private String section;
    private String date;
    private String url;
    private String author;

    public Story(String title, String section, String date, String url, String author){
        this.title = title;
        this.section= section;
        this.date = date;
        this.url = url;
        this.author = author;
    }

    public String getTitle(){ return title; }
    public String getSection(){ return section; }
    public String getDate(){ return date; }
    public String getUrl(){ return url; }
    public String getAuthor() {  return author; }


}
