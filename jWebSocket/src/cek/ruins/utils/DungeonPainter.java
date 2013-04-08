package cek.ruins.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import cek.ruins.world.locations.dungeons.Dungeon;
import cek.ruins.world.locations.dungeons.DungeonTile;
import cek.ruins.world.locations.dungeons.Tiles;

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
				Tiles tile = level.get(x).get(y).material();
				switch (tile) {
				case NONE:
					if (x%16 == 0 || y%16 == 0) //TEMP draw cells borders
						image.setRGB(x, y, Color.darkGray.getRGB());
					else
						image.setRGB(x, y, Color.black.getRGB());
					break;
				case DOLOMITE_FLOOR:
					image.setRGB(x, y, Color.green.getRGB());
					break;
				case DOLOMITE:
					image.setRGB(x, y, Color.red.getRGB());
					break;
				default:
					break;
				}
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
