package com.example.videogramfrontend;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SignupFragment() {
        url = "http://" + BuildConfig.Backend + ":3000/user/signup";
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    EditText username;
    EditText password;
    EditText passwordConfirm;
    EditText fName;
    EditText lNname;
    EditText email;
    Button signUp;
    // Make post url
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        MainActivity main = (MainActivity) getActivity();
        main.showUpButton();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        username = (EditText) view.findViewById(R.id.Username);
        password = (EditText) view.findViewById(R.id.Password);
        passwordConfirm = (EditText) view.findViewById(R.id.PasswordConfirm);
        fName = (EditText) view.findViewById(R.id.Firstname);
        lNname = (EditText) view.findViewById(R.id.Lastname);
        email = (EditText) view.findViewById(R.id.Email);
        signUp = (Button) view.findViewById(R.id.SignUpButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((MainActivity)getActivity()).onBackPressed();
                ValidateSignUpCredentials(username, password, passwordConfirm, fName, lNname, email);
            }
        });
        ViewManagerSingleton.GetSingleton().setCurrentView(CurrentView.SIGNUP);

        email.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    ValidateSignUpCredentials(username, password, passwordConfirm, fName, lNname, email);
                    return true;
                }
                return false;
            }
        });

    }

    public void HideKeyboard(EditText textfield) {
        //Hide Keyboard
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textfield.getWindowToken(), 0);
    }


    public void ValidateSignUpCredentials(EditText username, EditText password, EditText passwordConfirm, EditText Firstname, EditText Lastname, EditText Email) {
        HideKeyboard(email);
        if ((!username.getText().toString().equals(""))
                && (!password.getText().toString().equals(""))
                && (!passwordConfirm.getText().toString().equals(""))
                && (!Firstname.getText().toString().equals(""))
                && (!Lastname.getText().toString().equals(""))
                && (!Email.getText().toString().equals("")))
        {
            if ((!username.getText().toString().contains(" "))
                    && (!password.getText().toString().contains(" "))
                    && (!passwordConfirm.getText().toString().contains(" "))
                    && (!Firstname.getText().toString().contains(" "))
                    && (!Lastname.getText().toString().contains(" "))
                    && (!Email.getText().toString().contains(" ")))
            {
                if (password.getText().toString().equals(passwordConfirm.getText().toString()))
                {
                    // Make post JSON
                    JSONObject params = new JSONObject();
                    try {
                        params.put("Username", username.getText().toString());
                        params.put("Password", password.getText().toString());
                        params.put("Firstname", fName.getText().toString());
                        params.put("Lastname", lNname.getText().toString());
                        params.put("Email", email.getText().toString());
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    sendRequest(url, params);
                }
                else {
                    Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getContext(), "Fields may not contain spaces", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getContext(), "Fields left blank", Toast.LENGTH_SHORT).show();
        }
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

                                UserSingleton.getInstance().setUserId(response.getInt("User_Id"));

                                if (getView() != null)

                                    Navigation.findNavController(getView()).navigate(SignupFragmentDirections.actionSignupFragmentToHomeFragment());
                                    //ViewManagerSingleton.GetSingleton().setToView(ToView.HOME);
                            }
                            else if (response.getString("message").equals("failed")) {
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
