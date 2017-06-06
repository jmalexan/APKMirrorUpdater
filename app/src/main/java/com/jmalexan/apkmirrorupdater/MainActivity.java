package com.jmalexan.apkmirrorupdater;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new RetrieveFeedTask().execute(getApplicationContext());
    }
}

class RetrieveFeedTask extends AsyncTask<Context, Void, List<Boolean>> {

    private Exception exception;

    protected List<Boolean> doInBackground(Context... context) {
        List<PackageInfo> installedPackages = context[0].getPackageManager().getInstalledPackages(0);
        List<Boolean> packagesAreFound = new ArrayList<Boolean>();
        for (PackageInfo pi : installedPackages) {
            Log.d("MainActivity", "User App: " + pi.packageName);
            try {
                Document search = Jsoup.connect("http://www.apkmirror.com/?post_type=app_release&searchtype=app&s=" + pi.packageName).get();
                Elements searchResults = search.getElementById("content").getElementsByClass("listWidget").first().getElementsByClass("appRow");
                if (searchResults.size() > 0) {
                    Log.d("MainActivity", "Results found");
                    Elements step1 = searchResults.first().getElementsByClass("table-row");
                    Elements step2 = step1.first().getElementsByClass("table-cell");
                    Element step3 = step2.first().child(0);
                    Log.d("MainActivity", pi.applicationInfo.name + "  :  " + step3.html());
                    boolean isFound = searchResults.first().getElementsByClass("table-row").first().getElementsByClass("table-cell").first().child(0).html() == pi.applicationInfo.name;

                    Log.d("MainActivity", isFound ? "Found" : "Not found");
                    packagesAreFound.add(isFound);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return packagesAreFound;
    }
}