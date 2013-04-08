package cek.ruins.bookofnames.grammar;

import java.util.List;

public interface Evaluator {
	public String evaluate() throws Exception;

	public void addPostModifier(String modifier);
	public List<String> postModifier();
	
	public void setPreModifiers(List<String> preModifiers);
	public void addPreModifier(String modifier);
	public List<String> preModifier();
}
