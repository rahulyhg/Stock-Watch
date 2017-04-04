package com.example.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinay on 3/8/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private SQLiteDatabase myDataBase;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";
    private static DatabaseHandler myDataBaseHandler;

    private static final String SQL_CREATE_TABLE =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY + " Text not null )";
    private String TAG = "DataBaseHandler";

    public static DatabaseHandler getDatabaseHandler(Context context)
    {
        if(myDataBaseHandler == null){
            myDataBaseHandler = new DatabaseHandler(context);
        }
        return myDataBaseHandler;
    }

    private DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myDataBase = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do-nothing
    }

    // Add a stock to the database
    public void addStock(Stock stock)
    {
        Log.d(TAG,"Adding stock: " + stock.getMyStockSymbol());
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getMyStockSymbol());
        values.put(COMPANY, stock.getMyCompanyName());

        deleteStock(stock.getMyStockSymbol());
        myDataBase.insert(TABLE_NAME, null, values);
        Log.d(TAG,"Adding stock complete ");
    }

    // Delete a stock from the database
    public void deleteStock(String symbol)
    {
        Log.d(TAG,"Deleting stock: " + symbol);
        myDataBase.delete(TABLE_NAME, SYMBOL + " = ?", new String[]{ symbol });
        Log.d(TAG,"Deleting stock complete ");
    }

    public List<Stock> loadStocks()
    {
        List<Stock> stocks = new ArrayList<>();

        Cursor cursor = myDataBase.query(
                    TABLE_NAME, // The table to query
                    new String[]{ SYMBOL, COMPANY }, // The columns to return
                    null, // The columns for the WHERE clause, null means “*”
                    null, // The values for the WHERE clause, null means “*”
                    null, // don't group the rows
                    null, // don't filter by row groups
                    null); // The sort order

        if(cursor != null)
        {
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++)
            {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);

                Stock stock = new Stock();
                stock.setMyCompanyName(company);
                stock.setMyStockSymbol(symbol);
                stocks.add(stock);

                cursor.moveToNext();
            }
            cursor.close();
        }

        return stocks;
    }
}
