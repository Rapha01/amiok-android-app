package com.domain.amiok;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrackedEntriesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrackedEntriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrackedEntriesFragment extends Fragment {
    // TO DO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TO DO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private SQLiteDatabase dataBase;
    TrackedEntriesDbHelper dbHelper;

    public ArrayAdapter<Entry> entryAdapter;
    public ListView entryListView;

    public ArrayList entries = new ArrayList();

    JSONObject jsonObj;

    public TrackedEntriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrackedEntriesFragment.
     */
    // TO DO: Rename and change types and number of parameters
    public static TrackedEntriesFragment newInstance(String param1, String param2) {
        TrackedEntriesFragment fragment = new TrackedEntriesFragment();
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
        View v =  inflater.inflate(R.layout.fragment_tracked_entries, container, false);

        dbHelper = new TrackedEntriesDbHelper(getActivity());
        dataBase = dbHelper.getWritableDatabase();

        entryAdapter = new EntryAdapter(getActivity(), entries);

        entryListView = (ListView) v.findViewById(R.id.tracked_entry_results);
        entryListView.setAdapter(entryAdapter);

        return v;
    }

    public void updateTrackedEntries() {
        /*final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Searching. Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();*/

        String ids = "";
        ArrayList<Integer> trackedEntryIds = getTrackedEntryIds();
        for (int id : trackedEntryIds) {

        }
        JSONArray trackedEntryIdsJsArray = new JSONArray(trackedEntryIds);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(getString(R.string.server_domain))
                .appendPath(getString(R.string.subfolder_domain))
                .appendPath(getString(R.string.filename_domain))
                .appendQueryParameter("trackedEntries", trackedEntryIdsJsArray.toString())
                .appendQueryParameter("getTrackedEntries", "true");
        String requestUrl = builder.build().toString();
        Log.d("AAA", requestUrl);

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("AAA", " " + response);

                        entries.clear();

                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i=0;i<jsonArray.length();i++) {
                                jsonObj = jsonArray.getJSONObject(i);

                                Entry entry = new Entry(
                                        new Integer(jsonObj.getString("id")),
                                        jsonObj.getString("name"),
                                        new Integer(jsonObj.getString("frequency")),
                                        jsonObj.getString("email"),
                                        jsonObj.getString("message"),
                                        jsonObj.getString("lastupdate")
                                );

                                entries.add(entry);
                            }
                        } catch (JSONException e) {
                            Log.d("AAA", "JSONException: " + e);
                        }


                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                entryAdapter.notifyDataSetChanged();
                                //progressDialog.dismiss();

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
                        //progressDialog.dismiss();
                        Toast toast = Toast.makeText(getActivity(), "That didn't work!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private static boolean m_iAmVisible;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        m_iAmVisible = isVisibleToUser;

        if (m_iAmVisible) {
            updateTrackedEntries();
        }
    }

    // TO DO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onTrackedEntriesFragmentInteraction(uri);
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
        void onTrackedEntriesFragmentInteraction(Uri uri);
    }

    private ArrayList<Integer> getTrackedEntryIds() {
        ArrayList<Integer> trackedEntryIds = new ArrayList<Integer>();

        Cursor cursor = dataBase.query(
                TrackedEntriesContract.TrackedEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(TrackedEntriesContract.TrackedEntry.COLUMN_ENTRY_ID));
                trackedEntryIds.add(id);
            }
        } finally {
            cursor.close();
        }

        return trackedEntryIds;
    }

}
