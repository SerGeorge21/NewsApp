package com.example.android.newsappstage1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends ArrayAdapter {

    public NewsAdapter(Context context, List<Story> stories){
        super(context, 0, stories);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        Story currentStory = (Story) getItem(position);

        //find TextView with id title
        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        titleView.setText(currentStory.getTitle());

        //Find TextView with id section
        TextView sectionView = (TextView) listItemView.findViewById(R.id.section);
        sectionView.setText(currentStory.getSection());

        //find TextView with id Date
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date="";
        try{
            Date dateObject = dateFormat.parse(currentStory.getDate());
            date = dateFormat.format(dateObject);
        }catch(ParseException e){
            e.printStackTrace();
        }

        //String formattedDate = formatDate(dateObject);

        dateView.setText(date);

        TextView authorView = listItemView.findViewById(R.id.author);
        authorView.setText(currentStory.getAuthor());

        return listItemView;
    }


}
