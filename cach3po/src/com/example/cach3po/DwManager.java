package com.example.cach3po;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

import com.example.cach3po.R.drawable;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class DwManager {

	ImageView mImgv;
	String mpath, md5file;
	ImgInfo mImgi;
	Activity mActiv;

	public DwManager(Activity av, ImageView mImgv, String mpath, Context ct) {
		super();
		this.mImgv = mImgv;
		this.mpath = mpath;
		this.mActiv = av;

		this.mImgv.setImageResource(drawable.loading);
		this.mImgv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		Animation anim = AnimationUtils.loadAnimation(ct, R.anim.spinload);
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(700);
		this.mImgv.setAnimation(anim);
	}

	public ImageView getmImgv() {
		return mImgv;
	}

	public void loadImage() {
		if (mpath == null || mImgv == null)
			return;

		new RetrieveURLINFO().execute(mpath);
	}
	
	private void putImage() {
		Bitmap bm = null;
		String nameimg = mImgi.getHash();
		try {
			bm = getImage(nameimg);
			mImgv.setImageBitmap(bm);
			mImgv.setAnimation(null);
		} catch (Exception e) {
			comecarDownload(nameimg);
		}
	}

	public void comecarDownload(String md5file) {
		new RetrieveURLINFO().execute(mpath);
	}

	public void setmImgv(ImageView mImgv) {
		this.mImgv = mImgv;
	}

	public String getMpath() {
		return mpath;
	}

	public void setMpath(String mpath) {
		this.mpath = mpath;
	}

	public void saveBitmap(Bitmap bm, String name) throws IOException {
		if (Util.fileExists(this.mActiv, name))
			return;

		String path = this.mActiv.getCacheDir().getAbsolutePath();

		OutputStream fOut = null;
		File file = new File(path, name);
		fOut = new FileOutputStream(file);

		bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);

		fOut.flush();
		fOut.close();

		// MediaStore.Images.Media.insertImage(av.getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
	}

	private Bitmap getImage(String name) throws FileNotFoundException,
			IOException {
		File file = new File(mActiv.getCacheDir().getAbsolutePath(), name);

		return MediaStore.Images.Media.getBitmap(mActiv.getContentResolver(),
				Uri.fromFile(file));
	}

	private int filelenght(String urlPath) throws IOException {
		URL url = new URL(urlPath);
		URLConnection connection = url.openConnection();
		connection.connect();
		final String contentLengthStr = connection
				.getHeaderField("content-length");
		int length = connection.getContentLength();

		return length;
	}

	private String getHash(ImgInfo img) {
		int flenght = img.getSize();
		String input = this.mpath + ";" + flenght + ";" + img.getDate();
		try {
			// String slength = (flenght == null) ? 0 : flenght;
			input = this.mpath;
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String md5 = number.toString(16);
			while (md5.length() < 32)
				md5 = "0" + md5;
			return md5;
		} catch (Exception e) {
			if (e.getMessage() != null)
				Log.e("MD5", e.getMessage());
			return null;
		}
	}

	class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		long timestart;
		String urlPath, filenameHash;

		public DownloadImageTask(ImageView bmImage, ImgInfo fileInfo) {
			this.bmImage = bmImage;
			this.filenameHash = fileInfo.getHash();
		}

		protected Bitmap doInBackground(String... urls) {
			this.urlPath = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urlPath).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
			try {
				saveBitmap(result, filenameHash);
				bmImage.setAnimation(null);
			} catch (IOException e) {
				Log.e("DWMANAGER", "error saving file: " + e.getMessage());
			}
		}
	}

	class RetrieveURLINFO extends AsyncTask<String, Void, ImgInfo> {

		private Exception exception;

		protected ImgInfo doInBackground(String... urls) {
			try {
				URL url = new URL(urls[0]);
				URLConnection connection = url.openConnection();
				connection.connect();
				final String contentLengthStr = connection
						.getHeaderField("content-length");
				int length = connection.getContentLength();
				long date = connection.getDate();

				ImgInfo imgi = new ImgInfo(urls[0], date, length);

				return imgi;
			} catch (Exception e) {
				this.exception = e;
				return null;
			}
		}

		protected void onPostExecute(ImgInfo urlimg) {
			mImgi = urlimg;
			mImgi.setHash(getHash(urlimg));
			
			new DownloadImageTask(mImgv, urlimg).execute(mpath);
		}
	}

	class ImgInfo {
		String path, hash;
		int lenght;
		long date;

		public ImgInfo(String path, long date, int size) {
			super();
			this.path = path;
			this.date = date;
			this.lenght = lenght;
		}

		public String getPath() {
			return this.path;
		}

		public long getDate() {
			return date;
		}

		public int getSize() {
			return lenght;
		}

		public String getHash() {
			return hash;
		}

		public void setHash(String hash) {
			this.hash = hash;
		}

	}
}
