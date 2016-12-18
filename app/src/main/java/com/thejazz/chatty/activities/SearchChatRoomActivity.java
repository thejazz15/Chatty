package com.thejazz.chatty.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thejazz.chatty.R;
import com.thejazz.chatty.adapters.SearchChatRoomAdapter;
import com.thejazz.chatty.models.ChatRoom;
import com.thejazz.chatty.util.Endpoints;
import com.thejazz.chatty.util.MyApplication;
import com.thejazz.chatty.util.SimpleDividerItemDecoration;
import com.thejazz.chatty.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchChatRoomActivity extends AppCompatActivity {

    private TextView noChatsTv;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private JsonObjectRequest jsonObjectRequest;
    private SearchChatRoomAdapter myAdapter;
    private ArrayList<ChatRoom> chatRooms;
    private EditText searchEt;
    private Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_chat_room);
        chatRooms = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Find Groups");
        setSupportActionBar(toolbar);
        noChatsTv = (TextView) findViewById(R.id.no_chats_tv);
        progressBar = (ProgressBar) findViewById(R.id.load_progress);
        searchEt = (EditText) findViewById(R.id.search_et);
        searchBtn = (Button) findViewById(R.id.search_btn);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myAdapter = new SearchChatRoomAdapter(getApplicationContext(), chatRooms);
        recyclerView.setAdapter(myAdapter);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = searchEt.getText().toString().trim();
                if(!TextUtils.isEmpty(text)){
                    getChatRooms(text);
                }else{
                    Toast.makeText(getApplicationContext(), "Please enter text to search", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getChatRooms(String name) {
        progressBar.setVisibility(View.VISIBLE);
        String token = MyApplication.getInstance().getPrefManager().getToken();
        String url = Endpoints.CHAT_ROOM_SEARCH_URL.replace("_ID_",name)+token;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String message = response.getString("message");
                    JSONArray chatsrooms = response.getJSONArray("chat_rooms");
                    chatRooms = new ArrayList<ChatRoom>(chatsrooms.length());
                    for(int i=0; i<chatsrooms.length(); i++){
                        JSONObject room = chatsrooms.getJSONObject(i);
                        int id = room.getInt("id");
                        String name = room.getString("name");
                        ChatRoom chatRoom = new ChatRoom(id, name);
                        chatRooms.add(chatRoom);
                    }
                    progressBar.setVisibility(View.GONE);
                    if(chatRooms.size() == 0)
                        noChatsTv.setVisibility(View.VISIBLE);
                    else{
                        myAdapter.setChatRoomsList(chatRooms);
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

}
