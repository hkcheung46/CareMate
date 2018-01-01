package com.msccs.hku.familycaregiver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.msccs.hku.familycaregiver.Model.CustomTasks;
import com.msccs.hku.familycaregiver.Model.Polling;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.tempStructure.IdPolling;
import com.msccs.hku.familycaregiver.tempStructure.IdTask;

import java.util.ArrayList;

/**
 * Created by HoiKit on 31/12/2017.
 */

public class GroupPollingListAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<IdPolling> idPollingList;

    public GroupPollingListAdapter() {
    }

    public GroupPollingListAdapter(Context context, ArrayList<IdPolling> idPollingList) {
        this.context = context;
        this.idPollingList = idPollingList;
    }

    @Override
    public int getCount() {
        return idPollingList.size();
    }

    @Override
    public Object getItem(int position) {
        return idPollingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.group_polling_list_item, parent, false);
        }

        TextView pollingQuestionLbl = (TextView) convertView.findViewById(R.id.pollingQuestionLbl);
        TextView pollingStatusLbl = (TextView) convertView.findViewById(R.id.pollingStatusLbl);

        Polling polling = this.getPolling(position);
        pollingQuestionLbl.setText(polling.getPollingQuestion());

        switch (polling.getStatus()){
            case Polling.ACTIVE_POLLING_STATUS:
                pollingStatusLbl.setText(R.string.activePolling);
                pollingStatusLbl.setBackground(context.getResources().getDrawable(R.drawable.round_rect_green_shape));
                break;
            case Polling.CLOSED_POLLING_STATUS:
                pollingStatusLbl.setText(R.string.closedPolling);
                pollingStatusLbl.setBackground(context.getResources().getDrawable(R.drawable.round_rect_grey_shape));
                break;
        }
        return convertView;
    }


    //insert two method here for getting the CustomTask and Task Id directly
    public String getPollingId(int position){
        return idPollingList.get(position).getPollingId();
    };

    public Polling getPolling(int position){
        return idPollingList.get(position).getPolling();
    }

    public void removeData() {
        this.idPollingList.clear();
        notifyDataSetChanged();
    }
}
