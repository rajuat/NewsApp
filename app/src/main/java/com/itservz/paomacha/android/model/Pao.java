package com.itservz.paomacha.android.model;

import java.util.List;

/**
 * Created by raju.athokpam on 04-01-2017.
 */

public class Pao {
    public String uuid; // for bookmark and others
    public String createdOn; //date
    public String createdBy; //name
    public String imageUrl; // if this is absent, get from imageUrl
    public String image; // for user uploads
    public String title;
    public String body;
    public int likes;
    public int disLikes;
    public List<String> tags; // for category like manipur election, top news
    public String originalNewsUrl; //for displaying webpage
}
