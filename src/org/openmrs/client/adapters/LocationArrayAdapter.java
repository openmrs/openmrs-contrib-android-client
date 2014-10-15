package org.openmrs.client.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class LocationArrayAdapter extends ArrayAdapter<String> {

    private boolean emptyRemoved;

    public LocationArrayAdapter(Context context, List<String> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public int getCount() {
        if (emptyRemoved) {
            return super.getCount();
        }
        return super.getCount() - 1;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (!emptyRemoved) {
            emptyRemoved = true;
            setNotifyOnChange(false);
            remove(getItem(0));
            setNotifyOnChange(true);
        }
        return super.getDropDownView(position, convertView, parent);
    }

    @Override
    public long getItemId(int position) {
        if (emptyRemoved) {
            return position + 1;
        }
        return position;
    }
}
