package com.itservz.paomacha.android.backend;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.itservz.paomacha.android.PaoActivity;
import com.itservz.paomacha.android.model.Pao;

import java.util.Set;

/**
 * Created by Raju on 12/6/2016.
 */

public class FirebaseDatabaseService {
    private static final String TAG = "FirebaseDatabaseService";
    private FirebaseDatabaseService(String lastPosted){
    }

    public static FirebaseDatabaseService getInstance(String lastPosted){
        Log.d(TAG, "Last posted " + lastPosted);
        return new FirebaseDatabaseService(lastPosted);
    }

    public static void updateLikes(String uuid, Integer likes) {
        FirebaseService.getInstance().getDatabase().getReference("messages").child(uuid).child("likes").setValue(likes);
    }

    public static String postPao(Pao pao) {
        DatabaseReference childRef = FirebaseService.getInstance().getDatabase().getReference("test").child("fromuser");
        DatabaseReference reference = childRef.push();
        String uId = reference.getKey();
        pao.uuid = uId;
        Log.d(TAG, "Pao posting: " + pao.toString());
        reference.setValue(pao);
        return uId;
    }

    public void getTrending(final PaoListener listener){

    }
    //for bookmark, likes and dislikes
    public void getUserTags(final PaoListener listener, final Set<String> tags){
        Log.d(TAG, "No of tags: "+tags.size());
        DatabaseReference fromUser = FirebaseService.getInstance().getDatabase().getReference("test").child("fromuser");
        fromUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Pao pao = dataSnapshot.getValue(Pao.class);
                if ("true".equalsIgnoreCase(pao.needsApproval)) return;
                if(tags.contains(pao.uuid)) {
                    listener.onNewPao(pao);
                    Log.d(TAG, "getUserPao.onChildAdded: " + pao.toString());
                }
            }
            @Override  public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override  public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override  public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override  public void onCancelled(DatabaseError databaseError) { }
        });

        DatabaseReference fromPaoap = FirebaseService.getInstance().getDatabase().getReference("messages");
        fromPaoap.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Pao pao = dataSnapshot.getValue(Pao.class);
                if ("true".equalsIgnoreCase(pao.needsApproval)) return;
                if(tags.contains(pao.uuid)) {
                    listener.onNewPao(pao);
                    Log.d(TAG, "getUserPao.onChildAdded: " + pao.toString());
                }
            }
            @Override  public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override  public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override  public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override  public void onCancelled(DatabaseError databaseError) { }
        });

    }

    public void getNewForCategory(final PaoListener listener, String category){

    }

    @NonNull
    public void getUserPao(final PaoListener listener) {
        final DatabaseReference paoref = FirebaseService.getInstance().getDatabase().getReference("test").child("fromuser");
        paoref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Pao value = dataSnapshot.getValue(Pao.class);
                Log.d(TAG, "getUserPao.onChildAdded: " + value.toString());
                if ("true".equalsIgnoreCase(value.needsApproval)) return;
                listener.onNewPao(value);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @NonNull
    public DatabaseReference getPaoaps(final PaoListener listener) {
        final DatabaseReference paoref = FirebaseService.getInstance().getDatabase().getReference("messages");
        paoref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Pao value = dataSnapshot.getValue(Pao.class);
                if ("true".equalsIgnoreCase(value.needsApproval)) return;
                listener.onNewPao(value);
                Log.d(TAG, "getPaoaps.onChildAdded: " + value.toString());
            }
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Pao value = dataSnapshot.getValue(Pao.class);
                Log.d(TAG, "onChildRemoved: " + value.toString());
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Pao value = dataSnapshot.getValue(Pao.class);
                Log.d(TAG, "onChildChanged: " + value.toString());
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Pao value = dataSnapshot.getValue(Pao.class);
                Log.d(TAG, "onChildMoved: " + value.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG:", "Failed to read value.", error.toException());
            }
        });
        return paoref;
    }

    public interface PaoListener {
        public void onNewPao(Pao pao);
    }
}
