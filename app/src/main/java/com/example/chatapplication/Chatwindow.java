package com.example.chatapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chatwindow extends AppCompatActivity {

    String reciverimg, reciverUid, reciverName, SenderUID;
    CircleImageView profile;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    ImageView translate;
    TextView reciverNName;
    CardView sendbtn;
    EditText textmsg;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;

    public static String senderImg;
    public static String reciverIImg;
    String senderRoom, reciverRoom;
    RecyclerView messageAdpter;
    ArrayList<msgModel> messagessArrayList;
    messageAdapter mmessagesAdpter;

    SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatwindow);

        //firebase initialization
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        reciverName = getIntent().getStringExtra("nameee");
        reciverimg = getIntent().getStringExtra("reciverImg");
        reciverUid = getIntent().getStringExtra("uid");
        messagessArrayList = new ArrayList<>();

        //language translation code is here
        translate = findViewById(R.id.micbutton);
        sendbtn = findViewById(R.id.sendButton);
        textmsg = findViewById(R.id.textmsg);
        SenderUID = firebaseAuth.getUid();
        senderRoom = SenderUID + reciverUid;
        reciverRoom = reciverUid + SenderUID;
        messageAdpter = findViewById(R.id.msgadpter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdpter.setLayoutManager(linearLayoutManager);

        mmessagesAdpter = new messageAdapter(Chatwindow.this, messagessArrayList);
        messageAdpter.setAdapter(mmessagesAdpter);

        profile = findViewById(R.id.chatwindowprofile);
        reciverNName = findViewById(R.id.recivername);
        Picasso.get().load(reciverimg).into(profile);
        reciverNName.setText(String.format("%s", reciverName));

        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatreference = database.getReference().child("chats").child(senderRoom).child("messages");

        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagessArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModel messages = dataSnapshot.getValue(msgModel.class);
                    messagessArrayList.add(messages);
                }
                mmessagesAdpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

        final boolean[] initialDataFetched = {false};

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               senderImg= snapshot.child("profilepic").getValue().toString();
               reciverIImg=reciverimg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

     /*   chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!initialDataFetched[0]) {
                    initialDataFetched[0] = true;
                    Log.d(TAG, "Initial data fetch completed");
                }

                if (snapshot.exists()) {
                    Log.d(TAG, "Snapshot exists: true");

                    if (snapshot.hasChildren()) {
                        Log.d(TAG, "Snapshot has children: true");
                        messagessArrayList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                msgModel messages = dataSnapshot.getValue(msgModel.class);

                                if (messages != null) {
                                    messagessArrayList.add(messages);
                                }
                            }
                        }

                        mmessagesAdpter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Snapshot has children: false");
                    }

                } else {
                    Log.d(TAG, "Snapshot exists: false");
                }
            }

     *//*       @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                // Handle onCancelled
            }
        });
*/
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start speech recognition when translate button is clicked
                startSpeechRecognition();
            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = textmsg.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(Chatwindow.this, "Enter the message first", Toast.LENGTH_SHORT).show();
                    return;
                }
                textmsg.setText("");
                Date date = new Date();
                msgModel messagess = new msgModel(message, SenderUID, date.getTime());
                database = FirebaseDatabase.getInstance();

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push().setValue(messagess)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats")
                                        .child(reciverRoom)
                                        .child("messages")
                                        .push().setValue(messagess)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // Handle completion if needed
                                            }
                                        });
                            }
                        });
            }
        });
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            // Handle speech recognition not supported
            e.getMessage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);

                // Now you have the recognized speech, proceed with translation
                translateText(spokenText);
            }
        }
    }

    private void translateText(String textToTranslate) {
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                .setTargetLanguage(FirebaseTranslateLanguage.MR)
                .build();
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        translator.downloadModelIfNeeded()
                .addOnSuccessListener(aVoid -> {
                    // Model downloaded successfully, proceed with translation
                    translator.translate(textToTranslate)
                            .addOnSuccessListener(translatedText -> {
                                // Update your UI or perform actions with the translated text
                                textmsg.setText(translatedText);
                            })
                            .addOnFailureListener(e -> {
                                // Handle translation failure
                                Toast.makeText(Chatwindow.this, "Translation error", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle model download failure
                    Log.e("Translation", "Failed to download model: " + e.getMessage());
                    Toast.makeText(Chatwindow.this, "Translation error", Toast.LENGTH_SHORT).show();

                    // You may want to notify the user or retry downloading the model
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release resources
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}


