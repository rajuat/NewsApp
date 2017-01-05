package com.itservz.paomacha.android.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by raju.athokpam on 04-01-2017.
 */

public class Pao implements Serializable {
    public String uuid; // for bookmark and others
    public String createdOn; //date
    public String createdBy; //name
    public String imageUrl; // if this is absent, get from imageUrl
    public byte[] image; // for user uploads
    public String title;
    public String body;
    public int likes;
    public int disLikes;
    public List<String> tags; // for category like manipur election, top news
    public String originalNewsUrl; //for displaying webpage

    public Pao() {
        //for firebase
    }

    @Override
    public String toString() {
        return "Pao{" +
                "uuid='" + uuid + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", image='" + image + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", likes=" + likes +
                ", disLikes=" + disLikes +
                ", tags=" + tags +
                ", originalNewsUrl='" + originalNewsUrl + '\'' +
                '}';
    }
}
