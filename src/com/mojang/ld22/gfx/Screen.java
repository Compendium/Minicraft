package com.mojang.ld22.gfx;

import com.mojang.ld22.GameActivity;

public class Screen {
	public int xOffset;
	public int yOffset;

	public static final int BIT_MIRROR_X = 0x01;
	public static final int BIT_MIRROR_Y = 0x02;

	public final int w, h;
	public int[] pixels;
	public static int[] colors = new int[256];

	private SpriteSheet sheet;

	public Screen(int w, int h, SpriteSheet sheet) {
		this.sheet = sheet;
		this.w = w;
		this.h = h;

		pixels = new int[w * h];
		int pp = 0;
		for (int r = 0; r < 6; r++) {
			for (int g = 0; g < 6; g++) {
				for (int b = 0; b < 6; b++) {
					int rr = (r * 255 / 5);
					int gg = (g * 255 / 5);
					int bb = (b * 255 / 5);
					int mid = (rr * 30 + gg * 59 + bb * 11) / 100;

					int r1 = ((rr + mid * 1) / 2) * 230 / 255 + 10;
					int g1 = ((gg + mid * 1) / 2) * 230 / 255 + 10;
					int b1 = ((bb + mid * 1) / 2) * 230 / 255 + 10;
					colors[pp++] = r1 << 16 | g1 << 8 | b1;

				}
			}
		}
	}

	public void clear(int color) {
		for (int i = 0; i < pixels.length; i++)
			pixels[i] = color;
	}

	public void render(int xp, int yp, int tile, int colors, int bits) {
		xp -= xOffset;
		yp -= yOffset;
		boolean mirrorX = (bits & BIT_MIRROR_X) > 0;
		boolean mirrorY = (bits & BIT_MIRROR_Y) > 0;

		int xTile = tile % 32;
		int yTile = tile / 32;
		int toffs = xTile * 8 + yTile * 8 * sheet.width;

		int col, ys, xs;
		for (int y = 0; y < 8; y++) {
			ys = y;
			if (mirrorY) ys = 7 - y;
			if (y + yp < 0 || y + yp >= h) continue;
			for (int x = 0; x < 8; x++) {
				if (x + xp < 0 || x + xp >= w) continue;

				xs = x;
				if (mirrorX) xs = 7 - x;
				col = (colors >> (sheet.pixels[xs + ys * sheet.width + toffs] * 8)) & 255;
				if (col < 255) pixels[(x + xp) + (y + yp) * w] = Screen.colors[col];
			}
		}
	}
	
	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	private static int[] dither = new int[] { 0, 8, 2, 10, 12, 4, 14, 6, 3, 11, 1, 9, 15, 7, 13, 5, };
	public void overlay(Screen screen2, int xa, int ya) {
		int[] oPixels = screen2.pixels;
		int i = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (oPixels[i] / 10 <= dither[((x + xa) & 3) + ((y + ya) & 3) * 4]) pixels[i] = 0;
				i++;
			}

		}
	}

	public void renderLight(int x, int y, int r) {
		x -= xOffset;
		y -= yOffset;
		int x0 = x - r;
		int x1 = x + r;
		int y0 = y - r;
		int y1 = y + r;

		if (x0 < 0) x0 = 0;
		if (y0 < 0) y0 = 0;
		if (x1 > w) x1 = w;
		if (y1 > h) y1 = h;
		// System.out.println(x0 + ", " + x1 + " -> " + y0 + ", " + y1);
		int xd, yd, dist, br;
		for (int yy = y0; yy < y1; yy++) {
			yd = yy - y;
			yd = yd * yd;
			for (int xx = x0; xx < x1; xx++) {
				xd = xx - x;
				dist = xd * xd + yd;
				// System.out.println(dist);
				if (dist <= r * r) {
					br = 255 - dist * 255 / (r * r);
					if (pixels[xx + yy * w] < br) pixels[xx + yy * w] = br;
				}
			}
		}
	}
}