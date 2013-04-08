package cek.ruins.world.civilizations;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.dom4j.Element;

import cek.ruins.XmlDocument;

public class CivilizationsTemplates {
	private List<CivilizationTemplate> civilizationsTemplates;

	public CivilizationsTemplates() {
		this.civilizationsTemplates = new ArrayList<CivilizationTemplate>();
	}

	@SuppressWarnings("unchecked")
	public void loadData(String path) throws Exception {
		File templateFile = new File(path);

		if (templateFile.exists() && templateFile.isFile()) {
			XmlDocument civsTemplate = new XmlDocument(FileUtils.readFileToString(templateFile, "UTF-8"));

			int index = 0;

			Iterator<Element> civsIt = (Iterator<Element>) civsTemplate.selectNodes("/civilizations/civilization").iterator();
			while (civsIt.hasNext()) {
				Element civEl = civsIt.next();
				CivilizationTemplate template = new CivilizationTemplate();

				String name = civsTemplate.getTrimmedText("./species", civEl);
				template.setSpecies(name);

				String iconsFolder = civsTemplate.getTrimmedText("./iconsFolder", civEl);
				template.setIconsFolder(iconsFolder);

				String growthRate = civsTemplate.getTrimmedText("./growthRate", civEl);
				template.setGrowthRate(Float.parseFloat(growthRate));
				
				Iterator<Element> pBiomesIt = (Iterator<Element>) civsTemplate.selectNodes("./preferredBiomes/biome").iterator();
				while (pBiomesIt.hasNext()) {
					Element pBiome = pBiomesIt.next();
					String biomeName = pBiome.attributeValue("name", "");
					if (!biomeName.equals("")) {
						template.addPreferredBiome(biomeName);
					}
				}

				template.setIndex(index);
				index++;

				civilizationsTemplates.add(template);
			}
		}
		else
			throw new Exception(path + " not found.");
	}
	
	public Civilizations civilizationsNewInstance(Coats coats) {
		return new Civilizations(this, coats);
	}

	public List<CivilizationTemplate> templates() {
		return this.civilizationsTemplates;
	}
}
