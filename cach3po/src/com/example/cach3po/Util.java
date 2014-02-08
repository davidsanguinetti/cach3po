package com.example.cach3po;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Util {
	public static Bitmap getImage(String imgb64) {
		try {
		byte[] decodedString = Base64.decode(imgb64, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0,
				decodedString.length);
		if (bitmap != null) {
			return bitmap;
		} else
			return null;
		}catch (Exception e) {
			return null;
		}
	}
	
	public static void saveBitmap(Activity av, Bitmap bm, String name) throws IOException {
		fileExists(av, name);
		
		String path = av.getCacheDir().getAbsolutePath();
//		.getDownloadCacheDirectory().toString();
//		.getExternalStorageDirectory().toString();
		OutputStream fOut = null;
		File file = new File(path, name);
		fOut = new FileOutputStream(file);

		bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
		
		fOut.flush();
		fOut.close();

//		MediaStore.Images.Media.insertImage(av.getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
	}
	
	public static boolean fileExists(Activity av, String fileName) {
		
		if(!av.getCacheDir().exists())
			av.getCacheDir().mkdirs();
		
		File file = new File(av.getCacheDir().getAbsolutePath(), fileName);
		return (file.exists());
	}
	
	public static String getFileName(String path) {
		URI test = URI.create(path);
		File objFile = new File(path);
		return objFile.getName();
	}
}
