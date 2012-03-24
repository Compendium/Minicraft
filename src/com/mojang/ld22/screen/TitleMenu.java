package com.mojang.ld22.screen;

import java.io.File;

import com.mojang.ld22.Game;
import com.mojang.ld22.InputHandler;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

public class TitleMenu extends Menu {
	private int selected = 0;

	private static String[] options;

	boolean saveExists = false;

	public TitleMenu() {

	}

	public void init(Game game, InputHandler input) {
		this.input = input;
		this.game = game;

		if (game.mExternalStorageAvailable) {
			File file = new File(game.ctxt.getExternalFilesDir(null), "save.obj");
			saveExists = file.exists();
		} else {
			saveExists = false;
		}

		if (saveExists)
			options = new String[] { "Load game", "Start new game", "How to play", "About" };
		else
			options = new String[] { "Start game", "How to play", "About" };
	}

	public void tick() {
		if (input.up.clicked)
			selected--;
		if (input.down.clicked)
			selected++;

		int len = options.length;
		if (selected < 0)
			selected += len;
		if (selected >= len)
			selected -= len;

		if (input.attack.clicked || input.menu.clicked) {
			if (selected == 0) {
				if (saveExists)
					game.setMenu(new LoadingMenu(LoadingMenu.LOADGAME));
				else
					game.setMenu(new LoadingMenu(LoadingMenu.NEWGAME));
			}
			if (selected == 1) {
				if (saveExists)
					game.setMenu(new LoadingMenu(LoadingMenu.NEWGAME));
				else
					game.setMenu(new InstructionsMenu(this));
			}
			if (selected == 2) {
				if (saveExists)
					game.setMenu(new InstructionsMenu(this));
				else
					game.setMenu(new AboutMenu(this));
			}
			if (selected == 3) {
				if (saveExists)
					game.setMenu(new AboutMenu(this));
			}
		}
	}

	public void render(Screen screen) {
		screen.clear(0);

		int h = 2;
		int w = 13;
		int titleColor = Color.get(0, 010, 131, 551);
		int xo = (screen.w - w * 8) / 2;
		int yo = 24;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				screen.render(xo + x * 8, yo + y * 8, x + (y + 6) * 32, titleColor, 0);
			}
		}

		for (int i = 0; i < options.length; i++) {
			String msg = options[i];
			int col = Color.get(0, 222, 222, 222);
			if (i == selected) {
				msg = "> " + msg + " <";
				col = Color.get(0, 555, 555, 555);
			}
			Font.draw(msg, screen, (screen.w - msg.length() * 8) / 2, (8 + i) * 8, col);
		}
		Font.draw("(Use the touchscreen!)", screen, 0, screen.h - 8, Color.get(0, 111, 111, 111));
	}
}