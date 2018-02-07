package com.poweredbynazarovo.denkle.touchkeeper;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import android.widget.GridView;
import android.app.Activity;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.poweredbynazarovo.denkle.touchkeeper.barcode.BarcodeCaptureActivity;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//AppCompatActivity
//android.app.Application


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;

    private TextView mTextView;
    private String userIdVK="-1"; // used to get user ID in VK
    public String input;

    public int[] mThumbIds_g = {R.drawable.fb_g, R.drawable.rg_g, R.drawable.tw_g, R.drawable.ig_g, R.drawable.vk_g };
    public int[] mThumbIds; //initialize
    public int[] mThumbIds_c = {R.drawable.fb, R.drawable.rg, R.drawable.tw, R.drawable.ig, R.drawable.vk };
    //public int[] mThumbIds = {R.drawable.fb_g, R.drawable.rg_g, R.drawable.tw_g, R.drawable.ig_g, R.drawable.vk_g,R.drawable.fb_g, R.drawable.rg_g, R.drawable.tw_g, R.drawable.ig_g, R.drawable.vk_g  };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
        //VKSdk.initialize(Context applicationContext);

        //dm1003889
        mThumbIds=mThumbIds_g;
        if (VKSdk.isLoggedIn()) {
             mThumbIds[4]=mThumbIds_c[4];
        }



        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this, mThumbIds));
        mTextView = (TextView) findViewById(R.id.welcome);
        input = mTextView.getText().toString();

        // Create Listener for QR button
        final ImageButton buttonqr = (ImageButton) findViewById(R.id.button_qr);
        buttonqr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Context context = getApplicationContext();
                CharSequence text = "QR button pressed!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);

            }
        });

        // Create Listener for QR invite button
        final ImageButton buttoninv = (ImageButton) findViewById(R.id.button_invite);
        buttoninv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //prepare text to be encoded in QR-code

                JSONObject jsn = new JSONObject(); // We will store ids in that object
                //For VK
                VKAccessToken curToken = VKSdk.getAccessToken(); //access the token
                if (curToken!= null) { //check if token exists
                    userIdVK=curToken.userId; //get user ID from token
                    //write user ID to JSON object
                    try {
                        jsn.put("VK", userIdVK);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                input =jsn.toString(); // convert to string and write it to input which will be used to generate QR code


                // Code here executes on main thread after user presses button
                Context context = getApplicationContext();
                CharSequence text = "Invite button pressed!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                Intent intent = new Intent(MainActivity.this, QrGenerator.class);
                intent.putExtra("text", input); // put image data in Intent
                startActivity(intent); // start Intent

            }
        });


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // set an Intent to Another Activity
                Intent intent = new Intent(MainActivity.this, ZoomActivity.class);
                intent.putExtra("image", mThumbIds_c[position]); // put image data in Intent
                intent.putExtra("text", Integer.toString(position)); // put text data in Intent
                startActivity(intent); // start Intent

            }
        });





        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
        //@Override
        //  public void onClick(View view) {
            //        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //                .setAction("Action", null).show();
            //    }
            //});



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // get back the result after barcode scanning
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    String readResult=barcode.displayValue;
                    mTextView.setText(readResult);
                    //convert to JSON and send message in VK


                    try {
                        JSONObject jsn = new JSONObject(readResult);
                        String VK_ID = jsn.getString("VK");
                        //Send message to the recived ID
                        VKRequest request2 = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID, VK_ID, "message", "Da budet svet"));

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




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }





                } else mTextView.setText(R.string.no_barcode_captured);
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }

}
