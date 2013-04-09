// $ANTLR 3.4 /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g 2013-04-09 11:01:56

  package cek.ruins.bookofnames.grammar;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class SentencesGeneratorLexer extends Lexer {
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
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public SentencesGeneratorLexer() {} 
    public SentencesGeneratorLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public SentencesGeneratorLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "/home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g"; }

    // $ANTLR start "ASSIGNMENT"
    public final void mASSIGNMENT() throws RecognitionException {
        try {
            int _type = ASSIGNMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:11:12: ( '::=' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:11:14: '::='
            {
            match("::="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ASSIGNMENT"

    // $ANTLR start "SELECTION_SEPARATOR"
    public final void mSELECTION_SEPARATOR() throws RecognitionException {
        try {
            int _type = SELECTION_SEPARATOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:12:21: ( '|' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:12:23: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SELECTION_SEPARATOR"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:13:7: ( '$(' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:13:9: '$('
            {
            match("$("); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:14:7: ( '(' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:14:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:15:7: ( ')' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:15:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:16:7: ( ',' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:16:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:17:7: ( ':' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:17:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:18:7: ( ';' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:18:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:19:7: ( '==' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:19:9: '=='
            {
            match("=="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:20:7: ( '?(' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:20:9: '?('
            {
            match("?("); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__23"

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:21:7: ( 'I' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:21:9: 'I'
            {
            match('I'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:22:7: ( 'S' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:22:9: 'S'
            {
            match('S'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "PREMODIFIER"
    public final void mPREMODIFIER() throws RecognitionException {
        try {
            int _type = PREMODIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:28:13: ( '^' | '@' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:
            {
            if ( input.LA(1)=='@'||input.LA(1)=='^' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "PREMODIFIER"

    // $ANTLR start "POSTMODIFIER"
    public final void mPOSTMODIFIER() throws RecognitionException {
        try {
            int _type = POSTMODIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:32:14: ( '§' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:33:11: '§'
            {
            match('\u00A7'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "POSTMODIFIER"

    // $ANTLR start "COMMANDS"
    public final void mCOMMANDS() throws RecognitionException {
        try {
            int _type = COMMANDS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:36:10: ( '#' ( 'name' | 'grammar' | 'random' | 'todo' ) )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:37:7: '#' ( 'name' | 'grammar' | 'random' | 'todo' )
            {
            match('#'); 

            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:37:11: ( 'name' | 'grammar' | 'random' | 'todo' )
            int alt1=4;
            switch ( input.LA(1) ) {
            case 'n':
                {
                alt1=1;
                }
                break;
            case 'g':
                {
                alt1=2;
                }
                break;
            case 'r':
                {
                alt1=3;
                }
                break;
            case 't':
                {
                alt1=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;

            }

            switch (alt1) {
                case 1 :
                    // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:37:12: 'name'
                    {
                    match("name"); 



                    }
                    break;
                case 2 :
                    // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:37:21: 'grammar'
                    {
                    match("grammar"); 



                    }
                    break;
                case 3 :
                    // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:37:33: 'random'
                    {
                    match("random"); 



                    }
                    break;
                case 4 :
                    // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:37:44: 'todo'
                    {
                    match("todo"); 



                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMANDS"

    // $ANTLR start "LINEFEED"
    public final void mLINEFEED() throws RecognitionException {
        try {
            int _type = LINEFEED;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:40:10: ( '\\\\n' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:40:12: '\\\\n'
            {
            match("\\n"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LINEFEED"

    // $ANTLR start "EMPTY"
    public final void mEMPTY() throws RecognitionException {
        try {
            int _type = EMPTY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:42:7: ( '_' )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:42:9: '_'
            {
            match('_'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EMPTY"

    // $ANTLR start "WORD"
    public final void mWORD() throws RecognitionException {
        try {
            int _type = WORD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:44:6: ( ( ( 'a' .. 'z' | '0' .. '9' | '-' ) | ( '\\u0080' .. '\\ufffe' ) ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '\\u0080' .. '\\ufffe' | '\\'' | '-' )* )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:45:7: ( ( 'a' .. 'z' | '0' .. '9' | '-' ) | ( '\\u0080' .. '\\ufffe' ) ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '\\u0080' .. '\\ufffe' | '\\'' | '-' )*
            {
            if ( input.LA(1)=='-'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||(input.LA(1) >= '\u0080' && input.LA(1) <= '\uFFFE') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:45:53: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '\\u0080' .. '\\ufffe' | '\\'' | '-' )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0=='\''||LA2_0=='-'||(LA2_0 >= '0' && LA2_0 <= '9')||(LA2_0 >= 'A' && LA2_0 <= 'Z')||(LA2_0 >= 'a' && LA2_0 <= 'z')||(LA2_0 >= '\u0080' && LA2_0 <= '\uFFFE')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:
            	    {
            	    if ( input.LA(1)=='\''||input.LA(1)=='-'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||(input.LA(1) >= '\u0080' && input.LA(1) <= '\uFFFE') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WORD"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:47:4: ( ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) ) )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:48:7: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
            {
            if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


             _channel = HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "RULEID"
    public final void mRULEID() throws RecognitionException {
        try {
            int _type = RULEID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:50:8: ( ( 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '\\u0080' .. '\\ufffe' )* )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:51:7: ( 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '\\u0080' .. '\\ufffe' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:51:17: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '\\u0080' .. '\\ufffe' )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0 >= '0' && LA3_0 <= '9')||(LA3_0 >= 'A' && LA3_0 <= 'Z')||(LA3_0 >= 'a' && LA3_0 <= 'z')||(LA3_0 >= '\u0080' && LA3_0 <= '\uFFFE')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||(input.LA(1) >= '\u0080' && input.LA(1) <= '\uFFFE') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RULEID"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:54:9: ( ( '//' (~ ( '\\r' | '\\n' ) )* | '/*' ( . )* '*/' ) )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:55:7: ( '//' (~ ( '\\r' | '\\n' ) )* | '/*' ( . )* '*/' )
            {
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:55:7: ( '//' (~ ( '\\r' | '\\n' ) )* | '/*' ( . )* '*/' )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='/') ) {
                int LA6_1 = input.LA(2);

                if ( (LA6_1=='/') ) {
                    alt6=1;
                }
                else if ( (LA6_1=='*') ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 1, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;

            }
            switch (alt6) {
                case 1 :
                    // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:55:8: '//' (~ ( '\\r' | '\\n' ) )*
                    {
                    match("//"); 



                    // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:55:13: (~ ( '\\r' | '\\n' ) )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0 >= '\u0000' && LA4_0 <= '\t')||(LA4_0 >= '\u000B' && LA4_0 <= '\f')||(LA4_0 >= '\u000E' && LA4_0 <= '\uFFFF')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:
                    	    {
                    	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:56:9: '/*' ( . )* '*/'
                    {
                    match("/*"); 



                    // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:56:14: ( . )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0=='*') ) {
                            int LA5_1 = input.LA(2);

                            if ( (LA5_1=='/') ) {
                                alt5=2;
                            }
                            else if ( ((LA5_1 >= '\u0000' && LA5_1 <= '.')||(LA5_1 >= '0' && LA5_1 <= '\uFFFF')) ) {
                                alt5=1;
                            }


                        }
                        else if ( ((LA5_0 >= '\u0000' && LA5_0 <= ')')||(LA5_0 >= '+' && LA5_0 <= '\uFFFF')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:56:14: .
                    	    {
                    	    matchAny(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);


                    match("*/"); 



                    }
                    break;

            }


             _channel = HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:60:8: ( ( '\"' ( . )* '\"' )+ )
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:61:7: ( '\"' ( . )* '\"' )+
            {
            // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:61:7: ( '\"' ( . )* '\"' )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0=='\"') ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:61:8: '\"' ( . )* '\"'
            	    {
            	    match('\"'); 

            	    // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:61:12: ( . )*
            	    loop7:
            	    do {
            	        int alt7=2;
            	        int LA7_0 = input.LA(1);

            	        if ( (LA7_0=='\"') ) {
            	            alt7=2;
            	        }
            	        else if ( ((LA7_0 >= '\u0000' && LA7_0 <= '!')||(LA7_0 >= '#' && LA7_0 <= '\uFFFF')) ) {
            	            alt7=1;
            	        }


            	        switch (alt7) {
            	    	case 1 :
            	    	    // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:61:12: .
            	    	    {
            	    	    matchAny(); 

            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop7;
            	        }
            	    } while (true);


            	    match('\"'); 

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    public void mTokens() throws RecognitionException {
        // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:8: ( ASSIGNMENT | SELECTION_SEPARATOR | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | PREMODIFIER | POSTMODIFIER | COMMANDS | LINEFEED | EMPTY | WORD | WS | RULEID | COMMENT | STRING )
        int alt9=22;
        int LA9_0 = input.LA(1);

        if ( (LA9_0==':') ) {
            int LA9_1 = input.LA(2);

            if ( (LA9_1==':') ) {
                alt9=1;
            }
            else {
                alt9=7;
            }
        }
        else if ( (LA9_0=='|') ) {
            alt9=2;
        }
        else if ( (LA9_0=='$') ) {
            alt9=3;
        }
        else if ( (LA9_0=='(') ) {
            alt9=4;
        }
        else if ( (LA9_0==')') ) {
            alt9=5;
        }
        else if ( (LA9_0==',') ) {
            alt9=6;
        }
        else if ( (LA9_0==';') ) {
            alt9=8;
        }
        else if ( (LA9_0=='=') ) {
            alt9=9;
        }
        else if ( (LA9_0=='?') ) {
            alt9=10;
        }
        else if ( (LA9_0=='I') ) {
            int LA9_10 = input.LA(2);

            if ( ((LA9_10 >= '0' && LA9_10 <= '9')||(LA9_10 >= 'A' && LA9_10 <= 'Z')||(LA9_10 >= 'a' && LA9_10 <= 'z')||(LA9_10 >= '\u0080' && LA9_10 <= '\uFFFE')) ) {
                alt9=20;
            }
            else {
                alt9=11;
            }
        }
        else if ( (LA9_0=='S') ) {
            int LA9_11 = input.LA(2);

            if ( ((LA9_11 >= '0' && LA9_11 <= '9')||(LA9_11 >= 'A' && LA9_11 <= 'Z')||(LA9_11 >= 'a' && LA9_11 <= 'z')||(LA9_11 >= '\u0080' && LA9_11 <= '\uFFFE')) ) {
                alt9=20;
            }
            else {
                alt9=12;
            }
        }
        else if ( (LA9_0=='@'||LA9_0=='^') ) {
            alt9=13;
        }
        else if ( (LA9_0=='\u00A7') ) {
            int LA9_13 = input.LA(2);

            if ( (LA9_13=='\''||LA9_13=='-'||(LA9_13 >= '0' && LA9_13 <= '9')||(LA9_13 >= 'A' && LA9_13 <= 'Z')||(LA9_13 >= 'a' && LA9_13 <= 'z')||(LA9_13 >= '\u0080' && LA9_13 <= '\uFFFE')) ) {
                alt9=18;
            }
            else {
                alt9=14;
            }
        }
        else if ( (LA9_0=='#') ) {
            alt9=15;
        }
        else if ( (LA9_0=='\\') ) {
            alt9=16;
        }
        else if ( (LA9_0=='_') ) {
            alt9=17;
        }
        else if ( (LA9_0=='-'||(LA9_0 >= '0' && LA9_0 <= '9')||(LA9_0 >= 'a' && LA9_0 <= 'z')||(LA9_0 >= '\u0080' && LA9_0 <= '\u00A6')||(LA9_0 >= '\u00A8' && LA9_0 <= '\uFFFE')) ) {
            alt9=18;
        }
        else if ( ((LA9_0 >= '\t' && LA9_0 <= '\n')||(LA9_0 >= '\f' && LA9_0 <= '\r')||LA9_0==' ') ) {
            alt9=19;
        }
        else if ( ((LA9_0 >= 'A' && LA9_0 <= 'H')||(LA9_0 >= 'J' && LA9_0 <= 'R')||(LA9_0 >= 'T' && LA9_0 <= 'Z')) ) {
            alt9=20;
        }
        else if ( (LA9_0=='/') ) {
            alt9=21;
        }
        else if ( (LA9_0=='\"') ) {
            alt9=22;
        }
        else {
            NoViableAltException nvae =
                new NoViableAltException("", 9, 0, input);

            throw nvae;

        }
        switch (alt9) {
            case 1 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:10: ASSIGNMENT
                {
                mASSIGNMENT(); 


                }
                break;
            case 2 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:21: SELECTION_SEPARATOR
                {
                mSELECTION_SEPARATOR(); 


                }
                break;
            case 3 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:41: T__16
                {
                mT__16(); 


                }
                break;
            case 4 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:47: T__17
                {
                mT__17(); 


                }
                break;
            case 5 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:53: T__18
                {
                mT__18(); 


                }
                break;
            case 6 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:59: T__19
                {
                mT__19(); 


                }
                break;
            case 7 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:65: T__20
                {
                mT__20(); 


                }
                break;
            case 8 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:71: T__21
                {
                mT__21(); 


                }
                break;
            case 9 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:77: T__22
                {
                mT__22(); 


                }
                break;
            case 10 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:83: T__23
                {
                mT__23(); 


                }
                break;
            case 11 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:89: T__24
                {
                mT__24(); 


                }
                break;
            case 12 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:95: T__25
                {
                mT__25(); 


                }
                break;
            case 13 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:101: PREMODIFIER
                {
                mPREMODIFIER(); 


                }
                break;
            case 14 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:113: POSTMODIFIER
                {
                mPOSTMODIFIER(); 


                }
                break;
            case 15 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:126: COMMANDS
                {
                mCOMMANDS(); 


                }
                break;
            case 16 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:135: LINEFEED
                {
                mLINEFEED(); 


                }
                break;
            case 17 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:144: EMPTY
                {
                mEMPTY(); 


                }
                break;
            case 18 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:150: WORD
                {
                mWORD(); 


                }
                break;
            case 19 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:155: WS
                {
                mWS(); 


                }
                break;
            case 20 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:158: RULEID
                {
                mRULEID(); 


                }
                break;
            case 21 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:165: COMMENT
                {
                mCOMMENT(); 


                }
                break;
            case 22 :
                // /home/cek/gitrepositories/ruins/jWebSocket/SentencesGenerator.g:1:173: STRING
                {
                mSTRING(); 


                }
                break;

        }

    }


 

}