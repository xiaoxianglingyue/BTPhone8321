package com.xintu.smartcar.btphone.adapter;

import java.util.List;

import com.xintu.smartcar.btphone.R;
import com.xintu.smartcar.btphone.bean.DeviceInfo;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class BTDeviceAdapter extends BaseAdapter {

	private Context context;
	private List<DeviceInfo> lstDeviceInfo;
	public Handler ahandler;
	public BTDeviceAdapter(Context context,List<DeviceInfo> btDevice){
		this.context=context;
		this.lstDeviceInfo=btDevice;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lstDeviceInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return lstDeviceInfo.get(position);
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
		ViewHold holder;
		if(convertView==null)
		{
			view=View.inflate(context, R.layout.bluetooth_devices_item, null);
			holder=new ViewHold();
			holder.tv_tbdevices_mac=(TextView) view.findViewById(R.id.tv_tbdevices_mac);
			//holder.iv_deletemac=(ImageView) view.findViewById(R.id.iv_deletemac);
			view.setTag(holder);
		}
		else
		{
			view=convertView;
			holder=(ViewHold) view.getTag();
		}
		
		DeviceInfo btDevice=lstDeviceInfo.get(position);
		
		if(null != btDevice.m_strName){
			holder.tv_tbdevices_mac.setText(btDevice.m_strName);
		}
		/*if(null != ahandler){
			Message msg = ahandler.obtainMessage();
			Bundle bundle=new Bundle();
			bundle.putInt("position", position);
			bundle.putString("strMac", btDevice.m_strMacAddr);
			msg.setData(bundle);
			msg.what = 1;
			
			ahandler.sendMessage(msg);
		}*/
		return view;
	}
}

class ViewHold
{
	TextView tv_tbdevices_mac;
	//ImageView iv_deletemac;
}

