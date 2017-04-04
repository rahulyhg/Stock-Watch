package com.example.stockwatch;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by vinay on 3/7/2017.
 */

public class StocksAdapter extends RecyclerView.Adapter<MyRecyclerViewHolder>{

    private List<Stock> myStockList;
    private MainActivity myMainActivity;

    public StocksAdapter(List<Stock> stockList, MainActivity mainActivity)
    {
        myStockList = stockList;
        myMainActivity = mainActivity;
    }

    // create new views invoked by layout manager
    @Override
    public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_record_row, parent, false);
        myItemView.setOnClickListener(myMainActivity);
        myItemView.setOnLongClickListener(myMainActivity);
        return new MyRecyclerViewHolder(myItemView);
    }

    // replace the contents of the view invoked by the layout manager
    @Override
    public void onBindViewHolder(MyRecyclerViewHolder holder, int position) {
        Stock newStock = myStockList.get(position);
        holder.getMyStockSymbolView().setText(newStock.getMyStockSymbol());
        holder.getMyCompanyNameView().setText(newStock.getMyCompanyName());
        holder.getMyLastTradePriceView().setText(newStock.getMyLastTradePrice());

        if(Double.parseDouble(newStock.getMyPriceChangeAmount()) >= 0.0) {
            holder.getMyPriceChangeAmountView().setText("\u25B2 " + newStock.getMyPriceChangeAmount()+ " " + newStock.getMyPriceChangePercentage());
            SetGreenColor(holder);
        }
        else {
            holder.getMyPriceChangeAmountView().setText("\u25BC " + newStock.getMyPriceChangeAmount()+ " " + newStock.getMyPriceChangePercentage());
            SetRedColor(holder);
        }
    }

    private void SetGreenColor(MyRecyclerViewHolder holder)
    {
        holder.getMyStockSymbolView().setTextColor(Color.GREEN);
        holder.getMyCompanyNameView().setTextColor(Color.GREEN);
        holder.getMyLastTradePriceView().setTextColor(Color.GREEN);
        holder.getMyPriceChangeAmountView().setTextColor(Color.GREEN);
    }

    private void SetRedColor(MyRecyclerViewHolder holder)
    {
        holder.getMyStockSymbolView().setTextColor(Color.RED);
        holder.getMyCompanyNameView().setTextColor(Color.RED);
        holder.getMyLastTradePriceView().setTextColor(Color.RED);
        holder.getMyPriceChangeAmountView().setTextColor(Color.RED);
    }

    // return the size of the dataset
    @Override
    public int getItemCount() {
        return myStockList.size();
    }
}
