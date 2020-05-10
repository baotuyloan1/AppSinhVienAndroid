package com.sict.appsinhvien;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SinhVienAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private int layout;
    private List<SinhVien> sinhVienList;
    private MainActivity mainActivity;



    public SinhVienAdapter(Context context, int layout, List<SinhVien> sinhVienList, MainActivity mainActivity) {
        this.context = context;
        this.layout = layout;
        this.sinhVienList = sinhVienList;
        this.mainActivity = mainActivity;
    }

    public SinhVienAdapter(Context context, int layout, List<SinhVien> sinhVienList) {
        this.context = context;
        this.layout = layout;
        this.sinhVienList = sinhVienList;
    }

    @Override
    public int getCount() {
        return sinhVienList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void onClick(View v) {

    }

    private class ViewHolder{
        TextView txtID, txtFullName, txtSex, txtBirthDay, txtPhone, txtAddress, txtdDescription;
        ImageView imageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,parent,false);
            holder = new ViewHolder();
            holder.txtID = (TextView) convertView.findViewById(R.id.textViewID);
            holder.txtFullName = (TextView) convertView.findViewById(R.id.textViewTen);
            holder.txtSex = (TextView) convertView.findViewById(R.id.textViewSex);
            holder.txtBirthDay = (TextView) convertView.findViewById(R.id.textViewBirthday);
            holder.txtPhone = (TextView) convertView.findViewById(R.id.textViewSDT);
            holder.txtAddress = (TextView) convertView.findViewById(R.id.textViewAddress);
            holder.txtdDescription = (TextView) convertView.findViewById(R.id.textViewDescription);
            holder.imageView = (ImageView)convertView.findViewById(R.id.imageAvatar);
            convertView.setTag(holder);
        }else{
        holder = (ViewHolder) convertView.getTag();
        }
        SinhVien sinhVien = sinhVienList.get(position);
        holder.txtID.setText(sinhVien.getSinhVienID());
        holder.txtAddress.setText(sinhVien.getAddress());
        holder.txtBirthDay.setText(sinhVien.getBirthDay());
        holder.txtdDescription.setText(sinhVien.getDescription());
        holder.txtFullName.setText(sinhVien.getFullName());
        holder.txtPhone.setText(sinhVien.getPhone());
        if (sinhVien.isSex()) {
            holder.txtSex.setText("Nam");
        }else{
            holder.txtSex.setText("Nữ");
        }
        if (sinhVien.isChecked()){
            convertView.setBackgroundColor(Color.LTGRAY);
        }
        else {
            convertView.setBackgroundColor(Color.WHITE);
        }
        Picasso.with(context).load(sinhVien.getImageUrl()).fit().centerCrop().into(holder.imageView);
        return convertView;
    }


}
