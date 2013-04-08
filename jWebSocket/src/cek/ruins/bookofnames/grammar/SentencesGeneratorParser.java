// $ANTLR 3.4 /home/cek/workspace/jWebSocket/SentencesGenerator.g 2013-04-08 22:29:57

  package cek.ruins.bookofnames.grammar;

  import cek.ruins.bookofnames.BookOfNames;
  import java.util.Random;
  import java.util.LinkedList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class SentencesGeneratorParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ASSIGNMENT", "COMMANDS", "COMMENT", "EMPTY", "LINEFEED", "POSTMODIFIER", "PREMODIFIER", "RULEID", "SELECTION_SEPARATOR", "STRING", "WORD", "WS", "'$('", "'('", "')'", "','", "':'", "';'", "'=='", "'?('", "'I'", "'S'"
    };

    public static final int EOF=-1;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__19=19;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int ASSIGNMENT=4;
    public static final int COMMANDS=5;
    public static final int COMMENT=6;
    public static final int EMPTY=7;
    public static final int LINEFEED=8;
    public static final int POSTMODIFIER=9;
    public static final int PREMODIFIER=10;
    public static final int RULEID=11;
    public static final int SELECTION_SEPARATOR=12;
    public static final int STRING=13;
    public static final int WORD=14;
    public static final int WS=15;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public SentencesGeneratorParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public SentencesGeneratorParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return SentencesGeneratorParser.tokenNames; }
    public String getGrammarFileName() { return "/home/cek/workspace/jWebSocket/SentencesGenerator.g"; }






    // $ANTLR start "atom"
    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:64:1: atom[Grammar grammar] returns [Evaluator atom] : ( EMPTY | LINEFEED | (mod= PREMODIFIER )* WORD (pmod= POSTMODIFIER )* | (mod= PREMODIFIER )* RULEID (pmod= POSTMODIFIER )* | (mod= PREMODIFIER )* STRING (pmod= POSTMODIFIER )* ) ;
    public final Evaluator atom(Grammar grammar) throws RecognitionException {
        Evaluator atom = null;


        Token mod=null;
        Token pmod=null;
        Token WORD1=null;
        Token RULEID2=null;
        Token STRING3=null;

        try {
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:64:48: ( ( EMPTY | LINEFEED | (mod= PREMODIFIER )* WORD (pmod= POSTMODIFIER )* | (mod= PREMODIFIER )* RULEID (pmod= POSTMODIFIER )* | (mod= PREMODIFIER )* STRING (pmod= POSTMODIFIER )* ) )
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:65:7: ( EMPTY | LINEFEED | (mod= PREMODIFIER )* WORD (pmod= POSTMODIFIER )* | (mod= PREMODIFIER )* RULEID (pmod= POSTMODIFIER )* | (mod= PREMODIFIER )* STRING (pmod= POSTMODIFIER )* )
            {
             List<String> preModifiers = new LinkedList<String>(); 

            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:66:7: ( EMPTY | LINEFEED | (mod= PREMODIFIER )* WORD (pmod= POSTMODIFIER )* | (mod= PREMODIFIER )* RULEID (pmod= POSTMODIFIER )* | (mod= PREMODIFIER )* STRING (pmod= POSTMODIFIER )* )
            int alt7=5;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:67:9: EMPTY
                    {
                    match(input,EMPTY,FOLLOW_EMPTY_in_atom428); 

                     atom = new EmptyAtom(); 

                    }
                    break;
                case 2 :
                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:69:9: LINEFEED
                    {
                    match(input,LINEFEED,FOLLOW_LINEFEED_in_atom448); 

                     atom = new LineFeedAtom(); 

                    }
                    break;
                case 3 :
                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:71:9: (mod= PREMODIFIER )* WORD (pmod= POSTMODIFIER )*
                    {
                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:71:9: (mod= PREMODIFIER )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( (LA1_0==PREMODIFIER) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:71:10: mod= PREMODIFIER
                    	    {
                    	    mod=(Token)match(input,PREMODIFIER,FOLLOW_PREMODIFIER_in_atom471); 

                    	     preModifiers.add(mod.getText()); 

                    	    }
                    	    break;

                    	default :
                    	    break loop1;
                        }
                    } while (true);


                    WORD1=(Token)match(input,WORD,FOLLOW_WORD_in_atom485); 

                     atom = new WordAtom(WORD1.getText()); atom.setPreModifiers(preModifiers); 

                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:73:9: (pmod= POSTMODIFIER )*
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( (LA2_0==POSTMODIFIER) ) {
                            alt2=1;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:73:10: pmod= POSTMODIFIER
                    	    {
                    	    pmod=(Token)match(input,POSTMODIFIER,FOLLOW_POSTMODIFIER_in_atom500); 

                    	     atom.addPostModifier(pmod.getText()); 

                    	    }
                    	    break;

                    	default :
                    	    break loop2;
                        }
                    } while (true);


                    }
                    break;
                case 4 :
                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:76:9: (mod= PREMODIFIER )* RULEID (pmod= POSTMODIFIER )*
                    {
                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:76:9: (mod= PREMODIFIER )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==PREMODIFIER) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:76:10: mod= PREMODIFIER
                    	    {
                    	    mod=(Token)match(input,PREMODIFIER,FOLLOW_PREMODIFIER_in_atom526); 

                    	     preModifiers.add(mod.getText()); 

                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);


                    RULEID2=(Token)match(input,RULEID,FOLLOW_RULEID_in_atom540); 

                     atom = new RuleIdAtom(grammar, RULEID2.getText()); atom.setPreModifiers(preModifiers); 

                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:78:9: (pmod= POSTMODIFIER )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0==POSTMODIFIER) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:78:10: pmod= POSTMODIFIER
                    	    {
                    	    pmod=(Token)match(input,POSTMODIFIER,FOLLOW_POSTMODIFIER_in_atom555); 

                    	     atom.addPostModifier(pmod.getText()); 

                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);


                    }
                    break;
                case 5 :
                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:80:9: (mod= PREMODIFIER )* STRING (pmod= POSTMODIFIER )*
                    {
                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:80:9: (mod= PREMODIFIER )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==PREMODIFIER) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:80:10: mod= PREMODIFIER
                    	    {
                    	    mod=(Token)match(input,PREMODIFIER,FOLLOW_PREMODIFIER_in_atom580); 

                    	     preModifiers.add(mod.getText()); 

                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);


                    STRING3=(Token)match(input,STRING,FOLLOW_STRING_in_atom594); 

                     atom = new StringAtom(STRING3.getText()); atom.setPreModifiers(preModifiers); 

                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:82:9: (pmod= POSTMODIFIER )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==POSTMODIFIER) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:82:10: pmod= POSTMODIFIER
                    	    {
                    	    pmod=(Token)match(input,POSTMODIFIER,FOLLOW_POSTMODIFIER_in_atom609); 

                    	     atom.addPostModifier(pmod.getText()); 

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return atom;
    }
    // $ANTLR end "atom"



    // $ANTLR start "selection"
    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:86:1: selection[Grammar grammar] returns [Evaluator selection] : ( (mod= PREMODIFIER )* '(' arg1= phrase[$grammar] ( SELECTION_SEPARATOR (arg2= phrase[$grammar] ) )* ')' ) (pmod= POSTMODIFIER )* ;
    public final Evaluator selection(Grammar grammar) throws RecognitionException {
        Evaluator selection = null;


        Token mod=null;
        Token pmod=null;
        Evaluator arg1 =null;

        Evaluator arg2 =null;


        try {
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:86:58: ( ( (mod= PREMODIFIER )* '(' arg1= phrase[$grammar] ( SELECTION_SEPARATOR (arg2= phrase[$grammar] ) )* ')' ) (pmod= POSTMODIFIER )* )
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:87:7: ( (mod= PREMODIFIER )* '(' arg1= phrase[$grammar] ( SELECTION_SEPARATOR (arg2= phrase[$grammar] ) )* ')' ) (pmod= POSTMODIFIER )*
            {
             selection = new Selection(grammar);

            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:88:7: ( (mod= PREMODIFIER )* '(' arg1= phrase[$grammar] ( SELECTION_SEPARATOR (arg2= phrase[$grammar] ) )* ')' )
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:88:9: (mod= PREMODIFIER )* '(' arg1= phrase[$grammar] ( SELECTION_SEPARATOR (arg2= phrase[$grammar] ) )* ')'
            {
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:88:9: (mod= PREMODIFIER )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==PREMODIFIER) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:88:10: mod= PREMODIFIER
            	    {
            	    mod=(Token)match(input,PREMODIFIER,FOLLOW_PREMODIFIER_in_selection660); 

            	     selection.addPreModifier(mod.getText()); 

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            match(input,17,FOLLOW_17_in_selection672); 

            pushFollow(FOLLOW_phrase_in_selection682);
            arg1=phrase(grammar);

            state._fsp--;


             ((Selection)selection).addChoice(arg1); 

            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:91:7: ( SELECTION_SEPARATOR (arg2= phrase[$grammar] ) )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==SELECTION_SEPARATOR) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:91:8: SELECTION_SEPARATOR (arg2= phrase[$grammar] )
            	    {
            	    match(input,SELECTION_SEPARATOR,FOLLOW_SELECTION_SEPARATOR_in_selection694); 

            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:91:28: (arg2= phrase[$grammar] )
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:91:29: arg2= phrase[$grammar]
            	    {
            	    pushFollow(FOLLOW_phrase_in_selection699);
            	    arg2=phrase(grammar);

            	    state._fsp--;


            	     ((Selection)selection).addChoice(arg2); 

            	    }


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            match(input,18,FOLLOW_18_in_selection713); 

            }


            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:93:7: (pmod= POSTMODIFIER )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==POSTMODIFIER) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:93:8: pmod= POSTMODIFIER
            	    {
            	    pmod=(Token)match(input,POSTMODIFIER,FOLLOW_POSTMODIFIER_in_selection726); 

            	     selection.addPostModifier(pmod.getText()); 

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return selection;
    }
    // $ANTLR end "selection"



    // $ANTLR start "command"
    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:96:1: command[Grammar grammar] returns [Evaluator command] : (mod= PREMODIFIER )* COMMANDS '(' (arg1= phrase[$grammar] )? ( ',' arg2= phrase[$grammar] )* ')' (pmod= POSTMODIFIER )* ;
    public final Evaluator command(Grammar grammar) throws RecognitionException {
        Evaluator command = null;


        Token mod=null;
        Token pmod=null;
        Token COMMANDS4=null;
        Evaluator arg1 =null;

        Evaluator arg2 =null;


        try {
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:96:54: ( (mod= PREMODIFIER )* COMMANDS '(' (arg1= phrase[$grammar] )? ( ',' arg2= phrase[$grammar] )* ')' (pmod= POSTMODIFIER )* )
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:97:7: (mod= PREMODIFIER )* COMMANDS '(' (arg1= phrase[$grammar] )? ( ',' arg2= phrase[$grammar] )* ')' (pmod= POSTMODIFIER )*
            {
             command = new Command(grammar);

            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:98:7: (mod= PREMODIFIER )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==PREMODIFIER) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:98:8: mod= PREMODIFIER
            	    {
            	    mod=(Token)match(input,PREMODIFIER,FOLLOW_PREMODIFIER_in_command767); 

            	     command.addPreModifier(mod.getText()); 

            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);


            COMMANDS4=(Token)match(input,COMMANDS,FOLLOW_COMMANDS_in_command779); 

             ((Command)command).setCommand(COMMANDS4.getText()); 

            match(input,17,FOLLOW_17_in_command789); 

            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:101:9: (arg1= phrase[$grammar] )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==COMMANDS||(LA12_0 >= EMPTY && LA12_0 <= LINEFEED)||(LA12_0 >= PREMODIFIER && LA12_0 <= RULEID)||(LA12_0 >= STRING && LA12_0 <= WORD)||(LA12_0 >= 16 && LA12_0 <= 17)||LA12_0==23) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:101:10: arg1= phrase[$grammar]
                    {
                    pushFollow(FOLLOW_phrase_in_command802);
                    arg1=phrase(grammar);

                    state._fsp--;


                     ((Command)command).addArg((Phrase)arg1); 

                    }
                    break;

            }


            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:102:9: ( ',' arg2= phrase[$grammar] )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==19) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:102:10: ',' arg2= phrase[$grammar]
            	    {
            	    match(input,19,FOLLOW_19_in_command818); 

            	    pushFollow(FOLLOW_phrase_in_command822);
            	    arg2=phrase(grammar);

            	    state._fsp--;


            	     ((Command)command).addArg((Phrase)arg2); 

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            match(input,18,FOLLOW_18_in_command836); 

            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:104:7: (pmod= POSTMODIFIER )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==POSTMODIFIER) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:104:8: pmod= POSTMODIFIER
            	    {
            	    pmod=(Token)match(input,POSTMODIFIER,FOLLOW_POSTMODIFIER_in_command847); 

            	     command.addPostModifier(pmod.getText()); 

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return command;
    }
    // $ANTLR end "command"



    // $ANTLR start "variable"
    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:107:1: variable[Grammar grammar] returns [Evaluator variable] : (mod= PREMODIFIER )* '$(' (varname= phrase[$grammar] )? ')' (pmod= POSTMODIFIER )* ;
    public final Evaluator variable(Grammar grammar) throws RecognitionException {
        Evaluator variable = null;


        Token mod=null;
        Token pmod=null;
        Evaluator varname =null;


        try {
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:107:56: ( (mod= PREMODIFIER )* '$(' (varname= phrase[$grammar] )? ')' (pmod= POSTMODIFIER )* )
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:108:7: (mod= PREMODIFIER )* '$(' (varname= phrase[$grammar] )? ')' (pmod= POSTMODIFIER )*
            {
             variable = new Variable(grammar); 

            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:109:7: (mod= PREMODIFIER )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==PREMODIFIER) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:109:8: mod= PREMODIFIER
            	    {
            	    mod=(Token)match(input,PREMODIFIER,FOLLOW_PREMODIFIER_in_variable889); 

            	     variable.addPreModifier(mod.getText()); 

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);


            match(input,16,FOLLOW_16_in_variable901); 

            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:111:9: (varname= phrase[$grammar] )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==COMMANDS||(LA16_0 >= EMPTY && LA16_0 <= LINEFEED)||(LA16_0 >= PREMODIFIER && LA16_0 <= RULEID)||(LA16_0 >= STRING && LA16_0 <= WORD)||(LA16_0 >= 16 && LA16_0 <= 17)||LA16_0==23) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:111:10: varname= phrase[$grammar]
                    {
                    pushFollow(FOLLOW_phrase_in_variable914);
                    varname=phrase(grammar);

                    state._fsp--;


                     ((Variable)variable).setVariable((Phrase)varname); 

                    }
                    break;

            }


            match(input,18,FOLLOW_18_in_variable927); 

            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:113:7: (pmod= POSTMODIFIER )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==POSTMODIFIER) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:113:8: pmod= POSTMODIFIER
            	    {
            	    pmod=(Token)match(input,POSTMODIFIER,FOLLOW_POSTMODIFIER_in_variable938); 

            	     variable.addPostModifier(pmod.getText()); 

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return variable;
    }
    // $ANTLR end "variable"



    // $ANTLR start "condition"
    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:116:1: condition[Grammar grammar] returns [Evaluator condition] : (mod= PREMODIFIER )* '?(' arg1= phrase[$grammar] '==' arg2= phrase[$grammar] ')' ':' '(' thenBranch= phrase[$grammar] ')' ( ':' '(' elseBranch= phrase[$grammar] ')' )? (pmod= POSTMODIFIER )* ;
    public final Evaluator condition(Grammar grammar) throws RecognitionException {
        Evaluator condition = null;


        Token mod=null;
        Token pmod=null;
        Evaluator arg1 =null;

        Evaluator arg2 =null;

        Evaluator thenBranch =null;

        Evaluator elseBranch =null;


        try {
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:116:58: ( (mod= PREMODIFIER )* '?(' arg1= phrase[$grammar] '==' arg2= phrase[$grammar] ')' ':' '(' thenBranch= phrase[$grammar] ')' ( ':' '(' elseBranch= phrase[$grammar] ')' )? (pmod= POSTMODIFIER )* )
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:117:8: (mod= PREMODIFIER )* '?(' arg1= phrase[$grammar] '==' arg2= phrase[$grammar] ')' ':' '(' thenBranch= phrase[$grammar] ')' ( ':' '(' elseBranch= phrase[$grammar] ')' )? (pmod= POSTMODIFIER )*
            {
             condition = new Condition(grammar); 

            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:118:8: (mod= PREMODIFIER )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==PREMODIFIER) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:118:9: mod= PREMODIFIER
            	    {
            	    mod=(Token)match(input,PREMODIFIER,FOLLOW_PREMODIFIER_in_condition982); 

            	     condition.addPreModifier(mod.getText()); 

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);


            match(input,23,FOLLOW_23_in_condition995); 

            pushFollow(FOLLOW_phrase_in_condition1006);
            arg1=phrase(grammar);

            state._fsp--;


            match(input,22,FOLLOW_22_in_condition1009); 

            pushFollow(FOLLOW_phrase_in_condition1013);
            arg2=phrase(grammar);

            state._fsp--;


             ((Condition)condition).addLeftArg((Phrase)arg1); ((Condition)condition).addRightArg((Phrase)arg2); 

            match(input,18,FOLLOW_18_in_condition1025); 

            match(input,20,FOLLOW_20_in_condition1034); 

            match(input,17,FOLLOW_17_in_condition1036); 

            pushFollow(FOLLOW_phrase_in_condition1040);
            thenBranch=phrase(grammar);

            state._fsp--;


            match(input,18,FOLLOW_18_in_condition1043); 

             ((Condition)condition).addThenBranch((Phrase)thenBranch); 

            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:123:8: ( ':' '(' elseBranch= phrase[$grammar] ')' )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==20) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:123:10: ':' '(' elseBranch= phrase[$grammar] ')'
                    {
                    match(input,20,FOLLOW_20_in_condition1056); 

                    match(input,17,FOLLOW_17_in_condition1058); 

                    pushFollow(FOLLOW_phrase_in_condition1062);
                    elseBranch=phrase(grammar);

                    state._fsp--;


                    match(input,18,FOLLOW_18_in_condition1065); 

                    }
                    break;

            }


             ((Condition)condition).addElseBranch((Phrase)elseBranch); 

            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:124:8: (pmod= POSTMODIFIER )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==POSTMODIFIER) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:124:9: pmod= POSTMODIFIER
            	    {
            	    pmod=(Token)match(input,POSTMODIFIER,FOLLOW_POSTMODIFIER_in_condition1081); 

            	     condition.addPostModifier(pmod.getText()); 

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return condition;
    }
    // $ANTLR end "condition"



    // $ANTLR start "phrase"
    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:127:1: phrase[Grammar grammar] returns [Evaluator phrase] : ( condition[$grammar] | variable[$grammar] | command[$grammar] | atom[$grammar] | selection[$grammar] )+ ;
    public final Evaluator phrase(Grammar grammar) throws RecognitionException {
        Evaluator phrase = null;


        Evaluator condition5 =null;

        Evaluator variable6 =null;

        Evaluator command7 =null;

        Evaluator atom8 =null;

        Evaluator selection9 =null;


        try {
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:127:52: ( ( condition[$grammar] | variable[$grammar] | command[$grammar] | atom[$grammar] | selection[$grammar] )+ )
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:128:7: ( condition[$grammar] | variable[$grammar] | command[$grammar] | atom[$grammar] | selection[$grammar] )+
            {
             phrase = new Phrase(); 

            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:129:7: ( condition[$grammar] | variable[$grammar] | command[$grammar] | atom[$grammar] | selection[$grammar] )+
            int cnt21=0;
            loop21:
            do {
                int alt21=6;
                alt21 = dfa21.predict(input);
                switch (alt21) {
            	case 1 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:130:9: condition[$grammar]
            	    {
            	    pushFollow(FOLLOW_condition_in_phrase1130);
            	    condition5=condition(grammar);

            	    state._fsp--;


            	     ((Phrase)phrase).addWord(condition5); 

            	    }
            	    break;
            	case 2 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:131:9: variable[$grammar]
            	    {
            	    pushFollow(FOLLOW_variable_in_phrase1143);
            	    variable6=variable(grammar);

            	    state._fsp--;


            	     ((Phrase)phrase).addWord(variable6); 

            	    }
            	    break;
            	case 3 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:132:9: command[$grammar]
            	    {
            	    pushFollow(FOLLOW_command_in_phrase1156);
            	    command7=command(grammar);

            	    state._fsp--;


            	     ((Phrase)phrase).addWord(command7); 

            	    }
            	    break;
            	case 4 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:133:9: atom[$grammar]
            	    {
            	    pushFollow(FOLLOW_atom_in_phrase1169);
            	    atom8=atom(grammar);

            	    state._fsp--;


            	     ((Phrase)phrase).addWord(atom8); 

            	    }
            	    break;
            	case 5 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:134:9: selection[$grammar]
            	    {
            	    pushFollow(FOLLOW_selection_in_phrase1182);
            	    selection9=selection(grammar);

            	    state._fsp--;


            	     ((Phrase)phrase).addWord(selection9); 

            	    }
            	    break;

            	default :
            	    if ( cnt21 >= 1 ) break loop21;
                        EarlyExitException eee =
                            new EarlyExitException(21, input);
                        throw eee;
                }
                cnt21++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return phrase;
    }
    // $ANTLR end "phrase"



    // $ANTLR start "rule"
    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:137:1: rule[Grammar grammar] returns [Evaluator rule] : ( RULEID ASSIGNMENT phrase[$grammar] ';' ) ;
    public final Evaluator rule(Grammar grammar) throws RecognitionException {
        Evaluator rule = null;


        Token RULEID10=null;
        Evaluator phrase11 =null;


        try {
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:137:48: ( ( RULEID ASSIGNMENT phrase[$grammar] ';' ) )
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:138:7: ( RULEID ASSIGNMENT phrase[$grammar] ';' )
            {
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:138:7: ( RULEID ASSIGNMENT phrase[$grammar] ';' )
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:138:8: RULEID ASSIGNMENT phrase[$grammar] ';'
            {
            RULEID10=(Token)match(input,RULEID,FOLLOW_RULEID_in_rule1215); 

            match(input,ASSIGNMENT,FOLLOW_ASSIGNMENT_in_rule1217); 

            pushFollow(FOLLOW_phrase_in_rule1219);
            phrase11=phrase(grammar);

            state._fsp--;


            match(input,21,FOLLOW_21_in_rule1222); 

            }


             rule = new Rule(RULEID10.getText(), phrase11); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return rule;
    }
    // $ANTLR end "rule"



    // $ANTLR start "rules"
    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:142:1: rules[Grammar grammar] : ( rule[grammar] )* ;
    public final void rules(Grammar grammar) throws RecognitionException {
        Evaluator rule12 =null;


        try {
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:142:24: ( ( rule[grammar] )* )
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:143:7: ( rule[grammar] )*
            {
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:143:7: ( rule[grammar] )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==RULEID) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:143:8: rule[grammar]
            	    {
            	    pushFollow(FOLLOW_rule_in_rules1254);
            	    rule12=rule(grammar);

            	    state._fsp--;



            	            if (rule12 != null)
            	              grammar.registerRule((Rule)rule12);
            	          

            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "rules"



    // $ANTLR start "grammardef"
    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:149:1: grammardef[BookOfNames book, Random rng] returns [Grammar grammar] : 'I' ASSIGNMENT ( WORD | RULEID ) ';' 'S' ASSIGNMENT phrase[$grammar] ';' rules[$grammar] ;
    public final Grammar grammardef(BookOfNames book, Random rng) throws RecognitionException {
        Grammar grammar = null;


        Token WORD13=null;
        Token RULEID14=null;
        Evaluator phrase15 =null;


        try {
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:149:68: ( 'I' ASSIGNMENT ( WORD | RULEID ) ';' 'S' ASSIGNMENT phrase[$grammar] ';' rules[$grammar] )
            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:150:7: 'I' ASSIGNMENT ( WORD | RULEID ) ';' 'S' ASSIGNMENT phrase[$grammar] ';' rules[$grammar]
            {
             grammar = new Grammar(book, rng); 

            match(input,24,FOLLOW_24_in_grammardef1293); 

            match(input,ASSIGNMENT,FOLLOW_ASSIGNMENT_in_grammardef1295); 

            // /home/cek/workspace/jWebSocket/SentencesGenerator.g:151:22: ( WORD | RULEID )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==WORD) ) {
                alt23=1;
            }
            else if ( (LA23_0==RULEID) ) {
                alt23=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;

            }
            switch (alt23) {
                case 1 :
                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:151:23: WORD
                    {
                    WORD13=(Token)match(input,WORD,FOLLOW_WORD_in_grammardef1298); 

                     grammar.name = WORD13.getText(); 

                    }
                    break;
                case 2 :
                    // /home/cek/workspace/jWebSocket/SentencesGenerator.g:151:67: RULEID
                    {
                    RULEID14=(Token)match(input,RULEID,FOLLOW_RULEID_in_grammardef1304); 

                     grammar.name = RULEID14.getText(); 

                    }
                    break;

            }


            match(input,21,FOLLOW_21_in_grammardef1310); 

            match(input,25,FOLLOW_25_in_grammardef1318); 

            match(input,ASSIGNMENT,FOLLOW_ASSIGNMENT_in_grammardef1320); 

            pushFollow(FOLLOW_phrase_in_grammardef1322);
            phrase15=phrase(grammar);

            state._fsp--;


             grammar.generator = phrase15; 

            match(input,21,FOLLOW_21_in_grammardef1327); 

            pushFollow(FOLLOW_rules_in_grammardef1335);
            rules(grammar);

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return grammar;
    }
    // $ANTLR end "grammardef"

    // Delegated rules


    protected DFA7 dfa7 = new DFA7(this);
    protected DFA21 dfa21 = new DFA21(this);
    static final String DFA7_eotS =
        "\7\uffff";
    static final String DFA7_eofS =
        "\7\uffff";
    static final String DFA7_minS =
        "\1\7\2\uffff\1\12\3\uffff";
    static final String DFA7_maxS =
        "\1\16\2\uffff\1\16\3\uffff";
    static final String DFA7_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\3\1\4\1\5";
    static final String DFA7_specialS =
        "\7\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\1\1\2\1\uffff\1\3\1\5\1\uffff\1\6\1\4",
            "",
            "",
            "\1\3\1\5\1\uffff\1\6\1\4",
            "",
            "",
            ""
    };

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }
        public String getDescription() {
            return "66:7: ( EMPTY | LINEFEED | (mod= PREMODIFIER )* WORD (pmod= POSTMODIFIER )* | (mod= PREMODIFIER )* RULEID (pmod= POSTMODIFIER )* | (mod= PREMODIFIER )* STRING (pmod= POSTMODIFIER )* )";
        }
    }
    static final String DFA21_eotS =
        "\10\uffff";
    static final String DFA21_eofS =
        "\10\uffff";
    static final String DFA21_minS =
        "\1\5\1\uffff\1\5\5\uffff";
    static final String DFA21_maxS =
        "\1\27\1\uffff\1\27\5\uffff";
    static final String DFA21_acceptS =
        "\1\uffff\1\6\1\uffff\1\1\1\2\1\3\1\4\1\5";
    static final String DFA21_specialS =
        "\10\uffff}>";
    static final String[] DFA21_transitionS = {
            "\1\5\1\uffff\2\6\1\uffff\1\2\1\6\1\1\2\6\1\uffff\1\4\1\7\2\1"+
            "\1\uffff\2\1\1\3",
            "",
            "\1\5\4\uffff\1\2\1\6\1\uffff\2\6\1\uffff\1\4\1\7\5\uffff\1"+
            "\3",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA21_eot = DFA.unpackEncodedString(DFA21_eotS);
    static final short[] DFA21_eof = DFA.unpackEncodedString(DFA21_eofS);
    static final char[] DFA21_min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS);
    static final char[] DFA21_max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS);
    static final short[] DFA21_accept = DFA.unpackEncodedString(DFA21_acceptS);
    static final short[] DFA21_special = DFA.unpackEncodedString(DFA21_specialS);
    static final short[][] DFA21_transition;

    static {
        int numStates = DFA21_transitionS.length;
        DFA21_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA21_transition[i] = DFA.unpackEncodedString(DFA21_transitionS[i]);
        }
    }

    class DFA21 extends DFA {

        public DFA21(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 21;
            this.eot = DFA21_eot;
            this.eof = DFA21_eof;
            this.min = DFA21_min;
            this.max = DFA21_max;
            this.accept = DFA21_accept;
            this.special = DFA21_special;
            this.transition = DFA21_transition;
        }
        public String getDescription() {
            return "()+ loopback of 129:7: ( condition[$grammar] | variable[$grammar] | command[$grammar] | atom[$grammar] | selection[$grammar] )+";
        }
    }
 

    public static final BitSet FOLLOW_EMPTY_in_atom428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LINEFEED_in_atom448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PREMODIFIER_in_atom471 = new BitSet(new long[]{0x0000000000004400L});
    public static final BitSet FOLLOW_WORD_in_atom485 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_POSTMODIFIER_in_atom500 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_PREMODIFIER_in_atom526 = new BitSet(new long[]{0x0000000000000C00L});
    public static final BitSet FOLLOW_RULEID_in_atom540 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_POSTMODIFIER_in_atom555 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_PREMODIFIER_in_atom580 = new BitSet(new long[]{0x0000000000002400L});
    public static final BitSet FOLLOW_STRING_in_atom594 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_POSTMODIFIER_in_atom609 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_PREMODIFIER_in_selection660 = new BitSet(new long[]{0x0000000000020400L});
    public static final BitSet FOLLOW_17_in_selection672 = new BitSet(new long[]{0x0000000000836DA0L});
    public static final BitSet FOLLOW_phrase_in_selection682 = new BitSet(new long[]{0x0000000000041000L});
    public static final BitSet FOLLOW_SELECTION_SEPARATOR_in_selection694 = new BitSet(new long[]{0x0000000000836DA0L});
    public static final BitSet FOLLOW_phrase_in_selection699 = new BitSet(new long[]{0x0000000000041000L});
    public static final BitSet FOLLOW_18_in_selection713 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_POSTMODIFIER_in_selection726 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_PREMODIFIER_in_command767 = new BitSet(new long[]{0x0000000000000420L});
    public static final BitSet FOLLOW_COMMANDS_in_command779 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_command789 = new BitSet(new long[]{0x00000000008F6DA0L});
    public static final BitSet FOLLOW_phrase_in_command802 = new BitSet(new long[]{0x00000000000C0000L});
    public static final BitSet FOLLOW_19_in_command818 = new BitSet(new long[]{0x0000000000836DA0L});
    public static final BitSet FOLLOW_phrase_in_command822 = new BitSet(new long[]{0x00000000000C0000L});
    public static final BitSet FOLLOW_18_in_command836 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_POSTMODIFIER_in_command847 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_PREMODIFIER_in_variable889 = new BitSet(new long[]{0x0000000000010400L});
    public static final BitSet FOLLOW_16_in_variable901 = new BitSet(new long[]{0x0000000000876DA0L});
    public static final BitSet FOLLOW_phrase_in_variable914 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_variable927 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_POSTMODIFIER_in_variable938 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_PREMODIFIER_in_condition982 = new BitSet(new long[]{0x0000000000800400L});
    public static final BitSet FOLLOW_23_in_condition995 = new BitSet(new long[]{0x0000000000836DA0L});
    public static final BitSet FOLLOW_phrase_in_condition1006 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_condition1009 = new BitSet(new long[]{0x0000000000836DA0L});
    public static final BitSet FOLLOW_phrase_in_condition1013 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_condition1025 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_condition1034 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_condition1036 = new BitSet(new long[]{0x0000000000836DA0L});
    public static final BitSet FOLLOW_phrase_in_condition1040 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_condition1043 = new BitSet(new long[]{0x0000000000100202L});
    public static final BitSet FOLLOW_20_in_condition1056 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_condition1058 = new BitSet(new long[]{0x0000000000836DA0L});
    public static final BitSet FOLLOW_phrase_in_condition1062 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_condition1065 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_POSTMODIFIER_in_condition1081 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_condition_in_phrase1130 = new BitSet(new long[]{0x0000000000836DA2L});
    public static final BitSet FOLLOW_variable_in_phrase1143 = new BitSet(new long[]{0x0000000000836DA2L});
    public static final BitSet FOLLOW_command_in_phrase1156 = new BitSet(new long[]{0x0000000000836DA2L});
    public static final BitSet FOLLOW_atom_in_phrase1169 = new BitSet(new long[]{0x0000000000836DA2L});
    public static final BitSet FOLLOW_selection_in_phrase1182 = new BitSet(new long[]{0x0000000000836DA2L});
    public static final BitSet FOLLOW_RULEID_in_rule1215 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGNMENT_in_rule1217 = new BitSet(new long[]{0x0000000000836DA0L});
    public static final BitSet FOLLOW_phrase_in_rule1219 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_rule1222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_rules1254 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_24_in_grammardef1293 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGNMENT_in_grammardef1295 = new BitSet(new long[]{0x0000000000004800L});
    public static final BitSet FOLLOW_WORD_in_grammardef1298 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_RULEID_in_grammardef1304 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_grammardef1310 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_grammardef1318 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGNMENT_in_grammardef1320 = new BitSet(new long[]{0x0000000000836DA0L});
    public static final BitSet FOLLOW_phrase_in_grammardef1322 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_grammardef1327 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_rules_in_grammardef1335 = new BitSet(new long[]{0x0000000000000002L});

}