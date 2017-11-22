package com.isoftstone.smartsite.model.inspectplan.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.isoftstone.smartsite.R;
import com.isoftstone.smartsite.http.EQIRankingBean;
import com.isoftstone.smartsite.model.inspectplan.activity.PatrolPlanActivity;
import com.isoftstone.smartsite.model.inspectplan.bean.InspectPlanBean;
import com.isoftstone.smartsite.model.main.ui.AirMonitoringRankAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class PatrolPlanAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<EQIRankingBean.AQI> mList;

	public PatrolPlanAdapter(Context context){
		this.mContext = context;
	}

	public void setList(ArrayList<EQIRankingBean.AQI> list){
		mList = list;

	}
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PatrolPlanAdapter.ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_patrolplan_adapter,parent,false);
			holder = new PatrolPlanAdapter.ViewHolder(convertView);
		} else {
			holder = (PatrolPlanAdapter.ViewHolder) convertView.getTag();
		}

		return convertView;
	}

	private class ViewHolder{
		private TextView title;
		private TextView time;
		private TextView address;
		private ImageView zhuangtai;

		public ViewHolder(View convertView){
			this.title = (TextView) convertView.findViewById(R.id.title);
			this.time = (TextView) convertView.findViewById(R.id.time);
			this.address = (TextView) convertView.findViewById(R.id.address);
			this.zhuangtai = (ImageView) convertView.findViewById(R.id.zhuangtai);
			convertView.setTag(this);
		}
	}

}