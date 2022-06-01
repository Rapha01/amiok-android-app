package com.domain.amiok;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by user on 02.11.2017.
 */

public class EntryAdapter extends ArrayAdapter<Entry> {
    public EntryAdapter(Context context, ArrayList<Entry> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Entry entry = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem, parent, false);
        }

        // Lookup view for data population
        TextView entryName = (TextView) convertView.findViewById(R.id.entry_name);
        ImageView entryStatus = (ImageView) convertView.findViewById(R.id.status_image);
        // Populate the data into the template view using the data object
        entryName.setText(entry.name);
        if(entry.amiok == -1)
            entryStatus.setImageResource(R.drawable.ic_action_notok);
        else if(entry.amiok == 1)
            entryStatus.setImageResource(R.drawable.ic_action_ok);
        else
            entryStatus.setImageResource(R.drawable.ic_action_noalarm);

        // Cache user object inside the button using `setTag` and Attach the click event handler
        LinearLayout item = (LinearLayout) convertView.findViewById(R.id.item_wrapper);
        item.setTag(entry);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get Context and clicked Entry
                Context context = getContext();
                Entry entry = (Entry) view.getTag();

                //Start new Activity EditEntry
                Class destinationActivity = EditEntryActivity.class;
                Intent intent = new Intent(context,destinationActivity);
                intent.putExtra("entry", entry);
                context.startActivity(intent);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
