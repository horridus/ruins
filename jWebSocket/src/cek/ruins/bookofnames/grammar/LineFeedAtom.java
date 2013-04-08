package cek.ruins.bookofnames.grammar;

import java.util.LinkedList;
import java.util.List;

public class LineFeedAtom implements Evaluator {
	private List<String> preModifiers;
	private List<String> postModifiers;

	public LineFeedAtom() {
		this.preModifiers = new LinkedList<String>();
		this.postModifiers = new LinkedList<String>();
	}

	@Override
	public String evaluate() throws Exception {
		return "\n";
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
