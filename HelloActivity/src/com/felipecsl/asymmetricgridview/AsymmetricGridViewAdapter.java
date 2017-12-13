package com.felipecsl.asymmetricgridview;


import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

/**
 * 对 AppsAdapter 进行封装，并增加一些接口
 * @author chenyuye
 */
public final class AsymmetricGridViewAdapter extends BaseAdapter
    implements AGVBaseAdapterDao, WrapperListAdapter {
	//AppsAdapter存放数据源
	private final ListAdapter appsAdapter;
	//适配器接口
	private final AdapterImpl adapterImpl;
	/**
	 * 对 AppsAdapter 进行封装，并增加一些接口
	 * @param context
	 * @param listView  AsymmetricGridView
	 * @param adapter  AppsAdapter
	 */
	public AsymmetricGridViewAdapter(Context context, AsymmetricGridView listView,
			ListAdapter adapter) {
		this.adapterImpl = new AdapterImpl(context, this, listView);
		this.appsAdapter = adapter;
		//注册数据源变化观察者
		appsAdapter.registerDataSetObserver(new GridDataSetObserver());
	}

	class GridDataSetObserver extends DataSetObserver {
		
		@Override 
		public void onChanged() {
			recalculateItemsPerRow();
		}

		@Override 
		public void onInvalidated() {
			recalculateItemsPerRow();
		}
	}
	/**
	 * 重新计算Item获取每行数据
	 */
	void recalculateItemsPerRow() {
		adapterImpl.recalculateItemsPerRow();
	}
	/**
	 * 获取AsymmetricItem
	 */
	@Override 
	public AsymmetricItemDao getItem(int position) {
		return (AsymmetricItemDao) appsAdapter.getItem(position);
	}

	
	@Override 
	public AsymmetricViewHolder onCreateAsymmetricViewHolder(
			int position, ViewGroup parent, int viewType) {
		return new AsymmetricViewHolder<>(appsAdapter.getView(position, null, parent));
	}

	@Override 
	public void onBindAsymmetricViewHolder(
      AsymmetricViewHolder holder, ViewGroup parent, int position) {
		appsAdapter.getView(position, holder.itemView, parent);
	}

	@Override 
	public long getItemId(int position) {
		return appsAdapter.getItemId(position);
	}

	/**
	 * 重点关注，子控件布局由这里实现
	 */
	@Override 
	public View getView(int position, View convertView, ViewGroup parent) {
		//创建ListView的子Item  LinearLayout
		AdapterImpl.ViewHolder viewHolder = adapterImpl.onCreateViewHolder();
		adapterImpl.onBindViewHolder(viewHolder, position, parent);
		return viewHolder.itemView;
	}

	@Override 
	public int getCount() {
	    // Returns the row count for ListView display purposes
	    return adapterImpl.getRowCount();
	}
	/**
	 * 获取Item类型 偶数排列：1 基数排列：0
	 */
	@Override 
	public int getItemViewType(int position) {
		return appsAdapter.getItemViewType(position);
	}

	@Override 
	public int getActualItemCount() {
		return appsAdapter.getCount();
	}

	@Override 
	public ListAdapter getWrappedAdapter() {
		return appsAdapter;
	}
}