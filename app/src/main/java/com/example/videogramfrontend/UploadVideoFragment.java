package com.example.videogramfrontend;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;

// AWS Imports //
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.services.s3.AmazonS3Client;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadVideoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadVideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadVideoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView uploadPage;
    Button uploadButton;

    private OnFragmentInteractionListener mListener;

    public UploadVideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadVideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadVideoFragment newInstance(String param1, String param2) {
        UploadVideoFragment fragment = new UploadVideoFragment();
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
    public void onResume() {
        super.onResume();
        ViewManagerSingleton.GetSingleton().setCurrentView(CurrentView.UPLOAD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uploadPage = (TextView) view.findViewById(R.id.uploadPage);
        uploadButton = (Button) view.findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadToS3();
            }
        });

        //AWSConfiguration config = new AWSConfiguration(getContext());
        AWSMobileClient.getInstance().initialize(getContext(), new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {
                uploadPage.setText("success" + result.toString() + AWSMobileClient.getInstance().getCredentials().toString());
            }
            @Override
            public void onError(Exception e) {
                uploadPage.setText("error" + e.toString());
            }
        });
        uploadPage.setText("good to go");

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






    public void UploadToS3() {

        File file = new File("/file/path/here");
        if (file.exists()) {

            String KEY = "AKIATQXCI3PQK6VEPFHK";
            String SECRET = "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";
            BasicAWSCredentials credentials = new BasicAWSCredentials(KEY, SECRET);

            AmazonS3Client s3Client = new AmazonS3Client(credentials);
            TransferUtility transferUtility =
                    TransferUtility.builder()
                            .context(getContext())
                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                            .s3Client(s3Client)
                            .build();

            final TransferObserver uploadObserver =
                    transferUtility.upload(
                            "Videogram/User" + String.valueOf(UserSingleton.getInstance().getUserId()) + "/" + file.getName(),
                            file);

            // Attach a listener to the observer to get state update and progress notifications
            uploadObserver.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        // Handle a completed upload.
                        uploadPage.setText("Upload Complete");
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;
                    uploadPage.setText(String.valueOf(percentDone) + "%");

                }

                @Override
                public void onError(int id, Exception ex) {
                    // Handle errors
                    uploadPage.setText("errorno: " + String.valueOf(id) + " error: " + ex.toString());
                }

            });

            // If you prefer to poll for the data, instead of attaching a
            // listener, check for the state and progress in the observer.
            if (TransferState.COMPLETED == uploadObserver.getState()) {
                // Handle a completed upload.
            }
        }
        else {
            uploadPage.setText("File not valid");
        }
    }


}
