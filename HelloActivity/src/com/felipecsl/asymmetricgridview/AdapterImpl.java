package com.felipecsl.asymmetricgridview;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.android.helloactivity.R;
import com.felipecsl.activitys.FyLog;


final class AdapterImpl implements View.OnClickListener, View.OnLongClickListener {
	
	private static final String TAG = "AdapterImpl";
	private final Context mContext;
  
	private final Map<Integer, RowInfo> itemsPerRow = new HashMap<Integer, RowInfo>();//行数据表
	private final ObjectPool<LinearLayout> linearLayoutPool;
	private final Map<Integer, ObjectPool<AsymmetricViewHolder<?>>> viewHoldersMap = 
			new ArrayMap<Integer, ObjectPool<AsymmetricViewHolder<?>>>();
	private final AGVBaseAdapterDao<?> agvAdapter;
	private final AsymmetricViewDao listView;
	private final boolean debugEnabled;
	private ProcessRowsTask asyncTask;
	/**
	 * 
	 * @param context
	 * @param agvAdapter  AsymmetricGridViewAdapter
	 * @param listView   AsymmetricGridView
	 */
	AdapterImpl(Context context, AGVBaseAdapterDao<?> agvAdapter, AsymmetricViewDao listView) {
		this.mContext = context;
		this.agvAdapter = agvAdapter;
		this.listView = listView;
		this.debugEnabled = listView.isDebugging();
		this.linearLayoutPool = new ObjectPool<LinearLayout>(new PoolObjectFactoryDaoImpl(context));
	}

	/**
	 * 当Adapter的数据有变化时 启动异步任务进行排序获取ListView 每行数据
	 */
	void recalculateItemsPerRow() {
		if (asyncTask != null) {
			asyncTask.cancel(true);
		}

		linearLayoutPool.clear();
		itemsPerRow.clear();

		asyncTask = new ProcessRowsTask();
		asyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
  }

	@Override 
	public void onClick(@NonNull View v) {
		// noinspection unchecked
		ViewState rowItem = (ViewState) v.getTag();
		listView.fireOnItemClick(rowItem.rowItem.getIndex(), v);
	}

	@Override 
	public boolean onLongClick(@NonNull View v) {
		// noinspection unchecked
		ViewState rowItem = (ViewState) v.getTag();
		return listView.fireOnItemLongClick(rowItem.rowItem.getIndex(), v);
	}

