package com.felipecsl.asymmetricgridview;

import android.os.Parcelable;

public interface AsymmetricItemDao extends Parcelable {
	/**
	 * ����ռ��Ԫ��
	 * @return
	 */
	public int getColumnSpan();
	/**
	 * ����ռ��Ԫ�� 
	 * @return
	 */
	public int getRowSpan();
}
