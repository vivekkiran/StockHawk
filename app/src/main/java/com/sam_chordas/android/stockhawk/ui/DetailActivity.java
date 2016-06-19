package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.sam_chordas.android.stockhawk.CustomSpinnerAdapter;
import com.sam_chordas.android.stockhawk.QuoteService;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.Utility;
import com.sam_chordas.android.stockhawk.model.DateHigh;
import com.sam_chordas.android.stockhawk.model.DateHighMain;
import com.sam_chordas.android.stockhawk.model.Quote;
import com.sam_chordas.android.stockhawk.model.QuoteInfo;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity implements Callback<QuoteInfo> {
    private ProgressBar pb;
    public static String symbol;

    private DateHighMain dhm = new DateHighMain();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        symbol = getIntent().getExtras().getString("symbol");
        pb = (ProgressBar) findViewById(R.id.progressBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(symbol);

    }

    private void callRetrofitFetch(String symbol, String startDate, String endDate) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://query.yahooapis.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // prepare call in Retrofit 2.0
        QuoteService stackOverflowAPI = retrofit.create(QuoteService.class);
        String q = "select * from yahoo.finance.historicaldata where symbol = \"" + symbol + "\" and startDate = \"" + endDate + "\" and endDate = \"" + startDate + "\"";
        String diagnostics = "true";
        String env = "store://datatables.org/alltableswithkeys";
        String format = "json";
        Call<QuoteInfo> call = stackOverflowAPI.getObjectWithNestedArraysAndObject(q, diagnostics, env, format);

        //asynchronous call
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<QuoteInfo> call, Response<QuoteInfo> response) {
        pb.setVisibility(View.GONE);
        QuoteInfo quoteInfo = response.body();
        //Storing the response if any

        ArrayList<Quote> quoteArray = quoteInfo.query.results.quote;
        int i;
        dhm.getDatehigh().clear();
        for (i = 0; i < quoteArray.size(); i++
                ) {
            DateHigh dh = new DateHigh();
            dh.setQuoteDate(quoteArray.get(i).quote_date);
            dh.setQuoteHighValue(quoteArray.get(i).high);
            dhm.getDatehigh().add(dh);
        }


        Fragment fragment = LineChartFragment.newInstance(dhm);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_fg, fragment)
                .commit();
    }

    @Override
    public void onFailure(Call<QuoteInfo> call, Throwable t) {
        Toast.makeText(DetailActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_activity, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayList<String> list = new ArrayList<>();
        list.add(getString(R.string.one_week));
        list.add(getString(R.string.one_month));
        list.add(getString(R.string.three_month));
        list.add(getString(R.string.six_month));
        list.add(getString(R.string.one_year));
        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(
                getApplicationContext(), list);
        spinner.setAdapter(spinAdapter); // set the adapter to provide layout of rows and content
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                // On selecting a spinner item
                String item = adapter.getItemAtPosition(position).toString();
                String startDate = Utility.getFormattedDate(System.currentTimeMillis());
                Date date = new Date();
                switch (item) {
                    case "1W":
                        callRetrofitFetch(symbol, startDate, Utility.get1WeekBackDate(date));
                        break;
                    case "1M":
                        callRetrofitFetch(symbol, startDate, Utility.get1MonthBackDate(date));
                        break;
                    case "3M":
                        callRetrofitFetch(symbol, startDate, Utility.get3MonthsBackDate(date));
                        break;
                    case "6M":
                        callRetrofitFetch(symbol, startDate, Utility.get6MonthsBackDate(date));
                        break;
                    case "1Y":
                        callRetrofitFetch(symbol, startDate, Utility.get1YearBackDate(date));
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        return true;
    }
}
