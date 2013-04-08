package cek.ruins.world.civilizations;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import cek.ruins.bookofnames.BookOfNames;
import cek.ruins.map.Map;
import cek.ruins.map.PoliticalRegion;
import cek.ruins.map.Region;

public class Civilizations {
	private CivilizationsTemplates civilizationsTemplates;
	private Coats coats;
	private java.util.Map<UUID, Civilization> existingCivilizations;
	
	public Civilizations(CivilizationsTemplates templates, Coats coats) {
		this.civilizationsTemplates = templates;
		this.coats = coats;
		this.existingCivilizations = new HashMap<UUID, Civilization>();
	}
	
	public Civilization createCivilization(int templateNum, Map map, int regionIndex, BookOfNames bookOfNames, Random generator) throws Exception {
		Region region = map.regions().get(regionIndex);
		PoliticalRegion politicalRegion = map.politicalRegions().get(regionIndex);

		CivilizationTemplate template = this.civilizationsTemplates.templates().get(templateNum);

		//if index is in range and region isn't already owned by some other civ
		if (region != null && politicalRegion.ownerCivilization() == null && template != null) {
			Civilization civilization = new Civilization(template);
			civilization.setName(bookOfNames.generate(Civilization.NAMES_GRAMMAR)); //TODO la grammatica deve arrivare da conf dei singoli tamplate
			civilization.setCoat(this.coats.getCoat(generator));

			//Now, the new civilization is the owner of this region
			politicalRegion.setOwnerCivilization(civilization.id());
			
			this.existingCivilizations.put(civilization.id(), civilization);

			return civilization;
		}

		return null;
	}
	
	public void destroyCivilization(Civilization civilization) {
		this.existingCivilizations.remove(civilization.id());
		this.coats.releaseCoat(civilization.coat().index());
	}
	
	public Civilization civilization(UUID id) {
		return this.existingCivilizations.get(id);
	}
}
