package com.itservz.paomacha.android.backend;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Raju on 12/6/2016.
 */

public class FirebaseDatabaseService {
    private static final String TAG = "FirebaseDatabaseService";
    private DatabaseReference sellsReference = null;
    private Query sellsQuery = null;
    private static FirebaseDatabaseService INSTANCE = null;

    private FirebaseDatabaseService(String lastPosted){
    }

    public static FirebaseDatabaseService getInstance(String lastPosted){
        Log.d(TAG, "Last posted " + lastPosted);
        return new FirebaseDatabaseService(lastPosted);
    }

    @NonNull
    public DatabaseReference getDatabaseReference(final ArrayAdapter<String> adapter) {
        final DatabaseReference myRef = FirebaseService.getInstance().getDatabase().getReference("todoItems");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String value = dataSnapshot.getValue(String.class);
                adapter.add(value);
            }
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                adapter.remove(value);
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG:", "Failed to read value.", error.toException());
            }
        });
        return myRef;
    }
}
