package com.mojang.ld22;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class InputHandler {
	public class Key {
		public int presses=0, absorbs=0;
		public boolean down, clicked;

		public Key() {
			keys.add(this);
		}

		public void toggle(boolean pressed) {
			down = pressed;
			if (pressed) {
				presses++;
			}
		}

		public void tick() {
			if (absorbs < presses) {
				absorbs++;
				clicked = true;
			} else {
				clicked = false;
			}
		}
	}

	public List<Key> keys = new ArrayList<Key>();

	public Key up = new Key();
	public Key down = new Key();
	public Key left = new Key();
	public Key right = new Key();
	public Key attack = new Key();
	public Key menu = new Key();

	public void releaseAll() {
		for (int i = 0; i < keys.size(); i++) {
			keys.get(i).down = false;
		}
	}

	public void tick() {
		for (int i = 0; i < keys.size(); i++) {
			keys.get(i).tick();
		}
	}

	public InputHandler(Game game) {
//		game.addKeyListener(this);
		//keys.add(up);
		//keys.add(down);
		//keys.add(left);
		//keys.add(right);
	}
	
	public static final int UP = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	public static final int RIGHT = 4;
	public static final int ATTACK = 5;
	public static final int MENU = 6;
	
	public void keyEvent(int keyId, boolean isDown)
	{
		//Log.w("keyEvent", keyId + " " + isDown);
		switch (keyId)
		{
		case UP:
			up.toggle(isDown);
			break;
		case DOWN:
			down.toggle(isDown);
			break;
		case LEFT:
			left.toggle(isDown);
			break;
		case RIGHT:
			right.toggle(isDown);
			break;
		case MENU:
			menu.toggle(isDown);
			break;
		case ATTACK:
			attack.toggle(isDown);
			break;
			
		}
	}
	
	public boolean isPressed (int keyId)
	{
		return keys.get(keyId-1).down;
	}
/*
	public void keyPressed(KeyEvent ke) {
		toggle(ke, true);
	}

	public void keyReleased(KeyEvent ke) {
		toggle(ke, false);
	}*/
/*
	private void toggle(KeyEvent ke, boolean pressed) {
		if (ke.getKeyCode() == KeyEvent.VK_NUMPAD8) up.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_NUMPAD2) down.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_NUMPAD4) left.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_NUMPAD6) right.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_W) up.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_S) down.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_A) left.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_D) right.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_UP) up.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_DOWN) down.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_LEFT) left.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_RIGHT) right.toggle(pressed);

		if (ke.getKeyCode() == KeyEvent.VK_TAB) menu.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_ALT) menu.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_ALT_GRAPH) menu.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_SPACE) attack.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_CONTROL) attack.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_NUMPAD0) attack.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_INSERT) attack.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_ENTER) menu.toggle(pressed);

		if (ke.getKeyCode() == KeyEvent.VK_X) menu.toggle(pressed);
		if (ke.getKeyCode() == KeyEvent.VK_C) attack.toggle(pressed);
	}

	public void keyTyped(KeyEvent ke) {
	}*/
}
