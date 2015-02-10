package com.pipit.waffle;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Kyle on 2/10/2015.
 */
public class DrawerListAdapter extends ArrayAdapter<DrawerItem> {

    public DrawerListAdapter(Context context, int resource, List<DrawerItem> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.drawer_list_item, parent, false);

        }

        DrawerItem p = getItem(position);

        if (p != null) {

            TextView tt = (TextView) v.findViewById(R.id.drawer_text);
            ImageView ii = (ImageView) v.findViewById(R.id.drawer_icon);

            if (tt != null) {
                tt.setText(p.getText());
            }
            if (ii != null) {
                if(p.getImage_id()==0)
                    ii.setImageDrawable(v.getContext().getDrawable(R.drawable.icon_me));
                else if(p.getImage_id()==1)
                    ii.setImageDrawable(v.getContext().getDrawable(R.drawable.icon_ask));
                else if(p.getImage_id()==2)
                    ii.setImageDrawable(v.getContext().getDrawable(R.drawable.icon_answer));
                else if(p.getImage_id()==3)
                    ii.setImageDrawable(v.getContext().getDrawable(R.drawable.icon_settings));
            }
        }

        return v;

    }
}
