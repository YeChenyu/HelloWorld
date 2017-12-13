package com.felipecsl.asymmetricgridview;

import android.view.View;

interface AsymmetricViewDao {
	/**
	 * �Ƿ����
	 * @return 
	 * 		true ��
	 * 		false ��
	 */
	boolean isDebugging();
	/**
	 * ��ȡ����
	 * @return ����
	 */
	int getNumColumns();
	/**
	 * 
	 * @return
	 */
	boolean isAllowReordering();
	/**
	 * ���
	 * @param index
	 * @param v
	 */
	void fireOnItemClick(int index, View v);
	/**
	 * ����
	 * @param index
	 * @param v
	 * @return
	 */
	boolean fireOnItemLongClick(int index, View v);
	/**
	 * ��ȡ�п�
	 * @return
	 */
	int getColumnWidth();
	/**
	 * ��ȡ�ָ��߸߶�
	 * @return
	 */
	int getDividerHeight();
	/**
	 * ��ȡ�����м��
	 * @return
	 */
	int getRequestedHorizontalSpacing();
}
