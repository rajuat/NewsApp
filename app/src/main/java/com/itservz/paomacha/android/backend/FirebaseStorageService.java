package com.itservz.paomacha.android.backend;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by Raju on 12/6/2016.
 */

public class FirebaseStorageService {
    private StorageReference storageRef;
    private FirebaseStorageService() {
        storageRef = FirebaseService.getInstance().getStorage().getReferenceFromUrl("gs://paomacha2017.appspot.com");
    }
    private static FirebaseStorageService INSTANCE = new FirebaseStorageService();
    public static FirebaseStorageService getInstance() {
        return INSTANCE;
    }

    public Task<byte[]> getImage(String fileName) {
        StorageReference splashRef = storageRef.child(fileName);
        final long ONE_MEGABYTE = 1024 * 1024;
        return splashRef.getBytes(ONE_MEGABYTE);
    }

    public void setImage(String id, byte[] data){
        StorageReference ref = FirebaseService.getInstance().getStorage().getReference("books");
        UploadTask uploadTask = ref.child(id).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }
}
