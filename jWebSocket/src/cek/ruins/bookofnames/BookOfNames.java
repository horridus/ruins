package cek.ruins.bookofnames;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.FileUtils;

import cek.ruins.bookofnames.grammar.Grammar;
import cek.ruins.bookofnames.grammar.SentencesGeneratorLexer;
import cek.ruins.bookofnames.grammar.SentencesGeneratorParser;
import ee.joonas.NameGenerator;

public class BookOfNames {
	private Map<String, Grammar> grammars;
	private Map<String, NameGenerator> namesGenerators;
	private Random rng;

	public static void main(String[] args) throws Exception {
		BookOfNames bookOfNames = new BookOfNames(new Random());
		try {
			bookOfNames.loadData("/home/cek/workspace/jWebSocket/data/grammars/");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (RecognitionException e) {
			e.printStackTrace();
		}

		Map<String, String> variables = new HashMap<String, String>();
		//variables.put("race", "goblin");
		System.out.println(bookOfNames.generate("civilization", variables));
	}

	public BookOfNames() {
		this.grammars = new HashMap<String, Grammar>();
		this.namesGenerators = new HashMap<String, NameGenerator>();
		this.rng = new Random();
	}

	public BookOfNames(Random rng) {
		this.grammars = new HashMap<String, Grammar>();
		this.namesGenerators = new HashMap<String, NameGenerator>();
		this.rng = rng;
	}

	public void loadData(String path) throws Exception {
		File grammarsDir = new File(path);

		if (grammarsDir.isDirectory() && grammarsDir.canRead()) {
			Iterator<File> filesIt = FileUtils.iterateFiles(grammarsDir, new String[]{"grm","syl"}, true);
			while (filesIt.hasNext()) {
				File data = filesIt.next();

				if (data.getName().endsWith("grm")) {
					addGrammar(data.getAbsolutePath());
				}
				else if (data.getName().endsWith("syl")) {
					addNamesGenerator(data.getAbsolutePath());
				}
			}
		}
		else {
			throw new Exception("Grammars directory " + path + " does not exist or is unreadable");
		}
	}

	public void setRng(Random rng) {
		this.rng = rng;
	}

	public void addGrammar(String grammarPath) throws IOException, RecognitionException {
		SentencesGeneratorLexer lex = new SentencesGeneratorLexer(new ANTLRFileStream(grammarPath, "UTF-8"));
        CommonTokenStream tokens = new CommonTokenStream(lex);
        SentencesGeneratorParser parser = new SentencesGeneratorParser(tokens);

        Grammar grammar = parser.grammardef(this, this.rng);
        this.grammars.put(grammar.name(), grammar);
	}

	public void addNamesGenerator(String syllablesSetPath) throws IOException {
		NameGenerator generator = new NameGenerator(syllablesSetPath, rng);

		this.namesGenerators.put(generator.id(), generator);
	}

	public String generate(String grammarName) throws Exception {
		Grammar grammar = this.grammars.get(grammarName);
		if (grammar != null)
			return grammar.generate();
		else
			throw new Exception("Grammar " + grammarName + " not found.");
	}

	public String generate(String grammarName, Map<String, String> variables) throws Exception {
		Grammar grammar = this.grammars.get(grammarName);
		if (grammar != null)
			return grammar.generate(variables);
		else
			throw new Exception("Grammar " + grammarName + " not found.");
	}

	public String generateName(String syllablesSetName, int sylsNum) throws Exception {
		NameGenerator generator = this.namesGenerators.get(syllablesSetName);
		if (generator != null)
			return generator.compose(sylsNum);
		else
			throw new Exception("Name generator " + syllablesSetName + " not found.");
	}

	public Integer random(int min, int max) {
		return this.rng.nextInt(max -min) + min;
	}
}
