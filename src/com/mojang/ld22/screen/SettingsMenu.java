package com.mojang.ld22.screen;

import com.mojang.ld22.Game;
import com.mojang.ld22.InputHandler;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

public class SettingsMenu extends Menu {
	private Menu parent;
	private int selected = 0;
	private static String[] options = new String[] { "Controls", "Back" };

	public SettingsMenu(Menu parent) {
		this.parent = parent;
	}

	public void init(Game g, InputHandler ih) {
		this.game = g;
		this.input = ih;
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

		if (input.menu.clicked) {
			game.setMenu(parent);
		}
		
		if(input.attack.clicked){
			if(selected == options.length-1)
				game.setMenu(parent);
			else if (selected == 0)
			{
				game.settings.controlshflipped = !game.settings.controlshflipped;
			}
		}
	}

	public void render(Screen screen) {
		screen.clear(0);

		for (int i = 0; i < options.length; i++) {
			String msg;
			{
				if (i == 0) {
					msg = options[i] + " : " + (game.settings.controlshflipped ? "flipped" : "normal");
				} else {
					msg = options[i];
				}
			}
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
