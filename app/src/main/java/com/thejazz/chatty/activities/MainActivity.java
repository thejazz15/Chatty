package com.thejazz.chatty.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thejazz.chatty.R;
import com.thejazz.chatty.adapters.ChatRoomsAdapter;
import com.thejazz.chatty.models.ChatRoom;
import com.thejazz.chatty.util.Endpoints;
import com.thejazz.chatty.util.MyApplication;
import com.thejazz.chatty.util.SimpleDividerItemDecoration;
import com.thejazz.chatty.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private TextView noChatsTv;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private JsonObjectRequest jsonObjectRequest;
    private ChatRoomsAdapter myAdapter;
    private ArrayList<ChatRoom> chatRooms;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatRooms = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        noChatsTv = (TextView) findViewById(R.id.no_chats_tv);
        progressBar = (ProgressBar) findViewById(R.id.load_progress);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchChatRoomActivity.class));
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myAdapter = new ChatRoomsAdapter(getApplicationContext(), chatRooms);
        recyclerView.setAdapter(myAdapter);
        recyclerView.addOnItemTouchListener(new ChatRoomsAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new ChatRoomsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ChatRoom chatRoom = chatRooms.get(position);
                Intent intent = new Intent(MainActivity.this, ViewChatRoomActivity.class);
                intent.putExtra("chat_room_id", chatRoom.getId());
                intent.putExtra("name", chatRoom.getName());
                Log.v("ONCLICK", "Pos: "+position+ " CR id: "+chatRoom.getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        getChatRooms();
    }

    private void getChatRooms() {
        progressBar.setVisibility(View.VISIBLE);
        String token = MyApplication.getInstance().getPrefManager().getToken();
        String url = Endpoints.CHAT_ROOMS_URL+token;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String message = response.getString("message");
                    JSONArray chatsrooms = response.getJSONArray("chats");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                finish();
                MyApplication.getInstance().logout();
            case R.id.action_sync:
                getChatRooms();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}