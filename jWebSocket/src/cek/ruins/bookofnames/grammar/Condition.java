package cek.ruins.bookofnames.grammar;

import java.util.LinkedList;
import java.util.List;

public class Condition implements Evaluator {
	Grammar grammar;
	Phrase lArg;
	Phrase rArg;
	Phrase thenPhrase;
	Phrase elsePhrase;

	List<Phrase> args;
	List<String> preModifiers;
	private List<String> postModifiers;

	public Condition(Grammar grammar) {
		this.grammar = grammar;
		this.lArg = null;
		this.rArg = null;
		this.thenPhrase = null;
		this.elsePhrase = null;
		this.preModifiers = new LinkedList<String>();
		this.postModifiers = new LinkedList<String>();
	}

	public void addLeftArg(Phrase arg) {
		this.lArg = arg;
	}

	public void addRightArg(Phrase arg) {
		this.rArg = arg;
	}

	public void addThenBranch(Phrase branch) {
		this.thenPhrase = branch;
	}

	public void addElseBranch(Phrase branch) {
		this.elsePhrase = branch;
	}

	@Override
	public String evaluate() throws Exception {
		String result = "";

		String leftPhrase = this.lArg.evaluate();
		String rightPhrase = this.rArg.evaluate();

		if (leftPhrase.equals(rightPhrase))
			result = this.thenPhrase.evaluate();
		else if (this.elsePhrase != null)
			result = this.elsePhrase.evaluate();

		return result;
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
