package cek.ruins.world.locations.settlements;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.dom4j.Element;
import org.mozilla.javascript.Script;

import cek.ruins.ScriptExecutor;
import cek.ruins.XmlDocument;

public class SettlementsArchitect {

	protected Map<String, List<SettlementDistrict>> availableDistricts;

	public SettlementsArchitect() {
		this.availableDistricts = new HashMap<String, List<SettlementDistrict>>();
	}
	
	@SuppressWarnings("unchecked")
	public void loadData(String path) throws Exception {
		File templateFile = new File(path);

		if (templateFile.exists() && templateFile.isFile()) {
			XmlDocument settlementDistrictsTemplate = new XmlDocument(FileUtils.readFileToString(templateFile, "UTF-8"));
			
			Iterator<Element> districtsIt = (Iterator<Element>) settlementDistrictsTemplate.selectNodes("/districts/district").iterator();
			while (districtsIt.hasNext()) {
				Element block = districtsIt.next();
				
				String category = block.attributeValue("category");
				String id = block.attributeValue("id");
				
				if (category == null || id == null) {
					throw new Exception(path + " malformed: category attribute and id are mandatory.");
				}
				else {
					SettlementDistrict district = new SettlementDistrict(category, id);
					
					Iterator<Element> effectsIt = block.selectNodes("./oncreation/effect").iterator();
					while (effectsIt.hasNext()) {
						Element effect = effectsIt.next();
						Script onCreation = ScriptExecutor.executor().compileScript(effect.getText(), "SettlementsArchitect");
						district.onCreationEffects.add(onCreation);
					}
					
					effectsIt = block.selectNodes("./ondestruction/effect").iterator();
					while (effectsIt.hasNext()) {
						Element effect = effectsIt.next();
						Script onDestruction = ScriptExecutor.executor().compileScript(effect.getText(), "SettlementsArchitect");
						district.onDestructionEffects.add(onDestruction);
					}
					
					 List<SettlementDistrict> districts = this.availableDistricts.get(category);
					 if (districts == null) {
						 districts = new LinkedList<SettlementDistrict>();
						 this.availableDistricts.put(category, districts);
					 }
					 
					 districts.add(district);
				}
			}
			
		}
	}
	
	public Architect newArchitectInstance() {
		return new Architect(this);
	}
}
