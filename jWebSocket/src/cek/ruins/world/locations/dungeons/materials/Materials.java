package cek.ruins.world.locations.dungeons.materials;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.dom4j.Element;

import cek.ruins.ScriptExecutor;
import cek.ruins.XmlDocument;

public class Materials {
	private Map<String, Material> materialsTemplates;
	private static Material NOMATERIAL = new Material("NOMATERIAL", 255, 255); 
	
	public static Material NOMATERIAL() {
		return Materials.NOMATERIAL;
	}
	
	public Materials() {
		this.materialsTemplates = new HashMap<String, Material>();
	}
	
	@SuppressWarnings("unchecked")
	public void loadData(String path) throws Exception {
		//create empty material
		Material emptyMaterial = new Material("NOMATERIAL", 255, 255);
		this.materialsTemplates.put(emptyMaterial.id(), emptyMaterial);
		
		File materialsTemplatesDirectory = new File(path);
		File[] templateFiles = materialsTemplatesDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		
		if (templateFiles != null) {
			for (File templateFile : templateFiles) {
				if (templateFile.exists() && templateFile.isFile()) {
					XmlDocument materialsTemplate = new XmlDocument(FileUtils.readFileToString(templateFile, "UTF-8"));
					
					Iterator<Element> materialsIt = (Iterator<Element>) materialsTemplate.selectNodes("/materials/material").iterator();
					
					if (!materialsIt.hasNext()) {
						throw new Exception(path + File.separator + templateFile.getName() + " malformed: no element <material> found.");
					}
					
					while (materialsIt.hasNext()) {
						Element materialEl = materialsIt.next();
						String id = materialEl.attributeValue("id");
						Element name = (Element) materialEl.selectSingleNode("./name");
						Element coords = (Element) materialEl.selectSingleNode("./coords");
						Element passable = (Element) materialEl.selectSingleNode("./passable");
						
						if (id == null || coords == null) {
							throw new Exception(path + File.separator + templateFile.getName() + " malformed: missing required elements.");
						}
						else {
							int coordX = Integer.parseInt(coords.attributeValue("x", "0"));
							int coordY = Integer.parseInt(coords.attributeValue("y", "0"));
							
							Object[] args = {id, coordX, coordY};
							
							Material material = (Material) ScriptExecutor.executor().registerAndCreateObject(Material.class, args, true);
							material.setName(name.getText());
							material.setPassable(Boolean.parseBoolean(passable.getTextTrim()));
							
							this.materialsTemplates.put(material.id(), material);
						}
					}
				}
			}
		}
	}

	public Map<String, Material> templates() {
		return materialsTemplates;
	}
	
	public Material get(String materialId) {
		return this.materialsTemplates.get(materialId);
	}
}
