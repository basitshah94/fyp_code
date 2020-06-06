package com.fyp_project.bustracking.BusModule;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fyp_project.bustracking.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class BusAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] imgid;

    public BusAdapter(Activity context, String[] uploadsdatetime) {
        super(context, R.layout.list_black_text, uploadsdatetime);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.imgid = uploadsdatetime;

    }


    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_black_text, null, true);

        //   TextView titleText = (TextView) rowView.findViewById(R.id.file_name_upload);
        TextView uploadedDate = rowView.findViewById(R.id.datetime_upload_file);
        // TextView subtitleText = (TextView) rowView.findViewById(R.id.uploader_name_file);

        String dayText = imgid[position].substring(0, Math.min(imgid[position].length(), 2));
        String monthText = imgid[position].substring(3, Math.min(imgid[position].length(), 5));
        String yearText = imgid[position].substring(6, Math.min(imgid[position].length(), 10));

//        uploadedDate.setText(imgid[position] + ":" + dayText + ":" + monthText + ":" + yearText);

        try {
            uploadedDate.setText(dayText + " " + formatMonth(monthText) + " " + yearText);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //       titleText.setText(maintitle[position]);
//        uploadedDate.setText(imgid[position]);
        //     subtitleText.setText(subtitle[position]);

        return rowView;

    }

    public String formatMonth(String month) throws ParseException {
        SimpleDateFormat monthParse = new SimpleDateFormat("MM");
        SimpleDateFormat monthDisplay = new SimpleDateFormat("MMMM");
        return monthDisplay.format(monthParse.parse(month));

    }
}
