package com.example.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private List<Stock> myStocksList = new ArrayList<>();
    private RecyclerView myRecyclerView;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private StocksAdapter myStockAdapter;
    private DatabaseHandler myDatabaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        myStockAdapter = new StocksAdapter(myStocksList, this);

        myRecyclerView.setAdapter(myStockAdapter);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myDatabaseHandler = DatabaseHandler.getDatabaseHandler(this);

        mySwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        if(!isConnectionAvailable())
        {
            ShowNoNetworkDialog();
            return;
        }

        List<Stock> stockList = myDatabaseHandler.loadStocks();
        if(!stockList.isEmpty())
        {
            for(int index = 0 ; index < stockList.size(); index++)
            {
                AsyncStockFinancialDataLoaderTask task = new AsyncStockFinancialDataLoaderTask(MainActivity.this);
                task.execute(stockList.get(index).getMyStockSymbol(), stockList.get(index).getMyCompanyName());
            }
        }

        myRecyclerView.computeVerticalScrollOffset();
    }

    private void doRefresh()
    {
        if(!isConnectionAvailable())
        {
            mySwipeRefreshLayout.setRefreshing(false);
            ShowNoNetworkDialog();
            return;
        }
        myStocksList.clear();
        List<Stock> stockList = myDatabaseHandler.loadStocks();
        if(!stockList.isEmpty())
        {
            for(int index = 0 ; index < stockList.size(); index++)
            {
                AsyncStockFinancialDataLoaderTask task = new AsyncStockFinancialDataLoaderTask(MainActivity.this);
                task.execute(stockList.get(index).getMyStockSymbol(), stockList.get(index).getMyCompanyName());
            }
        }
        mySwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.addStockMenu :

                if(!isConnectionAvailable())
                {
                    ShowNoNetworkDialog();
                    break;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.stockSelection));
                builder.setMessage(getString(R.string.stockSelectionData));

                final EditText stockSymbol = new EditText(this);
                stockSymbol.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                stockSymbol.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                stockSymbol.setGravity(Gravity.CENTER_HORIZONTAL);

                builder.setView(stockSymbol);

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String stockSymbolText = stockSymbol.getText().toString();

                        AsyncStockSymbolLoaderTask task = new AsyncStockSymbolLoaderTask(MainActivity.this);
                        task.execute(stockSymbolText);
                    }
                });

                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            default :
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ShowNoNetworkDialog() {
        AlertDialog.Builder noConnectivityDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        noConnectivityDialogBuilder.setTitle(getString(R.string.noNetworkConnection));
        noConnectivityDialogBuilder.setMessage(getString(R.string.noNetworkConnectionData));
        AlertDialog noConnectivityStockDialog = noConnectivityDialogBuilder.create();
        noConnectivityStockDialog.show();
    }

    @Override
    public void onClick(View v) {
        final int position = myRecyclerView.getChildLayoutPosition(v);
        String url = getString(R.string.marketWatch);
        url += myStocksList.get(position).getMyStockSymbol();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);

        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public boolean onLongClick(View v) {
        final int position = myRecyclerView.getChildLayoutPosition(v);
        Stock stock = myStocksList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete);
        builder.setTitle(getString(R.string.deleteStock));
        builder.setMessage(getString(R.string.deleteStockSymbol) + " " + stock.getMyStockSymbol()+" ?");

        builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the stock details
                Stock stock = myStocksList.get(position);

                // Delete the stock from the database
                myDatabaseHandler.deleteStock(stock.getMyStockSymbol());

                // Delete the stock from the stock list
                myStocksList.remove(position);

                // Notify adapter about the change in stock list
                myStockAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    public void UpdateFromAsyncStockSymbolLoaderTask(final List<HashMap<String, String>> stockSymbolDataList, String stockSymbolText)
    {
        final String StockSymbol = getString(R.string.companySymbol);
        final String CompanyName = getString(R.string.companyName);

        if(stockSymbolDataList.size() == 0)
        {
            AlertDialog.Builder noStockDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            noStockDialogBuilder.setTitle(getString(R.string.symbolNotFound) + " " + stockSymbolText);
            noStockDialogBuilder.setMessage(getString(R.string.dataForStockSymbol));
            AlertDialog noStockDialog = noStockDialogBuilder.create();
            noStockDialog.show();
            return;
        }
        if(stockSymbolDataList.size() == 1)
        {
            HashMap<String, String> selectedItem = stockSymbolDataList.get(0);
            AsyncStockFinancialDataLoaderTask task = new AsyncStockFinancialDataLoaderTask(MainActivity.this);
            task.execute(selectedItem.get(StockSymbol), selectedItem.get(CompanyName));
        }
        else if(stockSymbolDataList.size() > 1)
        {
            final CharSequence[] stockSymbolList = new CharSequence[stockSymbolDataList.size()];
            for(int index = 0; index < stockSymbolDataList.size(); index++)
            {
                HashMap<String, String> item = stockSymbolDataList.get(index);
                stockSymbolList[index] = item.get(StockSymbol) + " - " + item.get(CompanyName);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.makeSelection));
            builder.setItems(stockSymbolList, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HashMap<String, String> selectedItem = stockSymbolDataList.get(which);
                    AsyncStockFinancialDataLoaderTask task = new AsyncStockFinancialDataLoaderTask(MainActivity.this);
                    task.execute(selectedItem.get(StockSymbol), selectedItem.get(CompanyName));
                }
            });

            builder.setNegativeButton(getString(R.string.neverMind), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User cancelled the dialog
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void UpdateFromAsyncStockFinancialLoaderTask(Stock stock, String stockSymbolText )
    {
        if(stock == null)
        {
            AlertDialog.Builder noStockDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            noStockDialogBuilder.setTitle(getString(R.string.symbolNotFound) + " " + stockSymbolText);
            noStockDialogBuilder.setMessage(getString(R.string.dataForStockSymbol));
            AlertDialog noStockDialog = noStockDialogBuilder.create();
            noStockDialog.show();
            return;
        }

        boolean duplicateStock = false;
        for(int index = 0; index < myStocksList.size(); index++)
        {
            if(stock.getMyStockSymbol().compareTo(myStocksList.get(index).getMyStockSymbol()) == 0 &&
                    stock.getMyCompanyName().compareTo(myStocksList.get(index).getMyCompanyName()) == 0)
            {
                duplicateStock = true;
            }
        }
        if(duplicateStock)
        {
            AlertDialog.Builder duplicateStockDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            duplicateStockDialogBuilder.setIcon(R.drawable.ic_duplicate);
            duplicateStockDialogBuilder.setTitle(getString(R.string.duplicateStock));
            duplicateStockDialogBuilder.setMessage(getString(R.string.stockSymbol) + " " + stockSymbolText + " " + getString(R.string.alreadyDisplayed));
            AlertDialog duplicateStockDialog = duplicateStockDialogBuilder.create();
            duplicateStockDialog.show();
            return;
        }

        myStocksList.add(stock);
        Collections.sort(myStocksList);
        myDatabaseHandler.addStock(stock);
        myStockAdapter.notifyDataSetChanged();
    }

    private boolean isConnectionAvailable()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
