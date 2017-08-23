package com.java.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class Test1 {

	public static void main(String[] args) throws Exception {
		// byte[][] b1 = new byte[1][];
		//
		// for (int i = 0; i < b1.length; i++) {
		// b1[i] = new byte[312645968];
		// }
		// BufferedImage bi= ImageIO.read(new File("F:/高达一亿像素的中国地图
		// 极适合做桌面.jpg"));

		File dir = new File("F:/test/");
		File[] files = dir.listFiles();
		long filesSize = 0;
		for (File f : files) {
			filesSize += f.length();
		}

		int threadSize = 8;

		Collection<Future<Void>> fs = new ArrayList<Future<Void>>();
//		if (filesSize > Runtime.getRuntime().freeMemory() * 0.1) {
//			threadSize = 2;
//		}
		ExecutorService es = Executors.newFixedThreadPool(threadSize);

		for (int i = 0; i < files.length; i++) {
			Future<Void> f = es.submit(new Task(files[i]));
			fs.add(f);
		}

		for (Future<Void> f : fs) {
			f.get();
		}
		es.shutdown();
	}

	static class Task implements Callable<Void> {

		private File image;

		Task(File image) {
			this.image = image;
		}

		@Override
		public Void call() throws Exception {
			// ImageIO.setUseCache(true);
			// ImageIO.setCacheDirectory(new File("F:/test1/"));
			// BufferedImage img = ImageIO.read(image);
			// Class dataBufferByte =
			// Class.forName("java.awt.image.DataBuffer");
			// Field filed = dataBufferByte.getDeclaredField("size");
			// filed.setAccessible(true);

			// ImageInputStream iis = ImageIO.createImageInputStream(image);
			// Iterator iter = ImageIO.getImageReaders(iis);
			// if (!iter.hasNext()) {
			// return null;
			// }
			//
			// ImageReader reader = (ImageReader)iter.next();
			// reader.getAspectRatio(16);
			// ImageReadParam param = new ImageReadParam();
			// param.set
			// BufferedImage img = ImageReadParam.getSourceSubSampling(wratio,
			// hratio, 0, 0);
			// System.out.println(filed.get(img.getData().getDataBuffer()));

			InputStream in = new FileInputStream(image);
			ImageInputStream stream = ImageIO.createImageInputStream(in);
			BufferedImage img = read(stream);
			System.out.println(img);
			return null;
		}
	}

	public static BufferedImage read(ImageInputStream stream) throws IOException {
		if (stream == null) {
			throw new IllegalArgumentException("stream == null!");
		}

		Iterator iter = ImageIO.getImageReaders(stream);
		if (!iter.hasNext()) {
			return null;
		}

		ImageReader reader = (ImageReader) iter.next();
		ImageReadParam param = reader.getDefaultReadParam();
		reader.setInput(stream, true, true);
		BufferedImage bi = null;
		try {
			bi = reader.read(0, param);
		} finally {
			reader.dispose();
			stream.close();
		}
		return bi;
	}
}
