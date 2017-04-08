package com.xintu.smartcar.btphone.adapter;

import java.util.List;
import java.util.Vector;

import com.xintu.smartcar.btphone.R;
import com.xintu.smartcar.btphone.biz.RadioButtonModel;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

@SuppressWarnings("rawtypes")
public class RadioGroupAdapter extends ListAdapter {

	private Activity activity;
	private List<RadioButtonModel> demodels;
	private LayoutInflater inflater;
	private int pos;
	private int lastPosition = -1;                                
	private Vector<Boolean> vector = new Vector<Boolean>();       

	public RadioGroupAdapter(Activity activity , List<RadioButtonModel> demodels){
		super(activity);
		this.activity = activity;
		this.demodels = demodels;
		inflater = LayoutInflater.from(activity);

		for (int i = 0; i < demodels.size(); i++) {
			vector.add(false);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return demodels.size();
	}

	@Override
	protected ViewHolder createViewHolder(View root) {
		// TODO Auto-generated method stub
		demodelHolder hold = new demodelHolder();
		hold.tv_demodel = (TextView) root.findViewById(R.id.tv_demodel);
		return hold;
	}

	@Override
	protected void fillView(View root, Object item, ViewHolder holder,
			int position) {
		// TODO Auto-generated method stub
		final demodelHolder hold = (demodelHolder)holder;
		hold.demodel = demodels.get(position);
		if(!"".equals(demodels.get(position).getContent())){
			hold.tv_demodel.setText(demodels.get(position).getContent());
		}
		if(vector.elementAt(position) == true){
			hold.tv_demodel.setTextColor(activity.getResources().getColor(R.color.white));
		}else{
			hold.tv_demodel.setTextColor(activity.getResources().getColor(R.color.slategrey));
		}
	}

	@Override
	protected int getItemViewId() {
		// TODO Auto-generated method stub
		return R.layout.item;
	}

	private class demodelHolder extends ViewHolder{
		private TextView tv_demodel;
		private RadioButtonModel demodel;
	}
	/**
	 * 修改选中时的状�?
	 * @param position
	 */
	public void changeState(int position){    
		if(lastPosition != -1)    
			vector.setElementAt(false, lastPosition);                   //取消上一次的选中状�?    
		vector.setElementAt(!vector.elementAt(position), position);     //直接取反即可    
		lastPosition = position;                                        //记录本次选中的位�?   
		notifyDataSetChanged();                                         //通知适配器进行更�?   
	}    
}
