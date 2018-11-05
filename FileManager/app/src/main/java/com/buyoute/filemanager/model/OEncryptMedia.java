package com.buyoute.filemanager.model;

import org.xutils.common.util.LogUtil;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.File;

@Table(name = "dbEncryptMedia")
public class OEncryptMedia {
    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "parent")
    private String parent;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private int type;//0-img,1-video

    @Column(name = "createTime")
    private long createTime;

    public OEncryptMedia() {
    }

    public OEncryptMedia(File encryptFile) {
        this.parent = encryptFile.getParent();
        this.name = encryptFile.getName();
        LogUtil.e("parent:" + parent + ",name:" + name);
        this.createTime = System.currentTimeMillis() / 1000;
    }

    public OEncryptMedia(String path, int type) {
        File mFile = new File(path);
        this.parent = mFile.getParent();
        this.name = mFile.getName();
        LogUtil.e("parent:" + parent + ",name:" + name);
        this.type = type;
        this.createTime = System.currentTimeMillis() / 1000;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
