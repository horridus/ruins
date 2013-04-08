package cek.ruins.world.civilizations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Coats {
	private List<Integer> unusedCoats;
	private CivilizationsHerald herald;
	
	public Coats(CivilizationsHerald herald) {
		this.unusedCoats = new ArrayList<Integer>();
		this.herald = herald;
		
		Iterator<Coat> coatsIt = this.herald.coats.iterator();
		while (coatsIt.hasNext()) {
			this.unusedCoats.add(coatsIt.next().index());
		}
	}
	
	public boolean isUnusedCoatsPresent() {
		return !this.unusedCoats.isEmpty();
	}
	
	public Coat getCoat(Random generator) {
		if (this.isUnusedCoatsPresent()) {
			Integer index = this.unusedCoats.remove(0);
			return this.herald.coats.get(index);
		}
		else {
			//TEMP/////
			//generate a coat on the fly
			return this.randomCoat(generator);
			///////////
		}
	}
	
	public void releaseCoat(int index) {
		if (index != -1 && !this.unusedCoats.contains(index))
			this.unusedCoats.add(index);
	}
	
	private Coat randomCoat(Random generator) {
		String hexTemplate = "0123456789ABCDEF";
		
		String primary = "#";
		String secondary = "#";
		for (int i = 0; i < 6; i++) {
			primary += hexTemplate.charAt(generator.nextInt(hexTemplate.length()));
			secondary += hexTemplate.charAt(generator.nextInt(hexTemplate.length()));
		}
		
		String pattern = "" + generator.nextInt(5);
		
		return new Coat(-1, primary, secondary, pattern, "TODO");
	}
}
