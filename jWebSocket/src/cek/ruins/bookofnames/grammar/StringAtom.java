package cek.ruins.bookofnames.grammar;

import java.util.LinkedList;
import java.util.List;

public class StringAtom implements Evaluator {
	String word;
	List<String> preModifiers;
	List<String> postModifiers;

	public StringAtom(String word) {
		this.word = word.substring(1, word.length() - 1);
		
		this.word = this.word.replace("\\n", "\n"); //FIXME
		
		this.preModifiers = new LinkedList<String>();
		this.postModifiers = new LinkedList<String>();
	}

	@Override
	public String evaluate() {

		return this.word;
	}

	@Override
	public void addPostModifier(String modifier) {
		this.postModifiers.add(modifier);
	}

	@Override
	public List<String> postModifier() {
		return this.postModifiers;
	}

	@Override
	public void setPreModifiers(List<String> preModifiers) {
		this.preModifiers = preModifiers;
	}
	
	@Override
	public void addPreModifier(String modifier) {
		this.preModifiers.add(modifier);
	}

	@Override
	public List<String> preModifier() {
		return this.preModifiers;
	}
}
