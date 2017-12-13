package com.felipecsl.asymmetricgridview.app.model;

import com.felipecsl.asymmetricgridview.AsymmetricItemDao;

import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.StateListDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * 单元格数据对象
 * @author chenyuye
 *
 */
public class DemoItem implements AsymmetricItemDao {

	public String packetName;
	public String activityName;
	private int columnSpan; //列所占单元数
	private int rowSpan;	//行所占单元数
	private int position;	//位置，第几个
	public ResolveInfo mResolveInfo;	//应用基本数据
	public StateListDrawable drawable;	//背景资源
	public String title;	//标签
	public int titleSize;	//标签字体大小
	public Bitmap icon;	//应用图标
  
  
	public DemoItem() {
		this(1, 1, 0);
	}

	public DemoItem(int columnSpan, int rowSpan, int position) {
		this.columnSpan = columnSpan;
		this.rowSpan = rowSpan;
		this.position = position;
	}

	public DemoItem(Parcel in) {
		readFromParcel(in);
	}

	@Override 
	public int getColumnSpan() {
		return columnSpan;
	}

	@Override 
	public int getRowSpan() {
		return rowSpan;
	}
	/**
	 * 应用位置
	 * @return
	 */
	public int getPosition() {
		return position;
	}

	@Override 
	public String toString() {
		return String.format("%s: %sx%s", position, rowSpan, columnSpan);
	}

	@Override 
	public int describeContents() {
		return 0;
	}

	private void readFromParcel(Parcel in) {
		columnSpan = in.readInt();
		rowSpan = in.readInt();
		position = in.readInt();
	}

	@Override 
	public void writeToParcel(@NonNull Parcel dest, int flags) {
		dest.writeInt(columnSpan);
		dest.writeInt(rowSpan);
		dest.writeInt(position);
	}

	/* Parcelable interface implementation */
	public static final Parcelable.Creator<DemoItem> CREATOR = new Parcelable.Creator<DemoItem>() {

		@Override 
		public DemoItem createFromParcel(@NonNull Parcel in) {
			return new DemoItem(in);
		}

		@Override @NonNull 
		public DemoItem[] newArray(int size) {
			return new DemoItem[size];
		}
	};
}
