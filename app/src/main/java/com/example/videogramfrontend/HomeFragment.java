package com.example.videogramfrontend;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // UI Elements
    RecyclerView RecView;
    Button MyVideosButton;
    Button MyLikesButton;
    VideoView VideoPlayer;

    // Data Arrays
    ArrayList<Map<String, String>> MyVideos;
    ArrayList<Map<String, String>> MyLikes;
    ArrayList<Map<String, String>> Users;

    // Adapters
    MyRecyclerViewAdapter MyVideosAdapter;
    MyRecyclerViewAdapter MyLikesAdapter;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        ((MainActivity)getActivity()).showNavigationBar();

    }

    @Override
    public void onResume() {
        super.onResume();
        ViewManagerSingleton viewManager = ViewManagerSingleton.GetSingleton();
        viewManager.setCurrentView(CurrentView.HOME);
        // if the user wants to go to search page then redirect
        if (viewManager.getToView() == ToView.SEARCH)
            Navigation.findNavController(getView()).navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment());
        // if the user wants to go to upload video page then redirect
        if (viewManager.getToView() == ToView.UPLOAD)
            Navigation.findNavController(getView()).navigate(HomeFragmentDirections.actionHomeFragmentToUploadVideoFragment());

        viewManager.setToView(ToView.LOGIN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity main = (MainActivity) getActivity();
        main.getSupportActionBar().setTitle("Home");

        RecView = (RecyclerView) view.findViewById(R.id.RecView);
        MyVideosButton = (Button) view.findViewById(R.id.MyVideos);
        MyLikesButton = (Button) view.findViewById(R.id.MyLikes);
        VideoPlayer = (VideoView) view.findViewById(R.id.videoPlayer);

        MyVideos = new ArrayList<Map<String, String>>();
        MyLikes = new ArrayList<Map<String, String>>();
        Users = new ArrayList<Map<String, String>>();

        // set up the RecyclerView
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecView.setLayoutManager(horizontalLayoutManager);
        RecView.addItemDecoration(new DividerItemDecoration(RecView.getContext(), DividerItemDecoration.HORIZONTAL));



        MyLikesAdapter = new MyRecyclerViewAdapter(getContext(), MyLikes);
        MyLikesAdapter.setClickListener(this);
        MyVideosAdapter = new MyRecyclerViewAdapter(getContext(), MyVideos);
        MyVideosAdapter.setClickListener(this);

        MyVideosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyVideos.clear();
                GetMyVideos();
                RecView.setAdapter(MyVideosAdapter);
            }
        });
        MyLikesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLikes.clear();
                GetMyVideoLikes();
                RecView.setAdapter(MyLikesAdapter);
            }
        });




        // INIT
        LoadUserList();
        GetMyVideos();
        RecView.setAdapter(MyVideosAdapter);

    }

    int PrevSelectedView;

    @Override
    public void onItemClick(View view, int position) {
        RecView.scrollToPosition(position);
        Video_STOP();
        if (RecView.getAdapter() == MyVideosAdapter) {
            Video_SETURL(MyVideosAdapter.getItem(position).get("Video_Link"));
            Video_START();
        }
        else if (RecView.getAdapter() == MyLikesAdapter) {
            Video_SETURL(MyLikesAdapter.getItem(position).get("Video_Link"));
            Video_START();
        }
    }

    private void Video_SETURL(String Link) {
        VideoPlayer.setVideoURI(Uri.parse(Link));
    }
    private void Video_START() { VideoPlayer.start(); }
    private void Video_STOP() { VideoPlayer.stopPlayback(); }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void AddVideoIntoMyLikes(String Video_Id) {
        JSONObject params = new JSONObject();
        try {
            params.put("Video_Id", Video_Id);
        } catch (JSONException e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        String url = "http://" + BuildConfig.Backend + ":3000/get/video/data";
        // Make JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // if valid credentials
                            if(response.getString("message").equals("success"))
                            {
                                JSONObject element = response.getJSONObject("result");
                                Map<String, String> dict = new HashMap<String, String>();
                                dict.put("Video_Id", element.getString("Video_Id"));
                                dict.put("Video_Link", element.getString("Video_Link"));
                                dict.put("Description", element.getString("Description"));
                                dict.put("Upload_Date", element.getString("Upload_Date"));
                                dict.put("Location", element.getString("Location"));
                                String UserId = element.getString("User");
                                dict.put("User_Id", UserId);
                                for (int j = 0; j < Users.size(); j++) {
                                    if (Users.get(j).get("User_Id").equals(UserId)) {
                                        dict.put("Username", Users.get(j).get("Username"));
                                        break;
                                    }
                                }
                                MyLikes.add(dict);
                                MyLikesAdapter.notifyDataSetChanged();

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


    private void GetMyVideoLikes() {
        JSONObject params = new JSONObject();
        try {
            params.put("User_Id", UserSingleton.getInstance().getUserId());
        } catch (JSONException e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        String url = "http://" + BuildConfig.Backend + ":3000/likes/for/user";
        // Make JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // if valid credentials
                            if(response.getString("message").equals("success"))
                            {
                                JSONArray videosliked = response.getJSONArray("result");
                                for (int i = 0; i < videosliked.length(); i++) {
                                    JSONObject element = videosliked.getJSONObject(i);
                                    Map<String, String> dict = new HashMap<String, String>();
                                    String Video_Id = element.getString("Video_Id");
                                    AddVideoIntoMyLikes(Video_Id);
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


    private void GetMyVideos() {
        JSONObject params = new JSONObject();
        try {
            params.put("User_Id", UserSingleton.getInstance().getUserId());
        } catch (JSONException e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        String url = "http://" + BuildConfig.Backend + ":3000/get/all/user/video/data/";
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
                                    String UserId = element.getString("User");
                                    dict.put("User_Id", UserId);
                                    for (int j = 0; j < Users.size(); j++) {
                                        if (Users.get(j).get("User_Id").equals(UserId)) {
                                            dict.put("Username", Users.get(j).get("Username"));
                                            break;
                                        }
                                    }
                                    MyVideos.add(dict);
                                }
                                MyVideosAdapter.notifyDataSetChanged();
                                //UserVideosAdapter.notifyDataSetChanged();
                                //SearchBar.setText(String.valueOf(adapter.getCount()));
                                //UserVideosListView.invalidateViews();

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



    private void LoadUserList() {
        Users.clear();
        JSONObject params = new JSONObject();
        String url = "http://" + BuildConfig.Backend + ":3000/find/users";
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
                                    Map<String, String> dict = new HashMap<String, String>();
                                    dict.put("User_Id", element.getString("User_Id"));
                                    dict.put("Username", element.getString("Username"));
                                    Users.add(dict);
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













    private void DeselectAllViewsInRecViewEXCEPT(int position) {
        int count = RecView.getAdapter().getItemCount();
        for (int i = 0; i < (count - 1); i++) {
            if (i != position) {
                View item = RecView.findViewHolderForAdapterPosition(i).itemView;
                DeselectRecViewSelection(item);
            }
        }
    }

    private void SetRecViewSelection(View view) {
        view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        TextView Username = (TextView) view.findViewById(R.id.Username);
        TextView Description = (TextView) view.findViewById(R.id.RevViewText);
        ImageView VideoIcon = (ImageView) view.findViewById(R.id.PlayIcon);

        Username.setTextColor(getResources().getColor(android.R.color.white));
        Description.setTextColor(getResources().getColor(android.R.color.white));
        VideoIcon.setColorFilter(getResources().getColor(android.R.color.white));
    }
    private void DeselectRecViewSelection(View view) {
        view.setBackgroundColor(getResources().getColor(R.color.design_default_color_background));
        TextView Username = (TextView) view.findViewById(R.id.Username);
        TextView Description = (TextView) view.findViewById(R.id.RevViewText);
        ImageView VideoIcon = (ImageView) view.findViewById(R.id.PlayIcon);

        Username.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        Description.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        VideoIcon.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
    }

}




class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Map<String, String>> mList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, ArrayList<Map<String, String>> list) {
        this.mInflater = LayoutInflater.from(context);
        this.mList = list;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_view, parent, false);
        return new ViewHolder(view);
    }


    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String videoDesc = mList.get(position).get("Description");
        String videoUser = mList.get(position).get("Username");
        holder.myTextView.setText(videoDesc);
        holder.Username.setText(videoUser);
        //holder.updateView();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mList.size();
    }






    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View myView;
        TextView myTextView;
        TextView Username;
        //int mSelectedIndex;

        ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            myTextView = itemView.findViewById(R.id.RevViewText);
            Username = itemView.findViewById(R.id.Username);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {

                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }







    }

    // convenience method for getting data at click position
    public Map<String, String> getItem(int id) {
        return mList.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);

    }


}





