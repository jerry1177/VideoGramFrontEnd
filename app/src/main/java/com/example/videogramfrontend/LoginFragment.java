package com.example.videogramfrontend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import static androidx.core.content.ContextCompat.getSystemService;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
        url = "http://" + BuildConfig.Backend + ":3000/user/login";


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    EditText username;
    EditText password;
    Button signUpButton;
    Button loginButton;

    // Make post url
    private String url;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //url = "http://192.168.1.14:3000/";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username = (EditText) view.findViewById(R.id.Username);
        password = (EditText) view.findViewById(R.id.Password);
        loginButton = (Button) view.findViewById(R.id.LoginButton);
        signUpButton = (Button) view.findViewById(R.id.SignUpButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                if (ValidLoginCredentials(username, password))
                {
                    // Make post JSON
                    JSONObject params = new JSONObject();
                    try {

                        // string "username" is the key in the json
                        // and username.getText() is the value part of the json
                        params.put("username", username.getText().toString());
                        params.put("password", password.getText().toString());

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    sendRequest(url, params);
                }
            }
        });

        password.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if (ValidLoginCredentials(username, password))
                    {
                        // Make post JSON
                        JSONObject params = new JSONObject();
                        try {

                            params.put("username", username.getText());
                            params.put("password", password.getText());

                        } catch (JSONException e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }

                        sendRequest(url, params);
                    }
                    return true;
                }
                return false;
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment());
            }
        });
    }



    private void sendRequest(String url, JSONObject params) {

        // Make JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            // if valid credentials
                            if(response.getString("message").equals("success")) {
                                if (getView() != null)
                                    Navigation.findNavController(getView()).navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment());
                            } else {
                                Toast.makeText(getContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
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

    private boolean ValidLoginCredentials(EditText username, EditText password) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
        if ((!username.getText().toString().equals("")) && (!password.getText().toString().equals("")))
        {
            //Validate Credentials further here
            return true;
        }
        else {
            return false;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        MainActivity main = ((MainActivity) getActivity());
        main.hideUpButton();
        main.hideNavigationBar();
        ViewManagerSingleton viewManager = ViewManagerSingleton.GetSingleton();
        viewManager.setCurrentView(CurrentView.LOGIN);
        if (viewManager.getToView() == ToView.HOME)
            Navigation.findNavController(getView()).navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment());

    }

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
}
