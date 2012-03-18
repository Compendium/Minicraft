package com.mojang.ld22.misc;

public class DataBufferInt extends DataBuffer
{
	private final WritableRaster raster;
	public DataBufferInt(WritableRaster writableRaster) {
		raster = writableRaster;
	}

	public int[] getData() {
		return raster.getData();
	}

}
