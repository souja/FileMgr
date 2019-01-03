package com.buyoute.filemanager.tool;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class MusicLoader {

    public static synchronized MusicLoader getInstance(ContentResolver contentResolver) {
        if (instance == null) {
            instance = new MusicLoader();
            instance.init();
            instance.contentResolver = contentResolver;
        }
        return instance;
    }


    private static MusicLoader instance;
    private ContentResolver contentResolver;
    private List<MusicInfo> musicList;
    private Uri inUri, outUri;

    private String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE
    };
    private String where =  "mime_type in ('audio/mpeg','audio/x-ms-wma') and bucket_display_name <> 'audio' and is_music > 0 " ;
    private String sortOrder = MediaStore.Audio.Media.DATA;
    private void init() {
        musicList = new ArrayList<>();
        inUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        outUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    private MusicLoader(){

    }

    private void scanData(Uri contentUri){
        Cursor cursor = contentResolver.query(contentUri, projection, where, null, sortOrder);
        if(cursor == null){
            LogUtil.e("Line(37	)	Music Loader cursor == null.");
        }else if(!cursor.moveToFirst()){
            LogUtil.e("Line(39	)	Music Loader cursor.moveToFirst() returns false.");
        }else{
            int displayNameCol = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int albumCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int idCol = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int durationCol = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int sizeCol = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
            int artistCol = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int urlCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do{
                String title = cursor.getString(displayNameCol);
                String album = cursor.getString(albumCol);
                long id = cursor.getLong(idCol);
                int duration = cursor.getInt(durationCol);
                long size = cursor.getLong(sizeCol);
                String artist = cursor.getString(artistCol);
                String url = cursor.getString(urlCol);

                MusicInfo musicInfo = new MusicInfo(id, title);
                musicInfo.setAlbum(album);
                musicInfo.setDuration(duration);
                musicInfo.setSize(size);
                musicInfo.setArtist(artist);
                musicInfo.setUrl(url);
                musicList.add(musicInfo);

            }while(cursor.moveToNext());
        }
    }

    static class MusicInfo implements Parcelable {
        private long id;
        private String title;
        private String album;
        private int duration;
        private long size;
        private String artist;
        private String url;

        public MusicInfo() {

        }

        public MusicInfo(long pId, String pTitle) {
            id = pId;
            title = pTitle;
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

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(title);
            dest.writeString(album);
            dest.writeString(artist);
            dest.writeString(url);
            dest.writeInt(duration);
            dest.writeLong(size);
        }

        public static final Parcelable.Creator<MusicInfo>
                CREATOR = new Creator<MusicLoader.MusicInfo>() {

            @Override
            public MusicInfo[] newArray(int size) {
                return new MusicInfo[size];
            }

            @Override
            public MusicInfo createFromParcel(Parcel source) {
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.setId(source.readLong());
                musicInfo.setTitle(source.readString());
                musicInfo.setAlbum(source.readString());
                musicInfo.setArtist(source.readString());
                musicInfo.setUrl(source.readString());
                musicInfo.setDuration(source.readInt());
                musicInfo.setSize(source.readLong());
                return musicInfo;
            }
        };
    }
}
