package com.example.lijian.mmomovie;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lijian.mmomovie.R;
import com.example.lijian.mmomovie.movieListFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.R.attr.animationDuration;
import static android.R.attr.fragment;

public class MainActivity extends AppCompatActivity {

    //The Movie DB api key (v3 auth):
    // Official Demo:
    // https://api.themoviedb.org/3/movie/550?api_key=
    // Udacity Demo:
    //http://api.themoviedb.org/3/movie/popular?language=zh&api_key=
    //http://api.themoviedb.org/3/movie/top_rated?language=zh&api_key=

    //About the image
    //https://image.tmdb.org/t/p/w185/AgzX7mmCrQcSozvqWGwSpFAsEXj.jpg


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.add(R.id.container_main,new movieListFragment());//1st para is id of container
            fragmentTransaction.commit();
        }
    }
}

//LOG
//1.Add Menu, inflate the menu
//2.Add click menu item event
//3.menu item event to new AsyncTask.
//  3.1 In order to AsyncTask to work, need to write the doInBackground() Function.
//  3.2 In  the doInBackground() Function, need to build the http connection.
//  3.3 Get the input Stream from the http connection.
//  3.4 Trans inputStream to String ( inputStreamReader -> BufferedReader -> StringBuffer
//      --> String[]
//  3.5 Parse JSON to extract movie Infos
//  3.6 Attach the movie infos to List Item.

// notes:
// 1. The Void class (not 'void')  is an uninstaniable placeholder class to hold a reference to
//    the Class object representing the Java keyword void.

