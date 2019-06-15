package com.example.android.newsappstage1;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader {

    private static final String LOG_TAG = NewsLoader.class.getName();
    private String mUrl;

    public NewsLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Story> loadInBackground() {
        if (mUrl == null) return null;

        //EDW GINETAI TO NETWORK REQUEST GIA NA PAROUME TI LISTA TWN NEWS
        List<Story> stories = QueryUtils.fetchNewsData(mUrl);
        return stories;
    }
}
