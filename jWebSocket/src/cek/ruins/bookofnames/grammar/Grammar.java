package cek.ruins.bookofnames.grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cek.ruins.bookofnames.BookOfNames;

public class Grammar {
	BookOfNames bookOfNames;
	Random random;
	String name;
	Evaluator generator;
	Map<String, Rule> rules;
	Map<String, String> variables;

	public Grammar(BookOfNames bookOfNames, Random rng) {
		this.bookOfNames = bookOfNames;
		this.random = rng;
		this.rules = new HashMap<String, Rule>();
		this.variables = null;
	}

	public void setRng(Random rng) {
		this.random = rng;
	}

	public void registerRule(Rule rule) {
		this.rules.put(rule.ruleId(), rule);
	}

	public String generate() throws Exception {
		this.variables = new HashMap<String, String>();
		String result = this.generator.evaluate();
		this.variables = null;

		return result;
	}

	public String generate(Map<String, String> variables) throws Exception {
		this.variables = variables;
		String result = this.generator.evaluate();
		this.variables = null;

		return result;
	}

	public String name() {
		return this.name;
	}

}
