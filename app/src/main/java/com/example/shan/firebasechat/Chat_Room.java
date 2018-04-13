package com.example.shan.firebasechat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by shan on 2018-03-04.
 */

public class Chat_Room extends AppCompatActivity {

    private Button btn_send_msg;
    private EditText input_msg;
    private TextView chat_conversation;
    private String user_name, room_name;
    private DatabaseReference roomReference;
    private String temp_key; //the key of the child

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room); //Set a view for activity

        btn_send_msg = (Button)findViewById(R.id.btn_send_msg);
        input_msg = (EditText)findViewById(R.id.input_msg);
        chat_conversation = (TextView)findViewById(R.id.textView);

        user_name = getIntent().getExtras().get("user_name").toString();
        room_name = getIntent().getExtras().get("room_name").toString();
        setTitle("Room - " + room_name); //Set the activity's title as the room's name

        roomReference = FirebaseDatabase.getInstance().getReference().child(room_name);
        //Without child(room_name) can only read the root's name
        //But with child(room_name) can access into the root

        //Add a message with msg text(from editText) and user name under room_name
        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<String, Object>();
                temp_key = roomReference.push().getKey(); //push means add a child for room_name, getKey means get the key of the child
                roomReference.updateChildren(map);

                //go to the child of the room
                DatabaseReference messageRef = roomReference.child(temp_key);
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("name", user_name); // key:name, value:user_name
                map2.put("message", input_msg.getText().toString()); // key:message, value:input_msg

                messageRef.updateChildren(map2); // save the map2 data into firebase
            }
        });

        //show message in textView, roomReference - room level
        roomReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);

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

    //show message in textView
    private String chat_msg, chat_user_name;
    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator(); //dataSnapshot: randomkey messages. iterator:dataSnapshot's children, msg and name
        while (iterator.hasNext()){
            chat_msg = (String) ((DataSnapshot)iterator.next()).getValue();
            //Get the string value of the child one after one, not by key. HashMap sorts by alphabet
            chat_user_name = (String) ((DataSnapshot)iterator.next()).getValue();

            //Show data in TextView
            chat_conversation.append(chat_user_name + " : " + chat_msg + "\n"); //Show the data to TextView chat_conversation
            //chat_conversation.setText(chat_user_name + " : " + chat_msg + "\n");//setText只显示该行，而append是在之前的文本上增加该行
        }
    }
}
