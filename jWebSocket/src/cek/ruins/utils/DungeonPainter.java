package cek.ruins.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import cek.ruins.world.locations.dungeons.Dungeon;
import cek.ruins.world.locations.dungeons.DungeonTile;
import cek.ruins.world.locations.dungeons.materials.Material;

public class DungeonPainter {
	public BufferedImage createDungeonImage(Dungeon dungeon, int lvlNumber) {
		BufferedImage image = new BufferedImage(dungeon.size(), dungeon.size(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		g.setColor(Color.black);
		g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
		
		List<List<DungeonTile>> level = dungeon.level(lvlNumber);
		for (int x = 0; x < dungeon.size(); x++) {
			for (int y = 0; y < dungeon.size(); y++) {
				//TEMP////////////////////////////
				Material tileMaterial = level.get(x).get(y).material();
				image.setRGB(x, y, new Color(tileMaterial.x(), tileMaterial.y(), 0, 255).getRGB());
				//////////////////////////////////
			}
		}
		
		//TEMP////////////////////////////
//		List<List<DungeonCell>> cells =  dungeon.cells(lvlNumber);
//		for (int r = 0; r < dungeon.numCellsPerSide(); r++)
//			for (int n = 0; n < dungeon.numCellsPerSide(); n++) {
//				DungeonCell cell = cells.get(n).get(r);
//				for (Point entrances : cell.entrances()) {
//					//FIXME perché ho dovuto invertire le cordinate?
//					//image.setRGB((int)entrances.y + n*cell.size(), (int)entrances.x + r*cell.size(), Color.blue.getRGB());
//					image.setRGB((int)entrances.x + n*cell.size(), (int)entrances.y + r*cell.size(), Color.magenta.getRGB());
//				}
//			}
		//////////////////////////////////
		
		return image;
	}
}
