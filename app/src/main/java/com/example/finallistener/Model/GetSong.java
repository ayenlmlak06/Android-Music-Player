package com.example.finallistener.Model;

public class GetSong {
    String songsCategory, songTitle, artist, imgSongUpload, songDuration, songLink, mkey;

    public GetSong(String songsCategory, String songTitle, String artist, String imgSongUpload, String songDuration, String songLink) {
        if(songTitle.trim().equals("")){
            songTitle = "No title";
        }
        this.songsCategory = songsCategory;
        this.songTitle = songTitle;
        this.artist = artist;
        this.imgSongUpload = imgSongUpload;
        this.songDuration = songDuration;
        this.songLink = songLink;
    }

    public GetSong() {
    }

    public String getSongsCategory() {
        return songsCategory;
    }

    public void setSongsCategory(String songsCategory) {
        this.songsCategory = songsCategory;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImgSongUpload() {
        return imgSongUpload;
    }

    public void setImgSongUpload(String imgSongUpload) {
        this.imgSongUpload = imgSongUpload;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public String getMkey() {
        return mkey;
    }

    public void setMkey(String mkey) {
        this.mkey = mkey;
    }
}
