package com.mojang.ld22.item;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

public class SaveStone extends Item {
	private static final long serialVersionUID = 2157891496716457992L;
	public String getName() {
		return "Mem Stone";
	}
	public int getSprite() {
		return 6 + 10 * 32;
	}
	public void renderInventory(Screen screen, int x, int y) {
		screen.render(x, y, getSprite(), Color.get(-1, 333, 555, 888), 0);
		Font.draw(getName(), screen, x + 8, y, Color.get(-1, 555, 555, 555));
	}
}
