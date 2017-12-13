package com.felipecsl.asymmetricgridview;


import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

/**
 * �� AppsAdapter ���з�װ��������һЩ�ӿ�
 * @author chenyuye
 */
public final class AsymmetricGridViewAdapter extends BaseAdapter
    implements AGVBaseAdapterDao, WrapperListAdapter {
	//AppsAdapter�������Դ
	private final ListAdapter appsAdapter;
	//�������ӿ�
	private final AdapterImpl adapterImpl;
	/**
	 * �� AppsAdapter ���з�װ��������һЩ�ӿ�
	 * @param context
	 * @param listView  AsymmetricGridView
	 * @param adapter  AppsAdapter
	 */
	public AsymmetricGridViewAdapter(Context context, AsymmetricGridView listView,
			ListAdapter adapter) {
		this.adapterImpl = new AdapterImpl(context, this, listView);
		this.appsAdapter = adapter;
		//ע������Դ�仯�۲���
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
	 * ���¼���Item��ȡÿ������
	 */
	void recalculateItemsPerRow() {
		adapterImpl.recalculateItemsPerRow();
	}
	/**
	 * ��ȡAsymmetricItem
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
	 * �ص��ע���ӿؼ�����������ʵ��
	 */
	@Override 
	public View getView(int position, View convertView, ViewGroup parent) {
		//����ListView����Item  LinearLayout
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
	 * ��ȡItem���� ż�����У�1 �������У�0
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