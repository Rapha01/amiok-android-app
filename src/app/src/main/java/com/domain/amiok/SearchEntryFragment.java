package com.domain.amiok;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.AppCompatButton;
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
 * {@link SearchEntryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchEntryFragment extends Fragment {
    // TO DO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TO DO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ArrayAdapter<Entry> entryAdapter;
    public ListView entryListView;

    public ArrayList entries = new ArrayList();
    public EditText input_searchName;

    JSONObject jsonObj;

    public SearchEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchEntryFragment.
     */
    // TO DO: Rename and change types and number of parameters
    public static SearchEntryFragment newInstance(String param1, String param2) {
        SearchEntryFragment fragment = new SearchEntryFragment();
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
        View v = inflater.inflate(R.layout.fragment_search_entry, container, false);

        // Set activate click event
        AppCompatButton btn_search = (AppCompatButton) v.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchEntries(v);
            }
        });

        input_searchName = (EditText) v.findViewById(R.id.search_name);
        entryAdapter = new EntryAdapter(getActivity(), entries);

        entryListView = (ListView) v.findViewById(R.id.search_results);
        entryListView.setAdapter(entryAdapter);

        return v;
    }

    void searchEntries(View v) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Searching. Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String search_name = input_searchName.getText().toString();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(getString(R.string.server_domain))
                .appendPath(getString(R.string.subfolder_domain))
                .appendPath(getString(R.string.filename_domain))
                .appendQueryParameter("name", search_name)
                .appendQueryParameter("searchEntry", "true");
        String requestUrl = builder.build().toString();

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
            mListener.onSearchEntryFragmentInteraction(uri);
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
        void onSearchEntryFragmentInteraction(Uri uri);
    }
}
