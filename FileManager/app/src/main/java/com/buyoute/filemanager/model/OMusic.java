package com.buyoute.filemanager.model;

import java.io.Serializable;

public class OMusic implements Serializable {
    private long id;
    private String title;
    private String album;
    private long duration;
    private long size;
    private String artist;
    private String path;

    public OMusic() {

    }

    public OMusic(long id, String title, String album, long duration, long size, String artist, String path) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.duration = duration;
        this.size = size;
        this.artist = artist;
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
