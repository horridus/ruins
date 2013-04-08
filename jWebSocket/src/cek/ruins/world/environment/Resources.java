package cek.ruins.world.environment;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.dom4j.Element;

import cek.ruins.XmlDocument;

public class Resources {
	public class Names {
		public static final String GROWTH = "growth";
	}

	private Map<String, Resource> resources;
	private Map<String, Map<String, Integer>> biomeResourcesMap;

	public Resources() {
		this.resources = new HashMap<String, Resource>();
		this.biomeResourcesMap = new HashMap<String, Map<String, Integer>>();
	}

	@SuppressWarnings("unchecked")
	public void loadData(String path) throws Exception {
		File templateFile = new File(path);

		if (templateFile.exists() && templateFile.isFile()) {
			XmlDocument resources = new XmlDocument(FileUtils.readFileToString(templateFile, "UTF-8"));

			Iterator<Element> resourcesIt = (Iterator<Element>) resources.selectNodes("/resources/resourcedescriptors/resource").iterator();
			while (resourcesIt.hasNext()) {
				Element resourceEl = resourcesIt.next();
				Resource resource = new Resource();

				String id = resources.getTrimmedText("./@id", resourceEl);
				resource.setResourceId(id);

				String name = resources.getTrimmedText("./name", resourceEl);
				resource.setName(name);

				Map<String, Map<String, Object>> effects = new HashMap<String, Map<String,Object>>();
				Iterator<Element> effectsIt = resourceEl.selectNodes("./effects/effect").iterator();
				while (effectsIt.hasNext()) {
					Element effect = effectsIt.next();
					Map<String, Object> effectArgs = new HashMap<String, Object>();
					
					String type = effect.attributeValue("type");

					if (type != null) {
						//check effects arguments type here
						if (type.equals("growth")) {
							try {
								Integer percentage = Integer.parseInt(effect.attributeValue("percentage", "0"));
								effectArgs.put("percentage", percentage);
							}
							catch(NumberFormatException e) {
								throw new Exception("Error parsing effect <growth> pergentage (" + effect.attributeValue("pergentage", "0") + ")", e);
							}
						}
	
						effects.put(effect.getName(), effectArgs);
					}
				}

				resource.setEffects(effects);
				this.resources.put(id, resource);
			}

			Iterator<Element> biomesIt = (Iterator<Element>) resources.selectNodes("/resources/biomesdescriptors/*").iterator();
			while (biomesIt.hasNext()) {
				Element biomeEl = biomesIt.next();
				Map<String, Integer> resourcesMap = new HashMap<String, Integer>();

				resourcesIt = biomeEl.selectNodes("./resource").iterator();
				while (resourcesIt.hasNext()) {
					Element resourceEl = resourcesIt.next();
					try {
						String resourceId = resourceEl.attributeValue("id", "");
						if (this.resources.containsKey(resourceId)) {
							Integer presence = Integer.parseInt(resourceEl.attributeValue("presence", "100"));
							resourcesMap.put(resourceId, presence);
						}
					}
					catch(NumberFormatException e) {
						throw new Exception("Error parsing resource presence (" + resourceEl.attributeValue("presence", "100") + ")", e);
					}

				}

				this.biomeResourcesMap.put(biomeEl.getName(), resourcesMap);
			}
		}
		else
			throw new Exception(path + " not found.");
	}

	public Resource getResource(String resourceId) {
		return this.resources.get(resourceId);
	}

	public Map<String, Integer> getBiomeResources(Biome biome) {
		return this.biomeResourcesMap.get(biome.toString());
	}
}

