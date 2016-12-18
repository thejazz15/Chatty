package com.thejazz.chatty.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thejazz.chatty.R;
import com.thejazz.chatty.models.Message;

import java.util.ArrayList;

import static com.thejazz.chatty.util.Utility.getTimeStamp;

/**
 * Created by TheJazz on 16/12/16.
 */

public class ChatThreadAdapter extends RecyclerView.Adapter<ChatThreadAdapter.MyViewHolder> {

    private Context mContext;
    private int currentUserId;
    private ArrayList<Message> chatRoomMessages;
    private int SELF = 100;

    public ChatThreadAdapter(Context context, int currentUserId, ArrayList<Message> chatRoomMessages){
        mContext = context;
        this.currentUserId = currentUserId;
        this.chatRoomMessages = chatRoomMessages;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = chatRoomMessages.get(position);
        if (message.getUser().getId() == currentUserId) {
            return SELF;
        }
        return position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);
        } else {
            // others message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_other, parent, false);
        }


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message message = chatRoomMessages.get(position);
        holder.msg.setText(message.getMessage());
        String timestamp = getTimeStamp(message.getCreatedAt());
        if (message.getUser().getName() != null)
            timestamp = message.getUser().getName() + ", " + timestamp;

        holder.timestamp.setText(timestamp);
    }

    @Override
    public int getItemCount() {
        return chatRoomMessages == null ? 0 : chatRoomMessages.size();
    }

    public void setMessagesList(ArrayList<Message> chatRoomMessages) {
        this.chatRoomMessages = new ArrayList<Message>(chatRoomMessages.size());
        this.chatRoomMessages = chatRoomMessages;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView msg, timestamp;
        public MyViewHolder(View itemView) {
            super(itemView);
            msg = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        }
    }
}
