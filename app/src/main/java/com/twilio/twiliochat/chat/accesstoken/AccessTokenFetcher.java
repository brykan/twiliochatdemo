package com.twilio.twiliochat.chat.accesstoken;

import android.content.Context;
import android.content.res.Resources;
import android.media.tv.TvInputService;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.application.SessionManager;
import com.twilio.twiliochat.application.TempUser;
import com.twilio.twiliochat.application.TwilioChatApplication;
import com.twilio.twiliochat.application.TwilioInfo;
import com.twilio.twiliochat.chat.listeners.TaskCompletionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.twilio.twiliochat.application.SessionManager.TAG;

public class AccessTokenFetcher {

  private Context context;

  public AccessTokenFetcher(Context context) {
    this.context = context;
  }

  public void fetch(final TaskCompletionListener<String, String> listener) {
    String requestUrl = SessionManager.getInstance().getUrl() + "/wp-json/intellichat/v1/get_token";
    final String requestHeader = "Bearer " + SessionManager.getInstance().getToken();
    Log.d(TAG, "fetch: myass");
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
//    JsonObjectRequest jsonObjReq =
//        new JsonObjectRequest(Request.Method.GET, requestUrl, null, new Response.Listener<JSONObject>() {
//
//          @Override
//          public void onResponse(JSONObject response) {
//            String token = null;
//            String nicename = null;
//            String email = null;
//            String displayname = null;
//            try {
//              token = response.getString("token");
//              nicename = response.getString("user_nicename");
//              email = response.getString("user_email");
//
//            } catch (JSONException e) {
//              e.printStackTrace();
//              listener.onError("Failed to parse token JSON response");
//            }
//            listener.onSuccess(token);
//          }
//        }, new Response.ErrorListener() {
//
//          @Override
//          public void onErrorResponse(VolleyError error) {
//
//            listener.onError("Failed to fetch token");
//          }
//
//        }){
//          @Override
//          public Map<String, String> getHeaders() throws AuthFailureError {
//            HashMap<String,String> headers = new HashMap();
//            headers.put("Authorization", requestHeader);
//            return headers;
//
//          }
//        };
//
//    jsonObjReq.setShouldCache(false);
//    TokenRequest.getInstance().addToRequestQueue(jsonObjReq);
    URL url;
    HttpURLConnection conn;
    try {
      Log.d(TAG, "fetch: "+requestUrl);
      Log.d(TAG, "fetch: "+requestHeader);
      url = new URL(requestUrl);
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Authorization", requestHeader);
      conn.setUseCaches(true);
      conn.setDoInput(true);
      StringBuilder sb = new StringBuilder();
      int HttpResult = conn.getResponseCode();
      if (HttpResult == HttpURLConnection.HTTP_OK) {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"));
        String line = null;
        while ((line = br.readLine()) != null) {
          sb.append(line + "\n");
        }
        br.close();
        Log.d("dicks","" + sb.toString());
        TwilioInfo twilioInfo = new Gson().fromJson(sb.toString(), TwilioInfo.class);
        SessionManager.getInstance().setTToken(twilioInfo.getToken());
        listener.onSuccess(twilioInfo.getToken());
      } else {
        Log.d("butt",conn.getResponseMessage());
        listener.onError("Failed to recieve token");
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
      listener.onError("Failed to recieve token");
    } catch (IOException e) {
      e.printStackTrace();
      listener.onError("Failed to recieve token");
    }
  }


  private String getStringResource(int id) {
    Resources resources = TwilioChatApplication.get().getResources();
    return resources.getString(id);
  }

}
