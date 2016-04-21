package com.hisham.portfolio.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

public final class FontsOverride {
	
    public static void setDefaultFont(Context context, String staticTypefaceFieldName, String fontAssetName){
    	final Typeface regular = Typeface.createFromAsset(context.getAssets(),fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    /*
    public static void setDefaultFont(Context context){
    	try {
    		setDefaultFontForTypeFaceMonospace(context);
    	} catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    private static void setDefaultFontForTypeFaceMonospace(Context context) throws NoSuchFieldException, IllegalAccessException {
        final Typeface bold = Typeface.createFromAsset(context.getAssets(), DEFAULT_MONOSPACE_BOLD_FONT_FILENAME);
        final Typeface italic = Typeface.createFromAsset(context.getAssets(), DEFAULT_MONOSPACE_ITALIC_FONT_FILENAME);
        final Typeface boldItalic = Typeface.createFromAsset(context.getAssets(), DEFAULT_MONOSPACE_BOLD_ITALIC_FONT_FILENAME);
        final Typeface normal = Typeface.createFromAsset(context.getAssets(), DEFAULT_MONOSPACE_NORMAL_FONT_FILENAME);

        setTypeFaceDefaults(normal, bold, italic, boldItalic, monospace_idx);
    }
    private static void setTypeFaceDefaults(Typeface normal, Typeface bold, Typeface italic, Typeface boldItalic, int typefaceIndex) throws NoSuchFieldException, IllegalAccessException {
        Field typeFacesField = Typeface.class.getDeclaredField("sTypefaceCache");
        typeFacesField.setAccessible(true);

        SparseArray<SparseArray<Typeface>> sTypefaceCacheLocal = new SparseArray<SparseArray<Typeface>>(3);
        typeFacesField.get(sTypefaceCacheLocal);

        SparseArray<Typeface> newValues = new SparseArray<Typeface>(4);
        newValues.put(Typeface.NORMAL, normal);
        newValues.put(Typeface.BOLD, bold);
        newValues.put(Typeface.ITALIC, italic);
        newValues.put(Typeface.BOLD_ITALIC, boldItalic);
        sTypefaceCacheLocal.put(typefaceIndex, newValues);

        typeFacesField.set(null, sTypefaceCacheLocal);
    }*/
}