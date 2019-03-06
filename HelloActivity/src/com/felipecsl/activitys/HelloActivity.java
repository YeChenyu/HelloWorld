/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.felipecsl.activitys;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.android.helloactivity.R;
import com.felipecsl.asymmetricgridview.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.AsymmetricGridViewAdapter;
import com.felipecsl.asymmetricgridview.app.model.DemoItem;
import com.felipecsl.asymmetricgridview.app.tool.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


/**
 * A minimal "Hello, World!" application.
 */
public class HelloActivity extends Activity {
	
	private String TAG = HelloActivity.class.getSimpleName();
	private Context mContext = HelloActivity.this;
	
	private AsymmetricGridView mGridView;
	private AppsAdapter mAdapter;
	private List<DemoItem> mItems;
	//?????????
	private int[] colors_pressed = new int[] { R.color.menu_color_1_press,
			R.color.menu_color_2_press, R.color.menu_color_3_press,
			R.color.menu_color_4_press, R.color.menu_color_5_press,
			R.color.menu_color_6_press, R.color.menu_color_7_press, 
			R.color.menu_color_8_press,	R.color.menu_color_9_press,
			R.color.menu_color_10_press,R.color.menu_color_11_press,
			R.color.menu_color_12_press};
	private int[] colors_normal = new int[] { R.color.menu_color_1_normal,
			R.color.menu_color_2_normal, R.color.menu_color_3_normal,
			R.color.menu_color_5_normal, R.color.menu_color_7_normal,
			R.color.menu_color_4_normal, R.color.menu_color_6_normal, 
			R.color.menu_color_8_normal, R.color.menu_color_9_normal,
			R.color.menu_color_10_normal,R.color.menu_color_11_normal,
			R.color.menu_color_12_normal };
    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// ?????????
        setContentView(R.layout.felipescsl_main_activity);
        fdjkfjdkfjd
        initViews();
        initDatas();
        打开房间打开附近的空间发呆发
    }
    
    
	private void initViews() {
		// TODO Auto-generated method stub
		//?????  ???????
		mGridView = (AsymmetricGridView)findViewById(R.id.mGridView);
		mItems = new ArrayList<DemoItem>();

		mAdapter = new AppsAdapter(mContext, mItems);
		mGridView.setRequestedColumnCount(3);
		mGridView.setDebugging(false);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				DemoItem item = mAdapter.getItem(position);
				Toast.makeText(mContext, item.title + " is clicked", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	
	private void initDatas() {
		// TODO Auto-generated method stub
		for (int i = 0 ; i < 7 ; i++) {
			int rowSpan = 1;
			int colSpan = 0;
			if (0 == i) {
				rowSpan = 1;
				colSpan = 2;
			} else if (1 == i) {
				rowSpan = 1;
				colSpan = 1;
				
			} else if (2 == i) {
				rowSpan = 1;
				colSpan = 1;
			} else if (3 == i) {
				rowSpan = 1;
				colSpan = 1;
			} else if (4 == i) {
				rowSpan = 2;
				colSpan = 1;
			}else if (5 == i) {
				rowSpan = 1;
				colSpan = 1;
			}else if (6 == i) {
				rowSpan = 1;
				colSpan = 1;
				
			}else if (7 == i) {
				rowSpan = 2;
				colSpan = 1;
			}else if (8 == i) {
				rowSpan = 1;
				colSpan = 2;
			}else if (9 == i) {
				rowSpan = 1;
				colSpan = 2;
			}
			DemoItem item = new DemoItem(colSpan, rowSpan, i);
			item.drawable = Utils.getStateListDrawable(colors_pressed[i],
					colors_normal[i], mContext);
			item.title = "Label"+i;
			item.icon = getIcons("ui_Speech Recorder_icon.png");
			item.titleSize = 10;
			mItems.add(item);
		}
		mAdapter.notifyDataSetChanged();
		mGridView.setAdapter(new AsymmetricGridViewAdapter(mContext, mGridView, mAdapter));
	}
	
	private Bitmap getIcons(String path){
		InputStream is;
		Bitmap bit = null;
		try {
			is = mContext.getResources().getAssets().open(path);
			bit = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bit;
	}
}

