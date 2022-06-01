package com.stream.myvoa.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class LRCObject implements Parcelable {
    String LRCSentence; //LRC文件中的一行，包含时间及句子
    long startTime; //解析出来的开始播放的时间，[02:23.12]格式转为毫秒值
    String content;//解析出来的内容

    public LRCObject(){};
    public LRCObject(String sec){
        this.LRCSentence = sec;
    };

    protected LRCObject(Parcel in) {
        LRCSentence = in.readString();
        startTime = in.readLong();
        content = in.readString();
    }

    public static final Creator<LRCObject> CREATOR = new Creator<LRCObject>() {
        @Override
        public LRCObject createFromParcel(Parcel in) {
            return new LRCObject(in);
        }

        @Override
        public LRCObject[] newArray(int size) {
            return new LRCObject[size];
        }
    };

    public long getStartTime() {
        return startTime;
    }

    public String getContent() {
        return content;
    }

    public String getLRCSentence() {
        return LRCSentence;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLRCSentence(String LRCSentence) {
        this.LRCSentence = LRCSentence;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(LRCSentence);
        dest.writeLong(startTime);
        dest.writeString(content);
    }
}
