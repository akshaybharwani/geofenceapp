package com.akshay.android.geofence_app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Akshay on 6/4/2018.
 */

public class InformationAdapter extends ArrayAdapter<Information> {

    private Activity context;
    private List<Information> informationList;

    public InformationAdapter(Activity context, List<Information> informationList) {
        super(context, R.layout.list_layout, informationList);
        this.context = context;
        this.informationList = informationList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_layout, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        Information currentInformation = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView informationTitle = listItemView.findViewById(R.id.information_title_text_view);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        informationTitle.setText(currentInformation.getmTitle());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView informationDescription = listItemView.findViewById(R.id.description_text_view);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        informationDescription.setText(currentInformation.getmDescription());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView geofenceName = listItemView.findViewById(R.id.geofence_location_name_textview);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        geofenceName.setText(currentInformation.getmGeofence().getPlaceName());

        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }
}
