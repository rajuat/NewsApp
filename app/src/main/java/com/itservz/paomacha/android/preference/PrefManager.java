package com.itservz.paomacha.android.preference;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by raju.athokpam on 04-01-2017.
 */

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "paoap";


    public PrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    private String BOOKMARK = "bookmark";

    public boolean hasBookmark(String bookmark) {
        Set<String> bookmarks = pref.getStringSet(BOOKMARK, new HashSet<String>());
        return bookmarks.contains(bookmark);
    }

    public Set<String> getBookmark() {
        return pref.getStringSet(BOOKMARK, new HashSet<String>());
    }

    public void addBookmark(String bookmark) {
        Set<String> bookmarks = pref.getStringSet(BOOKMARK, new HashSet<String>());
        bookmarks.add(bookmark);
        editor = pref.edit();
        editor.putStringSet(BOOKMARK, bookmarks);
        editor.commit();
    }

    public void removeBookmark(String bookmark) {
        Set<String> bookmarks = pref.getStringSet(BOOKMARK, new HashSet<String>());
        if(bookmarks.contains(bookmark)){
            bookmarks.remove(bookmark);
        }
        editor = pref.edit();
        editor.putStringSet(BOOKMARK, bookmarks);
        editor.commit();
    }

    private String LIKE = "like";

    public boolean hasLike(String like) {
        Set<String> likes = pref.getStringSet(LIKE, new HashSet<String>());
        return likes.contains(like);
    }

    public Set<String> getLike() {
        return pref.getStringSet(LIKE, new HashSet<String>());
    }

    public void addLike(String like) {
        Set<String> likes = pref.getStringSet(LIKE, new HashSet<String>());
        likes.add(like);
        editor = pref.edit();
        editor.putStringSet(LIKE, likes);
        editor.commit();
    }

    public void removeLike(String like) {
        Set<String> likes = pref.getStringSet(LIKE, new HashSet<String>());
        if(likes.contains(like)){
            likes.remove(like);
        }
        editor.putStringSet(LIKE, likes);
        editor.commit();
    }

    private String DISLIKE = "dislike";

    public boolean hasDislike(String dislike) {
        Set<String> dislikes = pref.getStringSet(DISLIKE, new HashSet<String>());
        return dislikes.contains(dislike);
    }

    public Set<String> getDislike() {
        return pref.getStringSet(DISLIKE, new HashSet<String>());
    }

    public void addDislike(String dislike) {
        Set<String> dislikes = pref.getStringSet(DISLIKE, new HashSet<String>());
        dislikes.add(dislike);
        editor = pref.edit();
        editor.putStringSet(DISLIKE, dislikes);
        editor.commit();
    }

    public void removeDislike(String dislike) {
        Set<String> dislikes = pref.getStringSet(DISLIKE, new HashSet<String>());
        if(dislikes.contains(dislike)){
            dislikes.remove(dislike);
        }
        editor = pref.edit();
        editor.putStringSet(DISLIKE, dislikes);
        editor.commit();
    }
}
