package com.mojang.ld22.misc;

import android.graphics.Canvas;

public class BufferStrategy {

	public Graphics getDrawGraphics(Canvas canvas) {
		return new Graphics(canvas);
	}

	public void show() {
		
	}

}
