package com.example.videogramfrontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;



public class MainActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener,
        SignupFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        UploadVideoFragment.OnFragmentInteractionListener {

    //BuildConfig.Backend = server ip address!

    BottomNavigationView bottomNavigation;

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            View view = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment).getView();

            ViewManagerSingleton viewManager = ViewManagerSingleton.GetSingleton();
            CurrentView currentView = viewManager.getCurrentView();
            if (item.getItemId() == R.id.homeMenuItem) {
                if (currentView != CurrentView.HOME) {
                    viewManager.setToView(ToView.HOME);
                    Navigation.findNavController(view).popBackStack();
                }
                return true;
            } else if (item.getItemId() == R.id.searchMenuItem) {
                if (currentView == CurrentView.HOME) {
                    Navigation.findNavController(view).navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment());
                }

                if (currentView == CurrentView.UPLOAD) {
                    viewManager.setToView(ToView.SEARCH);
                    Navigation.findNavController(view).popBackStack();

                }

                return true;

                // if the user wants to go to the upload page
            } else if ( item.getItemId() == R.id.uploadVideoMenuItem) {
                //if we are at the home page
                if (currentView == CurrentView.HOME) {
                    // we just navigate to the upload page
                    Navigation.findNavController(view).navigate(HomeFragmentDirections.actionHomeFragmentToUploadVideoFragment());

                }

                // if we are in the search view
                if (currentView == CurrentView.SEARCH) {

                    // we want to go to the upload page so we need to
                    // pop back to home and by setting the ToView to UPLOAD
                    // HOME page will navigate to upload page on the onResume callback
                    viewManager.setToView(ToView.UPLOAD);
                    Navigation.findNavController(view).popBackStack();

                }
                return true;
            } else {
                onBackPressed();
                return true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigationBar);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

    }

    public void showNavigationBar() {
        bottomNavigation.setVisibility(View.VISIBLE);
        //isNavBarShown = true;
    }
    public void hideNavigationBar() {

        bottomNavigation.setVisibility(View.GONE);
        //isNavBarShown = false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        View view = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment).getView();

        ViewManagerSingleton viewManager = ViewManagerSingleton.GetSingleton();
        CurrentView currentView = viewManager.GetSingleton().getCurrentView();
        if (item.getItemId() == R.id.homeMenuItem) {
            if (currentView != CurrentView.HOME) {
                viewManager.setToView(ToView.HOME);
                Navigation.findNavController(view).popBackStack();
                Toast.makeText(getApplicationContext(), "home", Toast.LENGTH_SHORT).show();
               }
            return true;
        } else if (item.getItemId() == R.id.searchMenuItem) {
            if (currentView == CurrentView.HOME) {
                Toast.makeText(getApplicationContext(), "search", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment());
            }

            if (currentView == CurrentView.UPLOAD) {
                Toast.makeText(getApplicationContext(), "home", Toast.LENGTH_SHORT).show();
                viewManager.setToView(ToView.SEARCH);
                Navigation.findNavController(view).popBackStack();


            }

            return true;

            // if the user wants to go to the upload page
        } else if ( item.getItemId() == R.id.uploadVideoMenuItem) {
            //if we are at the home page
            if (currentView == CurrentView.HOME) {
                Toast.makeText(getApplicationContext(), "upload", Toast.LENGTH_SHORT).show();
                // we just navigate to the upload page
                Navigation.findNavController(view).navigate(HomeFragmentDirections.actionHomeFragmentToUploadVideoFragment());

            }

            // if we are in the search view
            if (currentView == CurrentView.SEARCH) {
                Toast.makeText(getApplicationContext(), "home", Toast.LENGTH_SHORT).show();

                // we want to go to the upload page so we need to
                // pop back to home and by setting the ToView to UPLOAD
                // HOME page will navigate to upload page on the onResume callback
                viewManager.setToView(ToView.UPLOAD);
                Navigation.findNavController(view).popBackStack();

            }
            return true;
        } else {
            onBackPressed();
            return true;
        }
    }




    public void showUpButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void hideUpButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
