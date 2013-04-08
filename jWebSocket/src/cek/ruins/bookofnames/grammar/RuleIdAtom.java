package cek.ruins.bookofnames.grammar;

import java.util.LinkedList;
import java.util.List;

public class RuleIdAtom implements Evaluator {
	Grammar grammar;
	String ruleId;
	List<String> preModifiers;
	List<String> postModifiers;

	public RuleIdAtom(Grammar grammar, String ruleId) {
		this.grammar = grammar;
		this.ruleId = ruleId;
		this.preModifiers = new LinkedList<String>();
		this.postModifiers = new LinkedList<String>();
	}

	@Override
	public String evaluate() throws Exception {
		Rule rule = this.grammar.rules.get(this.ruleId);

		if (rule != null)
			return rule.evaluate();
		else
			return this.ruleId;
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
