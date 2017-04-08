package com.xintu.smartcar.btphone.view;

import com.xintu.smartcar.btphone.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * 
 *	@描述：自定义的listview
 */
public class MyListView extends ListView implements OnScrollListener, OnClickListener{
	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	private final static int RATIO = 3;

	private LayoutInflater inflater;

	private LinearLayout headView;

	private LinearLayout footerView;
	private ProgressBar footerProgressBar;
	private TextView footerInfoTv;

	private boolean isRecored;

	private int headContentWidth;
	private int headContentHeight;

	private int startY;
	private int firstItemIndex;
	private int mTotalItemCount;

	private int downY;
	private int upY;

	private int state;

	private boolean isBack;

	private OnRefreshListener refreshListener;

	private onHistoryListener historyListener;

	private boolean isRefreshable;
	private Context mContext;
	private boolean isHistoryShow;
	
	private boolean isRefreshing;
	private boolean isHistorying;
	
	public MyListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	private void init(Context context)
	{
		mContext = context;
		isHistoryShow = true;
		inflater = LayoutInflater.from(context);
		headView = (LinearLayout) inflater.inflate(R.layout.daily_listview_head, null);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();

		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		addHeaderView(headView, null, false);
		footerView = (LinearLayout) inflater.inflate(R.layout.daily_listview_footer, null);
		footerProgressBar = (ProgressBar) footerView.findViewById(R.id.footer_progressBar);
		footerInfoTv = (TextView) footerView.findViewById(R.id.footer_infoText);
		footerInfoTv.setOnClickListener(this);
		addFooterView(footerView, null, false);
		setOnScrollListener(this);

		state = DONE;
		isRefreshable = false;
		isRefreshing = false;
		isHistorying = false;
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int visibleItemCount, int totalItemCount)
	{
		firstItemIndex = firstVisiableItem;
		mTotalItemCount = totalItemCount;
	}

	public void onScrollStateChanged(AbsListView arg0, int arg1)
	{

	}

	public boolean onTouchEvent(MotionEvent event)
	{

		if (isRefreshable)
		{
			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					if (firstItemIndex == 0 && !isRecored)
					{
						isRecored = true;
						startY = (int) event.getY();
					}
					downY = (int) event.getY();
					break;

				case MotionEvent.ACTION_UP:
					if (getFirstVisiblePosition() == 0)
					{
						if (state != REFRESHING && state != LOADING && !isHistorying)
						{
							if (state == DONE)
							{
							}
							if (state == PULL_To_REFRESH)
							{
								state = DONE;
								changeHeaderViewByState();

							}
							if (state == RELEASE_To_REFRESH)
							{
								state = REFRESHING;
								changeHeaderViewByState();
								onRefresh();

							}
						}
					}
					else if (getLastVisiblePosition() == mTotalItemCount - 1)
					{
						upY = (int) event.getY();
						if (downY - upY > 150 && isHistoryShow && !isRefreshing && !isHistorying && state != REFRESHING)
						{
							onHistory();
						}

					}

					isRecored = false;
					isBack = false;

					break;

				case MotionEvent.ACTION_MOVE:
					int tempY = (int) event.getY();

					if (!isRecored && firstItemIndex == 0)
					{
						isRecored = true;
						startY = tempY;
					}

					if (state != REFRESHING && isRecored && state != LOADING && !isHistorying)
					{

						if (state == RELEASE_To_REFRESH)
						{
							setSelection(0);

							if (((tempY - startY) / RATIO < headContentHeight) && (tempY - startY) > 0)
							{
								state = PULL_To_REFRESH;
								changeHeaderViewByState();

							}
							else if (tempY - startY <= 0)
							{
								state = DONE;
								changeHeaderViewByState();

							}
							else
							{
							}
						}
						if (state == PULL_To_REFRESH)
						{

							setSelection(0);

							if ((tempY - startY) / RATIO >= headContentHeight)
							{
								state = RELEASE_To_REFRESH;
								isBack = true;
								changeHeaderViewByState();

							}
							else if (tempY - startY <= 0)
							{
								state = DONE;
								changeHeaderViewByState();

							}
						}

						if (state == DONE)
						{
							if (tempY - startY > 0)
							{
								state = PULL_To_REFRESH;
								changeHeaderViewByState();
							}
						}

						if (state == PULL_To_REFRESH)
						{
							headView.setPadding(0, -1 * headContentHeight + (tempY - startY) / RATIO, 0, 0);

						}

						if (state == RELEASE_To_REFRESH)
						{
							headView.setPadding(0, (tempY - startY) / RATIO - headContentHeight, 0, 0);
						}

					}
					break;
			}
		}

		return super.onTouchEvent(event);
	}

	private void changeHeaderViewByState()
	{
		switch (state)
		{
			case RELEASE_To_REFRESH:
				break;
			case PULL_To_REFRESH:
				break;

			case REFRESHING:

				headView.setPadding(0, 0, 0, 0);

				break;
			case DONE:
				headView.setPadding(0, -1 * headContentHeight, 0, 0);

				break;
		}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener)
	{
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener
	{
		public void onRefresh();
	}

	public void onRefreshComplete(String latestTime)
	{
		state = DONE;
		changeHeaderViewByState();
		isRefreshing = false;
	}

	private void onRefresh()
	{
		isRefreshing = true;
		if (refreshListener != null)
		{
			refreshListener.onRefresh();
		}
	}

	public void setonHistoryListener(onHistoryListener historyListener)
	{
		this.historyListener = historyListener;
	}

	public interface onHistoryListener
	{
		public void onHistory();
	}

	private synchronized void onHistory()
	{
		System.out.println("onhistory start......");
		isHistorying = true;
		if (historyListener != null)
		{
			if (footerInfoTv.getText().equals("更多"))
			{
				footerProgressBar.setVisibility(View.VISIBLE);
				footerInfoTv.setText("加载�?....");

			}
			historyListener.onHistory();
		}
	}

	public void onHistoryComplete()
	{
		footerProgressBar.setVisibility(View.GONE);
		footerInfoTv.setText("更多");
		isHistorying = false;
		System.out.println("onhistory end......");
	}

	// 估计headView的width以及height
	private void measureView(View child)
	{
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null)
		{
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0)
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		}
		else
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setAdapter(BaseAdapter adapter, String latestTime)
	{
		super.setAdapter(adapter);
	}
	
	public void setAdapter(BaseAdapter adapter)
	{
		setAdapter(adapter, "");
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.footer_infoText:
				if (state != REFRESHING && !isRefreshing && !isHistorying)
				{
					onHistory();
				}

				break;
		}

	}

	public void showRefresh()
	{
		if (state == DONE && !isHistorying)
		{
			System.out.println("show refreshing ....");
			setSelection(0);
			state = REFRESHING;
			changeHeaderViewByState();
			onRefresh();
		}

	}
	
	public void startGetComment()
	{
		setSelection(0);
		state = REFRESHING;
		changeHeaderViewByState();
		isRefreshing = true;
	}
	
	public void hiddenMore(){
		isHistoryShow = false;
		this.removeFooterView(footerView); 
	}
	
	public void showMore(){
		isHistoryShow = true;
		this.addFooterView(footerView);
	}
	
	public boolean getIsHistoryShow()
	{
		return isHistoryShow;
	}
}
