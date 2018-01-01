package com.msccs.hku.familycaregiver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.tempStructure.IdPolling;

import java.util.ArrayList;

/**
 * Created by HoiKit on 30/12/2017.
 */

public class VoterAdapter extends BaseAdapter {
    private Context context;
    private ArrayList voterNameList;

    public VoterAdapter() {
    }

    public VoterAdapter(Context context, ArrayList voterNameList) {
        this.context = context;
        this.voterNameList = voterNameList;
    }

    @Override
    public int getCount() {
        return voterNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return voterNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView txtView1 = (TextView) convertView.findViewById(android.R.id.text1);
        txtView1.setText(getItem(position).toString());

        return convertView;
    }

    public void updateData(ArrayList<String> list) {
        this.voterNameList = list;
        notifyDataSetChanged();
    }
}