	/**
	 * 重点函数，子空间对排列实现
	 * @param holder    ListView Item
	 * @param position  当前应用对位置
	 * @param parent   ListView 父控件
	 */
	void onBindViewHolder(ViewHolder holder, int position, ViewGroup parent) {
		if (debugEnabled) {
			Log.d(TAG, "onBindViewHolder(" + String.valueOf(position) + ")");
		}
		//已经排列好的一行数据
		RowInfo rowInfo = itemsPerRow.get(position);
		FyLog.i(TAG, "RowInfo  h="+rowInfo.getRowHeight() + " getSpaceLeft="+ rowInfo.getSpaceLeft()+ " size="+ rowInfo.getItems().size());
		if (rowInfo == null) {
			return;
		}

		List<RowItem> rowItems = new ArrayList<>(rowInfo.getItems());
		LinearLayout layout = initializeLayout(holder.itemView());
		// Index to control the current position of the current column in this row
		int columnIndex = 0;
		// Index to control the current position in the array of all the items available for this row
		int currentIndex = 0;
		float spaceLeftInArea = rowInfo.getSpaceLeft();
		int spaceLeftInColumn = listView.getNumColumns();	//总列数
		int spaceLeftInRow = rowInfo.getRowHeight();   //一行的单元高数
		boolean isClumFinish = false;
		List<String> rowSign = new ArrayList<>();
		while (!rowItems.isEmpty() && columnIndex < listView.getNumColumns()) {
			RowItem currentItem = rowItems.get(currentIndex);
			
			if (spaceLeftInColumn == 0) {
				// No more space in this column. Move to next one
				currentIndex = 0;
				spaceLeftInRow = rowInfo.getRowHeight();
				spaceLeftInColumn = listView.getNumColumns();
				continue;
			}
//			// 临时处理  最右边的可以进行行合并
//			if(position == 1 && currentItem.getItem().getRowSpan()==2&&rowItems.size()>1){
//				currentIndex++;
//				continue;
//			}
			// Is there enough space in this column to accommodate currentItem?
			int currentClum = currentItem.getItem().getColumnSpan();
			int currentRow = currentItem.getItem().getRowSpan();
			int currentArea = currentRow * currentClum;
			if (currentArea <= spaceLeftInArea) {
				rowItems.remove(currentItem);
				
				//都是对ObjectPool进行操作
				int actualIndex = currentItem.getIndex();
				int viewType = agvAdapter.getItemViewType(actualIndex);
				FyLog.d(TAG, "viewType="+ viewType+ "viewHoldersMap size is: "+ viewHoldersMap.size());
				ObjectPool<AsymmetricViewHolder<?>> pool = viewHoldersMap.get(viewType);
				if (pool == null) {
					pool = new ObjectPool<>();
					viewHoldersMap.put(viewType, pool);
				}
				AsymmetricViewHolder viewHolder = pool.get();
				if (viewHolder == null) {
					//持有AppsAdapter ListView Item
					viewHolder = agvAdapter.onCreateAsymmetricViewHolder(actualIndex, parent, viewType);
				}
				//将AppsAdapter中对Item 绑定到ViewHolder对LinearLayout中
				agvAdapter.onBindAsymmetricViewHolder(viewHolder, parent, actualIndex);
				
				View view = viewHolder.itemView;//持有AppsAdapter中对Item
				view.setTag(new ViewState(viewType, currentItem, viewHolder));
				view.setOnClickListener(this);
				view.setOnLongClickListener(this);
				
				if(!isClumFinish){
					//行没被全部填充的要进行备份
					spaceLeftInRow -= currentRow;
					FyLog.i(TAG, "spaceLeftInRow="+spaceLeftInRow);
					if(spaceLeftInRow != 0)
						rowSign.add(String.valueOf(columnIndex));
					spaceLeftInRow = rowInfo.getRowHeight();
				}
				currentIndex = 0;
				columnIndex++;
				spaceLeftInArea -= currentArea;
				
				boolean b = rowSign.contains(String.valueOf(columnIndex));
				if(isClumFinish && !b)
					columnIndex++;
				FyLog.e(TAG, "rowSign"+ b+ " rowInfo.getRowHeight()="+rowInfo.getRowHeight()+
						"  spaceLeftInColumn="+ spaceLeftInColumn+ " currentClum="+ currentClum);
				view.setBackgroundResource(R.drawable.rect_ring_bg);
				view.setLayoutParams(new LinearLayout.LayoutParams(getRowWidth(currentItem.getItem()),
						getRowHeight(currentItem.getItem())));
				LinearLayout childLayout = findOrInitializeChildLayout(layout, columnIndex);//垂直布局
				childLayout.addView(view);
				
				spaceLeftInColumn -= currentClum;
				if(spaceLeftInColumn == 0){
					spaceLeftInColumn = listView.getNumColumns();
					isClumFinish = true;
					columnIndex = -1;
				}
			} else if (currentIndex < rowItems.size() - 1) {
				// Try again with next item
				FyLog.i(TAG, "only add currentIndex="+currentIndex);
				currentIndex++;
			} else {
				rowSign = null;
				break;
			}
		}

		if (debugEnabled && position % 20 == 0) {
			Log.d(TAG, linearLayoutPool.getStats("LinearLayout"));
			for (Map.Entry<Integer, ObjectPool<AsymmetricViewHolder<?>>> e : viewHoldersMap.entrySet()) {
				Log.d(TAG, e.getValue().getStats("ConvertViewMap, viewType=" + e.getKey()));
			}
		}
	}

	/**
	 * 初始化布局  VERTICAL  w:WRAP_CONTENT h:MATCH_PARENT
	 * @param parentLayout
	 * @param childIndex
	 * @return
	 */
	private LinearLayout findOrInitializeChildLayout(LinearLayout parentLayout, int childIndex) {
		LinearLayout childLayout = (LinearLayout) parentLayout.getChildAt(childIndex);
		parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);

