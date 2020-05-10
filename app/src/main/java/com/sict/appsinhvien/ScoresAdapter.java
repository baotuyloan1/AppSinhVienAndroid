package com.sict.appsinhvien;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ScoresAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Scores> scoresList;


    public ScoresAdapter(Context context, int layout, List<Scores> scoresList) {
        this.context = context;
        this.layout = layout;
        this.scoresList = scoresList;
    }

    @Override
    public int getCount() {
        return scoresList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView txtMonHoc, txtScores;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            viewHolder = new ViewHolder();
            viewHolder.txtMonHoc = convertView.findViewById(R.id.textViewMonHoc);
            viewHolder.txtScores = convertView.findViewById(R.id.textViewScores);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Scores scores = scoresList.get(position);
        viewHolder.txtScores.setText(scores.getScores()+"");
        viewHolder.txtMonHoc.setText(scores.getTenMonHoc());
        return convertView;
    }
}
