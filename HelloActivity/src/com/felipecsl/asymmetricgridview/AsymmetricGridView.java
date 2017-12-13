package com.felipecsl.asymmetricgridview;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class AsymmetricGridView extends ListView implements AsymmetricViewDao {
	
	protected AdapterView.OnItemClickListener onItemClickListener;
	protected AdapterView.OnItemLongClickListener onItemLongClickListener;
	protected AsymmetricGridViewAdapter gridAdapter;
	private final AsymmetricViewData viewData;

	public AsymmetricGridView(Context context, AttributeSet attrs) {
		super(context, attrs);

		viewData = new AsymmetricViewData(context);
		//����ͼ����ȫ���¼��ı�ʱ�õ�֪ͨ�����ȫ���¼������������������Ĳ��֣��ӻ滭���̿�ʼ������ģʽ�ĸı��
		final ViewTreeObserver vto = getViewTreeObserver();
		if (vto != null) {
			vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override 
				public void onGlobalLayout() {
					//noinspection deprecation
					getViewTreeObserver().removeGlobalOnLayoutListener(this);
					//������Ч��ʾ��ȼ�������
					viewData.determineColumns(getAvailableSpace());
					if (gridAdapter != null) {
						gridAdapter.recalculateItemsPerRow();
					}
				}
			});
		}
	}

	@Override 
	public void setOnItemClickListener(OnItemClickListener listener) {
		onItemClickListener = listener;
	}

	

	@Override 
	public void setOnItemLongClickListener(OnItemLongClickListener listener) {
		onItemLongClickListener = listener;
	}

	@Override 
	public void setAdapter(@NonNull ListAdapter adapter) {
		if (!(adapter instanceof AsymmetricGridViewAdapter)) {
			throw new UnsupportedOperationException(
					"Adapter must be an instance of AsymmetricGridViewAdapter");
		}
		//����������
		gridAdapter = (AsymmetricGridViewAdapter) adapter;
		super.setAdapter(adapter);
		//��������
		gridAdapter.recalculateItemsPerRow();
	}
	
	/**
	 * ��UI�䶯�����¼�������
	 */
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		//ScrollView ListView Ƕ�ף�ListView��ӦScrollView�߶�
//		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//				MeasureSpec.AT_MOST);
//		super.onMeasure(widthMeasureSpec, expandSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		viewData.determineColumns(getAvailableSpace());
	}

	/**
	 * ���л� ����
	 */
	@Override @NonNull
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		return viewData.onSaveInstanceState(superState);
	}
	/**
	 * ���³�ʼ�� ���л�����
	 */
	@Override 
	public void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof AsymmetricViewData.SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}

		AsymmetricViewData.SavedState ss = (AsymmetricViewData.SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		viewData.onRestoreInstanceState(ss);
		//ListView ���¶�λ
		setSelectionFromTop(20, 0);
	}

	/**
	 * �Ƿ����
	 * @return 
	 * 		true ��
	 * 		false ��
	 */
  	@Override 
  	public boolean isDebugging() {
  		return viewData.isDebugging();
  	}
  	public void setDebugging(boolean debugging) {
  		viewData.setDebugging(debugging);
  	}
  	/**
	 * ��ȡ����
	 * @return ����
	 */
	@Override 
	public int getNumColumns() {
		return viewData.getNumColumns();
	}
	/**
	 * ��ȡ�п�
	 * @return
	 */
	@Override 
	public int getColumnWidth() {
		return viewData.getColumnWidth(getAvailableSpace());
	}
	/**
	 * ��ȡ��Ч��ʾ��� 
	 * @return
	 */
	private int getAvailableSpace() {
		return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
	}
	@Override 
	public boolean isAllowReordering() {
		return viewData.isAllowReordering();
	}
	/**
	 * �����п�
	 * @param width
	 */
	public void setRequestedColumnWidth(int width) {
		viewData.setRequestedColumnWidth(width);
	}
	/**
	 * ��������
	 * @param requestedColumnCount
	 */
	public void setRequestedColumnCount(int requestedColumnCount) {
		viewData.setRequestedColumnCount(requestedColumnCount);
	}

	public int getRequestedHorizontalSpacing() {
		return viewData.getRequestedHorizontalSpacing();
	}
	/**
	 * �����м��
	 * @param spacing
	 */
	public void setRequestedHorizontalSpacing(int spacing) {
		viewData.setRequestedHorizontalSpacing(spacing);
	}
	/**
	 * ��������
	 */
	public void determineColumns() {
		viewData.determineColumns(getAvailableSpace());
	}
	public void setAllowReordering(boolean allowReordering) {
  		viewData.setAllowReordering(allowReordering);
    	if (gridAdapter != null) {
    		gridAdapter.recalculateItemsPerRow();
    	}
  	}
	
	@Override 
	public void fireOnItemClick(int position, View v) {
		if (onItemClickListener != null) {
			onItemClickListener.onItemClick(this, v, position, v.getId());
		}
	}
	@Override 
	public boolean fireOnItemLongClick(int position, View v) {
		return onItemLongClickListener != null && onItemLongClickListener
				.onItemLongClick(this, v, position, v.getId());
	}
}
