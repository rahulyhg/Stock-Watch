package com.example.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
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
 * Created by vinay on 3/10/2017.
 */

public class AsyncStockFinancialDataLoaderTask extends AsyncTask<String, Void, Stock> {

    private String myStockSearchApiURL = "http://finance.google.com/finance/info?";
    private final String StockSymbol = "t";
    private final String LastTradePrice = "l";
    private final String PriceChangeAmount = "c";
    private final String PriceChangePercentage = "cp";
    private MainActivity myMainActivity;
    private String myStockSymbolText;

    public AsyncStockFinancialDataLoaderTask(MainActivity mainActivity)
    {
        myMainActivity = mainActivity;
    }

    @Override
    protected Stock doInBackground(String... params) {
        Stock stock = null;
        myStockSymbolText = params[0];
        String companyName = params[1];
        stock = LoadStockFinancialData(companyName);
        return stock;
    }

    @Override
    protected void onPostExecute(Stock stock) {
        myMainActivity.UpdateFromAsyncStockFinancialLoaderTask(stock, myStockSymbolText);
    }

    private Stock LoadStockFinancialData(String companyName)
    {
        Uri.Builder buildURL = Uri.parse(myStockSearchApiURL).buildUpon();
        buildURL.appendQueryParameter("client", "ig");
        buildURL.appendQueryParameter("q", myStockSymbolText);
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

        return ParseJson(sb.toString(), companyName);
    }

    private Stock ParseJson(String jsonString, String companyName)
    {
        Stock stock = new Stock();
        try
        {
            // remove first "//" characters before parsing json
            jsonString = jsonString.replaceFirst("//","");

            JSONArray jsonArray = new JSONArray(jsonString);

            for(int index = 0; index < jsonArray.length(); index++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                String stockSymbol = jsonObject.getString(StockSymbol);
                String lastTradePrice = jsonObject.getString(LastTradePrice);
                String priceChangeAmount = jsonObject.getString(PriceChangeAmount);
                String priceChangePercentage = jsonObject.getString(PriceChangePercentage);

                stock.setMyStockSymbol(stockSymbol);
                stock.setMyCompanyName(companyName);
                stock.setMyLastTradePrice(lastTradePrice);
                stock.setMyPriceChangeAmount(priceChangeAmount);
                stock.setMyPriceChangePercentage("(" + priceChangePercentage + "%)");
            }
        }
        catch(Exception e)
        {
            e.getStackTrace();
            return null;
        }

        return stock;
    }
}
