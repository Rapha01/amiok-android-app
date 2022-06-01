package com.domain.amiok;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class EditEntryActivity extends AppCompatActivity {

    EditText nameInput;
    EditText passwordInput;
    EditText emailInput;
    EditText messageInput;
    EditText frequencyInput;
    ImageView statusImage;
    Intent intent;
    Switch trackSwitch;
    Entry entry;
    Context context;
    JSONObject jsonObj;

    private SQLiteDatabase dataBase;
    TrackedEntriesDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        context = this;
        intent = getIntent();

        dbHelper = new TrackedEntriesDbHelper(this);
        dataBase = dbHelper.getWritableDatabase();

        nameInput = (EditText) findViewById(R.id.input_name);
        trackSwitch = (Switch) findViewById(R.id.track_switch);
        passwordInput = (EditText) findViewById(R.id.input_password);
        emailInput = (EditText) findViewById(R.id.input_email);
        messageInput = (EditText) findViewById(R.id.input_message);
        frequencyInput = (EditText) findViewById(R.id.input_interval);
        statusImage = (ImageView) findViewById(R.id.status_image);

        // Set edit click event
        AppCompatButton btn_edit_entry = (AppCompatButton) findViewById(R.id.btn_edit_entry);
        btn_edit_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        // Set delete click event
        AppCompatButton btn_delete_entry = (AppCompatButton) findViewById(R.id.btn_delete_entry);
        btn_delete_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO servercall
            }
        });

        entry = (Entry) intent.getSerializableExtra("entry");

        refreshValuesById();

        trackSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    addToDatabase(entry.id);
                } else {
                    removeFromDatabase(entry.id);
                }
            }
        });
    }

    void update() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Retrieving Data. Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(this.getResources().getString(R.string.server_domain))
                .appendPath(this.getResources().getString(R.string.subfolder_domain))
                .appendPath(this.getResources().getString(R.string.filename_domain))
                .appendQueryParameter("id", new Integer(entry.id).toString())
                .appendQueryParameter("name", nameInput.getText().toString())
                .appendQueryParameter("password", passwordInput.getText().toString())
                .appendQueryParameter("frequency", frequencyInput.getText().toString())
                .appendQueryParameter("email", emailInput.getText().toString())
                .appendQueryParameter("message", messageInput.getText().toString())
                .appendQueryParameter("updateEntry", "true");
        String requestUrl = builder.build().toString();
        Log.d("AAA", " " + requestUrl);
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    String responseMsg;

                    @Override
                    public void onResponse(String response) {
                        Log.d("AAA", " " + response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if(jsonObject.has("error"))
                                responseMsg = jsonArray.getJSONObject(0).getString("error");
                            else
                                responseMsg = "Successfully updated";
                        } catch (JSONException e) {
                            Log.d("AAA", " " + "JSONException");
                        }

                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();

                                Toast toast = Toast.makeText(context, responseMsg, Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        };

                        Handler mHandler = new Handler();
                        mHandler.postDelayed(r, 1000);
                        refreshValuesById();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        //Toast toast = Toast.makeText(context, "That didn't work!", Toast.LENGTH_SHORT);
                        //toast.show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        trackSwitch.setChecked(true);

    }


    private void refreshValuesById() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Retrieving Data. Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(getString(R.string.server_domain))
                .appendPath(getString(R.string.subfolder_domain))
                .appendPath(getString(R.string.filename_domain))
                .appendQueryParameter("id", new Integer(entry.id).toString())
                .appendQueryParameter("getEntry", "true");
        String requestUrl = builder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("AAA", " " + response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            jsonObj = jsonArray.getJSONObject(0);

                            Entry entry = new Entry(
                                    new Integer(jsonObj.getString("id")),
                                    jsonObj.getString("name"),
                                    new Integer(jsonObj.getString("frequency")),
                                    jsonObj.getString("email"),
                                    jsonObj.getString("message"),
                                    jsonObj.getString("lastupdate")
                            );

                            Log.d("AAA", " " + entry.amiok);

                            nameInput.setText(entry.name);
                            emailInput.setText(entry.email);
                            messageInput.setText(entry.message);
                            frequencyInput.setText(((Integer) entry.interval).toString());

                            if(entry.amiok == -1)
                                statusImage.setImageResource(R.drawable.ic_action_notok);
                            else if(entry.amiok == 1)
                                statusImage.setImageResource(R.drawable.ic_action_ok);
                            else
                                statusImage.setImageResource(R.drawable.ic_action_noalarm);

                        } catch (JSONException e) {
                            Log.d("AAA", " " + "JSONException");
                        }

                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();

                                //Toast toast = Toast.makeText(getActivity(), "Successful", Toast.LENGTH_SHORT);
                                //toast.show();
                            }
                        };

                        Handler mHandler = new Handler();
                        mHandler.postDelayed(r, 1000);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        //Toast toast = Toast.makeText(context, "That didn't work!", Toast.LENGTH_SHORT);
                        //toast.show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        trackSwitch.setChecked(true);

    }

    private long addToDatabase(int id) {
        ContentValues cv = new ContentValues();
        cv.put(TrackedEntriesContract.TrackedEntry.COLUMN_ENTRY_ID, id);

        return dataBase.insert(TrackedEntriesContract.TrackedEntry.TABLE_NAME, null, cv);
    }

    private boolean removeFromDatabase(int id) {
        ContentValues cv = new ContentValues();
        cv.put(TrackedEntriesContract.TrackedEntry.COLUMN_ENTRY_ID, id);

        return dataBase.delete(TrackedEntriesContract.TrackedEntry.TABLE_NAME,
                TrackedEntriesContract.TrackedEntry.COLUMN_ENTRY_ID + "=" + new Integer(id).toString(), null) > 0;
    }

}
