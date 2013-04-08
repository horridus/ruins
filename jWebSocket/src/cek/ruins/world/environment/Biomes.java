package cek.ruins.world.environment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import cek.ruins.XmlDocument;

public class Biomes {
	private Map<Biome, Map<String, Object>> biomesAttributes;

	public Biomes() {
		this.biomesAttributes = new HashMap<Biome, Map<String,Object>>();
	}

	public void loadData(String path) throws Exception {
		File templateFile = new File(path);

		if (templateFile.exists() && templateFile.isFile()) {
			XmlDocument biomes = new XmlDocument(FileUtils.readFileToString(templateFile, "UTF-8"));

			for (Biome biome : Biome.values()) {
				Map<String, Object> attributes = new HashMap<String, Object>();

				Integer evironmentalResistanceModifier = Integer.parseInt(biomes.getAttributeValue("//biome[@name='" + biome.toString() + "']/@envresistance", "0"));
				Integer migrationFailure = Integer.parseInt(biomes.getAttributeValue("//biome[@name='" + biome.toString() + "']/@migrationfail", "0"));

				attributes.put("envresistance", evironmentalResistanceModifier);
				attributes.put("migrationfail", migrationFailure);

				this.biomesAttributes.put(biome, attributes);
			}
		}
	}

	public Map<String, Object> biomeAttributes(Biome biome) {
		return this.biomesAttributes.get(biome);
	}
}
