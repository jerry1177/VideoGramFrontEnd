package com.example.videogramfrontend;

import android.app.ActionBar;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText SearchBar;
    ListView UserListView;
    ListView UserVideosListView;
    TextView UserName;
    private String url;
    ArrayList<String> UserList;
    ArrayList<Map<String, String>> UserVideos;
    ArrayAdapter<String> UserListAdapter;
    UserVideosCustomAdapter UserVideosAdapter;


    RelativeLayout Video_Page;
    TextView Video_Page_Item_1;
    VideoView Video_Page_Item_2;




    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
        //url = "http://" + BuildConfig.Backend + ":3000/find/users";
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchBar = (EditText) view.findViewById(R.id.SearchBar);
        UserListView = (ListView) view.findViewById(R.id.UserListView);
        UserVideosListView = (ListView) view.findViewById(R.id.UserVideosListView);

        Video_Page_INIT(view);
        Video_Page_HIDE();


        UserList = new ArrayList<String>();
        UserVideos = new ArrayList<Map<String, String>>();
        UserListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, UserList);
        UserListView.setAdapter(UserListAdapter);

        UserVideosAdapter = new UserVideosCustomAdapter();
        UserVideosListView.setAdapter(UserVideosAdapter);
        UserVideosListView.setVisibility(View.INVISIBLE);




        SearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (UserListView.getVisibility() == View.INVISIBLE) {
                    UserListView.setVisibility(View.VISIBLE);
                }
                UserListAdapter.getFilter().filter(s.toString());
                UserListAdapter.notifyDataSetChanged();
                //SearchBar.setText(String.valueOf(adapter.getCount()));
                UserListView.invalidateViews();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        SearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserListView.setVisibility(View.VISIBLE);
                UserVideosListView.setVisibility(View.INVISIBLE);
            }
        });
        UserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchBar.setText(UserListAdapter.getItem(position).toString());
                UserListView.setVisibility(View.INVISIBLE);
                UserVideosListView.setVisibility(View.VISIBLE);
                GetChosenUserId_AndPopulateVideos(UserListAdapter.getItem(position).toString());
            }
        });
        UserVideosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //UserVideosListView.setVisibility(View.INVISIBLE);
                int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 225, getResources().getDisplayMetrics());
                UserVideosListView.getLayoutParams().height = dp;
                UserVideosListView.requestLayout();
                Video_Page_SHOW();
                Video_Page_SET_ITEMS(UserVideos.get(position));
                Video_Page_START();
            }
        });


        PopulateUsersList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        ViewManagerSingleton.GetSingleton().setCurrentView(CurrentView.SEARCH);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }















    private void Video_Page_INIT(View view) {
        Video_Page = (RelativeLayout) view.findViewById(R.id.VideoRelativeView);
        Video_Page_Item_1 = (TextView) view.findViewById(R.id.VideoRelativeView1);
        Video_Page_Item_2 = (VideoView) view.findViewById(R.id.VideoRelativeView2);
    }
    private void Video_Page_HIDE() {
        Video_Page.setVisibility(View.INVISIBLE);
        Video_Page_Item_1.setVisibility(View.INVISIBLE);
        Video_Page_Item_2.setVisibility(View.INVISIBLE);
    }
    private void Video_Page_SHOW() {
        Video_Page.setVisibility(View.VISIBLE);
        Video_Page_Item_1.setVisibility(View.VISIBLE);
        Video_Page_Item_2.setVisibility(View.VISIBLE);
    }
    private void Video_Page_SET_ITEMS(Map<String, String> Dict) {
        String Description = SearchBar.getText().toString() + ": " + Dict.get("Description").toString();
        Video_Page_Item_1.setText(Description);
        Video_Page_Item_2.setVideoURI(Uri.parse(Dict.get("Video_Link").toString()));
    }
    private void Video_Page_START() {
        Video_Page_Item_2.start();
    }







    private void PopulateUsersList() {
        JSONObject params = new JSONObject();
        url = "http://" + BuildConfig.Backend + ":3000/find/users";
        // Make JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // if valid credentials
                            if(response.getString("message").equals("success"))
                            {
                                JSONArray users = response.getJSONArray("result");
                                //SearchBar.setText(String.valueOf(users.length()));
                                for (int i = 0; i < users.length(); i++) {
                                    JSONObject element = users.getJSONObject(i);
                                    UserList.add(element.getString("Username"));
                                }
                                UserListAdapter.notifyDataSetChanged();
                                //SearchBar.setText(String.valueOf(adapter.getCount()));
                                UserListView.invalidateViews();
                            }
                            else if (response.get("message").equals("failed")) {
                                Toast.makeText(getContext(), response.getString("result"), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            //Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "retrieve JSON OBJECT ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(getContext(),error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        // Send request by adding it to the request que
        SingletonRequestQueue.getInstance(getContext()).getRequestQueue().add(jsonObjectRequest);
    }


    private void GetChosenUserId_AndPopulateVideos(final String Username) {
        JSONObject params = new JSONObject();
        url = "http://" + BuildConfig.Backend + ":3000/find/users";
        // Make JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // if valid credentials
                            if(response.getString("message").equals("success"))
                            {
                                JSONArray users = response.getJSONArray("result");
                                //SearchBar.setText(String.valueOf(users.length()));
                                for (int i = 0; i < users.length(); i++) {
                                    JSONObject element = users.getJSONObject(i);
                                    if (element.getString("Username").equals(Username))
                                    {
                                        PopulateUserVideosList(element.getInt("User_Id"));
                                        break;
                                    }
                                }
                            }
                            else if (response.get("message").equals("failed")) {
                                Toast.makeText(getContext(), response.getString("result"), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            //Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "retrieve JSON OBJECT ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(getContext(),error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        // Send request by adding it to the request que
        SingletonRequestQueue.getInstance(getContext()).getRequestQueue().add(jsonObjectRequest);
    }

    private void PopulateUserVideosList(int User_Id_Chosen) {
        JSONObject params = new JSONObject();
        try {
            params.put("User_Id", User_Id_Chosen);
        } catch (JSONException e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        url = "http://" + BuildConfig.Backend + ":3000/get/all/user/video/data/";
        // Make JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // if valid credentials
                            if(response.getString("message").equals("success"))
                            {
                                JSONArray users = response.getJSONArray("result");
                                //SearchBar.setText(String.valueOf(users.length()));
                                for (int i = 0; i < users.length(); i++) {
                                    JSONObject element = users.getJSONObject(i);
                                    Map<String, String> dict = new HashMap<String, String>();
                                    dict.put("Video_Id", element.getString("Video_Id"));
                                    dict.put("Video_Link", element.getString("Video_Link"));
                                    dict.put("Description", element.getString("Description"));
                                    dict.put("Upload_Date", element.getString("Upload_Date"));
                                    dict.put("Location", element.getString("Location"));
                                    dict.put("User_Id", element.getString("User"));
                                    UserVideos.add(dict);
                                }
                                UserVideosAdapter.notifyDataSetChanged();
                                //SearchBar.setText(String.valueOf(adapter.getCount()));
                                UserVideosListView.invalidateViews();
                            }
                            else if (response.get("message").equals("failed")) {
                                Toast.makeText(getContext(), response.getString("result"), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            //Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "retrieve JSON OBJECT ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(getContext(),error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        // Send request by adding it to the request que
        SingletonRequestQueue.getInstance(getContext()).getRequestQueue().add(jsonObjectRequest);
    }























    class UserVideosCustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return UserVideos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.listview_custom, null);
            ImageView thumbnail = (ImageView) view.findViewById(R.id.Thumbnail_Image);
            TextView Description = (TextView) view.findViewById(R.id.Video_Description);
            TextView Date = (TextView) view.findViewById(R.id.Video_Date);
            //TextView Location = (TextView) view.findViewById(R.id.Video_Location);
            //thumbnail.setImageResource(R.mipmap.error_icon);
            Description.setText(UserVideos.get(position).get("Description").toString());
            Date.setText(UserVideos.get(position).get("Upload_Date").toString());
            //Location.setText(UserVideos.get(position).get("Location").toString());

            return view;
        }
    }

}





