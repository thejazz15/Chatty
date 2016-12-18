package com.thejazz.chatty.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thejazz.chatty.R;
import com.thejazz.chatty.adapters.ChatRoomsAdapter;
import com.thejazz.chatty.adapters.ChatThreadAdapter;
import com.thejazz.chatty.models.ChatRoom;
import com.thejazz.chatty.models.Message;
import com.thejazz.chatty.models.User;
import com.thejazz.chatty.util.Endpoints;
import com.thejazz.chatty.util.MyApplication;
import com.thejazz.chatty.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ViewChatRoomActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatThreadAdapter myAdapter;
    private JsonObjectRequest jsonObjectRequest;
    private EditText msgText;
    private Button sendBtn;
    private int chatRoomId, userId;
    private String chatRoomName;
    private ArrayList<Message> chatRoomMessages;
    private TextView noChatsTv;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chat_room);
        chatRoomMessages = new ArrayList<>();
        userId = MyApplication.getInstance().getPrefManager().getUser().getId();
        if(getIntent().hasExtra("chat_room_id") && getIntent().hasExtra("name")){
            chatRoomId = getIntent().getIntExtra("chat_room_id", 0);
            chatRoomName = getIntent().getStringExtra("name");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(chatRoomName);
        setSupportActionBar(toolbar);
        msgText = (EditText) findViewById(R.id.message_et);
        sendBtn = (Button) findViewById(R.id.btn_send);
        noChatsTv = (TextView) findViewById(R.id.no_chats_tv);
        progressBar = (ProgressBar) findViewById(R.id.load_progress);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        myAdapter = new ChatThreadAdapter(getApplicationContext(), userId, chatRoomMessages);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myAdapter);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(msgText.getText().toString().trim() != null){
                        sendMessage();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Please enter your message", Toast.LENGTH_SHORT).show();
            }
        });

        getMessages();
    }

    private void sendMessage() {
        JSONObject param = new JSONObject();
        try {
            param.put("chat_room_id", chatRoomId);
            param.put("message", msgText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msgText.getText().clear();
        Toast.makeText(getApplicationContext(), "Sending...", Toast.LENGTH_SHORT).show();
        String token = MyApplication.getInstance().getPrefManager().getToken();
        String url = Endpoints.MESSAGE_CREATE_URL+token;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    JSONObject msg_det = response.getJSONObject("message_details");
                    int id = msg_det.getInt("id");
                    String message = msg_det.getString("message");
                    String created_at = msg_det.getString("created_at");
                    User user = MyApplication.getInstance().getPrefManager().getUser();
                    Message msg = new Message(id, message, created_at, user);
                    chatRoomMessages.add(msg);
                    myAdapter.notifyDataSetChanged();
                    if (myAdapter.getItemCount() > 1) {
                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, myAdapter.getItemCount() - 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = Utility.VolleyErrorMessage(error);
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonObjectRequest.setRetryPolicy(policy);
        MyApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void getMessages() {
        progressBar.setVisibility(View.VISIBLE);
        String token = MyApplication.getInstance().getPrefManager().getToken();
        String url = Endpoints.CHAT_ROOM_INFO_URL.replace("_ID_", Integer.toString(chatRoomId))+token;
        Log.v("URL MSGS", url);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int id, senderId;
                    String message, createdAt, senderName, senderEmail, senderUsername;
                    User user;
                    JSONObject chatRoom = response.getJSONObject("chat_room");
                    JSONArray messages = chatRoom.getJSONArray("messages");
                    chatRoomMessages = new ArrayList<Message>(messages.length());
                    for(int i=0; i<messages.length();i++){
                        JSONObject msg = messages.getJSONObject(i);
                        id = msg.getInt("id");
                        message = msg.getString("message");
                        createdAt = msg.getString("created_at");
                        JSONObject sender = msg.getJSONObject("view_user");
                        senderId = sender.getInt("id");
                        senderName = sender.getString("name");
                        senderEmail = sender.getString("email");
                        senderUsername = sender.getString("username");
                        user = new User(senderId, senderName, senderEmail, senderUsername);
                        Message m = new Message(id, message, createdAt, user);
                        chatRoomMessages.add(m);
                    }
                    progressBar.setVisibility(View.GONE);
                    if(chatRoomMessages.size() == 0)
                        noChatsTv.setVisibility(View.VISIBLE);
                    else{
                        myAdapter.setMessagesList(chatRoomMessages);
                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, myAdapter.getItemCount() - 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                String errorMsg = Utility.VolleyErrorMessage(error);
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sync:
                getMessages();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
