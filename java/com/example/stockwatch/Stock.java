package com.example.stockwatch;

import android.support.annotation.NonNull;

/**
 * Created by vinay on 3/7/2017.
 */

public class Stock implements Comparable{
    private String myStockSymbol;
    private String myCompanyName;
    private String myLastTradePrice;
    private String myPriceChangeAmount;
    private String myPriceChangePercentage;

    public Stock(){
        myStockSymbol = null;
        myCompanyName = null;
        myLastTradePrice = null;
        myPriceChangeAmount = null;
        myPriceChangePercentage = null;
    }

    public String getMyStockSymbol() {
        return myStockSymbol;
    }

    public void setMyStockSymbol(String myStockSymbol) {
        this.myStockSymbol = myStockSymbol;
    }

    public String getMyCompanyName() {
        return myCompanyName;
    }

    public void setMyCompanyName(String myCompanyName) {
        this.myCompanyName = myCompanyName;
    }

    public String getMyLastTradePrice() {
        return myLastTradePrice;
    }

    public void setMyLastTradePrice(String myLastTradePrice) {
        this.myLastTradePrice = myLastTradePrice;
    }

    public String getMyPriceChangeAmount() {
        return myPriceChangeAmount;
    }

    public void setMyPriceChangeAmount(String myPriceChangeAmount) {
        this.myPriceChangeAmount = myPriceChangeAmount;
    }

    public String getMyPriceChangePercentage() {
        return myPriceChangePercentage;
    }

    public void setMyPriceChangePercentage(String myPriceChangePercentage) {
        this.myPriceChangePercentage = myPriceChangePercentage;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return this.getMyStockSymbol().compareTo(((Stock)o).getMyStockSymbol());
    }
}
