package com.example.stockwatch;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by vinay on 3/7/2017.
 */

public class MyRecyclerViewHolder extends ViewHolder {

    private TextView myStockSymbol;
    private TextView myCompanyName;
    private TextView myLastTradePrice;
    private TextView myPriceChangeAmount;

    public MyRecyclerViewHolder(View itemView) {
        super(itemView);
        setMyStockSymbolView((TextView) itemView.findViewById(R.id.stockSymbol));
        setMyCompanyNameView((TextView) itemView.findViewById(R.id.companyName));
        setMyLastTradePriceView((TextView) itemView.findViewById(R.id.lastTradePrice));
        setMyPriceChangeAmountView((TextView) itemView.findViewById(R.id.priceChangeAmount));
    }

    public TextView getMyStockSymbolView() {
        return myStockSymbol;
    }

    public void setMyStockSymbolView(TextView myStockSymbol) {
        this.myStockSymbol = myStockSymbol;
    }

    public TextView getMyCompanyNameView() {
        return myCompanyName;
    }

    public void setMyCompanyNameView(TextView myCompanyName) {
        this.myCompanyName = myCompanyName;
    }

    public TextView getMyLastTradePriceView() {
        return myLastTradePrice;
    }

    public void setMyLastTradePriceView(TextView myLastTradePrice) {
        this.myLastTradePrice = myLastTradePrice;
    }

    public TextView getMyPriceChangeAmountView() {
        return myPriceChangeAmount;
    }

    public void setMyPriceChangeAmountView(TextView myPriceChangeAmount) {
        this.myPriceChangeAmount = myPriceChangeAmount;
    }
}
