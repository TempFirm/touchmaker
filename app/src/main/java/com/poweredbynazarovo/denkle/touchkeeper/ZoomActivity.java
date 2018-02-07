package com.poweredbynazarovo.denkle.touchkeeper;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;



public class ZoomActivity extends AppCompatActivity {
    private TextView mTextView;
    private ImageView selectedImage;
    private String input;
    private String userId="-1";
    private Boolean logState=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);
        selectedImage = (ImageView) findViewById(R.id.icon2); // init a ImageView
        mTextView=  (TextView) findViewById(R.id.username);
        Intent intent = getIntent(); // get Intent which we set from Previous Activity
        selectedImage.setImageResource(intent.getIntExtra("image", 0)); // get image from Intent and set it in ImageView
        final Button but_lo = (Button) findViewById(R.id.button_logout);

        but_lo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                VKSdk.logout();
                mTextView.setText(getText(R.string.log_state));

            }
        });


        // get id of the icon
        Bundle b = intent.getExtras();

        if(b!=null)
        {
            input =(String) b.get("text");
        }
        int position= Integer.parseInt(input);

        if (position == 4) {
            Context context = getApplicationContext();
            CharSequence text = "VK button pressed";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            //toast.show();

            // VK staff
            //https://github.com/VKCOM/vk-android-sdk/issues/250. Use link to read about authorization
            // make two requests

            //VKParameters parametrs=VKParameters.from(VKApiConst.USER_ID, "457452420", "text", "Parvarda", "follow", 1); // 457452420 is for Grin
            //VKRequest request2 = VKApi.friends().add(parametrs); //add to friends
            //Send message instead

            VKRequest request2 = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID, "457452420", "message", "probuem"));


            //VKSdk.logout(); //will log in the user and invalidate the current token
            logState=VKSdk.isLoggedIn(); // check if logged in
            if (logState == false) {
                //if not logged in initialize log in
                //String scope = "friends";
                String scope = "messages";
                VKSdk.login(this, scope);
            } else {
                VKAccessToken curToken = VKSdk.getAccessToken(); //access the token
                userId=curToken.userId;
                VKAccessToken curToken2 = VKSdk.getAccessToken(); //access the token
                VKParameters parametr=VKParameters.from("user_ids", userId);
                VKRequest request = VKApi.users().get(parametr); //get users

                //execute the request
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response)  {
                        super.onComplete(response);
                        //Do complete stuff

                        try {
                            //process the response to get the name of the user
                            JSONArray s = response.json.getJSONArray("response");
                            JSONObject jsn = s.getJSONObject(0);
                            String name = jsn.getString("first_name") + " " + jsn.getString("last_name");
                            mTextView.setText("You are logged in as " + name);
                            //s.toString();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Context context = getApplicationContext();
                        CharSequence text = "Got response from API";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                    }
                    @Override
                    public void onError(VKError error) {
                        //Do error stuff
                        Context context = getApplicationContext();
                        CharSequence text = "Got error API";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();


                    }
                    @Override
                    public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                        //I don't really believe in progress
                        Context context = getApplicationContext();
                        CharSequence text = "Attempt failed from API";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });




                //token != null && !token.isExpired();

            }

















            /*
            request2.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response)  {
                    super.onComplete(response);
                    //Do complete stuff
                    try {
                        JSONArray s = response.json.getJSONArray("response");
                        s.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast toast = Toast.makeText(getApplicationContext(), "Got response from FRIEND", Toast.LENGTH_SHORT);
                    toast.show();

                }
                @Override
                public void onError(VKError error) {
                    //Do error stuff
                    Toast toast = Toast.makeText(getApplicationContext(), "Got ERROR from FRIEND", Toast.LENGTH_SHORT);
                    toast.show();

                }
                @Override
                public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                    //I don't really believe in progress
                }
            });


            */





            //check than login was correct





        }



    }
}