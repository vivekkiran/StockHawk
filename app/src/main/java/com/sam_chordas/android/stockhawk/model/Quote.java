package com.sam_chordas.android.stockhawk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vivek Kiran on 14/6/2016.
 */
public class Quote {
    @SerializedName("Symbol")
    public String symbol;
    @SerializedName("Date")
    public String quote_date;
    @SerializedName("Open")
    public String open;
    @SerializedName("High")
    public String high;
    @SerializedName("Low")
    public String low;
    @SerializedName("Close")
    public String close;
    @SerializedName("Volume")
    public String volume;
    @SerializedName("Adj_Close")
    public String Adj_Close;

}
