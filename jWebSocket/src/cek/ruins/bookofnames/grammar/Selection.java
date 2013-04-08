package cek.ruins.bookofnames.grammar;

import java.util.LinkedList;
import java.util.List;

public class Selection implements Evaluator {
	private Grammar grammar;
	private List<Evaluator> choices;
	List<String> preModifiers;
	private List<String> postModifiers;

	public Selection(Grammar grammar) {
		this.grammar = grammar;
		this.choices = new LinkedList<Evaluator>();
		this.preModifiers = new LinkedList<String>();
		this.postModifiers = new LinkedList<String>();
	}

	public void addChoice(Evaluator evaluator) {
		this.choices.add(evaluator);
	}

	@Override
	public String evaluate() throws Exception {
		int choice = this.grammar.random.nextInt(this.choices.size());
		return this.choices.get(choice).evaluate();
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
