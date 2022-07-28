package com.cst2335.kwok0020;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    Button sendBtn;
    Button receivedBtn;
    EditText edit;
    ListView listView;
    MyAdapter theAdapter;
    ArrayList<Message> messages = new ArrayList<>();

    public static final String ITEM_SELECTED = "ITEM";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        sendBtn = findViewById(R.id.sendButton);
        receivedBtn = findViewById(R.id.receiveButton);
        edit = findViewById(R.id.editMsg);
        listView = findViewById(R.id.ListView);

        theAdapter = new MyAdapter();
        listView.setAdapter(theAdapter);

        boolean isTablet = findViewById(R.id.fragmentLocation) != null; //check if the FrameLayout is loaded

        listView.setOnItemClickListener((list, item, position, id)->{
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_SELECTED, messages.get(position).messageTyped );
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID, id);

            if(isTablet)
            {
                DetailsFragment dFragment = new DetailsFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(ChatRoomActivity.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

        sendBtn.setOnClickListener( click ->{
            String type = edit.getText().toString();
            if ( !type.isEmpty()) {
                messages.add(new Message(type, true));

                edit.setText("");//clear the text

                //notify that new data was added at a row:
                theAdapter.notifyDataSetChanged(); //at the end of ArrayList,

            }
        });



        receivedBtn.setOnClickListener( click ->{
            String type = edit.getText().toString();

            if ( !type.isEmpty()) {
                messages.add(new Message(type, false));

                edit.setText("");//clear the text

                //notify that new data was added at a row:
                theAdapter.notifyDataSetChanged(); //at the end of ArrayList,

            }


        });

        listView.setOnItemLongClickListener((p, b, pos, id) -> {

            Message clicked = messages.get(pos);

            AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoomActivity.this);
            builder.setTitle("Question:")
                    .setMessage("Do you want to delete this:" + clicked.getMessageTyped())
                    .setNegativeButton("Negetive", (dialog, click1) -> {
                    })
                    .setPositiveButton("Positive", (dialog, click2) -> {
                        //actually delete something:
                        messages.remove(pos);
                        theAdapter.notifyDataSetChanged();
                    }).create().show();

            return false;
        });



    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getViewTypeCount(){
            return 2;
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Object getItem(int position) {
            return position % 2;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater li = getLayoutInflater();

                if (messages.get(position).send() == true) {
                    convertView = li.inflate(R.layout.activity_chatroom_left, parent, false);
                    TextView send = convertView.findViewById(R.id.sendMsg);
                    ImageView img = convertView.findViewById(R.id.sendIcon);
                    send.setText(messages.get(position).getMessageTyped());
                } else {
                    convertView = li.inflate(R.layout.activity_chatroom_right, parent, false);
                    TextView receive = convertView.findViewById(R.id.receiveMsg);
                    ImageView img = convertView.findViewById(R.id.receiveIcon);
                    receive.setText(messages.get(position).getMessageTyped());
                }

            return convertView;

        }

    }

    public class Message {
        String messageTyped;
        boolean send;

        public Message(String messageTyped, boolean send) {

            this.messageTyped = messageTyped;
            this.send = send;
        }

        public String getMessageTyped() {

            return messageTyped;
        }

        public boolean send(){
            return send;
        }

    }




}

