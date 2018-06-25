package com.twilio.twiliochat.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.twilio.twiliochat.chat.accesstoken.TokenRequest;
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


public class SessionManager {
  public static final String TAG = "dicks";
  public static final String KEY_USERNAME = "username";
  public static final String KEY_URL = "url";
  public static final String KEY_PASSWORD = "password";
  public static final String KEY_EMAIL = "email";
  public static final String KEY_NICENAME = "nicename";
  public static final String KEY_DISPLAYNAME = "displayname";
  public static final String KEY_TOKEN = "authtoken";
  public static final String KEY_TTOKEN = "twiltoken";

  private static final String PREF_NAME = "TWILIOCHAT";
  private static final String IS_LOGGED_IN = "IsLoggedIn";
  private static SessionManager instance =
      new SessionManager(TwilioChatApplication.get().getApplicationContext());
  SharedPreferences pref;
  Editor editor;
  Context context;
  int PRIVATE_MODE = 0;

  private SessionManager(Context context) {
    this.context = context;
    pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    editor = pref.edit();
  }

  public static SessionManager getInstance() {
    return instance;
  }

  public void createLoginSession(final String username,final String mUrl,final String password, final TaskCompletionListener<String,String> listener) {
    Map<String, String> params = new HashMap<>();
    Log.d(TAG, "createLoginSession: tryna make a change "+username +" " + mUrl + " " + password);
    params.put("username", username);
    params.put("password", password);
    JSONObject obj = new JSONObject(params);
    URL url;
    HttpURLConnection conn;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    StrictMode.setThreadPolicy(policy);

    try {
      JSONObject auth = new JSONObject();
      auth.put("username", username);
      auth.put("password",password);
      url = new URL(mUrl+ "/wp-json/jwt-auth/v1/token");
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");

      conn.setUseCaches(true);
      conn.setDoInput(true);
      conn.setDoOutput(true);

      OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
      writer.write(auth.toString());
      writer.flush();

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
        TempUser tempUser = new Gson().fromJson(sb.toString(), TempUser.class);
        editor.putBoolean(IS_LOGGED_IN, true);
                editor.putString(KEY_USERNAME, username);
                editor.putString(KEY_URL,mUrl);
                editor.putString(KEY_PASSWORD,password);
                // commit changes
                editor.commit();
        listener.onSuccess(tempUser.getToken());
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
    } catch (JSONException e) {
      e.printStackTrace();
      listener.onError("Failed to recieve token");
    }


//  @Override
//  protected void onPostExecute(final Boolean success) {
//
//    if (success){
//      Log.d(TAG, "onPostExecute: succ");
//
//    } else {
//      Log.d(TAG, "onPostExecute: fail");
//    }
//  }
//
//  @Override
//  protected void onCancelled() {
//    Log.d(TAG, "onCancelled: cance");
//  }
//    StringRequest jsonObjReq =
//            new StringRequest(Request.Method.POST, url + "/wp-json/jwt-auth/v1/token", new Response.Listener<String>() {
//            String token = null;
//              @Override
//              public void onResponse(String response) {
////                try {
//                  Log.d(TAG, "onResponse: "+response);
//                  //token = response.getString("token");
//
////                } catch (JSONException e) {
////                  e.printStackTrace();
////                  listener.onError("Failed to parse token JSON response");
////                }
//                listener.onSuccess(token);
//                editor.putBoolean(IS_LOGGED_IN, true);
//                editor.putString(KEY_USERNAME, username);
//                editor.putString(KEY_URL,url);
//                editor.putString(KEY_PASSWORD,password);
//                // commit changes
//                editor.commit();
//              }
//            }, new Response.ErrorListener() {
//
//              @Override
//              public void onErrorResponse(VolleyError error) {
//
//                listener.onError("Failed to fetch token");
//              }
//
//            }){
//
//              @Override
//              public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String,String> headers = new HashMap();
//                headers.put("Content-Type", "application/json");
//                return headers;
//              }
//              @Override
//              public Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("username", username);
//                params.put("password", password);
//                return params;
//              }
//            };

//    jsonObjReq.setShouldCache(true);
//    Log.d(TAG, "createLoginSession: "+jsonObjReq.getBodyContentType());
//    TokenRequest.getInstance().addToRequestQueue(jsonObjReq);
//    UserLoginTask loginTask = new UserLoginTask(url,username,password);
//    loginTask.execute((Void) null);

  }

  public HashMap<String, String> getUserDetails() {
    HashMap<String, String> user = new HashMap<String, String>();
    user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

    return user;
  }

  public String getUsername() {
    return pref.getString(KEY_USERNAME, null);
  }
  public String getUrl() {
    return pref.getString(KEY_URL, null);

  }
  public String getPassword(){
    return pref.getString(KEY_PASSWORD,null);
  }
  public void setToken(String token){
    editor.putString(KEY_TOKEN,token);
    editor.commit();
  }
  public String getToken(){
    Log.d(TAG, "getToken: getting token");
    return pref.getString(KEY_TOKEN,null);
  }
  public void setTToken(String token){
    editor.putString(KEY_TTOKEN,token);
    editor.commit();
  }
  public String getTToken(){
    Log.d(TAG, "getTTwiloken: getting token");
    return pref.getString(KEY_TTOKEN,null);
  }
  public void logoutUser() {
    String tempurl = getUrl();
    editor = editor.clear();
    editor.commit();
    editor.putString(KEY_URL,tempurl);
    editor.commit();
  }

  public boolean isLoggedIn() {
    return pref.getBoolean(IS_LOGGED_IN, false);
  }
}

