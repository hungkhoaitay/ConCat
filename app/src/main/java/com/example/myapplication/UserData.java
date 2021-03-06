package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static android.content.ContentValues.TAG;
import static com.example.myapplication.MainActivity.buttonIDs;

public class UserData {
    private Buttons button;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private class Buttons {
        private String[] buttons;

        private Buttons() {
            String[] buttons = new String[6];
            Arrays.fill(buttons, AppInfo.EMPTY);
            this.buttons = buttons;
        }

        private void updateButton(int pos, String appName) {
            this.buttons[pos] = appName;
        }

        private void set(AppCompatActivity ac) {
            for (int i = 0; i < MainActivity.NUMBER_OF_BUTTONS; i++) {
                AppInfo.of(buttons[i]).setButton(ac, ac.findViewById(buttonIDs[i]), i);
            }
        }

        private HashMap<String, String> toHashMap() {
            HashMap<String, String> has = new HashMap<>();
            for (int i = 0; i < MainActivity.NUMBER_OF_BUTTONS; i++) {
                has.put(Integer.toString(i), buttons[i]);
            }
            return has;
        }

        private void sendData(AppCompatActivity ac) {
            db.collection(FirebaseAuth.getInstance().getUid())
                    .document("buttons")
                    .set(toHashMap());
        }

        private void update(String userID, AppCompatActivity ac) {
            DocumentReference docRef = db.collection(FirebaseAuth.getInstance().getUid()).document("buttons");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> has = document.getData();
                            Log.d(TAG, "DocumentSnapshot data: " + has);
                            for (int i = 0; i < MainActivity.NUMBER_OF_BUTTONS; i++) {
                                Buttons.this.buttons[i] = Optional.of((String) has.get(Integer.toString(i))).orElse(AppInfo.EMPTY);
                                AppInfo.of(buttons[i]).setButton(ac, ac.findViewById(buttonIDs[i]), i);
                            }
                        } else {
                            clean();
                            set(ac);
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        private void clean() {
            Arrays.fill(this.buttons, AppInfo.EMPTY);
        }

        private String[] data() {
            return this.buttons;
        }
    }

    public UserData() {
        this.button = new Buttons();
    }

    public static UserData USERDATA = new UserData();

    public void updateButton(int pos, String appName) {
        this.button.updateButton(pos, appName);
    }

    public void set(AppCompatActivity ac) {
        this.button.set(ac);
    }

    public void sendData(AppCompatActivity ac) {
        this.button.sendData(ac);
    }

    public void clean() {
        this.button = new Buttons();
    }

    public void update(String userID, AppCompatActivity ac) {
        this.button.update(userID, ac);
    }

    public void check() {
        for (int i = 0; i < MainActivity.NUMBER_OF_BUTTONS; i++) {
            Log.d(TAG, "check data: " + i + button.buttons[i]);
        }
    }

    public String[] buttonsData() {
        return this.button.data();
    }
}
