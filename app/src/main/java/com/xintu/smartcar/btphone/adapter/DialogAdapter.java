package com.xintu.smartcar.btphone.adapter;

import java.util.ArrayList;

import com.xintu.smartcar.btphone.R;
import com.xintu.smartcar.btphone.bean.SelectContact;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogAdapter extends BaseAdapter {

	Context context;
	ArrayList <SelectContact> list;
	public DialogAdapter(Context context,ArrayList <SelectContact> list){
		this.context=context;
		this.list=list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view=null;
		view=View.inflate(context, R.layout.dialog_item, null);
		ImageView iv_item=(ImageView) view.findViewById(R.id.iv_item);
		TextView tv_dialog_name=(TextView) view.findViewById(R.id.tv_dialog_name);
		TextView tv_dialog_number=(TextView) view.findViewById(R.id.tv_dialog_number);
	     Log.d("MM", position+"<><><><>"+list.size());
			SelectContact selectContact = list.get(position);
			if(null!=selectContact.getName() && !"".equals(selectContact.getName())){
				tv_dialog_name.setText(selectContact.getName());
				tv_dialog_number.setText(" : "+selectContact.getNumber());
			}else{
				tv_dialog_name.setText("");
				tv_dialog_number.setText("");
			}
			setData(position,iv_item);
		return view;
	}

	public void setData(int position,ImageView iv_item){

		switch (position) {
		case 0:
			iv_item.setImageResource(R.drawable.image1);	
			break;
		case 1:
			iv_item.setImageResource(R.drawable.image2);
			break;
		case 2:
			iv_item.setImageResource(R.drawable.image3);
			break;
		case 3:
			iv_item.setImageResource(R.drawable.image4);
			break;
		case 4:	
			iv_item.setImageResource(R.drawable.image5);
			break;
		case 5:	
			iv_item.setImageResource(R.drawable.image6);
			break;
		case 6:	
			iv_item.setImageResource(R.drawable.image7);
			break;
		case 7:	
			iv_item.setImageResource(R.drawable.image8);
			break;
		case 8:	
			iv_item.setImageResource(R.drawable.image9);
			break;
		case 9:	
			iv_item.setImageResource(R.drawable.image10);
			break;
		default:
			iv_item.setImageResource(R.drawable.image1);	
			break;
		}
	}
}
