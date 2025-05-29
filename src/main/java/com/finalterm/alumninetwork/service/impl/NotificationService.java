package com.finalterm.alumninetwork.service.impl;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {

    private final FirebaseDatabase firebaseDatabase;

    public NotificationService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    public void sendNotification(String userId, Map<String, Object> data) {
        DatabaseReference ref = firebaseDatabase.getReference("notifications").child(userId);
        ref.push().setValueAsync(data);
    }
}