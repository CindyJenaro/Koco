package com.java.jiangbaisheng;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class Newsdata {
    @PrimaryKey(autoGenerate = true)
    public int id;

    private String type;
    private String title;
    private String content;

    public Newsdata(String type, String title, String content) {
        this.type = type;
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


