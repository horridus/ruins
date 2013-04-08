package cek.ruins.bookofnames.grammar;

import java.util.List;

public class Rule implements Evaluator {
	private String ruleId;
	private Evaluator phrase;

	public Rule(String ruleId, Evaluator phrase) {
		this.ruleId = ruleId;
		this.phrase = phrase;
	}

	public String ruleId() {
		return this.ruleId;
	}

	@Override
	public String evaluate() throws Exception {
		return this.phrase.evaluate();
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
