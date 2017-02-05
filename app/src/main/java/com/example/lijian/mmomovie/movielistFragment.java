package com.example.loopat.mmomovie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.loopat.mmomovie.BuildConfig;
import com.example.loopat.mmomovie.R;
import com.example.loopat.mmomovie.SettingsActivity;
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
import java.security.Policy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.id.list;

//import android.app.Fragment package;
public class movielistFragment extends Fragment {


    /**
     * popular movies:
     * https://api.themoviedb.org/3/movie/popular?api_key=<<api_key>>&language=en-US&page=1
     *
     * rated movies:
     * https://api.themoviedb.org/3/movie/top_rated?api_key=<<api_key>>&language=en-US&page=1
     *
     * general demo:
     * http://api.themoviedb.org/3/movie/popular?language=zh&api_key=<>
     * http://api.themoviedb.org/3/movie/top_rated?language=zh&api_key=<>
     */


    private final int ITEM_NUMS = 16;
    private final int DETAIL_ITMES = 5;

    //For Test Image show
    //String internalUrl = "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg";
    //ArrayAdapter<String> mArrayAdapter;

    String imageBaseUrl = "https://image.tmdb.org/t/p/w500/";
    String [] mStrMovieImageUrl = new String[ITEM_NUMS];

    //Name,Image,Intro,Score,Date
    List<String[]> mListDetail = new ArrayList<String[]>();

    GridView mGridView;
    com.example.loopat.mmomovie.ImageAdapter mImageAdapter;

    FetchMovieInfoTask fetchMovieInfoTask;

    public movielistFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        updateMoviesInfo();
        super.onStart();
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //inflate menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    //process the menu item click event
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){

            case R.id.menu_item_id_refresh:

                updateMoviesInfo();
                return true;

            case R.id.menu_id_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMoviesInfo() {

        if(isOnline())
        {
            fetchMovieInfoTask = new FetchMovieInfoTask();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    getActivity());
            String sortby = sharedPreferences.getString(
                    getString(R.string.settings_sort_key),
                    getString(R.string.setting_sort_items_value_top_rated)
            );

            fetchMovieInfoTask.execute(sortby);
        } else{
            return;
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movielist,container,false);
        mGridView = (GridView) rootView.findViewById(R.id.movieslistview);

        mImageAdapter = new com.example.loopat.mmomovie.ImageAdapter(getActivity(),mStrMovieImageUrl);
        mGridView.setAdapter(mImageAdapter);

        //GridView Item Click Event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Test click event
                Toast.makeText(getActivity(),"Item position is : " + i, Toast.LENGTH_SHORT).show();

                // Start detailed activity
                Intent intent = new Intent(getActivity(), com.example.loopat.mmomovie.DetailedActivity.class)
                        .putExtra("Movie Detail",mListDetail.get(i));

                String []detailStr = mListDetail.get(i);

//                for(String str : detailStr){
//                    Log.v("Get Info", "Get Info " + str);
//                }

                startActivity(intent);
            }
        });

        return rootView;
    }


    private final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final String MOVIE_LANGUAGE = "?language=zh";
    private final String MOVIE_SORT_BY_POP = "popular";
    private final String MOVIE_SORT_BY_RATE = "top_rated";
    private final String MOVIE_API_KEY = "&api_key=";

    //To using the moviedb API get info
    public String[] getMoviesInfo(String str){

        //Get settings about sort by
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortby = sharedPreferences.getString(
                getString(R.string.settings_sort_label),
                getString(R.string.setting_sort_items_value_top_rated)
        );

        String resultStr="";
//      String strUrl = "http://api.themoviedb.org/3/movie/top_rated?language=zh&api_key=XXXXXXXX";
        String strUrl = MOVIE_BASE_URL + str + MOVIE_LANGUAGE + MOVIE_API_KEY
                + BuildConfig.THE_MOVIE_DB_API_KEY;

        HttpURLConnection httpURLConnection = null;
        StringBuffer stringBuffer = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(strUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            if(inputStream == null){
                return null;
            }

            //Get the JSON data to String variable.
            stringBuffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = reader.readLine()) != null){
                stringBuffer.append(line + "\n");
            }

            if(stringBuffer.length() == 0){
                resultStr = null;
            }

            resultStr = stringBuffer.toString();

            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }
        } catch(IOException e){
            e.printStackTrace();
            //Log.v("Get Info", "Get Info IOException " + e.toString());
        } finally {

            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        return ParseJSON2Array(resultStr);

    }

    private String[] ParseJSON2Array(String resultStr) {
        if(resultStr == null){
            return null;
        }

        //The following is parsing JSON Format.
        String []strInfo = new String[ITEM_NUMS];

        try {
            JSONObject jsonInfo = new JSONObject(resultStr);
            int pages = jsonInfo.getInt("total_pages");
            JSONArray jsonArrayResult = jsonInfo.getJSONArray("results");
            JSONObject jsonArrayResultOverview;

            String temp;

            mListDetail.clear();
            for(int i = 0; i < ITEM_NUMS; i ++){
                String []detailStr = new String[DETAIL_ITMES];

                jsonArrayResultOverview  = (JSONObject)jsonArrayResult.get(i);
                //********Name,Image,Intro,Score,Date**************
                //1. Name
                temp = jsonArrayResultOverview.getString("title");
                detailStr[0] = temp;

                //2. Image
                temp = jsonArrayResultOverview.getString("backdrop_path");
                mStrMovieImageUrl[i] = imageBaseUrl + temp.substring(1);
                detailStr[1] = mStrMovieImageUrl[i];
                //3. Intro
                temp = jsonArrayResultOverview.getString("overview");
                detailStr[2] = temp;
                strInfo[i] = temp;
                //4. Score
                temp = jsonArrayResultOverview.getString("vote_average");
                detailStr[3] = temp;
                //5. Date
                temp = jsonArrayResultOverview.getString("release_date");
                detailStr[4] = temp;

                mListDetail.add(i,detailStr);

            }
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return mStrMovieImageUrl;
    }

    //Async Task
    private class FetchMovieInfoTask extends AsyncTask<String,Void,String[]> {

        @Override
        protected String[] doInBackground(String... strings) {
            return getMoviesInfo(strings[0]);
        }


        @Override
        protected void onPostExecute(String []result) {
            mGridView.setAdapter(mImageAdapter);
        }
    }
}


//Fragment
//Fragment override function onCreatView();
//In the override,
//1. New Array List
//2. Find the rootView and then find the listView
//3. New adapter.
//   ArrayAdapter adapter = new ArrayAdapter<Type>(context, the contained layout
//   context, getActivity, layout id, textView id
//4. Using listView.setAdapter();


//1. mAdapter is set in the method "onCreatView" Function.
//2. In AsyncTask onPostExecute Method to update the ArrayList.
//3. BufferedReader should be closed after reading in try catch block.
//4. Close httpURLConnection.

