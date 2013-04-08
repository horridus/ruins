package cek.ruins.world.locations.settlements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.mozilla.javascript.Script;

import cek.ruins.ScriptExecutor;
import cek.ruins.world.civilizations.Civilization;
import cek.ruins.world.locations.Settlement;

public class Architect {

	private SettlementsArchitect settlementsArchitect;

	public Architect(SettlementsArchitect settlementsArchitect) {
		this.settlementsArchitect = settlementsArchitect;
	}
	
	public Settlement initSettlement(Settlement settlement, Civilization ownerCivilization, Random generator) {
		//TEMP///
		List<SettlementDistrict> startingDistricts = this.settlementsArchitect.availableDistricts.get("start");
		int selected = generator.nextInt(startingDistricts.size());
		SettlementDistrict startingDistrict = startingDistricts.get(selected);
		
		//add district to settlement
		settlement.setDistrict(Settlement.MAX_DISTRICTS_MAP_SIZE/2, Settlement.MAX_DISTRICTS_MAP_SIZE/2, startingDistrict);
		
		ScriptExecutor executor = ScriptExecutor.executor();
		Map<String, Object> objs = new HashMap<String, Object>();
		objs.put("settlement", settlement);
		objs.put("ownerCivilization", ownerCivilization);
		
		for (Script script : startingDistrict.onCreationEffects) {
			executor.executeScript(script, objs);
		}
		
		///////
		return settlement;
	}

	public float growSettlement(Settlement settlement, Civilization ownerCivilization, float environmentalResistance) {
		//TODO finire logica di crescita
		//settlements growth logic
		float currentPopulation = settlement.population();
		float growthRate = (float) (settlement.growthRate() / 100.0);

		//add settlement's support to compute how many population is really supported in this settlement.
		float totalPopulationSupported = environmentalResistance + settlement.support();
		
		currentPopulation = currentPopulation + currentPopulation * growthRate * (1.0f - currentPopulation/totalPopulationSupported);
		settlement.setPopulation(currentPopulation);
		
		return currentPopulation;
	}
	
	public boolean isMigrationNeeded(Settlement settlement, Civilization ownerCivilization, float environmentalResistance) {
		//TEMP////////////
				/*
				float ninetyPerCentER = environmentalResistance * 0.9f;
				if (settlement.population() > ninetyPerCentER) {
					return true;  //TODO should be stochastic
				}
				*/
				float overcrowdingThreshold = 1.01f;
				
				
				//if settlement is level 0, no migration happens.
				if (settlement.level() == 0)
					return false;
					
				//add settlement's support to compute how many population is really supported in this settlement.
				float totalPopulationSupported = environmentalResistance + settlement.support();
				
				if ((totalPopulationSupported / settlement.population()) < overcrowdingThreshold)
					return true;
				else
					return false;
				//////////////////
	}
	
	public boolean updateSettlementLevel(Settlement settlement, Civilization ownerCivilization) throws Exception {
		boolean hasChanged = false;
		
		//check if settlement category changes
		if (settlement.population() < 50 && settlement.level() != 0) {
			//camp
			settlement.setLevel(0);
			hasChanged = true;
		}
		else if (settlement.population() > 50 && settlement.population() < 250 && settlement.level() != 1) {
			//small hamlet
			settlement.setLevel(1);
			hasChanged = true;
		}
		
		return hasChanged;
	}
}
