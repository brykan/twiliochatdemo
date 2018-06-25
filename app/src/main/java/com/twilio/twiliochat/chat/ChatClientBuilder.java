package com.twilio.twiliochat.chat;

import android.content.Context;
import android.util.Log;

import com.twilio.chat.CallbackListener;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ErrorInfo;
import com.twilio.twiliochat.chat.listeners.TaskCompletionListener;

import static com.twilio.twiliochat.application.SessionManager.TAG;

public class ChatClientBuilder extends CallbackListener<ChatClient> {

  private Context context;
  private TaskCompletionListener<ChatClient, String> buildListener;

  public ChatClientBuilder(Context context) {
    this.context = context;
  }

  public void build(String token, final TaskCompletionListener<ChatClient, String> listener) {
    ChatClient.Properties props =
        new ChatClient.Properties.Builder()
            .setRegion("us1")
            .createProperties();

    this.buildListener = listener;
    ChatClient.create(context.getApplicationContext(),
        token,
        props,
        this);
  }


  @Override
  public void onSuccess(ChatClient chatClient) {
    Log.d(TAG, "onSuccess: Client sucessfully created");
    this.buildListener.onSuccess(chatClient);
  }

  @Override
  public void onError(ErrorInfo errorInfo) {
    this.buildListener.onError(errorInfo.getMessage());
  }
}
