package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.sound.Sound;

public class LoadingMenu extends Menu
{
	public boolean displayed = false;
	
	public LoadingMenu()
	{
	}

	public void tick() {
		if(displayed)
		{
				Sound.tick.play();
				game.resetGame();
				game.setMenu(null);
		}
	}

	public void render(Screen screen) {
		screen.clear(0);
		String msg1 = "Loading";
		String msg2 = "Please be patient <3";
		Font.draw(msg1, screen, screen.w / 2 - (msg1.length()*8/2), screen.h / 2 - 4, Color.get(0,111,111,111));
		Font.draw(msg2, screen, screen.w / 2 - (msg2.length()*8/2), screen.h / 2 + 4, Color.get(0,111,111,111));
		displayed = true;
	}
}
