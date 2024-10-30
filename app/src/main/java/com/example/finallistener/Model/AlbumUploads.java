package com.example.finallistener.Model;

public class AlbumUploads {
    public String names;
    public String urls;
    public String songsCategory;

    public AlbumUploads(String names, String urls, String songsCategory) {
        this.names = names;
        this.urls = urls;
        this.songsCategory = songsCategory;
    }

    public AlbumUploads() {
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public String getSongsCategory() {
        return songsCategory;
    }

    public void setSongsCategory(String songsCategory) {
        this.songsCategory = songsCategory;
    }
}