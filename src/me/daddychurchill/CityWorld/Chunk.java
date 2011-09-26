package me.daddychurchill.CityWorld;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;

public class Chunk 
{
	public byte[] blocks;
	
	// keeps the same stair type throughout a building
	private byte buildingStairID;
	
	// Glass type for a building
	private byte buildingGlassID;
	
		
	Chunk () 
	{
		blocks = new byte[32768];
	}
	
	protected void setBlock(int x, int y, int z, byte blockId) 
	{
		blocks[(x * 16 + z) * 128 + y] = blockId;
	}
	
	protected byte getBlock(int x, int y, int z) 
	{
		return blocks[(x * 16 + z) * 128 + y];
	}
	
	public void setLayer(Random random, int blocky, Material material)
	{
		int x, z;
		byte materialId = (byte) material.getId();
		
		for (x = 0; x < 16; x++) {
			for (z = 0; z < 16; z++) {
				setBlock(x, blocky, z, materialId);
			}
		}
	}
	
	private void setLayer(Random random, int blocky, int height, Material material) 
	{
		int x, z, y;
		byte materialId = (byte) material.getId();
		
		for (x = 0; x < 16; x++) {
			for (z = 0; z < 16; z++) {
				for (y = blocky; y < blocky + height; y++) {
					setBlock(x, y, z, materialId);
				}
			}
		}
	}

	final static int chunkwidth = 16;
	final static int foundationheight = 2;
	final static int wallheight = 4;
	final static int wallinset = 1;
	final static int windowsall = 1;
	final static int windowsrandom = 0;
	final static int windowsnone = -1;
	final static int stairinsetx = 5;
	final static int stairinsetz = 6;
	final static int sidewalkwidth = 3;
	
	private byte getWallMaterial(Random random, int i, byte materialId, byte windowId, int windowsize) 
	{
		
		// assume the wall is the winner
		byte blockId = materialId;
		
		// when we are not talking about the corners, when they are glass they look weird
		if (i != wallinset && i != chunkwidth - wallinset - 1 && windowsize != windowsnone) 
		{
			
			// windows win if it all windows or...
			if (windowsize == windowsall || (windowsize != windowsrandom && i % windowsize != 0) ||	(windowsize == windowsrandom && random.nextInt(3) == 0))
			{
				blockId = windowId;
			} 	
		}
		
		return blockId;
	}
	
	private void setFloorWall(Random random, int blocky, Material material, byte glass, int windowsize) 
	{
		int x, y, z;
		byte materialId = (byte) material.getId();
		byte windowId = glass;
		byte blockId;
		
		for (x = wallinset; x < chunkwidth - wallinset; x++) 
		{
			blockId = getWallMaterial(random, x, materialId, windowId, windowsize);
			
			// lets do it
			for (y = blocky; y < blocky + wallheight - 1; y++) 
			{
				setBlock(x, y, wallinset, blockId);
				setBlock(x, y, chunkwidth - wallinset - 1, blockId);
			}
		}
			
		for (z = wallinset; z < chunkwidth - wallinset; z++) 
		{
			blockId = getWallMaterial(random, z, materialId, windowId, windowsize);
			
			// lets do it
			for (y = blocky; y < blocky + wallheight - 1; y++) 
			{
				setBlock(wallinset, y, z, blockId);
				setBlock(chunkwidth - wallinset - 1, y, z, blockId);
			}
		}
	}
	
	private void setFloorLayer(Random random, int blocky, Material material)
	{
		int x, z;
		byte materialId = (byte) material.getId();
		
		for (x = wallinset; x < chunkwidth - wallinset; x++) 
		{
			for (z = wallinset; z < chunkwidth - wallinset; z++) 
			{
				setBlock(x, blocky, z, materialId);
			}
		}
	}
	
	private void setFloor(Random random, int blocky, Material material, Material layer, byte glass, int windowsize, byte stairId)
	{

		// the big bits
		setFloorWall(random, blocky, material, glass, windowsize);
		setFloorLayer(random, blocky + wallheight - 1, layer);
		
		// the stairs
		byte airId = (byte) Material.AIR.getId();
		// Material.SMOOTH_STAIRS.getId();
		
		int x;
		for (x = 0; x < wallheight; x++) 
		{
			setBlock(x + stairinsetx, blocky + wallheight - 1, stairinsetz, airId);
			setBlock(x + stairinsetx, blocky + wallheight - 1, stairinsetz + 1, airId);
			
			setBlock(x + stairinsetx, blocky + x, stairinsetz, stairId);
			setBlock(x + stairinsetx, blocky + x, stairinsetz + 1, stairId);
		}
	}
	
	protected int getFloorCount(Random random)
	{
		switch (random.nextInt(5)) 
		{
		case 0:
			return random.nextInt(4) + 1;
		case 1:
		case 2:
		case 3:
			return random.nextInt(10) + 5;
		default:
			return random.nextInt(5) + 12;
		}
	}
	
	protected Material getRandomStairType(Random random)
	{
		switch (random.nextInt(5)) 
		{
		case 1: return Material.WOOD_STAIRS;
		case 2: return Material.BRICK_STAIRS;
		case 3: return Material.SMOOTH_STAIRS;
		//case 4: return Material.Nether_Stairs;  <-- future stair :)
		default: return Material.COBBLESTONE_STAIRS;
		
		}
	}
	protected Material getRandomWallMaterial(Random random)
	{
		switch (random.nextInt(15)) 
		{
		case 1: return Material.COBBLESTONE;
		case 2: return Material.STONE;
		case 3: return Material.SMOOTH_BRICK;
		case 4: return Material.CLAY;
		case 5: return Material.IRON_BLOCK;
		case 6: return Material.BRICK;
		case 7: return Material.MOSSY_COBBLESTONE;
		case 8: return Material.DOUBLE_STEP;
		case 9: return Material.SANDSTONE;
		case 10: return Material.WOOD;
		case 11: return Material.WOOL;
		case 12: return Material.LAPIS_BLOCK;
		case 14: return Material.LOG;
		default: return Material.SAND;
		
		}
	}
	
