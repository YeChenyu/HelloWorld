package com.felipecsl.asymmetricgridview.app.widget;

import android.widget.ListAdapter;

import com.felipecsl.asymmetricgridview.app.model.DemoItem;

import java.util.List;

public interface MyBaseAdapter extends ListAdapter {
	/**
	 * ����µ�����Դ
	 * @param newItems
	 */
	void appendItems(List<DemoItem> newItems);
	/**
	 * ��������Դ
	 * @param newItems
	 */
	void setItems(List<DemoItem> moreItems);
}
