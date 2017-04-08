package com.xintu.smartcar.btphone.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xintu.smartcar.btphone.R;
import com.xintu.smartcar.btphone.bean.ContactItem;


public class ContactAdapter extends BaseAdapter
{

	//上下文
	private Context m_context;
	//数据
	private ArrayList<ContactItem> m_listContact;
	//选择人员的存放列
	private ArrayList<ContactItem> selectpersons;
	//控制滑动条滑动的handler
	private Handler handler;
	
	public ContactAdapter(Context context, ArrayList<ContactItem> listContact){
		m_context = context;
		m_listContact = listContact;
		handler = new Handler();
		selectpersons = new ArrayList<ContactItem>();
	}
	
	@Override
	public int getCount() {
		return m_listContact.size();
	}
	public ArrayList<ContactItem> getList()
	{
		return m_listContact;
	}

	public ArrayList<ContactItem> getSelectList()
	{
		return selectpersons;
	}
	
	@Override
	public Object getItem(int position) {
		return m_listContact.get(position);
	}
	@Override
	public long getItemId(int position) {
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=null;
		ViewHold2 holder;
		ContactItem item = m_listContact.get(position);
		if(convertView==null)
		{
			view=View.inflate(m_context, R.layout.contact_item, null);
			holder=new ViewHold2();
			holder.tv_name=(TextView) view.findViewById(R.id.contact_item_name);
			holder.listview_tv_catlog = (TextView) view.findViewById(R.id.listview_tv_catlog);
			view.setTag(holder);
		}
		else
		{
			view=convertView;
			holder=(ViewHold2) view.getTag();
		}
		
		int section = getSectionForPosition(position);
		if (position == getPositionForSection(section))
		{
			holder.listview_tv_catlog.setVisibility(View.VISIBLE);
			holder.listview_tv_catlog.setText(item.getSortletter());
			//Log.d("MainActivity", item.getSortletter()+">>>>>>>>");
		}
		else
		{
			holder.listview_tv_catlog.setVisibility(View.GONE);
		}
		
		
		if(null != item.m_strName && !"".equals(item.m_strName)){
			holder.tv_name.setText(item.m_strName);
			/*if(item.m_iType == 0){
				holder.tv_name.setText(item.m_strName + "(手机)");
			}
			else{
				holder.tv_name.setText(item.m_strName + "(其他)");
			}*/
		}
		else if(null == item.m_strNumber && null != item.m_strNumber){
			   holder.tv_name.setText(item.m_strNumber);
		}
		return view;
	}
	
	public ArrayList<ContactItem> geList()
	{
		return m_listContact;
	}

	public int getSectionForPosition(int position)
	{
		return m_listContact.get(position).getSortletter().charAt(0);
	}

	public int getPositionForSection(int section)
	{
		for (int i = 0; i < getCount(); i++)
		{
			String sortStr = m_listContact.get(i).getSortletter();
			char firstChar = sortStr.charAt(0);
			if (firstChar == section)
			{
				return i;
			}
		}
		return -1;
	}

}

class ViewHold2
{
	TextView tv_name;
	TextView listview_tv_catlog;
}