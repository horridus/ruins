package cek.ruins.bookofnames.grammar;

import java.util.LinkedList;
import java.util.List;

public class Variable implements Evaluator {
	Grammar grammmar;
	Phrase variable;
	List<String> preModifiers;
	List<String> postModifiers;

	public Variable(Grammar grammar) {
		this.grammmar = grammar;
		this.variable = new Phrase();
		this.preModifiers = new LinkedList<String>();
		this.postModifiers = new LinkedList<String>();
	}

	public Phrase variable() throws Exception {
		return this.variable;
	}

	public void setVariable(Phrase variable) {
		this.variable = variable;
	}

	@Override
	public String evaluate() throws Exception {
		String variableName = this.variable().evaluate();

		if (this.grammmar.variables.containsKey(variableName))
			return this.grammmar.variables.get(variableName);
		else
			return variableName;
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

	@Override
	public void addPostModifier(String modifier) {
		this.postModifiers.add(modifier);
	}

	@Override
	public List<String> postModifier() {
		return this.postModifiers;
	}
}