		if (childLayout == null) {
			childLayout = linearLayoutPool.get();
			childLayout.setOrientation(LinearLayout.VERTICAL);

			if (debugEnabled) {
				parentLayout.setBackgroundColor(Color.parseColor("#837BF2"));
			}

			childLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
			childLayout.setDividerDrawable(
					ContextCompat.getDrawable(mContext, R.drawable.item_divider_vertical));
			childLayout.setLayoutParams(new AbsListView.LayoutParams(
					AbsListView.LayoutParams.WRAP_CONTENT,
						AbsListView.LayoutParams.MATCH_PARENT));

			parentLayout.addView(childLayout);
		}
		return childLayout;
	}
	
	int getRowHeight(AsymmetricItemDao item) {
		return getRowHeight(item.getRowSpan());
	}

	int getRowHeight(int rowSpan) {
		final int rowHeight = listView.getColumnWidth() * rowSpan;
		// when the item spans multiple rows, we need to account for the vertical padding
		// and add that to the total final height
		return rowHeight + ((rowSpan - 1) * listView.getDividerHeight());
	}

	int getRowWidth(AsymmetricItemDao item) {
		return getRowWidth(item.getColumnSpan());
	}

	protected int getRowWidth(int columnSpan) {
		final int rowWidth = listView.getColumnWidth() * columnSpan;
		// when the item spans multiple columns, we need to account for the horizontal padding
		// and add that to the total final width
		return Math.min(rowWidth + ((columnSpan - 1) * listView.getRequestedHorizontalSpacing()),
				Utils.getScreenWidth(mContext));
	}

  	public int getRowCount() {
		return itemsPerRow.size();
	}
  	
  	/**
	 * 创建ListView的Item的LinearLayout
	 * @return
	 */
	ViewHolder onCreateViewHolder() {
		if (debugEnabled) {
			Log.d(TAG, "onCreateViewHolder()");
		}
		LinearLayout layout = new LinearLayout(mContext, null);
		if (debugEnabled) {
			layout.setBackgroundColor(Color.parseColor("#83F27B"));
		}

		layout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
		layout.setDividerDrawable(
				ContextCompat.getDrawable(mContext, R.drawable.item_divider_horizontal));

		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
        AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(layoutParams);
		return new ViewHolder(layout);
	}

	/**
	 * 从LinearLayout Object池中获取一个无子控件的空LinearLyaout
	 * @param layout
	 * @return
	 */
	private LinearLayout initializeLayout(LinearLayout layout) {
		// Clear all layout children before starting
		int childCount = layout.getChildCount();
		for (int j = 0; j < childCount; j++) {
			LinearLayout tempChild = (LinearLayout) layout.getChildAt(j);
			linearLayoutPool.put(tempChild);
			int innerChildCount = tempChild.getChildCount();
			for (int k = 0; k < innerChildCount; k++) {
				View innerView = tempChild.getChildAt(k);
				ViewState viewState = (ViewState) innerView.getTag();
				ObjectPool<AsymmetricViewHolder<?>> pool = viewHoldersMap.get(viewState.viewType);
				pool.put(viewState.viewHolder);
			}
			tempChild.removeAllViews();
		}
		layout.removeAllViews();

		return layout;
	}
  	
  	/**
  	 * 将每行数据排列
  	 * @author chenyuye
  	 *
  	 */
  	class ProcessRowsTask extends AsyncTask<Void, Void, List<RowInfo>> {
  		@Override 
  		protected final List<RowInfo> doInBackground(Void... params) {
  			// We need a map in order to associate the item position in the wrapped adapter.
  			List<RowItem> itemsToAdd = new ArrayList<>();
  			for (int i = 0; i < agvAdapter.getActualItemCount(); i++) {
  				try {
  					//从AsymmetricGridViewAdapter获取数据源RowItem
  					itemsToAdd.add(new RowItem(i, agvAdapter.getItem(i)));
  				} catch (CursorIndexOutOfBoundsException e) {
  					Log.w(TAG, e);
  				}
  			}
  			FyLog.e(TAG, "itemsToAdd list="+ itemsToAdd.size());
  			//再封装成RowInfo返回
  			return calculateItemsPerRow(itemsToAdd);
  		}

  		@Override 
  		protected void onPostExecute(List<RowInfo> rows) {
  			for (RowInfo row : rows) {
  				//添加到每行数据表中 key 第几行  value List<RowInfo>
  				itemsPerRow.put(getRowCount(), row);
  			}

  			if (debugEnabled) {
  				for (Map.Entry<Integer, RowInfo> e : itemsPerRow.entrySet()) {
  					Log.d(TAG, "row: " + e.getKey() + ", items: " + e.getValue().getItems().size());
  				}
  			}
  			agvAdapter.notifyDataSetChanged();
  		}

  		private List<RowInfo> calculateItemsPerRow(List<RowItem> itemsToAdd) {
  			List<RowInfo> rows = new ArrayList<>();

  			while (!itemsToAdd.isEmpty()) {
  				//获取一行数据已经填满的
  				RowInfo stuffThatFit = calculateItemsForRow(itemsToAdd);
  				//取出该行数据
  				List<RowItem> itemsThatFit = stuffThatFit.getItems();

  				if (itemsThatFit.isEmpty()) {
  					// we can't fit a single item inside a row.
  					// bail out.
  					break;
  				}
  				//将已经填好的数据移除
  				for (RowItem entry : itemsThatFit) {
  					itemsToAdd.remove(entry);
  				}
  				rows.add(stuffThatFit);
  			}
  			return rows;
  		}
  	}
  	/**
  	 * 根据数据源分解成ListView一行数据
  	 * @param items
  	 * @return
  	 */
  	private RowInfo calculateItemsForRow(List<RowItem> items) {
		//根据当前显示的列数计算获取行
		return calculateItemsForRow(items, listView.getNumColumns());
	}
	/**
	 * 根据当前显示的列数计算获取ListView一行数据
	 * @param items 数据源
	 * @param initialSpaceLeft  显示的列数
	 * @return
	 */
	private RowInfo calculateItemsForRow(List<RowItem> items, float initialSpaceLeft) {
		final List<RowItem> itemsThatFit = new ArrayList<>();
		int currentItem = 0; 
		int rowHeight = 1;
		float areaLeft = 1 * initialSpaceLeft; //剩余单元面积数
		float totalArea = 0;
		//还有列没填充             还有剩余数据没有使用
		while (areaLeft > 0 && currentItem < items.size()) {
			final RowItem item = items.get(currentItem++);
			//计算所占单元面积数
			float itemArea = item.getItem().getRowSpan() * item.getItem().getColumnSpan();
			//小于当前格行所占行数
			FyLog.i(TAG, "rowHeight="+ rowHeight+ " item.getItem().getRowSpan()="+ item.getItem().getRowSpan());
			if (rowHeight < item.getItem().getRowSpan()) {
				// restart with double height 重新计算
				FyLog.i(TAG, "clear");
				itemsThatFit.clear();
				rowHeight = item.getItem().getRowSpan();  //使用最高的行单元数进行计算
				currentItem = 0;
				areaLeft = initialSpaceLeft * item.getItem().getRowSpan(); //计算总单元面积数
				totalArea = 0;
			} else if (areaLeft >= itemArea) {
				//还有地方没被占用
				areaLeft -= itemArea;//剩余单元面积数
				totalArea += itemArea;
				FyLog.e(TAG, "itemArea="+ itemArea + ", areaLeft="+ areaLeft);
				itemsThatFit.add(item);
			} else if (!listView.isAllowReordering()) {
				FyLog.i(TAG, "isAllowReordering");
				break;
			}
		}
		FyLog.e(TAG, "list="+ itemsThatFit.size()+ "totalarea="+ totalArea);
		return new RowInfo(rowHeight, itemsThatFit, totalArea);
	}
	
  	private static class ViewState {
  		private final int viewType;
  		private final RowItem rowItem;
  		private final AsymmetricViewHolder<?> viewHolder;

  		private ViewState(int viewType, RowItem rowItem, AsymmetricViewHolder<?> viewHolder) {
  			this.viewType = viewType;
  			this.rowItem = rowItem;
  			this.viewHolder = viewHolder;
  		}
  	}

  	static class ViewHolder extends RecyclerView.ViewHolder {
  		ViewHolder(LinearLayout itemView) {
  			super(itemView);
  		}

  		LinearLayout itemView() {
  			return (LinearLayout) itemView;
  		}
  	}
}
