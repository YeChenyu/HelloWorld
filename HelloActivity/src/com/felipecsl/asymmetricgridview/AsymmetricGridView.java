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
		//在视图树种全局事件改变时得到通知。这个全局事件不仅还包括整个树的布局，从绘画过程开始，触摸模式的改变等
		final ViewTreeObserver vto = getViewTreeObserver();
		if (vto != null) {
			vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override 
				public void onGlobalLayout() {
					//noinspection deprecation
					getViewTreeObserver().removeGlobalOnLayoutListener(this);
					//根据有效显示宽度计算列数
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
		//设置适配器
		gridAdapter = (AsymmetricGridViewAdapter) adapter;
		super.setAdapter(adapter);
		//计算行数
		gridAdapter.recalculateItemsPerRow();
	}
	
	/**
	 * 若UI变动则重新计算列数
	 */
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		//ScrollView ListView 嵌套，ListView适应ScrollView高度
//		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//				MeasureSpec.AT_MOST);
//		super.onMeasure(widthMeasureSpec, expandSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		viewData.determineColumns(getAvailableSpace());
	}

	/**
	 * 序列化 参数
	 */
	@Override @NonNull
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		return viewData.onSaveInstanceState(superState);
	}
	/**
	 * 重新初始化 序列化参数
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
		//ListView 重新定位
		setSelectionFromTop(20, 0);
	}

	/**
	 * 是否调试
	 * @return 
	 * 		true 是
	 * 		false 否
	 */
  	@Override 
  	public boolean isDebugging() {
  		return viewData.isDebugging();
  	}
  	public void setDebugging(boolean debugging) {
  		viewData.setDebugging(debugging);
  	}
  	/**
	 * 获取列数
	 * @return 列数
	 */
	@Override 
	public int getNumColumns() {
		return viewData.getNumColumns();
	}
	/**
	 * 获取列宽
	 * @return
	 */
	@Override 
	public int getColumnWidth() {
		return viewData.getColumnWidth(getAvailableSpace());
	}
	/**
	 * 获取有效显示宽度 
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
	 * 设置列宽
	 * @param width
	 */
	public void setRequestedColumnWidth(int width) {
		viewData.setRequestedColumnWidth(width);
	}
	/**
	 * 设置列数
	 * @param requestedColumnCount
	 */
	public void setRequestedColumnCount(int requestedColumnCount) {
		viewData.setRequestedColumnCount(requestedColumnCount);
	}

	public int getRequestedHorizontalSpacing() {
		return viewData.getRequestedHorizontalSpacing();
	}
	/**
	 * 设置列间距
	 * @param spacing
	 */
	public void setRequestedHorizontalSpacing(int spacing) {
		viewData.setRequestedHorizontalSpacing(spacing);
	}
	/**
	 * 计算列数
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
