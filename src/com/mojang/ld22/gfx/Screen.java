package com.mojang.ld22.gfx;

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
					colors[pp++] = 0xff000000 | r1 << 16 | g1 << 8 | b1;

				}
			}
		}
	}

	public void clear(int color) {
		for (int i = 0; i < pixels.length; i++)
			pixels[i] = color | 0xff000000;
	}

	public void renderRect(int xp, int yp, int xs, int ys, int colors) {
		for (int x = xp; x < xp + xs; x++) {
			for (int y = yp; y < yp + ys; y++) {
				if ((x < w && y < h))
					setPixel(x, y, colors);
			}
		}
	}

	public void renderLine(int x1, int x2, int y1, int y2, int color) {
		int c = color;
		int deltax = Math.abs(x2 - x1);
		int deltay = Math.abs(y2 - y1);
		int x = x1;
		int y = y1;

		int xinc1, xinc2, yinc1, yinc2;
		if (x2 >= x1) {
			xinc1 = 1;
			xinc2 = 1;
		} else {
			xinc1 = -1;
			xinc2 = -1;
		}

		if (y2 >= y1) {
			yinc1 = 1;
			yinc2 = 1;
		} else {
			yinc1 = -1;
			yinc2 = -1;
		}

		int den, num, numadd, numpixels;
		if (deltax >= deltay) {
			xinc1 = 0;
			yinc2 = 0;
			den = deltax;
			num = deltax / 2;
			numadd = deltay;
			numpixels = deltax;
		} else {
			xinc2 = 0;
			yinc1 = 0;
			den = deltay;
			num = deltay / 2;
			numadd = deltax;
			numpixels = deltay;
		}

		for (int curpixel = 0; curpixel <= numpixels; curpixel++) {
			if (color == -1)
				invertPixel(x, y, c);
			else
				setPixel(x, y, color);
			num += numadd;
			if (num >= den) {
				num -= den;
				x += xinc1;
				y += yinc1;
			}
			x += xinc2;
			y += yinc2;
		}
	}

	// thanks to Bresenham!
	public void renderCircle(int x0, int y0, int radius, int color) {
		int f = 1 - radius;
		int ddF_x = 1;
		int ddF_y = -2 * radius;
		int x = 0;
		int y = radius;

		// setPixel(x0, y0 + radius, color);
		// setPixel(x0, y0 - radius, color);
		// setPixel(x0 + radius, y0, color);
		// setPixel(x0 - radius, y0, color);
		renderLine(x0, x0, y0 + radius, y0 - radius, color);
		renderLine(x0 - radius, x0 + radius, y0, y0, color);

		if (color == -1) {
			while (x < y) {
				if (f >= 0) {
					y--;
					ddF_y += 2;
					f += ddF_y;
				}
				x++;
				ddF_x += 2;
				f += ddF_x;

				invertPixel(x0 + x, y0 + y, color);
				invertPixel(x0 - x, y0 + y, color);

				invertPixel(x0 + x, y0 - y, color);
				invertPixel(x0 - x, y0 - y, color);

				invertPixel(x0 + y, y0 + x, color);
				invertPixel(x0 - y, y0 + x, color);

				invertPixel(x0 + y, y0 - x, color);
				invertPixel(x0 - y, y0 - x, color);
			}
		} else {
			while (x < y) {
				if (f >= 0) {
					y--;
					ddF_y += 2;
					f += ddF_y;
				}
				x++;
				ddF_x += 2;
				f += ddF_x;

				setPixel(x0 + x, y0 + y, color);
				setPixel(x0 - x, y0 + y, color);

				setPixel(x0 + x, y0 - y, color);
				setPixel(x0 - x, y0 - y, color);

				setPixel(x0 + y, y0 + x, color);
				setPixel(x0 - y, y0 + x, color);

				setPixel(x0 + y, y0 - x, color);
				setPixel(x0 - y, y0 - x, color);
			}
		}
	}

	private void invertPixel(int x, int y, int color) {
		if (x > 0 && y > 0 && x < w && y < h) {
			// if(color == -1)
			if ((pixels[x + y * w] & 0x80000000) == 0)
				pixels[x + y * w] = ~(pixels[x + y * w]) | 0xff000000;
			// else
			// pixels[x+y*w] = color;
		}
	}

	public void setPixel(int x, int y, int color) {
		if (x > 0 && y > 0 && x < w && y < h) {
			//ARGB
			//pixels[x + y * w] = color;
			int alpha = ((color & 0xff000000) >> 24) & 0xff;
			float a = (float)alpha / (float)0xff;
			
			int sr = ((pixels[x+y*w] & 0x00ff0000) >> 16) & 0xff;
			int sg = ((pixels[x+y*w] & 0x0000ff00) >> 8) & 0xff;
			int sb = ((pixels[x+y*w] & 0x000000ff) >> 0) & 0xff;
			
			int s2r = ((color & 0x00ff0000) >> 16) & 0xff;
			int s2g = ((color & 0x0000ff00) >> 8) & 0xff;
			int s2b = ((color & 0x000000ff) >> 0) & 0xff;
			
			int e = (((int)((float)sr * a)&0xff) << 16) | (((int)((float)sg * a)&0xff) << 8) | (((int)((float)sb * a)&0xff) << 0) | 0xff000000;
			a = 1-a;
			int d = (((int)((float)s2r * a)&0xff) << 16) | (((int)((float)s2g * a)&0xff) << 8) | (((int)((float)s2b * a)&0xff) << 0) | 0xff000000;
			pixels[x+y*w] = (d+e) | 0xff000000;
			
			//pixels[x + y * w] = alpha * Source + (1-alpha) * Source2;
		}
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
			if (mirrorY)
				ys = 7 - y;
			if (y + yp < 0 || y + yp >= h)
				continue;
			for (int x = 0; x < 8; x++) {
				if (x + xp < 0 || x + xp >= w)
					continue;

				xs = x;
				if (mirrorX)
					xs = 7 - x;
				col = (colors >> (sheet.pixels[xs + ys * sheet.width + toffs] * 8)) & 255;
				if (col < 255)
					pixels[(x + xp) + (y + yp) * w] = Screen.colors[col] | 0xff000000;
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
				if (oPixels[i] / 10 <= dither[((x + xa) & 3) + ((y + ya) & 3) * 4])
					pixels[i] = 0;
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

		if (x0 < 0)
			x0 = 0;
		if (y0 < 0)
			y0 = 0;
		if (x1 > w)
			x1 = w;
		if (y1 > h)
			y1 = h;
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
					if (pixels[xx + yy * w] < br)
						pixels[xx + yy * w] = br;
				}
			}
		}
	}
}