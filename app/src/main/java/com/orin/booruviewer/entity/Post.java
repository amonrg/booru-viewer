package com.orin.booruviewer.entity;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class Post implements Serializable {
    private int id;
    private String filename;
    private String fileurl;
    private String tags;
    private String thumburl;
    private Set<Tag> tagsSet;

    public Post() {
        tagsSet = new LinkedHashSet<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        this.tagsSet.add(tag);
    }

    public Set<Tag> getTagsSet() {
        return tagsSet;
    }

    public String getThumburl() {
        return thumburl;
    }

    public void setThumburl(String thumburl) {
        this.thumburl = thumburl;
    }
}
