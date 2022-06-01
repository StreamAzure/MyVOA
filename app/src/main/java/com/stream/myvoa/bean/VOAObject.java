package com.stream.myvoa.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class VOAObject implements Parcelable {
    private String title; //标题
    private String album; //所属专辑
    private String content;//音频文本
    private String mp3_url; //mp3连接
    private String lrc_path; //lrc保存位置
    public VOAObject() {
        title = "";
        album = "";
        content ="";
        mp3_url = "";
    }
    public VOAObject(String title, String content, String mp3_url, String lrc_path){
        this.title = title;
        this.content = content;
        this.mp3_url = mp3_url;
        this.lrc_path = lrc_path;
        this.album = "";
    }

    protected VOAObject(Parcel in) {
        title = in.readString();
        album = in.readString();
        content = in.readString();
        mp3_url = in.readString();
        lrc_path = in.readString();
    }

    public static final Creator<VOAObject> CREATOR = new Creator<VOAObject>() {
        @Override
        public VOAObject createFromParcel(Parcel in) {
            return new VOAObject(in);
        }

        @Override
        public VOAObject[] newArray(int size) {
            return new VOAObject[size];
        }
    };

    public String getAlbum() {
        return album;
    }
    public String getContent() {
        return content;
    }
    public String getmp3_url() {
        return mp3_url;
    }
    public String getTitle() {
        return title;
    }
    public String getlrc_path() {
        return lrc_path;
    }
    public void setAlbum(String album) {
        if (album.length()>0)
            this.album = album;
    }
    public void setContent(String content) {
        if (!content.isEmpty())
            this.content = content;
    }
    public void setmp3_url(String mp3_url) {
        this.mp3_url = mp3_url;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setlrc_path(String lrc_path) {
        this.lrc_path = lrc_path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(album);
        dest.writeString(content);
        dest.writeString(mp3_url);
        dest.writeString(lrc_path);
    }
}

