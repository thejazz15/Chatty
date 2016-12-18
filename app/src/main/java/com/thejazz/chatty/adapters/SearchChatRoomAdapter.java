package com.thejazz.chatty.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thejazz.chatty.R;
import com.thejazz.chatty.models.ChatRoom;
import com.thejazz.chatty.models.Message;
import com.thejazz.chatty.util.Endpoints;
import com.thejazz.chatty.util.MyApplication;
import com.thejazz.chatty.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.thejazz.chatty.R.id.timestamp;
import static com.thejazz.chatty.util.Utility.getTimeStamp;

/**
 * Created by TheJazz on 17/12/16.
 */

public class SearchChatRoomAdapter extends RecyclerView.Adapter<SearchChatRoomAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<ChatRoom> chatRooms;
    private JsonObjectRequest jsonObjectRequest;

    public SearchChatRoomAdapter(Context context, ArrayList<ChatRoom> chatRooms){
        mContext = context;
        this.chatRooms = chatRooms;
    }

    @Override
    public SearchChatRoomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.search_chat_room_row, parent, false);
        return new SearchChatRoomAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SearchChatRoomAdapter.MyViewHolder holder, int position) {
        final ChatRoom chatRoom = chatRooms.get(position);
        holder.name.setText(chatRoom.getName());
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addChatRoom(chatRoom.getId());
                holder.addBtn.setImageResource(R.mipmap.ic_check_circle_black_24dp);
            }
        });
    }

    private void addChatRoom(int id) {
        String token = MyApplication.getInstance().getPrefManager().getToken();
        String url = Endpoints.CHAT_ROOM_JOIN_URL.replace("_ID_",Integer.toString(id))+token;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String message = response.getString("message");
                    Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = Utility.VolleyErrorMessage(error);
                Toast.makeText(mContext.getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return chatRooms == null ? 0 : chatRooms.size();
    }

    public void setChatRoomsList(ArrayList<ChatRoom> chatRooms) {
        this.chatRooms = new ArrayList<ChatRoom>(chatRooms.size());
        this.chatRooms = chatRooms;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        ImageButton addBtn;
        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            addBtn = (ImageButton) itemView.findViewById(R.id.add_btn);
        }
    }
}
