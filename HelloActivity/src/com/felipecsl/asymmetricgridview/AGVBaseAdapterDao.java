package com.felipecsl.asymmetricgridview;


import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

interface AGVBaseAdapterDao<T extends RecyclerView.ViewHolder> {
	
	int getActualItemCount();
  
	AsymmetricItemDao getItem(int position);
  
	void notifyDataSetChanged();
	/**
	 * ��ȡItem���� ż�����У�1 �������У�0
	 */
	int getItemViewType(int actualIndex);
  
	AsymmetricViewHolder<T> onCreateAsymmetricViewHolder(int position, ViewGroup parent, int viewType);
	
	void onBindAsymmetricViewHolder(AsymmetricViewHolder<T> holder, ViewGroup parent, int position);
}
