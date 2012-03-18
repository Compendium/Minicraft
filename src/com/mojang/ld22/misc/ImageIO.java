package com.mojang.ld22.misc;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageIO {

	public static BufferedImage read(InputStream resourceAsStream) throws IOException
	{
		Bitmap bitmap = BitmapFactory.decodeStream(resourceAsStream);
		return new BufferedImage(bitmap);
	}

}
