package com.felipecsl.asymmetricgridview.app.model;

import com.felipecsl.asymmetricgridview.AsymmetricItemDao;

import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.StateListDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * ��Ԫ�����ݶ���
 * @author chenyuye
 *
 */
public class DemoItem implements AsymmetricItemDao {

	public String packetName;
	public String activityName;
	private int columnSpan; //����ռ��Ԫ��
	private int rowSpan;	//����ռ��Ԫ��
	private int position;	//λ�ã��ڼ���
	public ResolveInfo mResolveInfo;	//Ӧ�û�������
	public StateListDrawable drawable;	//������Դ
	public String title;	//��ǩ
	public int titleSize;	//��ǩ�����С
	public Bitmap icon;	//Ӧ��ͼ��
  
  
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
	 * Ӧ��λ��
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
