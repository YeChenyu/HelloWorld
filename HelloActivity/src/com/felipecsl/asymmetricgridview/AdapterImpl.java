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
  
	private final Map<Integer, RowInfo> itemsPerRow = new HashMap<Integer, RowInfo>();//�����ݱ�
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
	 * ��Adapter�������б仯ʱ �����첽������������ȡListView ÿ������
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
	 * �ص㺯�����ӿռ������ʵ��
	 * @param holder    ListView Item
	 * @param position  ��ǰӦ�ö�λ��
	 * @param parent   ListView ���ؼ�
	 */
	void onBindViewHolder(ViewHolder holder, int position, ViewGroup parent) {
		if (debugEnabled) {
			Log.d(TAG, "onBindViewHolder(" + String.valueOf(position) + ")");
		}
		//�Ѿ����кõ�һ������
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
		int spaceLeftInColumn = listView.getNumColumns();	//������
		int spaceLeftInRow = rowInfo.getRowHeight();   //һ�еĵ�Ԫ����
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
//			// ��ʱ����  ���ұߵĿ��Խ����кϲ�
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
				
				//���Ƕ�ObjectPool���в���
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
					//����AppsAdapter ListView Item
					viewHolder = agvAdapter.onCreateAsymmetricViewHolder(actualIndex, parent, viewType);
				}
				//��AppsAdapter�ж�Item �󶨵�ViewHolder��LinearLayout��
				agvAdapter.onBindAsymmetricViewHolder(viewHolder, parent, actualIndex);
				
				View view = viewHolder.itemView;//����AppsAdapter�ж�Item
				view.setTag(new ViewState(viewType, currentItem, viewHolder));
				view.setOnClickListener(this);
				view.setOnLongClickListener(this);
				
				if(!isClumFinish){
					//��û��ȫ������Ҫ���б���
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
				LinearLayout childLayout = findOrInitializeChildLayout(layout, columnIndex);//��ֱ����
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
	 * ��ʼ������  VERTICAL  w:WRAP_CONTENT h:MATCH_PARENT
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
	 * ����ListView��Item��LinearLayout
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
	 * ��LinearLayout Object���л�ȡһ�����ӿؼ��Ŀ�LinearLyaout
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
  	 * ��ÿ����������
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
  					//��AsymmetricGridViewAdapter��ȡ����ԴRowItem
  					itemsToAdd.add(new RowItem(i, agvAdapter.getItem(i)));
  				} catch (CursorIndexOutOfBoundsException e) {
  					Log.w(TAG, e);
  				}
  			}
  			FyLog.e(TAG, "itemsToAdd list="+ itemsToAdd.size());
  			//�ٷ�װ��RowInfo����
  			return calculateItemsPerRow(itemsToAdd);
  		}

  		@Override 
  		protected void onPostExecute(List<RowInfo> rows) {
  			for (RowInfo row : rows) {
  				//��ӵ�ÿ�����ݱ��� key �ڼ���  value List<RowInfo>
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
  				//��ȡһ�������Ѿ�������
  				RowInfo stuffThatFit = calculateItemsForRow(itemsToAdd);
  				//ȡ����������
  				List<RowItem> itemsThatFit = stuffThatFit.getItems();

  				if (itemsThatFit.isEmpty()) {
  					// we can't fit a single item inside a row.
  					// bail out.
  					break;
  				}
  				//���Ѿ���õ������Ƴ�
  				for (RowItem entry : itemsThatFit) {
  					itemsToAdd.remove(entry);
  				}
  				rows.add(stuffThatFit);
  			}
  			return rows;
  		}
  	}
  	/**
  	 * ��������Դ�ֽ��ListViewһ������
  	 * @param items
  	 * @return
  	 */
  	private RowInfo calculateItemsForRow(List<RowItem> items) {
		//���ݵ�ǰ��ʾ�����������ȡ��
		return calculateItemsForRow(items, listView.getNumColumns());
	}
	/**
	 * ���ݵ�ǰ��ʾ�����������ȡListViewһ������
	 * @param items ����Դ
	 * @param initialSpaceLeft  ��ʾ������
	 * @return
	 */
	private RowInfo calculateItemsForRow(List<RowItem> items, float initialSpaceLeft) {
		final List<RowItem> itemsThatFit = new ArrayList<>();
		int currentItem = 0; 
		int rowHeight = 1;
		float areaLeft = 1 * initialSpaceLeft; //ʣ�൥Ԫ�����
		float totalArea = 0;
		//������û���             ����ʣ������û��ʹ��
		while (areaLeft > 0 && currentItem < items.size()) {
			final RowItem item = items.get(currentItem++);
			//������ռ��Ԫ�����
			float itemArea = item.getItem().getRowSpan() * item.getItem().getColumnSpan();
			//С�ڵ�ǰ������ռ����
			FyLog.i(TAG, "rowHeight="+ rowHeight+ " item.getItem().getRowSpan()="+ item.getItem().getRowSpan());
			if (rowHeight < item.getItem().getRowSpan()) {
				// restart with double height ���¼���
				FyLog.i(TAG, "clear");
				itemsThatFit.clear();
				rowHeight = item.getItem().getRowSpan();  //ʹ����ߵ��е�Ԫ�����м���
				currentItem = 0;
				areaLeft = initialSpaceLeft * item.getItem().getRowSpan(); //�����ܵ�Ԫ�����
				totalArea = 0;
			} else if (areaLeft >= itemArea) {
				//���еط�û��ռ��
				areaLeft -= itemArea;//ʣ�൥Ԫ�����
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