	//randomly pick between glass blocks and glass panes
	protected Material getRandomGlassType(Random rand)
	{
		switch(rand.nextInt(4))
		{
		case 1: return Material.THIN_GLASS;
		case 2: return Material.GLASS;
		case 3: return Material.THIN_GLASS;
		default: return Material.GLASS; 
		}
	}
	
	protected Material getRandomLayerMaterial(Random random) 
	{
		switch (random.nextInt(14)) 
		{
		case 1: return Material.COBBLESTONE;
		case 2: return Material.STONE;
		case 3: return Material.SMOOTH_BRICK;
		case 4: return Material.CLAY;
		case 5: return Material.IRON_BLOCK;
		case 6: return Material.BRICK;
		case 7: return Material.MOSSY_COBBLESTONE;
		case 8: return Material.DOUBLE_STEP;
		case 9: return Material.SANDSTONE;
		case 10: return Material.WOOD;
		case 11: return Material.LAPIS_BLOCK;
		case 13: return Material.LOG;
		default: return Material.WOOL;
		}
	}
	
	public void setBuilding(World world, Random random, int blocky) 
	{
		int floor;
		int floors = getFloorCount(random);
		int basementfloors = random.nextInt(3);
		int windowsize = random.nextInt(5);
		
		// let's pick some materials
		Material foundation = Material.STONE;
		Material material = getRandomWallMaterial(random);
		Material layer = getRandomLayerMaterial(random);
		buildingStairID = (byte)getRandomStairType(random).getId();
		buildingGlassID = (byte)getRandomGlassType(random).getId();
		
		
		// Always use 2 separate materials for buildings to keep things looking nice
		while (layer == material)
		{
			layer = getRandomLayerMaterial(random);
		}
		
		// some basements, maybe 
		// **** need the ability to define max basements in config ****
		if (basementfloors > 0) 
		{
			
			// outer layer
			setLayer(random, blocky + 1, foundation);

			// the rooms them
			for (floor = -basementfloors; floor < 0; floor++) 
			{
				setFloor(random, blocky - Math.abs(floor) * wallheight + foundationheight, material, layer, buildingGlassID, windowsnone, buildingStairID);
			}
			
			// bottom most bit
			setFloorLayer(random, blocky - basementfloors * wallheight - 1 + foundationheight, layer);
		} 
		else
		{
			
			// if no basement then a foundation
			setLayer(random, blocky, foundationheight, foundation);
		}
		
		// now the floor itself
		for (floor = 0; floor < floors; floor++) 
		{
			setFloor(random, blocky + floor * 4 + foundationheight, material, layer, buildingGlassID, windowsize, buildingStairID);
		}
	}
	
	private void setSidewalkPart(int blocky, int xStart, int zStart, int xSize, int zSize, Material material) 
	{
		int x, z;
		byte materialId = (byte) material.getId();
		
		for (x = xStart; x < xStart + xSize; x++)
		{
			for (z = zStart; z < zStart + zSize; z++) 
			{
				setBlock(x, blocky, z, materialId);
			}
		}
	}

	public void setSidewalks(Random random, int blocky, Material material, boolean northsouth, boolean eastwest) 
	{
		
		// corners
		setSidewalkPart(blocky, 0, 0, sidewalkwidth, sidewalkwidth, material);
		setSidewalkPart(blocky, chunkwidth - sidewalkwidth, 0, sidewalkwidth, sidewalkwidth, material);
		setSidewalkPart(blocky, 0, chunkwidth - sidewalkwidth, sidewalkwidth, sidewalkwidth, material);
		setSidewalkPart(blocky, chunkwidth - sidewalkwidth, chunkwidth - sidewalkwidth, sidewalkwidth, sidewalkwidth, material);
		
		// strait bits
		if (!northsouth)
		{
			setSidewalkPart(blocky, sidewalkwidth, 0, chunkwidth - sidewalkwidth * 2, sidewalkwidth, material);
			setSidewalkPart(blocky, sidewalkwidth, chunkwidth - sidewalkwidth, chunkwidth - sidewalkwidth * 2, sidewalkwidth, material);
		}
		if (!eastwest) 
		{
			setSidewalkPart(blocky, 0, sidewalkwidth, sidewalkwidth, chunkwidth - sidewalkwidth * 2, material);
			setSidewalkPart(blocky, chunkwidth - sidewalkwidth, sidewalkwidth, sidewalkwidth, chunkwidth - sidewalkwidth * 2, material);
		}

		// TODO street lights
}

	public void setStreet(World world, Random random, int blocky, boolean northsouth, boolean eastwest) 
	{
		// top layers
		setLayer(random, blocky, Material.STONE);
		setSidewalks(random, blocky + 1, Material.STEP, northsouth, eastwest);
		
		// TODO traffic lanes
		// TODO sewers
		// TODO vaults
		// TODO plumbing
	}
	
	public void setPark(World world, Random random, int blocky) 
	{
		// top layers
		setLayer(random, blocky, Material.STONE);
		setLayer(random, blocky + 1, Material.GRASS);
		
		// TODO trees via world.generateTree(..)
		// TODO fountain
		// TODO cistern
	}
}
