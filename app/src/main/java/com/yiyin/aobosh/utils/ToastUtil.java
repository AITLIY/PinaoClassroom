/**
 * 
 */
package com.yiyin.aobosh.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	static Toast toast;
	public static void show(Context context, String info) {
		if (context!=null){
			if (toast==null) {
				toast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
			}

			toast.setText(info);
			toast.show();
		}

	}

	public static void show(Context context, int info) {
		if (context!=null){
			if (toast==null) {
				toast=Toast.makeText(context, context.getResources().getString(info), Toast.LENGTH_SHORT);
			}
			toast.setText(context.getResources().getString(info));
			toast.show();
		}
	}
}
