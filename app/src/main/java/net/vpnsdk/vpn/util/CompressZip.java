package net.vpnsdk.vpn.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompressZip {
	private static final int BUFFER = 2048;

	private String[] mFiles;
	private FileOutputStream mZipFile;

	public CompressZip(String[] files, FileOutputStream zipFile) {
		mFiles = files;
		mZipFile = zipFile;
	}

	public void zip() {
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = mZipFile;
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));

			byte data[] = new byte[BUFFER];

			for (int i = 0; i < mFiles.length; i++) {
				if (mFiles[i] == null) {
					continue;
				}
				Log.v("Compress", "Adding: " + mFiles[i]);
				FileInputStream fi = new FileInputStream(mFiles[i]);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(mFiles[i].substring(mFiles[i]
						.lastIndexOf("/") + 1));
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}