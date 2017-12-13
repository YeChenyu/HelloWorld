package com.felipecsl.asymmetricgridview.app.widget;

import android.widget.ListAdapter;

import com.felipecsl.asymmetricgridview.app.model.DemoItem;

import java.util.List;

public interface MyBaseAdapter extends ListAdapter {
	/**
	 * 添加新的数据源
	 * @param newItems
	 */
	void appendItems(List<DemoItem> newItems);
	/**
	 * 设置数据源
	 * @param newItems
	 */
	void setItems(List<DemoItem> moreItems);
}
