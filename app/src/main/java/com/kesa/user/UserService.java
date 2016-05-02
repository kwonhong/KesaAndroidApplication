package com.kesa.user;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.kesa.app.KesaApplication;
import com.kesa.util.ImageManager;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public class UserService extends Service {

    @Inject @Named("users") Firebase firebase;
    @Inject ImageManager imageEncoder;

    @Override
    public void onCreate() {
        super.onCreate();
        ((KesaApplication) getApplication()).getComponent().inject(this); // Dependency Injection
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        firebase
            .addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    saveOrUpdateUser(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    saveOrUpdateUser(dataSnapshot);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    // Checking the local data of the user.
                    List<User> localUsers = User.find(User.class, "uid = ?", user.getUid());
                    localUsers.iterator().next().delete(); // Deleting
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    // Do not need to handle
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    // TODO(hongil):
                }

                private void saveOrUpdateUser(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    // Checking the local data of the user.
                    List<User> localUsers = User.find(User.class, "uid = ?", user.getUid());
                    if (!localUsers.isEmpty()) {
                        long currentUserId = localUsers.iterator().next().getId();
                        user.setId(currentUserId);
                    }

                    // Storing the image in internal directory & updating profileImage as the path.
                    Bitmap profileBitmap = imageEncoder.decodeBase64(user.getProfileImage());
                    String profileImagePath =
                        imageEncoder.saveBitmapToInternalStorage(
                            UserService.this, profileBitmap, user.getUid());
                    user.setProfileImage(profileImagePath);
                    user.save();
                }
            });

        return START_STICKY;
    }

    /**
     * Returns {@code true} if {@link UserService} is currently running on the device. It checks
     * by searching through the services that are currently running on the device.
     */
    public static boolean isServiceRunning(Context context) {
        ActivityManager manager =
            (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (UserService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
