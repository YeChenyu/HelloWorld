package com.felipecsl.asymmetricgridview;

import android.os.Parcelable;

public interface AsymmetricItemDao extends Parcelable {
	/**
	 * 列所占单元数
	 * @return
	 */
	public int getColumnSpan();
	/**
	 * 行所占单元数 
	 * @return
	 */
	public int getRowSpan();
}
