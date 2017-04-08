package com.xintu.smartcar.btphone.adapter;

import java.util.List;

import com.xintu.smartcar.btphone.R;
import com.xintu.smartcar.btphone.bean.DeviceInfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NoBTDeviceAdapter extends BaseAdapter {

	private Context context;
	private List<DeviceInfo> macList;
	public NoBTDeviceAdapter(Context context, List<DeviceInfo> macList){
		this.context=context;
		this.macList=macList;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return macList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return macList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view=null;
		ViewHolder holder;
		if(convertView==null)
		{
			view=View.inflate(context, R.layout.nopeidui_item, null);
			holder=new ViewHolder();
			holder.tv_mac=(TextView) view.findViewById(R.id.tv_mac);
			view.setTag(holder);
		}
		else
		{
			view=convertView;
			holder=(ViewHolder) view.getTag();
		}
		DeviceInfo btDevice=macList.get(position);
		holder.tv_mac.setText(btDevice.m_strMacAddr);
		return view;
	}
}

class ViewHolder
{
	TextView tv_mac;
}
