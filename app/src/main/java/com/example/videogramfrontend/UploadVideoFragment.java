package com.example.videogramfrontend;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

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

import org.json.JSONException;
import org.json.JSONObject;

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
    ImageButton uploadButton;
    String AccessKeyId;
    String SecretKey;
    AmazonS3Client s3Client;

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
        uploadButton = (ImageButton) view.findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(intent, 1);
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
        loadAccessKeys();

    }

    private final synchronized String getPath(Uri uri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, proj,
                null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //uploadPage.setText(data.getData().toString());
                Uri uri = data.getData();
                String path = getPath(uri);
                if (!path.isEmpty()) {
                    File file = new File(path);
                    UploadToS3(file);
                }
                else {
                    Toast.makeText(getContext(),"file invalid", Toast.LENGTH_SHORT).show();
                }

            }
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






    public void UploadToS3(File file) {

        final String s3Bucket = "videogramuploadbucket";
        final String s3FilePath = "Videogram/User" + String.valueOf(UserSingleton.getInstance().getUserId()) + "/" + file.getName();

        BasicAWSCredentials credentials = new BasicAWSCredentials(AccessKeyId, SecretKey);

        s3Client = new AmazonS3Client(credentials);
        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();

        final TransferObserver uploadObserver =
                transferUtility.upload(
                        file.getName(),
                        file);

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                    URL UploadedFileURL = s3Client.getUrl(s3Bucket, s3FilePath);
                    Toast.makeText(getContext(), UploadedFileURL.toExternalForm(), Toast.LENGTH_SHORT).show();
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





    public void loadAccessKeys() {
        String json = null;
        try {
            InputStream is = getContext().getResources().openRawResource(R.raw.accesskeys);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);
            AccessKeyId = obj.getString("accessKeyId");
            SecretKey = obj.getString("secretKey");
            uploadPage.setText("Keys set to " + AccessKeyId + "   " + SecretKey);
        } catch (IOException | JSONException e) {
            uploadPage.setText("keys couldnt be retrieved " + e.toString());
        }








    }

}
