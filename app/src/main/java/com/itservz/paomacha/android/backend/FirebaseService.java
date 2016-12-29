package com.itservz.paomacha.android.backend;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Created by raju.athokpam on 15-12-2016.
 */

public class FirebaseService {

    private FirebaseApp app;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    private FirebaseService() {
        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
        auth = FirebaseAuth.getInstance(app);
        storage = FirebaseStorage.getInstance(app);
        database.setPersistenceEnabled(true);
    }
    private static FirebaseService INSTANCE = new FirebaseService();
    public static FirebaseService getInstance() {
        return INSTANCE;
    }

    public FirebaseApp getApp() {
        return app;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }
}
