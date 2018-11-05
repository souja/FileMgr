package com.buyoute.filemanager;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EncriptDemo {

    public static final int XOR_CONST = "bytmgr2018".hashCode(); //密钥

    /**
     * @param args
     */
    public static void main(String[] args) {
    	File out;
        try {
        	//指定要加密的目录
        	File dir = new File("C:/每天猜猜猜0612Home/疯狂猜电影/assets/new/img");
        	File[] files = dir.listFiles();
        	for(File f : files){
        		//加密后输出的目录
        		 out = new File("C:/每天猜猜猜0612Home/疯狂猜电影/assets/new/jiami/"+f.getName());
        		 encrImg(f, out);
        		 System.out.println("正在加密"+f.getName());
        	}
        	System.out.println("加密完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void encrImg(File src, File dest) throws Exception {
        FileInputStream fis = new FileInputStream(src);
        FileOutputStream fos = new FileOutputStream(dest);

        int read;
        while ((read = fis.read()) > -1) {
            fos.write(read ^ XOR_CONST);
        }
        fos.flush();
        fos.close();
        fis.close();
    }

    public static Bitmap readBitmap(File file) {
        Bitmap bitmap = null;
        List<Byte> list = new ArrayList<>();
        try {
            InputStream is = new FileInputStream(file);
            int read;
            while ((read = is.read()) > -1) {
                read = read ^ XOR_CONST;
                list.add((byte) read);
            }

            byte[] arr = new byte[list.size()];
            int i = 0;
            for (Byte item : list) {
                arr[i++] = item;
            }
            bitmap = BitmapFactory.decodeByteArray(arr, 0, list.size());
            System.out.println(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
