package cek.ruins.bookofnames.grammar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Command implements Evaluator {
	Grammar grammar;
	String command;

	List<Phrase> args;
	List<String> preModifiers;
	private List<String> postModifiers;
	
	public Command(Grammar grammar) {
		this.grammar = grammar;
		this.command = "";
		this.args = new ArrayList<Phrase>();
		this.preModifiers = new LinkedList<String>();
		this.postModifiers = new LinkedList<String>();
	}
	
	public String command() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	public void addArg(Phrase arg) {
		this.args.add(arg);
	}
	
	@Override
	public String evaluate() throws Exception {
		String result = "??" + this.command + "??";
		
		if (this.command.equals("#name")) {
			if (this.args.size() < 2)
				throw new Exception(errorArgsNum("#name", 2));
			
			String generatorId = this.args.get(0).evaluate();
			int syllblesNum = Integer.parseInt(this.args.get(1).evaluate());
			
			result = this.grammar.bookOfNames.generateName(generatorId, syllblesNum);
		}
		else if (this.command.equals("#grammar")) {
			if (this.args.size() < 1)
				throw new Exception(errorArgsNum("#grammar", 1));
			
			String grammarName = this.args.get(0).evaluate();
			
			result = grammar.bookOfNames.generate(grammarName);
		}
		else if (this.command.equals("#random")) {
			if (this.args.size() < 1)
				throw new Exception(errorArgsNum("#random", 1));
			
			int min = Integer.parseInt(this.args.get(0).evaluate());
			
			if (this.args.size() >= 2) {
				int max = Integer.parseInt(this.args.get(1).evaluate());
				result = grammar.bookOfNames.random(min, max).toString();
			}
			else
				result = grammar.bookOfNames.random(0, min).toString();
		}
		else if (this.command.equals("#todo")) {
			result = "fortytwo";
		}
		
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
	
	protected String errorArgsNum(String info, int expected) {
		return info + ": not enought args, expecting " + expected + " got " + this.args.size();
	}

}
