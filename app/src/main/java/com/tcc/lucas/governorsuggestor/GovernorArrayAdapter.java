package com.tcc.lucas.governorsuggestor;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 10/13/2015.
 */
public class GovernorArrayAdapter extends ArrayAdapter<Governor>
{
    private Context mContext;
    private List<Governor> mGovernorList;
    private List<Integer> mIconsList;

    public GovernorArrayAdapter(Context context, List<Governor> governorList)
    {
        super(context, R.layout.governor_list_view, governorList);

        mContext = context;
        mGovernorList = governorList;
        mIconsList = new ArrayList<>();

        mIconsList.add(R.drawable.circle_shape_orange);
        mIconsList.add(R.drawable.circle_shape_green);
        mIconsList.add(R.drawable.circle_shape_yellow);
        mIconsList.add(R.drawable.circle_shape_blue);
        mIconsList.add(R.drawable.circle_shape_red);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.governor_list_view, parent, false);

        TextView governorName = (TextView) rowView.findViewById(R.id.governorName);
        TextView performanceScoreTextView = (TextView) rowView.findViewById(R.id.performanceScore);
        TextView batteryScoreTextView = (TextView) rowView.findViewById(R.id.batteryScore);
        TextView governorLetter = (TextView) rowView.findViewById(R.id.governorLetterTextView);

        ImageView governorIcon = (ImageView) rowView.findViewById(R.id.governorImageView);

        Governor governor = null;

        if(position <= mGovernorList.size())
            governor = mGovernorList.get(position);

        if(governor != null)
        {
            String governorStr = governor.getName().toString();

            governorName.setText(governorStr);
            governorLetter.setText(Character.toString(governorStr.charAt(0)));

            NumberFormat numberFormat = new DecimalFormat("#.00");

            double performanceScore = Double.parseDouble(numberFormat.format(governor.getPerformanceScore()));
            String performanceText = rowView.getResources().getString(R.string.performanceScoreText) + Double.toString(performanceScore);
            performanceScoreTextView.setText(performanceText);

            double batteryScore = Double.parseDouble(numberFormat.format(governor.getBatteryScore()));
            String batteryText = rowView.getResources().getString(R.string.batteryScoreText) + Double.toString(batteryScore);
            batteryScoreTextView.setText(batteryText);

            governorIcon.setImageDrawable(rowView.getResources().getDrawable(mIconsList.get(position), this.getContext().getTheme()));
        }

        return rowView;
    }
}
