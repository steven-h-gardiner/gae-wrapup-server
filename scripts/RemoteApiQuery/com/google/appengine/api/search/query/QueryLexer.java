

package com.google.appengine.api.search.query;

import org.antlr.runtime.*;

public class QueryLexer extends Lexer {
    public static final int REWRITE=31;
    public static final int NUMBER_PREFIX=40;
    public static final int UNICODE_ESC=34;
    public static final int TEXT=32;
    public static final int VALUE=15;
    public static final int MINUS=38;
    public static final int BACKSLASH=37;
    public static final int DISJUNCTION=6;
    public static final int OCTAL_ESC=35;
    public static final int LITERAL=11;
    public static final int TEXT_ESC=41;
    public static final int LPAREN=24;
    public static final int RPAREN=25;
    public static final int EQ=22;
    public static final int FUNCTION=8;
    public static final int NOT=28;
    public static final int NE=21;
    public static final int AND=26;
    public static final int QUOTE=33;
    public static final int ESCAPED_CHAR=44;
    public static final int ARGS=4;
    public static final int MID_CHAR=42;
    public static final int START_CHAR=39;
    public static final int ESC=36;
    public static final int SEQUENCE=14;
    public static final int GLOBAL=10;
    public static final int HEX_DIGIT=45;
    public static final int WS=16;
    public static final int EOF=-1;
    public static final int EMPTY=7;
    public static final int GE=19;
    public static final int COMMA=29;
    public static final int OR=27;
    public static final int FUZZY=9;
    public static final int NEGATION=12;
    public static final int GT=20;
    public static final int DIGIT=43;
    public static final int CONJUNCTION=5;
    public static final int FIX=30;
    public static final int EXCLAMATION=46;
    public static final int LESSTHAN=18;
    public static final int STRING=13;
    public static final int LE=17;
    public static final int HAS=23;

    private boolean exclamationNotFollowedByEquals() {
      if (input.LA(1) != '!') {
        throw new IllegalStateException();
      }
      return input.LA(2) != '=';
    }

