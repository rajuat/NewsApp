package com.itservz.paomacha.android.backend;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.itservz.paomacha.android.model.Pao;

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

    @NonNull
    public DatabaseReference getDatabaseReference(final PaoListener listener) {
        final DatabaseReference paoref = FirebaseService.getInstance().getDatabase().getReference("messages");
        paoref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Pao value = dataSnapshot.getValue(Pao.class);
                listener.onNewPao(value);
                Log.d(TAG, "onChildAdded: " + value.toString());
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
