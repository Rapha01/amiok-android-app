package com.domain.amiok;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.ProgressDialog;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewEntryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewEntryFragment extends Fragment {
    // TO DO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TO DO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EditText input_name;
    private EditText input_password;
    private EditText input_frequency;
    private EditText input_email;
    private EditText input_message;
    private Boolean addEntrySuccess = false;

    private SQLiteDatabase dataBase;
    TrackedEntriesDbHelper dbHelper;

    public NewEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewEntryFragment.
     */
    // TO DO: Rename and change types and number of parameters
    public static NewEntryFragment newInstance(String param1, String param2) {
        NewEntryFragment fragment = new NewEntryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_entry, container, false);

        // Set activate click event
        AppCompatButton btn_add_entry = (AppCompatButton) v.findViewById(R.id.btn_add_entry);
        btn_add_entry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addEntry(v);
            }
        });

        dbHelper = new TrackedEntriesDbHelper(getActivity());
        dataBase = dbHelper.getWritableDatabase();

        input_name = (EditText) v.findViewById(R.id.input_name);
        input_password = (EditText) v.findViewById(R.id.input_password);
        input_frequency = (EditText) v.findViewById(R.id.input_frequency);
        input_email = (EditText) v.findViewById(R.id.input_email);
        input_message = (EditText) v.findViewById(R.id.input_message);

        return v;
    }

    public void addEntry(View v) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Adding Entry. Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String name = input_name.getText().toString();
        String password = input_password.getText().toString();
        String frequency = input_frequency.getText().toString();
        String email = input_email.getText().toString();
        String message = input_message.getText().toString();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(getString(R.string.server_domain))
                .appendPath(getString(R.string.subfolder_domain))
                .appendPath(getString(R.string.filename_domain))
                .appendQueryParameter("name", name)
                .appendQueryParameter("password", password)
                .appendQueryParameter("frequency", frequency)
                .appendQueryParameter("email", email)
                .appendQueryParameter("message", message)
                .appendQueryParameter("newEntry", "true");
        String requestUrl = builder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        input_name.setText("");
                        input_password.setText("");
                        input_frequency.setText("");
                        input_email.setText("");
                        input_message.setText("");

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            input_message.setText("Response is: " + response);

                            int id = new Integer(jsonArray.getJSONObject(0).getString("id"));
                            addToDatabase(id);

                        } catch (JSONException e) {
                            Log.d("AAA", " " + "JSONException");
                        }

                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();

                                // TODO check retrieved id and change toast on error
                                Toast toast = Toast.makeText(getActivity(), "Successful", Toast.LENGTH_SHORT);
                                toast.show();
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
                        Toast toast = Toast.makeText(getActivity(), "That didn't work!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    // TO DO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onNewEntryFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TO DO: Update argument type and name
        void onNewEntryFragmentInteraction(Uri uri);
    }

    private long addToDatabase(int id) {
        ContentValues cv = new ContentValues();
        cv.put(TrackedEntriesContract.TrackedEntry.COLUMN_ENTRY_ID, id);

        return dataBase.insert(TrackedEntriesContract.TrackedEntry.TABLE_NAME, null, cv);
    }
}
