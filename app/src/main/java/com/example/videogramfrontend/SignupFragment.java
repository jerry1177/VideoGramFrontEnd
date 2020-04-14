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
        // Required empty public constructor
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
    EditText fName;
    EditText lNname;
    EditText email;
    Button signUp;

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
        fName = (EditText) view.findViewById(R.id.Firstname);
        lNname = (EditText) view.findViewById(R.id.Lastname);
        email = (EditText) view.findViewById(R.id.Email);
        signUp = (Button) view.findViewById(R.id.SignUpButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).onBackPressed();
                if (ValidSignUpCredentials(username, password, fName, lNname, email))
                {
                    ViewManagerSingleton.GetSingleton().setToView(ToView.HOME);
                }
            }
        });
        ViewManagerSingleton.GetSingleton().setCurrentView(CurrentView.SIGNUP);

        password.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if (ValidSignUpCredentials(username, password, fName, lNname, email))
                    {
                        ViewManagerSingleton.GetSingleton().setToView(ToView.HOME);
                    }
                    return true;
                }
                return false;
            }
        });

    }


    public boolean ValidSignUpCredentials(EditText username, EditText password, EditText Firstname, EditText Lastname, EditText Email) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
        if ((!username.getText().toString().equals(""))
                && (!password.getText().toString().equals(""))
                && (!Firstname.getText().toString().equals(""))
                && (!Lastname.getText().toString().equals(""))
                && (!Email.getText().toString().equals("")))
        {
            //Validate Credentials further here
            return true;
        }
        else {
            return false;
        }
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
