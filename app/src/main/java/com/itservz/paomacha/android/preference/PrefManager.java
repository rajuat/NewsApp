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
        editor = pref.edit();
    }

    private String BOOKMARK = "bookmark";

    public Set<String> getBookmark() {
        return pref.getStringSet(BOOKMARK, new HashSet<String>());
    }

    public void addBookmark(String bookmark) {
        Set<String> bookmarks = pref.getStringSet(BOOKMARK, new HashSet<String>());
        bookmarks.add(bookmark);
        editor.putStringSet(BOOKMARK, bookmarks);
        editor.commit();
    }

    public void removeBookmark(String bookmark) {
        Set<String> bookmarks = pref.getStringSet(BOOKMARK, new HashSet<String>());
        if(bookmarks.contains(bookmark)){
            bookmarks.remove(bookmark);
        }
        editor.putStringSet(BOOKMARK, bookmarks);
        editor.commit();
    }
}
