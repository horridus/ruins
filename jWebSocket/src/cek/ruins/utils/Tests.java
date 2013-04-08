package cek.ruins.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;

import cek.ruins.configuration.Configuration;
import cek.ruins.map.Map;
import cek.ruins.world.environment.Climates;
import cek.ruins.world.environment.Resources;

public class Tests {

	public static void main(String[] args) throws IOException {
		Random generator = new Random(new Date().getTime());
		Tests.simulatePopulationGrowth(20, 0.2f, 100, 100);
	}

	static private void testNoise(Random generator) throws IOException {
		int s = 1000;

		BufferedImage image = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();

		g.setColor(MapPainter.TRANSPARENT_BLACK_COLOR);
		g.fillRect( 0, 0, image.getWidth(), image.getHeight() );

		int pixels[] = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

		int a = generator.nextInt();
		int b = generator.nextInt();
		int c = generator.nextInt();
		int d = generator.nextInt();
		int e = generator.nextInt();

		for (int y = 0; y < s; y++) {
			for (int x = 0; x < s; x++) {
				float z0 = (float) ImprovedNoise.noise(x/100.0, y/100.0, a);
				z0 = ((z0 + 1.0f)/2.0f);

				float z1 = (float) ImprovedNoise.noise(x/200.0, y/200.0, b);
				z1 = ((z1 + 1.0f)/2.0f);

				float z2 = (float) ImprovedNoise.noise(x/1000.0, y/1000.0, c);
				z2 = ((z2 + 1.0f)/2.0f);

				float z3 = (float) ImprovedNoise.noise(x/100.0, y/100.0, d);
				z3 = ((z3 + 1.0f)/2.0f);

				float z4 = (float) ImprovedNoise.noise(x/1000.0, y/1000.0, e);
				z4 = ((z4 + 1.0f)/2.0f);

				float z13 = z3 *0.5f + z1 * 0.5f;
				float z24 = z2 *0.5f + z4 * 0.5f;

				float z = z13;

				//z = (z + 1.0f)/2.0f;
				System.out.println(z);

				z = (float) ((z<0.0)? -z : z);
				pixels[x+y*s] = new Color(z,z,z).getRGB();
			}
		}



		ImageIO.write(image, "png", new File("/tmp/noisrTest00.png"));
	}

	static private void testNoise2(Random generator) throws IOException {
		int s = 1000;

		BufferedImage image = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();

		g.setColor(MapPainter.TRANSPARENT_BLACK_COLOR);
		g.fillRect( 0, 0, image.getWidth(), image.getHeight() );

		int pixels[] = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

		int a = generator.nextInt();
		int b = generator.nextInt();
		int c = generator.nextInt();
		int d = generator.nextInt();
		int e = generator.nextInt();

		NoiseGenerator ng = new NoiseGenerator();

		for (int y = 0; y < s; y++) {
			for (int x = 0; x < s; x++) {
				float z0 = (float) ng.noise01(x/100.0, y/100.0, a);

				float z1 = (float) ng.noise01(x/200.0, y/200.0, b);

				float z2 = (float) ng.noise01(x/1000.0, y/1000.0, c);

				float z3 = (float) ng.noise01(x/100.0, y/100.0, d);

				float z4 = (float) ng.noise01(x/1000.0, y/1000.0, e);

				float z13 = z3 *0.5f + z1 * 0.5f;
				float z24 = z2 *0.5f + z4 * 0.5f;

				float z = z13;

				//z = (z + 1.0f)/2.0f;
				System.out.println(z);

				z = (float) ((z<0.0)? -z : z);
				pixels[x+y*s] = new Color(z,z,z).getRGB();
			}
		}



		ImageIO.write(image, "png", new File("/tmp/noisrTest00.png"));
	}


	static private Map createNewWorld(Random generator, int size, int numSites, Climates climate, double baseTemperature, double thermicalExcursionA, double thermicalExcursionB, Resources resources) throws IOException {
		UUID id = UUID.randomUUID();

		//generate a fresh map
		Map worldMap = new Map(id, climate, baseTemperature, thermicalExcursionA, thermicalExcursionB);
		worldMap.generate(size, numSites, generator, resources);

		//create a new entry in worlds folder
		File worldDirectory = new File(Configuration.WORLDS_FOLDER_LOCATION + File.separator + id);
		boolean dirCreated = worldDirectory.mkdir();

		if (dirCreated) {
			MapPainter mapPainter = new MapPainter(worldMap, generator);
			BufferedImage image = mapPainter.createPaperMap();

			File outputfile = new File(worldDirectory, "paper.png");
			ImageIO.write(image, "png", outputfile);

			image = mapPainter.createElevationMap();

			outputfile = new File(worldDirectory, "elevation.png");
			ImageIO.write(image, "png", outputfile);

			image = mapPainter.createMoistureMap();

			outputfile = new File(worldDirectory, "moisture.png");
			ImageIO.write(image, "png", outputfile);

			image = mapPainter.createTemperatureMap();

			outputfile = new File(worldDirectory, "temperature.png");
			ImageIO.write(image, "png", outputfile);

			image = mapPainter.createBiomesMap();

			outputfile = new File(worldDirectory, "biomes.png");
			ImageIO.write(image, "png", outputfile);

			image = mapPainter.createRegionIndex();

			//create coords to regions map with image data
			int[] imgData = new int[image.getWidth() * image.getHeight()];
			for (int y = 0; y < worldMap.size(); y++) {
				for (int x = 0; x < worldMap.size(); x++) {
					imgData[x + y*worldMap.size()] = ((image.getRGB(x, y) >> 8)  & 0x0000FFFF) - 1;
				}
			}

			worldMap.processCoordsToRegionsMap(imgData);

			outputfile = new File(worldDirectory, "rindex.png");
			ImageIO.write(image, "png", outputfile);

			//now save map to db
			//TODO
		}
		else {
			throw new IOException("Cannot create directory: " + worldDirectory.getAbsolutePath());
		}

		return worldMap;
	}

	static private void simulatePopulationGrowth(int startinPopulation, float growthRate, float environmentalResistance, int numIterations) {
		float currentPopulation = startinPopulation;

		for (int i=0; i<numIterations; i++) {
			float factor = (1.0f - currentPopulation/environmentalResistance);
			float deltaPop = currentPopulation * growthRate * (1.0f - currentPopulation/environmentalResistance);
			currentPopulation = currentPopulation + deltaPop;
			System.out.println(i + " " + currentPopulation + " " + deltaPop + " " + factor);
		}
	}
}
