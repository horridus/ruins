grammar SentencesGenerator;

options {
  language = Java;
}

tokens {
  ASSIGNMENT = '::=';
  SELECTION_SEPARATOR = '|';
}

@header {
  package cek.ruins.bookofnames.grammar;

  import cek.ruins.bookofnames.BookOfNames;
  import java.util.Random;
  import java.util.LinkedList;
}

@lexer::header {
  package cek.ruins.bookofnames.grammar;
}

@members {

}

PREMODIFIER :
          '^' | '@'
      ;

POSTMODIFIER :
          '§'
      ;

COMMANDS :
      '#' ('name' | 'grammar' | 'random' | 'todo')
      ;

LINEFEED : '\\n';

EMPTY : '_';

WORD :
      (('a'..'z'|'0'..'9'|'-')|('\u0080'..'\ufffe'))('a'..'z'|'A'..'Z'|'0'..'9'|'\u0080'..'\ufffe'|'\''|'-')*
      ;
WS :
      ((' '|'\r'|'\t'|'\u000C'|'\n')) { $channel = HIDDEN;}
      ;
RULEID :
      ('A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'\u0080'..'\ufffe')*
      ;

COMMENT :
      ('//' ~( '\r' | '\n' )*
      | '/*' .* '*/')
      { $channel = HIDDEN;}
      ;

STRING :
      ('"' .* '"')+
      ;

atom [Grammar grammar] returns [Evaluator atom]:
      { List<String> preModifiers = new LinkedList<String>(); }
      (
        EMPTY { $atom = new EmptyAtom(); }
      |
        LINEFEED { $atom = new LineFeedAtom(); }
      |
        (mod=PREMODIFIER { preModifiers.add($mod.getText()); })*
        WORD { $atom = new WordAtom($WORD.getText()); $atom.setPreModifiers(preModifiers); }
        (pmod=POSTMODIFIER { $atom.addPostModifier($pmod.getText()); })*

      |
        (mod=PREMODIFIER { preModifiers.add($mod.getText()); })*
        RULEID { $atom = new RuleIdAtom($grammar, $RULEID.getText()); $atom.setPreModifiers(preModifiers); }
        (pmod=POSTMODIFIER { $atom.addPostModifier($pmod.getText()); })*
      |
        (mod=PREMODIFIER { preModifiers.add($mod.getText()); })*
        STRING { $atom = new StringAtom($STRING.getText()); $atom.setPreModifiers(preModifiers); }
        (pmod=POSTMODIFIER { $atom.addPostModifier($pmod.getText()); })*
      )
      ;

selection [Grammar grammar] returns [Evaluator selection]:
      { $selection = new Selection($grammar);}
      ( (mod=PREMODIFIER { $selection.addPreModifier($mod.getText()); })*
      '('
      arg1=phrase[$grammar] { ((Selection)$selection).addChoice($arg1.phrase); }
      (SELECTION_SEPARATOR (arg2=phrase[$grammar]{ ((Selection)$selection).addChoice($arg2.phrase); }) )*
      ')' )
      (pmod=POSTMODIFIER { $selection.addPostModifier($pmod.getText()); })*
      ;

command [Grammar grammar] returns [Evaluator command]:
      { $command = new Command($grammar);}
      (mod=PREMODIFIER { $command.addPreModifier($mod.getText()); })*
      COMMANDS { ((Command)$command).setCommand($COMMANDS.getText()); }
      '('
        (arg1=phrase[$grammar] { ((Command)$command).addArg((Phrase)$arg1.phrase); })?
        (',' arg2=phrase[$grammar] { ((Command)$command).addArg((Phrase)$arg2.phrase); } )*
      ')'
      (pmod=POSTMODIFIER { $command.addPostModifier($pmod.getText()); })*
       ;

variable [Grammar grammar] returns [Evaluator variable]:
      { $variable = new Variable($grammar); }
      (mod=PREMODIFIER { $variable.addPreModifier($mod.getText()); })*
      '$('
        (varname=phrase[$grammar] { ((Variable)$variable).setVariable((Phrase)$varname.phrase); })?
      ')'
      (pmod=POSTMODIFIER { $variable.addPostModifier($pmod.getText()); })*
       ;

condition [Grammar grammar] returns [Evaluator condition]:
       { $condition = new Condition($grammar); }
       (mod=PREMODIFIER { $condition.addPreModifier($mod.getText()); })*
       '?('
       arg1=phrase[$grammar] '==' arg2=phrase[$grammar] { ((Condition)$condition).addLeftArg((Phrase)$arg1.phrase); ((Condition)$condition).addRightArg((Phrase)$arg2.phrase); }
       ')'
       ':' '(' thenBranch=phrase[$grammar] ')' { ((Condition)$condition).addThenBranch((Phrase)$thenBranch.phrase); }
       ( ':' '(' elseBranch=phrase[$grammar] ')')? { ((Condition)$condition).addElseBranch((Phrase)$elseBranch.phrase); }
       (pmod=POSTMODIFIER { $condition.addPostModifier($pmod.getText()); })*
       ;

phrase [Grammar grammar] returns [Evaluator phrase]:
      { $phrase = new Phrase(); }
      (
        condition[$grammar] { ((Phrase)$phrase).addWord($condition.condition); }
      | variable[$grammar] { ((Phrase)$phrase).addWord($variable.variable); }
      | command[$grammar] { ((Phrase)$phrase).addWord($command.command); }
      | atom[$grammar] { ((Phrase)$phrase).addWord($atom.atom); }
      | selection[$grammar] { ((Phrase)$phrase).addWord($selection.selection); } )+
      ;

rule [Grammar grammar] returns [Evaluator rule]:
      (RULEID ASSIGNMENT phrase[$grammar] ';')
      { $rule = new Rule($RULEID.getText(), $phrase.phrase); }
      ;

rules [Grammar grammar]:
      (rule[grammar] {
        if ($rule.rule != null)
          $grammar.registerRule((Rule)$rule.rule);
      })*
      ;

grammardef [BookOfNames book, Random rng] returns [Grammar grammar]:
      { $grammar = new Grammar($book, $rng); }
      'I' ASSIGNMENT (WORD { $grammar.name = $WORD.getText(); } | RULEID { $grammar.name = $RULEID.getText(); } ) ';'
      'S' ASSIGNMENT phrase[$grammar] { $grammar.generator = $phrase.phrase; } ';'
      rules[$grammar]
      ;