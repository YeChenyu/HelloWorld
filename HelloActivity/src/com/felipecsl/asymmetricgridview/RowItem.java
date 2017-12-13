package com.felipecsl.asymmetricgridview;


final class RowItem {
	//具体实现为AsymmetricItem
	private final AsymmetricItemDao item;  
	//索引
	private final int index;
	/**
	 * 对AsymmetricItem进行封装
	 * @param index
	 * @param item 具体实现为AsymmetricItem
	 */
	RowItem(int index, AsymmetricItemDao item) {
		this.item = item;
		this.index = index;
	}

	AsymmetricItemDao getItem() {
		return item;
	}

	int getIndex() {
		return index;
	}
}
