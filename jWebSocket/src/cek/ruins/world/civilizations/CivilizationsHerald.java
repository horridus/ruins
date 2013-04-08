package cek.ruins.world.civilizations;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;

import cek.ruins.XmlDocument;

public class CivilizationsHerald {
	protected List<Coat> coats;
	
	public CivilizationsHerald() {
		this.coats = new ArrayList<Coat>();
	}
	
	@SuppressWarnings("unchecked")
	public void loadData(String path) throws Exception {
		File templateFile = new File(path);

		if (templateFile.exists() && templateFile.isFile()) {
			XmlDocument coatsTemplate = new XmlDocument(FileUtils.readFileToString(templateFile, "UTF-8"));
			
			Iterator<Element> coatsIt = (Iterator<Element>) coatsTemplate.selectNodes("//coat").iterator();
			while (coatsIt.hasNext()) {
				Element coatEl =  coatsIt.next();
				
				Element colorsEl = (Element) coatEl.selectSingleNode("./colors");
				Attribute flagSrc = (Attribute) coatEl.selectSingleNode("./flag/@src");
				
				if (colorsEl != null && flagSrc != null) {
					String primary = colorsEl.attributeValue("primary");
					String secondary = colorsEl.attributeValue("secondary");
					String pattern = colorsEl.attributeValue("pattern");
					
					if (primary != null && pattern != null || secondary != null) {
						int index = this.coats.size();
						Coat coat = new Coat(index, primary, secondary, pattern, flagSrc.getValue());
						this.coats.add(coat);
					}
					else {
						throw new Exception(path + " malformed.");
					}
				}
				else {
					throw new Exception(path + " malformed.");
				}
					
			}
		}
	}
	
	public Coats newCoatsInstance() {
		return new Coats(this);
	}
}
