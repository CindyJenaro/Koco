package com.java.jiangbaisheng;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONObject;

@Entity()
public class Newsdata {
    @PrimaryKey(autoGenerate = true)
    public int id;

    private String json;
    private String newsid;
    private String type;
    private String title;
    private String content;
    private String time;

<<<<<<< HEAD
    public Newsdata(String json, String type, String title, String content,String newsid, String time) {
        this.json=json;
=======
    public Newsdata(String type, String title, String content, String newsid, String time) {
>>>>>>> 717c4cfbf330270f4c0808397d226ef12906f1b4
        this.newsid =  newsid;
        this.type = type;
        this.title = title;
        this.content = content;
        this.time= time;
    }

    public Newsdata() { }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNewsid() {
        return newsid;
    }

    public void setNewsid(String newsid) {
        this.newsid = newsid;
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


