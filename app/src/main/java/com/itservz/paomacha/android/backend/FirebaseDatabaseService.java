package com.itservz.paomacha.android.backend;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itservz.paomacha.android.model.Categories;
import com.itservz.paomacha.android.model.Pao;
import com.itservz.paomacha.android.utils.DatabaseFolders;

import java.util.List;
import java.util.Set;

/**
 * Created by Raju on 12/6/2016.
 */

public class FirebaseDatabaseService {
    private static final String TAG = "FirebaseDatabaseService";

    private FirebaseDatabaseService(String lastPosted) {
    }

    public static FirebaseDatabaseService getInstance(String lastPosted) {
        Log.d(TAG, "Last posted " + lastPosted);
        return new FirebaseDatabaseService(lastPosted);
    }

    public static void updateDisLikes(Pao pao) {
        updateTags(pao, pao.disLikes);
    }

    public static void updateLikes(Pao pao) {
        updateTags(pao, pao.likes);
    }

    private static void updateTags(Pao pao, int likes) {
        pao.uuid = pao.uuid != null ? pao.uuid.trim() : null;
        if (pao.originalNewsUrl == null || pao.originalNewsUrl.isEmpty()) { //user news
            FirebaseService.getInstance().getDatabase().getReference(DatabaseFolders.prod.name()).child(DatabaseFolders.fromuser.name()).child(pao.uuid).child("likes").setValue(likes);
        } else {
            FirebaseService.getInstance().getDatabase().getReference(DatabaseFolders.prod.name()).child(DatabaseFolders.frompao.name()).child(pao.uuid).child("likes").setValue(likes);
        }
    }

    public static String createUserPao(Pao pao) {
        DatabaseReference childRef = FirebaseService.getInstance().getDatabase().getReference(DatabaseFolders.prod.name()).child(DatabaseFolders.fromuser.name());
        DatabaseReference reference = childRef.push();
        String uId = reference.getKey();
        pao.uuid = uId;
        Log.d(TAG, "Pao posting: " + pao.toString());
        reference.setValue(pao);
        return uId;
    }

    //for bookmark, likes and dislikes
    public void getUserTags(final PaoListener listener, final Set<String> tags) {
        Log.d(TAG, "No of tags: " + tags.size());
        DatabaseReference fromUser = FirebaseService.getInstance().getDatabase().getReference(DatabaseFolders.prod.name()).child(DatabaseFolders.frompao.name());
        fromUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Pao pao = dataSnapshot.getValue(Pao.class);
                pao.uuid = pao.uuid != null ? pao.uuid.trim() : null;
                if ("true".equalsIgnoreCase(pao.needsApproval)) return;
                if (tags.contains(pao.uuid)) {
                    listener.onNewPao(pao);
                    Log.d(TAG, "getUserPaoLatest.onChildAdded: " + pao.toString());
                }
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

        DatabaseReference fromPaoap = FirebaseService.getInstance().getDatabase().getReference(DatabaseFolders.prod.name()).child(DatabaseFolders.fromuser.name());
        fromPaoap.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Pao pao = dataSnapshot.getValue(Pao.class);
                if ("true".equalsIgnoreCase(pao.needsApproval)) return;
                pao.uuid = pao.uuid != null ? pao.uuid.trim() : null;
                if (tags.contains(pao.uuid)) {
                    listener.onNewPao(pao);
                    Log.d(TAG, "getUserPaoLatest.onChildAdded: " + pao.toString());
                }
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

    public void getTrendingPao(final PaoListener listener) {
        getPao(listener, DatabaseFolders.frompao.name(), "likes", null);
        getPao(listener, DatabaseFolders.fromuser.name(), "likes", null);
    }

    public void getPaoForCategory(final PaoListener listener, String category) {
        getPao(listener, DatabaseFolders.frompao.name(), "createdOn", category);
        getPao(listener, DatabaseFolders.fromuser.name(), "createdOn", category);
    }

    public void getUserPaoLatest(final PaoListener listener) {
        getPao(listener, DatabaseFolders.fromuser.name(), "createdOn", null);
    }

    public void getPaoLatest(final PaoListener listener) {
        getPao(listener, DatabaseFolders.frompao.name(), "createdOn", null);
    }

    private void getPao(final PaoListener listener, final String fromuserORfrompao, String filterby, final String category) {
        final DatabaseReference df = FirebaseService.getInstance().getDatabase().getReference(DatabaseFolders.prod.name()).child(fromuserORfrompao);
        Query paoref = df.orderByChild(filterby).limitToFirst(23);//latest
        paoref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Pao pao = dataSnapshot.getValue(Pao.class);
                Log.d(TAG, fromuserORfrompao + " :category: " + category + " :onChildAdded: " + pao.title);
                //when user post - needsApproval is true, we can "hide" news violation
                if ("true".equalsIgnoreCase(pao.needsApproval) || "hide".equalsIgnoreCase(pao.needsApproval))
                    return;
                pao.uuid = pao.uuid != null ? pao.uuid.trim() : null;
                //if category is null - showall, else look for categories in the pao
                if (category == null) {
                    listener.onNewPao(pao);
                } else if (pao.tags != null && !pao.tags.isEmpty() && pao.tags.contains(category.trim())) {
                    listener.onNewPao(pao);
                }
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

    public static void getPaoNotification(final PaoListener listener, long createdOn) {

        final DatabaseReference df = FirebaseService.getInstance().getDatabase().getReference(DatabaseFolders.prod.name()).child(DatabaseFolders.frompao.name());
        Query paoref;
        if (createdOn == -1) {
            paoref = df.orderByChild("createdOn").limitToFirst(1);//latest
        } else {
            long later = createdOn - 1;
            paoref = df.orderByChild("createdOn").endAt(later);//latest
        }
        paoref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Pao pao = dataSnapshot.getValue(Pao.class);
                //when user post - needsApproval is true, we can "hide" news violation
                if ("true".equalsIgnoreCase(pao.needsApproval) || "hide".equalsIgnoreCase(pao.needsApproval))
                    return;
                pao.uuid = pao.uuid != null ? pao.uuid.trim() : null;
                if (pao.tags != null && !pao.tags.isEmpty()) {
                    listener.onNewPao(pao);
                }
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

    //get pao category
    public static void getCategories(final List<String> categories) {
        DatabaseReference reference = FirebaseService.getInstance().getDatabase().getReference(DatabaseFolders.prod.name()).child("categories");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Categories cats = dataSnapshot.getValue(Categories.class);
                Log.d("TAG", "onDataChange " + cats.categories);
                if (cats.categories != null) {
                    String[] split = cats.categories.split(",");
                    for (String cat : split) {
                        categories.add(cat.trim());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public interface PaoListener {
        public void onNewPao(Pao pao);
    }
}
