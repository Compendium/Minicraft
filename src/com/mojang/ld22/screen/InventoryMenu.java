package com.mojang.ld22.screen;

import android.util.Log;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.SaveStone;

public class InventoryMenu extends Menu {
	private Player player;
	private int selected = 0;

	public InventoryMenu(Player player) {
		this.player = player;

		if (player.activeItem != null) {
			player.inventory.items.add(0, player.activeItem);
			player.activeItem = null;
		}
	}

	public void tick() {
		if (input.menu.clicked)
			game.setMenu(null);

		if (input.up.clicked)
			selected--;
		if (input.down.clicked)
			selected++;

		int len = player.inventory.items.size();
		if (len == 0)
			selected = 0;
		if (selected < 0)
			selected += len;
		if (selected >= len)
			selected -= len;

		if (input.attack.clicked && len > 0) {
			if (player.inventory.items.get(selected) instanceof SaveStone) {
				// game.save();
				Log.w("DEBUG", "saved");
				//TODO add menu that tells the user that saving is in progress
				game.setMenu(null);
			} else {
				Item item = player.inventory.items.remove(selected);
				player.activeItem = item;
				game.setMenu(null);
			}
		}
	}

	public void render(Screen screen) {
		// screen.clear(0);
		Font.renderFrame(screen, "inventory", 1, 1, 12, 11);
		renderItemList(screen, 1, 1, 12, 11, player.inventory.items, selected);
	}
}