package com.mojang.ld22.level.tile;

import java.io.Serializable;
import java.util.Random;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.Mob;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;

public class Tile implements Serializable {
	private static final long serialVersionUID = -5589510017462744706L;
	public static int tickCount = 0;
	protected Random random = new Random();

	transient public final static Tile[] tiles = new Tile[32];
	transient public final static Tile grass = new GrassTile(0);
	transient public final static Tile rock = new RockTile(1);
	transient public final static Tile water = new WaterTile(2);
	transient public final static Tile flower = new FlowerTile(3);
	transient public final static Tile tree = new TreeTile(4);
	transient public final static Tile dirt = new DirtTile(5);
	transient public final static Tile sand = new SandTile(6);
	transient public final static Tile cactus = new CactusTile(7);
	transient public final static Tile hole = new HoleTile(8);
	transient public final static Tile treeSapling = new SaplingTile(9, grass, tree);
	transient public final static Tile cactusSapling = new SaplingTile(10, sand, cactus);
	transient public final static Tile farmland = new FarmTile(11);
	transient public final static Tile wheat = new WheatTile(12);
	transient public final static Tile lava = new LavaTile(13);
	transient public final static Tile stairsDown = new StairsTile(14, false);
	transient public final static Tile stairsUp = new StairsTile(15, true);
	transient public final static Tile infiniteFall = new InfiniteFallTile(16);
	transient public final static Tile cloud = new CloudTile(17);
	transient public final static Tile hardRock = new HardRockTile(18);
	transient public final static Tile ironOre = new OreTile(19, new Resource("I.ORE", 10 + 4 * 32, Color.get(-1, 100, 322, 544)));
	transient public final static Tile goldOre = new OreTile(20, new Resource("G.ORE", 10 + 4 * 32, Color.get(-1, 110, 440, 553)));
	transient public final static Tile gemOre = new OreTile(21, new Resource("gem", 13 + 4 * 32, Color.get(-1, 101, 404, 545)));
	transient public final static Tile cloudCactus = new CloudCactusTile(22);

	public final byte id;

	public boolean connectsToGrass = false;
	public boolean connectsToSand = false;
	public boolean connectsToLava = false;
	public boolean connectsToWater = false;

	public Tile(int id) {
		this.id = (byte) id;
		if (tiles[id] != null) throw new RuntimeException("Duplicate tile ids!");
		tiles[id] = this;
	}

	public void render(Screen screen, Level level, int x, int y) {
	}

	public boolean mayPass(Level level, int x, int y, Entity e) {
		return true;
	}

	public int getLightRadius(Level level, int x, int y) {
		return 0;
	}

	public void hurt(Level level, int x, int y, Mob source, int dmg, int attackDir) {
	}

	public void bumpedInto(Level level, int xt, int yt, Entity entity) {
	}

	public void tick(Level level, int xt, int yt) {
	}

	public void steppedOn(Level level, int xt, int yt, Entity entity) {
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
		return false;
	}

	public boolean use(Level level, int xt, int yt, Player player, int attackDir) {
		return false;
	}

	public boolean connectsToLiquid() {
		return connectsToWater || connectsToLava;
	}
	
	@Override
	public boolean equals(Object o) {
		if(((Tile)o).id == this.id) {
			return true;
		}
		return false;
	}
}