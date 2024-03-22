package com.example.mychat;

import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mychat.adapter.ChatRecyclerAdapter;
import com.example.mychat.model.ChatMessageModel;
import com.example.mychat.model.ChatroomModel;
import com.example.mychat.model.GroupChatModel;
import com.example.mychat.model.UserModel;
import com.example.mychat.utill.AndroidUtil;
import com.example.mychat.utill.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatGroupActivity extends AppCompatActivity {

    ImageButton gbackBtn;
    UserModel otherUser;
    String groupChatId;
    GroupChatModel groupChatModel;
    ChatRecyclerAdapter adapter;

    EditText gmessageInput;
    ImageButton gsendMessageBtn;

    TextView groupchatname;
    RecyclerView recyclerView;
    ImageView gimageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group);
        gbackBtn = findViewById(R.id.g_back_btn);

        groupChatId = FirebaseUtil.getgroupChatId();

        gmessageInput = findViewById(R.id.g_chat_message_input);
        gsendMessageBtn = findViewById(R.id.g_message_send_btn);

        groupchatname = findViewById(R.id.group_chat_name);
        recyclerView = findViewById(R.id.chat_recycler_view);
        gimageView = findViewById(R.id.profile_pic_image_view);

        // image set from backend
        FirebaseUtil.getOtherProfilePicStorageRef(groupChatId+".jpg").getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri  = t.getResult();
                        AndroidUtil.setProfilePic(this,uri,gimageView);
                    }
                });

        groupchatname.setText("JEE Devloper");
        gsendMessageBtn.setOnClickListener(v -> {
            String message = gmessageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        });

        getOrCreateChatroomModel();
        setupChatRecyclerView();

// back to main chat activity
        gbackBtn.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void setupChatRecyclerView() {
        Query query = FirebaseUtil.getgroupChatMessageReference(groupChatId)
                .orderBy("timestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();
        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void getOrCreateChatroomModel() {
        FirebaseUtil.getGroupChatReference(groupChatId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                groupChatModel = task.getResult().toObject(GroupChatModel.class);
                if(groupChatModel == null){
                    // first time chat.
                    groupChatModel = new GroupChatModel(
                            groupChatId,
                            Timestamp.now(),
                            FirebaseUtil.currentUserId(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(groupChatId).set(groupChatModel);
                }
            }
        });
    }

    private void sendMessageToUser(String message) {

        groupChatModel.setLastMessageTimestamp(Timestamp.now());
        groupChatModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        groupChatModel.setLastMessage(message);
        FirebaseUtil.getGroupChatReference(groupChatId).set(groupChatModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtil.currentUserId(),Timestamp.now());
        FirebaseUtil.getgroupChatMessageReference(groupChatId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    gmessageInput.setText("");

                }
            }
        });
    }
}