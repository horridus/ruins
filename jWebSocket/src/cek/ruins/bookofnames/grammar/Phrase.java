package cek.ruins.bookofnames.grammar;

import java.util.ArrayList;
import java.util.List;

public class Phrase implements Evaluator {
	private List<Evaluator> words;

	public Phrase() {
		this.words = new ArrayList<Evaluator>();
	}

	public void addWord(Evaluator evaluator) {
		this.words.add(evaluator);
	}

	@Override
	public String evaluate() throws Exception {
		StringBuilder result = new StringBuilder();

		int phraseSize = this.words.size();
		for (int i = 0; i < phraseSize; i++) {
			Evaluator word = this.words.get(i);
			String evaluatedWord = word.evaluate();
			
			//premodifiers
			if (word.preModifier() != null && word.preModifier().contains("@")) {
				evaluatedWord = Character.toUpperCase(evaluatedWord.charAt(0)) + evaluatedWord.substring(1);
			}
			
			result.append(evaluatedWord);
			
			if (i < phraseSize - 1) {
				if (this.words.get(i + 1).preModifier() != null && !this.words.get(i + 1).preModifier().contains("^")) {
					result.append(' ');
				}
			}
		}
		
		return result.toString().trim();
	}

	@Override
	public void addPostModifier(String modifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> postModifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPreModifiers(List<String> preModifiers) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addPreModifier(String modifier) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> preModifier() {
		// TODO Auto-generated method stub
		return null;
	}

}