    public QueryLexer() {;}
    public QueryLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public QueryLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return ""; }

    public final void mHAS() throws RecognitionException {
        try {
            int _type = HAS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match(':');

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match("OR");

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match("AND");

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match("NOT");

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mREWRITE() throws RecognitionException {
        try {
            int _type = REWRITE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match('~');

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mFIX() throws RecognitionException {
        try {
            int _type = FIX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match('+');

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mESC() throws RecognitionException {
        try {
            int _type = ESC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            int alt1=3;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '\\':
                    {
                    alt1=1;
                    }
                    break;
                case 'u':
                    {
                    alt1=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt1=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 1, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    {
                    match('\\');
                    if ( input.LA(1)=='\"'||input.LA(1)=='\\' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}

                    }
                    break;
                case 2 :
                    {
                    mUNICODE_ESC();

                    }
                    break;
                case 3 :
                    {
                    mOCTAL_ESC();

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match('(');

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match(')');

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match(',');

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mBACKSLASH() throws RecognitionException {
        try {
            int _type = BACKSLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match('\\');

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mLESSTHAN() throws RecognitionException {
        try {
            int _type = LESSTHAN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match('<');

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match('>');

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mGE() throws RecognitionException {
        try {
            int _type = GE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match(">=");

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mLE() throws RecognitionException {
        try {
            int _type = LE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match("<=");

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mNE() throws RecognitionException {
        try {
            int _type = NE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match("!=");

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match('=');

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match('-');

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mQUOTE() throws RecognitionException {
        try {
            int _type = QUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            match('\"');

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }

    public final void mTEXT() throws RecognitionException {
        try {
            int _type = TEXT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            {
            int alt2=3;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='!') && (( exclamationNotFollowedByEquals() ))) {
                alt2=1;
            }
            else if ( ((LA2_0>='#' && LA2_0<='\'')||LA2_0=='*'||(LA2_0>='.' && LA2_0<='/')||LA2_0==';'||(LA2_0>='?' && LA2_0<='[')||(LA2_0>=']' && LA2_0<='}')||(LA2_0>='\u00A1' && LA2_0<='\uFFEE')) ) {
                alt2=1;
            }
            else if ( (LA2_0=='-'||(LA2_0>='0' && LA2_0<='9')) ) {
                alt2=2;
            }
            else if ( (LA2_0=='\\') ) {
                alt2=3;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    {
                    mSTART_CHAR();

                    }
                    break;
                case 2 :
                    {
                    mNUMBER_PREFIX();

                    }
                    break;
                case 3 :
                    {
                    mTEXT_ESC();

                    }
                    break;

            }

            loop3:
            do {
                int alt3=3;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='!') && (( exclamationNotFollowedByEquals() ))) {
                    alt3=1;
                }
                else if ( ((LA3_0>='#' && LA3_0<='\'')||(LA3_0>='*' && LA3_0<='+')||(LA3_0>='-' && LA3_0<='9')||LA3_0==';'||(LA3_0>='?' && LA3_0<='[')||(LA3_0>=']' && LA3_0<='}')||(LA3_0>='\u00A1' && LA3_0<='\uFFEE')) ) {
                    alt3=1;
                }
                else if ( (LA3_0=='\\') ) {
                    alt3=2;
                }

                switch (alt3) {
            	case 1 :
            	    {
            	    mMID_CHAR();

            	    }
            	    break;
            	case 2 :
            	    {
            	    mTEXT_ESC();

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
        }
    }

    public final void mNUMBER_PREFIX() throws RecognitionException {
        try {
            {
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='-') ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    {
                    mMINUS();

                    }
                    break;

            }

            mDIGIT();

            }

        }
        finally {
        }
    }

    public final void mTEXT_ESC() throws RecognitionException {
        try {
            int alt5=3;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '+':
                case ',':
                case ':':
                case '<':
                case '=':
                case '>':
                case '\\':
                case '~':
                    {
                    alt5=1;
                    }
                    break;
                case 'u':
                    {
                    alt5=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt5=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    {
                    mESCAPED_CHAR();

                    }
                    break;
                case 2 :
                    {
                    mUNICODE_ESC();

                    }
                    break;
                case 3 :
                    {
                    mOCTAL_ESC();

                    }
                    break;

            }
        }
        finally {
        }
    }

    public final void mUNICODE_ESC() throws RecognitionException {
        try {
            {
            match('\\');
            match('u');
            mHEX_DIGIT();
            mHEX_DIGIT();
            mHEX_DIGIT();
            mHEX_DIGIT();

            }

        }
        finally {
        }
    }

    public final void mOCTAL_ESC() throws RecognitionException {
        try {
            int alt6=3;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='\\') ) {
                int LA6_1 = input.LA(2);

                if ( ((LA6_1>='0' && LA6_1<='3')) ) {
                    int LA6_2 = input.LA(3);

                    if ( ((LA6_2>='0' && LA6_2<='7')) ) {
                        int LA6_4 = input.LA(4);

                        if ( ((LA6_4>='0' && LA6_4<='7')) ) {
                            alt6=1;
                        }
                        else {
                            alt6=2;}
                    }
                    else {
                        alt6=3;}
                }
                else if ( ((LA6_1>='4' && LA6_1<='7')) ) {
                    int LA6_3 = input.LA(3);

                    if ( ((LA6_3>='0' && LA6_3<='7')) ) {
                        alt6=2;
                    }
                    else {
                        alt6=3;}
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
                    {
                    match('\\');
                    {
                    matchRange('0','3');

                    }

                    {
                    matchRange('0','7');

                    }

                    {
                    matchRange('0','7');

                    }

                    }
                    break;
                case 2 :
                    {
                    match('\\');
                    {
                    matchRange('0','7');

                    }

                    {
                    matchRange('0','7');

                    }

                    }
                    break;
                case 3 :
                    {
                    match('\\');
                    {
                    matchRange('0','7');

                    }

                    }
                    break;

            }
        }
        finally {
        }
    }

    public final void mDIGIT() throws RecognitionException {
        try {
            {
            matchRange('0','9');

            }

        }
        finally {
        }
    }

    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            }

        }
        finally {
        }
    }

    public final void mSTART_CHAR() throws RecognitionException {
        try {
            int alt7=12;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    {
                    mEXCLAMATION();

                    }
                    break;
                case 2 :
                    {
                    matchRange('#','\'');

                    }
                    break;
                case 3 :
                    {
                    match('*');

                    }
                    break;
                case 4 :
                    {
                    match('.');

                    }
                    break;
                case 5 :
                    {
                    match('/');

                    }
                    break;
                case 6 :
                    {
                    match(';');

                    }
                    break;
                case 7 :
                    {
                    match('?');

                    }
                    break;
                case 8 :
                    {
                    match('@');

                    }
                    break;
                case 9 :
                    {
                    matchRange('A','Z');

                    }
                    break;
                case 10 :
                    {
                    match('[');

                    }
                    break;
                case 11 :
                    {
                    matchRange(']','}');

                    }
                    break;
                case 12 :
                    {
                    matchRange('\u00A1','\uFFEE');

                    }
                    break;

            }
        }
        finally {
        }
    }

    public final void mMID_CHAR() throws RecognitionException {
        try {
            int alt8=4;
            int LA8_0 = input.LA(1);

            if ( (LA8_0=='!') && (( exclamationNotFollowedByEquals() ))) {
                alt8=1;
            }
            else if ( ((LA8_0>='#' && LA8_0<='\'')||LA8_0=='*'||(LA8_0>='.' && LA8_0<='/')||LA8_0==';'||(LA8_0>='?' && LA8_0<='[')||(LA8_0>=']' && LA8_0<='}')||(LA8_0>='\u00A1' && LA8_0<='\uFFEE')) ) {
                alt8=1;
            }
            else if ( ((LA8_0>='0' && LA8_0<='9')) ) {
                alt8=2;
            }
            else if ( (LA8_0=='+') ) {
                alt8=3;
            }
            else if ( (LA8_0=='-') ) {
                alt8=4;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    {
                    mSTART_CHAR();

                    }
                    break;
                case 2 :
                    {
                    mDIGIT();

                    }
                    break;
                case 3 :
                    {
                    match('+');

                    }
                    break;
                case 4 :
                    {
                    match('-');

                    }
                    break;

            }
        }
        finally {
        }
    }

    public final void mESCAPED_CHAR() throws RecognitionException {
        try {
            int alt9=9;
            alt9 = dfa9.predict(input);
            switch (alt9) {
                case 1 :
                    {
                    match("\\,");

                    }
                    break;
                case 2 :
                    {
                    match("\\:");

                    }
                    break;
                case 3 :
                    {
                    match("\\=");

                    }
                    break;
                case 4 :
                    {
                    match("\\<");

                    }
                    break;
                case 5 :
                    {
                    match("\\>");

                    }
                    break;
                case 6 :
                    {
                    match("\\+");

                    }
                    break;
                case 7 :
                    {
                    match("\\~");

                    }
                    break;
                case 8 :
                    {
                    match("\\\"");

                    }
                    break;
                case 9 :
                    {
                    match("\\\\");

                    }
                    break;

            }
        }
        finally {
        }
    }

    public final void mEXCLAMATION() throws RecognitionException {
        try {
            {
            if ( !(( exclamationNotFollowedByEquals() )) ) {
                throw new FailedPredicateException(input, "EXCLAMATION", " exclamationNotFollowedByEquals() ");
            }
            match('!');

            }

        }
        finally {
        }
    }

    public void mTokens() throws RecognitionException {
        int alt10=21;
        alt10 = dfa10.predict(input);
        switch (alt10) {
            case 1 :
                {
                mHAS();

                }
                break;
            case 2 :
                {
                mOR();

                }
                break;
            case 3 :
                {
                mAND();

                }
                break;
            case 4 :
                {
                mNOT();

                }
                break;
            case 5 :
                {
                mREWRITE();

                }
                break;
            case 6 :
                {
                mFIX();

                }
                break;
            case 7 :
                {
                mESC();

                }
                break;
            case 8 :
                {
                mWS();

                }
                break;
            case 9 :
                {
                mLPAREN();

                }
                break;
            case 10 :
                {
                mRPAREN();

                }
                break;
            case 11 :
                {
                mCOMMA();

                }
                break;
            case 12 :
                {
                mBACKSLASH();

                }
                break;
            case 13 :
                {
                mLESSTHAN();

                }
                break;
            case 14 :
                {
                mGT();

                }
                break;
            case 15 :
                {
                mGE();

                }
                break;
            case 16 :
                {
                mLE();

                }
                break;
            case 17 :
                {
                mNE();

                }
                break;
            case 18 :
                {
                mEQ();

                }
                break;
            case 19 :
                {
                mMINUS();

                }
                break;
            case 20 :
                {
                mQUOTE();

                }
                break;
            case 21 :
                {
                mTEXT();

                }
                break;

        }

    }

    protected DFA7 dfa7 = new DFA7(this);
    protected DFA9 dfa9 = new DFA9(this);
    protected DFA10 dfa10 = new DFA10(this);
    static final String DFA7_eotS =
        "\15\uffff";
    static final String DFA7_eofS =
        "\15\uffff";
    static final String DFA7_minS =
        "\1\41\14\uffff";
    static final String DFA7_maxS =
        "\1\uffee\14\uffff";
    static final String DFA7_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14";
    static final String DFA7_specialS =
        "\1\0\14\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\1\1\uffff\5\2\2\uffff\1\3\3\uffff\1\4\1\5\13\uffff\1\6\3"+
            "\uffff\1\7\1\10\32\11\1\12\1\uffff\41\13\43\uffff\uff4e\14",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            return "335:10: fragment START_CHAR : ( EXCLAMATION | '#' .. '\\'' | '*' | '.' | '/' | ';' | '?' | '@' | 'A' .. 'Z' | '[' | ']' .. '}' | '\\u00a1' .. '\\uffee' );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 :
                        int LA7_0 = input.LA(1);

                        int index7_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA7_0=='!') && (( exclamationNotFollowedByEquals() ))) {s = 1;}

                        else if ( ((LA7_0>='#' && LA7_0<='\'')) ) {s = 2;}

                        else if ( (LA7_0=='*') ) {s = 3;}

                        else if ( (LA7_0=='.') ) {s = 4;}

                        else if ( (LA7_0=='/') ) {s = 5;}

                        else if ( (LA7_0==';') ) {s = 6;}

                        else if ( (LA7_0=='?') ) {s = 7;}

                        else if ( (LA7_0=='@') ) {s = 8;}

                        else if ( ((LA7_0>='A' && LA7_0<='Z')) ) {s = 9;}

                        else if ( (LA7_0=='[') ) {s = 10;}

                        else if ( ((LA7_0>=']' && LA7_0<='}')) ) {s = 11;}

                        else if ( ((LA7_0>='\u00A1' && LA7_0<='\uFFEE')) ) {s = 12;}

                        input.seek(index7_0);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 7, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA9_eotS =
        "\13\uffff";
    static final String DFA9_eofS =
        "\13\uffff";
    static final String DFA9_minS =
        "\1\134\1\42\11\uffff";
    static final String DFA9_maxS =
        "\1\134\1\176\11\uffff";
    static final String DFA9_acceptS =
        "\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11";
    static final String DFA9_specialS =
        "\13\uffff}>";
    static final String[] DFA9_transitionS = {
            "\1\1",
            "\1\11\10\uffff\1\7\1\2\15\uffff\1\3\1\uffff\1\5\1\4\1\6\35"+
            "\uffff\1\12\41\uffff\1\10",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "357:10: fragment ESCAPED_CHAR : ( '\\\\,' | '\\\\:' | '\\\\=' | '\\\\<' | '\\\\>' | '\\\\+' | '\\\\~' | '\\\\\\\"' | '\\\\\\\\' );";
        }
    }
    static final String DFA10_eotS =
        "\2\uffff\3\22\2\uffff\1\33\4\uffff\1\35\1\37\1\41\1\uffff\1\42\2"+
        "\uffff\1\44\2\43\1\47\1\uffff\3\47\12\uffff\1\53\1\54\2\uffff\2"+
        "\47\3\uffff\1\47\1\uffff\1\47";
    static final String DFA10_eofS =
        "\61\uffff";
    static final String DFA10_minS =
        "\1\11\1\uffff\1\122\1\116\1\117\2\uffff\1\42\4\uffff\3\75\1\uffff"+
        "\1\60\2\uffff\1\41\1\104\1\124\1\41\1\60\3\41\12\uffff\2\41\1\uffff"+
        "\1\60\2\41\2\uffff\1\60\1\41\1\60\1\41";
    static final String DFA10_maxS =
        "\1\uffee\1\uffff\1\122\1\116\1\117\2\uffff\1\176\4\uffff\3\75\1"+
        "\uffff\1\71\2\uffff\1\uffee\1\104\1\124\1\uffee\1\146\3\uffee\12"+
        "\uffff\2\uffee\1\uffff\1\146\2\uffee\2\uffff\1\146\1\uffee\1\146"+
        "\1\uffee";
    static final String DFA10_acceptS =
        "\1\uffff\1\1\3\uffff\1\5\1\6\1\uffff\1\10\1\11\1\12\1\13\3\uffff"+
        "\1\22\1\uffff\1\24\1\25\10\uffff\1\14\1\20\1\15\1\17\1\16\1\21\1"+
        "\25\1\23\1\25\1\2\2\uffff\1\7\3\uffff\1\3\1\4\4\uffff";
    static final String DFA10_specialS =
        "\16\uffff\1\0\42\uffff}>";
    static final String[] DFA10_transitionS = {
            "\2\10\1\uffff\2\10\22\uffff\1\10\1\16\1\21\5\22\1\11\1\12\1"+
            "\22\1\6\1\13\1\20\14\22\1\1\1\22\1\14\1\17\1\15\2\22\1\3\14"+
            "\22\1\4\1\2\14\22\1\7\41\22\1\5\42\uffff\uff4e\22",
            "",
            "\1\23",
            "\1\24",
            "\1\25",
            "",
            "",
            "\1\26\10\uffff\2\22\3\uffff\4\31\4\32\2\uffff\1\22\1\uffff"+
            "\3\22\35\uffff\1\30\30\uffff\1\27\10\uffff\1\22",
            "",
            "",
            "",
            "",
            "\1\34",
            "\1\36",
            "\1\40",
            "",
            "\12\43",
            "",
            "",
            "\1\43\1\uffff\5\43\2\uffff\2\43\1\uffff\15\43\1\uffff\1\43"+
            "\3\uffff\77\43\43\uffff\uff4e\43",
            "\1\45",
            "\1\46",
            "\1\43\1\uffff\5\43\2\uffff\2\43\1\uffff\15\43\1\uffff\1\43"+
            "\3\uffff\77\43\43\uffff\uff4e\43",
            "\12\50\7\uffff\6\50\32\uffff\6\50",
            "\1\43\1\uffff\5\43\2\uffff\2\43\1\uffff\15\43\1\uffff\1\43"+
            "\3\uffff\77\43\43\uffff\uff4e\43",
            "\1\43\1\uffff\5\43\2\uffff\2\43\1\uffff\3\43\10\51\2\43\1\uffff"+
            "\1\43\3\uffff\77\43\43\uffff\uff4e\43",
            "\1\43\1\uffff\5\43\2\uffff\2\43\1\uffff\3\43\10\52\2\43\1\uffff"+
            "\1\43\3\uffff\77\43\43\uffff\uff4e\43",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\43\1\uffff\5\43\2\uffff\2\43\1\uffff\15\43\1\uffff\1\43"+
            "\3\uffff\77\43\43\uffff\uff4e\43",
            "\1\43\1\uffff\5\43\2\uffff\2\43\1\uffff\15\43\1\uffff\1\43"+
            "\3\uffff\77\43\43\uffff\uff4e\43",
            "",
            "\12\55\7\uffff\6\55\32\uffff\6\55",
            "\1\43\1\uffff\5\43\2\uffff\2\43\1\uffff\3\43\10\56\2\43\1\uffff"+
            "\1\43\3\uffff\77\43\43\uffff\uff4e\43",
            "\1\43\1\uffff\5\43\2\uffff\2\43\1\uffff\15\43\1\uffff\1\43"+
            "\3\uffff\77\43\43\uffff\uff4e\43",
            "",
            "",
            "\12\57\7\uffff\6\57\32\uffff\6\57",
            "\1\43\1\uffff\5\43\2\uffff\2\43\1\uffff\15\43\1\uffff\1\43"+
            "\3\uffff\77\43\43\uffff\uff4e\43",
            "\12\60\7\uffff\6\60\32\uffff\6\60",
            "\1\43\1\uffff\5\43\2\uffff\2\43\1\uffff\15\43\1\uffff\1\43"+
            "\3\uffff\77\43\43\uffff\uff4e\43"
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( HAS | OR | AND | NOT | REWRITE | FIX | ESC | WS | LPAREN | RPAREN | COMMA | BACKSLASH | LESSTHAN | GT | GE | LE | NE | EQ | MINUS | QUOTE | TEXT );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 :
                        int LA10_14 = input.LA(1);

                        int index10_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA10_14=='=') ) {s = 32;}

                        else s = 33;

                        input.seek(index10_14);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 10, _s, input);
            error(nvae);
            throw nvae;
        }
    }

}
