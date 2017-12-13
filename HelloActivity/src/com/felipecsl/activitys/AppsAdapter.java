package com.felipecsl.activitys;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.helloactivity.R;
import com.felipecsl.asymmetricgridview.app.model.DemoItem;
import com.felipecsl.asymmetricgridview.app.widget.MyBaseAdapter;

public class AppsAdapter extends  ArrayAdapter<DemoItem> implements MyBaseAdapter{
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<DemoItem> mItems;
	private boolean isShowDelete = false;

	@SuppressWarnings("rawtypes")
	public AppsAdapter(final Context context, final List<DemoItem> items) {
		super(context, 0, items);
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mItems = items;
	}

	public AppsAdapter(final Context context) {
		super(context, 0);
		this.mContext = context;
		mInflater = LayoutInflater.from(context);

	}
	
	@Override 
	public int getViewTypeCount() {
		return 2;
	}

	/**
	 * ��ȡItem���� ż�����У�1 �������У�0
	 */
	@Override 
	public int getItemViewType(int position) {
		int clum = mItems.get(position).getColumnSpan();
		int row = mItems.get(position).getRowSpan();
		if(row == 1 && clum == 1){
			return 0;
		}else if(row == 1 && clum == 2){
			return 1;
		}else if(row == 2 && clum == 1){
			return 2;
		}else{
			return 3;
		}
	}

	@Override
	public DemoItem getItem(int position) {
		// TODO Auto-generated method stub
		return mItems.get(position);
	}

	@SuppressLint("NewApi")
	@Override
	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
		DemoItem item = mItems.get(position);
		//����ͼ��������� ���� ���� ����
		if ( item.getColumnSpan() == 1 ) {
			convertView = mInflater.inflate(R.layout.felipescsl_list_item_v, null);
		}else{
			convertView = mInflater.inflate(R.layout.felipescsl_list_item_h, null);
		}
		//�����ӿؼ�
		ImageView mImageView = (ImageView) convertView.findViewById(R.id.mImage);
		TextView mTextView = (TextView) convertView.findViewById(R.id.mText);
		ImageView unInstall = (ImageView) convertView.findViewById(R.id.unInstall);
		unInstall.setVisibility(View.INVISIBLE);
		//����Ӧ�õĻ��������Ƿ�������ж��Ƿ���ʾ����
		if (null != item.title) {
			if(item.drawable != null)
				convertView.setBackgroundDrawable(item.drawable);//����
			if (isShowDelete) {
				unInstall.setVisibility(View.VISIBLE);
			}
			mImageView.setVisibility(View.VISIBLE);	//ͼ��
			mTextView.setVisibility(View.VISIBLE);	//��ǩ
			mTextView.setTextSize(item.titleSize);
			if(item.icon != null)	//
				mImageView.setImageBitmap(item.icon);
			if(!item.title.equals(""))	//
				mTextView.setText(item.title);
			else
				mTextView.setText("������");
			
		} else {
			if(item.drawable != null)
				convertView.setBackgroundDrawable(item.drawable);
			mImageView.setVisibility(View.VISIBLE);
			mTextView.setVisibility(View.VISIBLE);
			mTextView.setTextSize(item.titleSize);
			if(item.icon != null)
				mImageView.setImageBitmap(item.icon);
			if(!item.title.equals(""))
				mTextView.setText(item.title);
			else
				mTextView.setText("������");
		}
		
		return convertView;
	}

	/**
	 * ��������Դ
	 * @param items
	 */
	public void setData(List<DemoItem> items) {
		this.mItems = items;
		notifyDataSetChanged();
	}
	/**
	 * �Ƿ�Ϊж��״̬
	 * @return
	 */
	public boolean isShowDelete() {
		return isShowDelete;
	}
	/**
	 * ����ж��״̬
	 * @param isShowDelete
	 */
	public void setShowDelete(boolean isShowDelete) {
		this.isShowDelete = isShowDelete;
		notifyDataSetChanged();
	}

	@Override
	public void setItems(List<DemoItem> moreItems) {
		// TODO Auto-generated method stub
	    clear();
	    this.appendItems(moreItems);
	}

	@Override
	public void appendItems(List<DemoItem> newItems) {
		// TODO Auto-generated method stub
	    this.addAll(newItems);
	    notifyDataSetChanged();
	}
}
