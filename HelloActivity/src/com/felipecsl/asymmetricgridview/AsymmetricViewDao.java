package com.felipecsl.asymmetricgridview;

import android.view.View;

interface AsymmetricViewDao {
	/**
	 * 是否调试
	 * @return 
	 * 		true 是
	 * 		false 否
	 */
	boolean isDebugging();
	/**
	 * 获取列数
	 * @return 列数
	 */
	int getNumColumns();
	/**
	 * 
	 * @return
	 */
	boolean isAllowReordering();
	/**
	 * 点击
	 * @param index
	 * @param v
	 */
	void fireOnItemClick(int index, View v);
	/**
	 * 长按
	 * @param index
	 * @param v
	 * @return
	 */
	boolean fireOnItemLongClick(int index, View v);
	/**
	 * 获取列宽
	 * @return
	 */
	int getColumnWidth();
	/**
	 * 获取分割线高度
	 * @return
	 */
	int getDividerHeight();
	/**
	 * 获取请求行间隔
	 * @return
	 */
	int getRequestedHorizontalSpacing();
}
