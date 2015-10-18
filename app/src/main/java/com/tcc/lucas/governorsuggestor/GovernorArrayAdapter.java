package com.tcc.lucas.governorsuggestor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Lucas on 10/13/2015.
 */
public class GovernorArrayAdapter extends ArrayAdapter<Governor>
{
    private Context mContext;
    private List<Governor> mGovernorList;

    public GovernorArrayAdapter(Context context, List<Governor> governorList)
    {
        super(context, R.layout.governor_list_view, governorList);

        this.mContext = context;
        this.mGovernorList = governorList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.governor_list_view, parent, false);

        TextView governorName = (TextView) rowView.findViewById(R.id.governorName);
        TextView governorScore = (TextView) rowView.findViewById(R.id.governorScore);

        Governor governor = null;

        if(position <= mGovernorList.size())
            governor = mGovernorList.get(position);

        if(governor != null)
        {
            governorName.setText(governor.getName().toString());

            NumberFormat numberFormat = new DecimalFormat("#.00");
            double formattedScore = Double.parseDouble(numberFormat.format(governor.getTotalScore()));

            StringBuilder stringBuilder = new StringBuilder(Double.toString(formattedScore));
            governorScore.setText(stringBuilder);
        }

        return rowView;
    }
}
