package com.example.mychat.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;
import com.example.mychat.model.ChatMessageModel;
import com.example.mychat.utill.AndroidUtil;
import com.example.mychat.utill.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {


    Context context;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        // Current User
        if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.leftprofilePic.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(model.getMessage());
//            holder.chatTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
            holder.chatTimestamp.setText(FirebaseUtil.androdiTimestampToString(model.getTimestamp()));
            // firebase Current user image
            FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
//                   AndroidUtil.setProfilePic(context,uri,holder.rightprofilePic);
                    // set image using Picasso.
                    AndroidUtil.setGroupChatPic(uri, holder.rightprofilePic);
//                   Picasso.get().load(uri).transform(new CropCircleTransformation()).into(holder.rightprofilePic);
//                   holder.rightChatLayout.setVisibility(View.VISIBLE);
//                   holder.rightChatTextview.setText(model.getMessage());
                }
            });

        } else {
            //Other User
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.rightprofilePic.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(model.getMessage());
            // set timestamp for all chat
//            holder.chatTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
            holder.chatTimestamp.setText(FirebaseUtil.androdiTimestampToString(model.getTimestamp()));
            // get user image from fire storage for other user
            FirebaseUtil.getOtherProfilePicStorageRef(model.getSenderId()).getDownloadUrl().addOnCompleteListener(task ->
            {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    AndroidUtil.setProfilePic(context, uri, holder.leftprofilePic);
                }

            });

        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cha_message_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextview, rightChatTextview, chatTimestamp;
        ImageView leftprofilePic, rightprofilePic;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            leftprofilePic = itemView.findViewById(R.id.leftProfilePic);
            rightprofilePic = itemView.findViewById(R.id.rightProfilePic);
            chatTimestamp = itemView.findViewById(R.id.chat_timestamp);
        }
    }
}
