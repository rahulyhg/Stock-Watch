package com.example.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vinay on 3/9/2017.
 */

public class AsyncStockSymbolLoaderTask extends AsyncTask<String, Void, List<HashMap<String, String>>> {

    private String myStockSearchApiURL = "http://stocksearchapi.com/api/?";
    private final String myApiKey = "467978712a87471b2290e8fdc78b9a6a5eb33a06";
    private final String TAG = "AsyncSymbolLoader";
    private final String StockSymbol = "company_symbol";
    private final String CompanyName = "company_name";
    private MainActivity myMainActivity;
    private String myStockSymbolText;

    public AsyncStockSymbolLoaderTask(MainActivity mainActivity)
    {
        myMainActivity = mainActivity;
    }

    @Override
    protected List<HashMap<String, String>> doInBackground(String... params)
    {
        List<HashMap<String, String>> stockSymbolList = null;
        myStockSymbolText = params[0];
        try {
            stockSymbolList = LoadStockSymbolData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(stockSymbolList == null)
        {
            return new ArrayList<>();
        }
        return stockSymbolList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
        myMainActivity.UpdateFromAsyncStockSymbolLoaderTask(hashMaps, myStockSymbolText);
    }

    private List<HashMap<String, String>> LoadStockSymbolData() throws IOException
    {
        Uri.Builder buildURL = Uri.parse(myStockSearchApiURL).buildUpon();
        buildURL.appendQueryParameter("api_key", myApiKey);
        buildURL.appendQueryParameter("search_text", myStockSymbolText);
        String urlToUse = buildURL.build().toString();

        StringBuilder sb = new StringBuilder();
        try
        {
            URL url = new URL(urlToUse);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = reader.readLine()) != null){
                sb.append(line).append('\n');
            }
        }
        catch(Exception e)
        {
            e.getStackTrace();
            return null;
        }

        //Log.d(TAG, "LoadStockSymbolData" + sb.toString());

        return ParseJson(sb.toString());
    }

    private List<HashMap<String, String>> ParseJson(String s)
    {
        List<HashMap<String, String>> stockSymbolDataList = new ArrayList<>();
        try
        {
            JSONArray jsonArray = new JSONArray(s);

            for(int index = 0; index < jsonArray.length(); index++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                String stockSymbol = jsonObject.getString(StockSymbol);
                String companyName = jsonObject.getString(CompanyName);

                HashMap<String, String> stockSymbolData = new HashMap<>();
                stockSymbolData.put(StockSymbol, stockSymbol);
                stockSymbolData.put(CompanyName, companyName);

                stockSymbolDataList.add(stockSymbolData);
            }
        }
        catch(Exception e)
        {
            e.getStackTrace();
            return null;
        }

        return stockSymbolDataList;
    }
}
