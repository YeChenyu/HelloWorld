package com.felipecsl.asymmetricgridview;


final class RowItem {
	//����ʵ��ΪAsymmetricItem
	private final AsymmetricItemDao item;  
	//����
	private final int index;
	/**
	 * ��AsymmetricItem���з�װ
	 * @param index
	 * @param item ����ʵ��ΪAsymmetricItem
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
