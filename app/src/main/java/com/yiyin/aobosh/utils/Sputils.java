package com.yiyin.aobosh.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * 封装了首选项的工具类
 * 
 * */
public class Sputils {

    //保存一个对象，object必须是普通类，而不是泛型，如果是泛型
    public static void putObject(Context context, Object object) {
        String key = getKey(object.getClass());
        Gson gson = new Gson();
        String json = gson.toJson(object);
        putString(context, key, json);
    }

    // 通过类名字去获取一个对象
    public static <T> T getObject(Context context, Class<T> clazz) {
        String key = getKey(clazz);
        String json = getString(context, key, null);
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    //保存一个泛型对象 如果你要保存 List<Person> 这个类, type应该 传入 new TypeToken<List<object>>() {}.getType()
    public static void putObject(Context context, Object object, Type type) {
        String key = getKey(type);
        Gson gson = new Gson();
        String json = gson.toJson(object);
        putString(context, key, json);
    }

    // 通过Type去获取一个泛型对象
    public static <T> T getObject(Context context, Type type) {
        String key = getKey(type);
        String json = getString(context, key, null);
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    public static void removeObject(Context context, Class<?> clazz) {
        remove(context, getKey(clazz));
    }

    public static void removeObject(Context context, Type type) {
        remove(context, getKey(type));
    }

    public static String getKey(Class<?> clazz) {
        return clazz.getName();
    }

    public static String getKey(Type type) {
        return type.toString();
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putString(key, value).commit();
    }

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(key, defValue);
    }

    //----------------------------------------------------------------------------------------------

	private static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences("information", Context.MODE_PRIVATE);
	}
	
	public static int getSpInt(Context context, String title, int defautValue) {
		SharedPreferences sp = getSharedPreferences(context);
		return sp.getInt(title, defautValue);
	}
	
	public static String getSpString(Context context, String title, String defaultValue) {
		SharedPreferences sp = getSharedPreferences(context);
		return sp.getString(title, defaultValue);
	}
	
	public static boolean getSpBoolean(Context context, String title, boolean defaultValue) {
		SharedPreferences sp = getSharedPreferences(context);
		return sp.getBoolean(title, defaultValue);
	}
	public static long getSpLong(Context context, String title, long defaultValue) {
		SharedPreferences sp = getSharedPreferences(context);
		return sp.getLong(title, defaultValue);
	}
    public static void put(Context context, String title, Object value) {
		SharedPreferences sp = getSharedPreferences(context);
		if(value instanceof Integer) {
			sp.edit().putInt(title, (Integer)value).commit();
		} else if(value instanceof String) {
			sp.edit().putString(title, (String)value).commit();
		} else if(value instanceof Boolean) {
			sp.edit().putBoolean(title, (Boolean)value).commit();
		}else if (value instanceof Long){
			sp.edit().putLong(title, (Long) value).commit();
		}
	}
	
	public static void remove(Context context, String title) {
		SharedPreferences sp = getSharedPreferences(context);
		sp.edit().remove(title).commit();
	}

    public static void clear(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
    }

	
}
