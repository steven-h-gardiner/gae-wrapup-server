

package com.google.appengine.api.search.query;

import org.antlr.runtime.*;

import org.antlr.runtime.tree.*;

public class QueryParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ARGS", "CONJUNCTION", "DISJUNCTION", "EMPTY", "FUNCTION", "FUZZY", "GLOBAL", "LITERAL", "NEGATION", "STRING", "SEQUENCE", "VALUE", "WS", "LE", "LESSTHAN", "GE", "GT", "NE", "EQ", "HAS", "LPAREN", "RPAREN", "AND", "OR", "NOT", "COMMA", "FIX", "REWRITE", "TEXT", "QUOTE", "UNICODE_ESC", "OCTAL_ESC", "ESC", "BACKSLASH", "MINUS", "START_CHAR", "NUMBER_PREFIX", "TEXT_ESC", "MID_CHAR", "DIGIT", "ESCAPED_CHAR", "HEX_DIGIT", "EXCLAMATION"
    };
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

        public QueryParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public QueryParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);

        }

    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return QueryParser.tokenNames; }
    public String getGrammarFileName() { return ""; }

      @Override
      public Object recoverFromMismatchedSet(IntStream input,
          RecognitionException e, BitSet follow) throws RecognitionException {
        throw e;
      }

      @Override
      protected Object recoverFromMismatchedToken(
          IntStream input, int ttype, BitSet follow) throws RecognitionException {
        throw new MismatchedTokenException(ttype, input);
      }

    public static class query_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.query_return query() throws RecognitionException {
        QueryParser.query_return retval = new QueryParser.query_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token WS1=null;
        Token EOF2=null;
        Token WS3=null;
        Token WS5=null;
        Token EOF6=null;
        QueryParser.expression_return expression4 = null;

        CommonTree WS1_tree=null;
        CommonTree EOF2_tree=null;
        CommonTree WS3_tree=null;
        CommonTree WS5_tree=null;
        CommonTree EOF6_tree=null;
        RewriteRuleTokenStream stream_WS=new RewriteRuleTokenStream(adaptor,"token WS");
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            int alt4=2;
            alt4 = dfa4.predict(input);
            switch (alt4) {
                case 1 :
                    {
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( (LA1_0==WS) ) {
                            alt1=1;
                        }

                        switch (alt1) {
                    	case 1 :
                    	    {
                    	    WS1=(Token)match(input,WS,FOLLOW_WS_in_query145);
                    	    stream_WS.add(WS1);

                    	    }
                    	    break;

                    	default :
                    	    break loop1;
                        }
                    } while (true);

                    EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_query148);
                    stream_EOF.add(EOF2);

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(EMPTY, "EMPTY"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    {
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( (LA2_0==WS) ) {
                            alt2=1;
                        }

                        switch (alt2) {
                    	case 1 :
                    	    {
                    	    WS3=(Token)match(input,WS,FOLLOW_WS_in_query177);
                    	    stream_WS.add(WS3);

                    	    }
                    	    break;

                    	default :
                    	    break loop2;
                        }
                    } while (true);

                    pushFollow(FOLLOW_expression_in_query180);
                    expression4=expression();

                    state._fsp--;

                    stream_expression.add(expression4.getTree());
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==WS) ) {
                            alt3=1;
                        }

                        switch (alt3) {
                    	case 1 :
                    	    {
                    	    WS5=(Token)match(input,WS,FOLLOW_WS_in_query182);
                    	    stream_WS.add(WS5);

                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);

                    EOF6=(Token)match(input,EOF,FOLLOW_EOF_in_query185);
                    stream_EOF.add(EOF6);

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        adaptor.addChild(root_0, stream_expression.nextTree());

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.expression_return expression() throws RecognitionException {
        QueryParser.expression_return retval = new QueryParser.expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        QueryParser.sequence_return sequence7 = null;

        QueryParser.andOp_return andOp8 = null;

        QueryParser.sequence_return sequence9 = null;

        RewriteRuleSubtreeStream stream_sequence=new RewriteRuleSubtreeStream(adaptor,"rule sequence");
        RewriteRuleSubtreeStream stream_andOp=new RewriteRuleSubtreeStream(adaptor,"rule andOp");
        try {
            {
            pushFollow(FOLLOW_sequence_in_expression208);
            sequence7=sequence();

            state._fsp--;

            stream_sequence.add(sequence7.getTree());
            int alt6=2;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        adaptor.addChild(root_0, stream_sequence.nextTree());

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    {
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        alt5 = dfa5.predict(input);
                        switch (alt5) {
                    	case 1 :
                    	    {
                    	    pushFollow(FOLLOW_andOp_in_expression245);
                    	    andOp8=andOp();

                    	    state._fsp--;

                    	    stream_andOp.add(andOp8.getTree());
                    	    pushFollow(FOLLOW_sequence_in_expression247);
                    	    sequence9=sequence();

                    	    state._fsp--;

                    	    stream_sequence.add(sequence9.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CONJUNCTION, "CONJUNCTION"), root_1);

                        if ( !(stream_sequence.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_sequence.hasNext() ) {
                            adaptor.addChild(root_1, stream_sequence.nextTree());

                        }
                        stream_sequence.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class sequence_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.sequence_return sequence() throws RecognitionException {
        QueryParser.sequence_return retval = new QueryParser.sequence_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token WS11=null;
        QueryParser.factor_return factor10 = null;

        QueryParser.factor_return factor12 = null;

        CommonTree WS11_tree=null;
        RewriteRuleTokenStream stream_WS=new RewriteRuleTokenStream(adaptor,"token WS");
        RewriteRuleSubtreeStream stream_factor=new RewriteRuleSubtreeStream(adaptor,"rule factor");
        try {
            {
            pushFollow(FOLLOW_factor_in_sequence285);
            factor10=factor();

            state._fsp--;

            stream_factor.add(factor10.getTree());
            int alt9=2;
            alt9 = dfa9.predict(input);
            switch (alt9) {
                case 1 :
                    {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        adaptor.addChild(root_0, stream_factor.nextTree());

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    {
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        alt8 = dfa8.predict(input);
                        switch (alt8) {
                    	case 1 :
                    	    {
                    	    int cnt7=0;
                    	    loop7:
                    	    do {
                    	        int alt7=2;
                    	        int LA7_0 = input.LA(1);

                    	        if ( (LA7_0==WS) ) {
                    	            alt7=1;
                    	        }

                    	        switch (alt7) {
                    	    	case 1 :
                    	    	    {
                    	    	    WS11=(Token)match(input,WS,FOLLOW_WS_in_sequence321);
                    	    	    stream_WS.add(WS11);

                    	    	    }
                    	    	    break;

                    	    	default :
                    	    	    if ( cnt7 >= 1 ) break loop7;
                    	                EarlyExitException eee =
                    	                    new EarlyExitException(7, input);
                    	                throw eee;
                    	        }
                    	        cnt7++;
                    	    } while (true);

                    	    pushFollow(FOLLOW_factor_in_sequence324);
                    	    factor12=factor();

                    	    state._fsp--;

                    	    stream_factor.add(factor12.getTree());

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

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SEQUENCE, "SEQUENCE"), root_1);

                        if ( !(stream_factor.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_factor.hasNext() ) {
                            adaptor.addChild(root_1, stream_factor.nextTree());

                        }
                        stream_factor.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class factor_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.factor_return factor() throws RecognitionException {
        QueryParser.factor_return retval = new QueryParser.factor_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        QueryParser.term_return term13 = null;

        QueryParser.orOp_return orOp14 = null;

        QueryParser.term_return term15 = null;

        RewriteRuleSubtreeStream stream_orOp=new RewriteRuleSubtreeStream(adaptor,"rule orOp");
        RewriteRuleSubtreeStream stream_term=new RewriteRuleSubtreeStream(adaptor,"rule term");
        try {
            {
            pushFollow(FOLLOW_term_in_factor365);
            term13=term();

            state._fsp--;

            stream_term.add(term13.getTree());
            int alt11=2;
            alt11 = dfa11.predict(input);
            switch (alt11) {
                case 1 :
                    {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        adaptor.addChild(root_0, stream_term.nextTree());

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    {
                    int cnt10=0;
                    loop10:
                    do {
                        int alt10=2;
                        alt10 = dfa10.predict(input);
                        switch (alt10) {
                    	case 1 :
                    	    {
                    	    pushFollow(FOLLOW_orOp_in_factor397);
                    	    orOp14=orOp();

                    	    state._fsp--;

                    	    stream_orOp.add(orOp14.getTree());
                    	    pushFollow(FOLLOW_term_in_factor399);
                    	    term15=term();

                    	    state._fsp--;

                    	    stream_term.add(term15.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt10 >= 1 ) break loop10;
                                EarlyExitException eee =
                                    new EarlyExitException(10, input);
                                throw eee;
                        }
                        cnt10++;
                    } while (true);

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DISJUNCTION, "DISJUNCTION"), root_1);

                        if ( !(stream_term.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_term.hasNext() ) {
                            adaptor.addChild(root_1, stream_term.nextTree());

                        }
                        stream_term.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class term_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.term_return term() throws RecognitionException {
        QueryParser.term_return retval = new QueryParser.term_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        QueryParser.primitive_return primitive16 = null;

        QueryParser.notOp_return notOp17 = null;

        QueryParser.primitive_return primitive18 = null;

        RewriteRuleSubtreeStream stream_primitive=new RewriteRuleSubtreeStream(adaptor,"rule primitive");
        RewriteRuleSubtreeStream stream_notOp=new RewriteRuleSubtreeStream(adaptor,"rule notOp");
        try {
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==LPAREN||(LA12_0>=FIX && LA12_0<=QUOTE)) ) {
                alt12=1;
            }
            else if ( (LA12_0==NOT||LA12_0==MINUS) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_primitive_in_term433);
                    primitive16=primitive();

                    state._fsp--;

                    adaptor.addChild(root_0, primitive16.getTree());

                    }
                    break;
                case 2 :
                    {
                    pushFollow(FOLLOW_notOp_in_term439);
                    notOp17=notOp();

                    state._fsp--;

                    stream_notOp.add(notOp17.getTree());
                    pushFollow(FOLLOW_primitive_in_term441);
                    primitive18=primitive();

                    state._fsp--;

                    stream_primitive.add(primitive18.getTree());

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(NEGATION, "NEGATION"), root_1);

                        adaptor.addChild(root_1, stream_primitive.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class primitive_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.primitive_return primitive() throws RecognitionException {
        QueryParser.primitive_return retval = new QueryParser.primitive_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        QueryParser.restriction_return restriction19 = null;

        QueryParser.composite_return composite20 = null;

        try {
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( ((LA13_0>=FIX && LA13_0<=QUOTE)) ) {
                alt13=1;
            }
            else if ( (LA13_0==LPAREN) ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_restriction_in_primitive467);
                    restriction19=restriction();

                    state._fsp--;

                    adaptor.addChild(root_0, restriction19.getTree());

                    }
                    break;
                case 2 :
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_composite_in_primitive473);
                    composite20=composite();

                    state._fsp--;

                    adaptor.addChild(root_0, composite20.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class restriction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.restriction_return restriction() throws RecognitionException {
        QueryParser.restriction_return retval = new QueryParser.restriction_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        QueryParser.comparable_return comparable21 = null;

        QueryParser.comparator_return comparator22 = null;

        QueryParser.arg_return arg23 = null;

        RewriteRuleSubtreeStream stream_comparator=new RewriteRuleSubtreeStream(adaptor,"rule comparator");
        RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");
        RewriteRuleSubtreeStream stream_comparable=new RewriteRuleSubtreeStream(adaptor,"rule comparable");
        try {
            {
            pushFollow(FOLLOW_comparable_in_restriction490);
            comparable21=comparable();

            state._fsp--;

            stream_comparable.add(comparable21.getTree());
            int alt14=2;
            alt14 = dfa14.predict(input);
            switch (alt14) {
                case 1 :
                    {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(HAS, "HAS"), root_1);

                        adaptor.addChild(root_1, (CommonTree)adaptor.create(GLOBAL, "GLOBAL"));
                        adaptor.addChild(root_1, stream_comparable.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    {
                    pushFollow(FOLLOW_comparator_in_restriction525);
                    comparator22=comparator();

                    state._fsp--;

                    stream_comparator.add(comparator22.getTree());
                    pushFollow(FOLLOW_arg_in_restriction527);
                    arg23=arg();

                    state._fsp--;

                    stream_arg.add(arg23.getTree());

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_comparator.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_comparable.nextTree());
                        adaptor.addChild(root_1, stream_arg.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class comparator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.comparator_return comparator() throws RecognitionException {
        QueryParser.comparator_return retval = new QueryParser.comparator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token x=null;
        Token WS24=null;
        Token WS25=null;

        CommonTree x_tree=null;
        CommonTree WS24_tree=null;
        CommonTree WS25_tree=null;
        RewriteRuleTokenStream stream_NE=new RewriteRuleTokenStream(adaptor,"token NE");
        RewriteRuleTokenStream stream_LESSTHAN=new RewriteRuleTokenStream(adaptor,"token LESSTHAN");
        RewriteRuleTokenStream stream_LE=new RewriteRuleTokenStream(adaptor,"token LE");
        RewriteRuleTokenStream stream_HAS=new RewriteRuleTokenStream(adaptor,"token HAS");
        RewriteRuleTokenStream stream_WS=new RewriteRuleTokenStream(adaptor,"token WS");
        RewriteRuleTokenStream stream_EQ=new RewriteRuleTokenStream(adaptor,"token EQ");
        RewriteRuleTokenStream stream_GT=new RewriteRuleTokenStream(adaptor,"token GT");
        RewriteRuleTokenStream stream_GE=new RewriteRuleTokenStream(adaptor,"token GE");

        try {
            {
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==WS) ) {
                    alt15=1;
                }

                switch (alt15) {
            	case 1 :
            	    {
            	    WS24=(Token)match(input,WS,FOLLOW_WS_in_comparator557);
            	    stream_WS.add(WS24);

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            int alt16=7;
            switch ( input.LA(1) ) {
            case LE:
                {
                alt16=1;
                }
                break;
            case LESSTHAN:
                {
                alt16=2;
                }
                break;
            case GE:
                {
                alt16=3;
                }
                break;
            case GT:
                {
                alt16=4;
                }
                break;
            case NE:
                {
                alt16=5;
                }
                break;
            case EQ:
                {
                alt16=6;
                }
                break;
            case HAS:
                {
                alt16=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    {
                    x=(Token)match(input,LE,FOLLOW_LE_in_comparator563);
                    stream_LE.add(x);

                    }
                    break;
                case 2 :
                    {
                    x=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_comparator569);
                    stream_LESSTHAN.add(x);

                    }
                    break;
                case 3 :
                    {
                    x=(Token)match(input,GE,FOLLOW_GE_in_comparator575);
                    stream_GE.add(x);

                    }
                    break;
                case 4 :
                    {
                    x=(Token)match(input,GT,FOLLOW_GT_in_comparator581);
                    stream_GT.add(x);

                    }
                    break;
                case 5 :
                    {
                    x=(Token)match(input,NE,FOLLOW_NE_in_comparator587);
                    stream_NE.add(x);

                    }
                    break;
                case 6 :
                    {
                    x=(Token)match(input,EQ,FOLLOW_EQ_in_comparator593);
                    stream_EQ.add(x);

                    }
                    break;
                case 7 :
                    {
                    x=(Token)match(input,HAS,FOLLOW_HAS_in_comparator599);
                    stream_HAS.add(x);

                    }
                    break;

            }

            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==WS) ) {
                    alt17=1;
                }

                switch (alt17) {
            	case 1 :
            	    {
            	    WS25=(Token)match(input,WS,FOLLOW_WS_in_comparator602);
            	    stream_WS.add(WS25);

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

            retval.tree = root_0;
            RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            {
                adaptor.addChild(root_0, stream_x.nextNode());

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class comparable_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.comparable_return comparable() throws RecognitionException {
        QueryParser.comparable_return retval = new QueryParser.comparable_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        QueryParser.member_return member26 = null;

        QueryParser.function_return function27 = null;

        try {
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( ((LA18_0>=FIX && LA18_0<=REWRITE)||LA18_0==QUOTE) ) {
                alt18=1;
            }
            else if ( (LA18_0==TEXT) ) {
                int LA18_2 = input.LA(2);

                if ( (LA18_2==EOF||(LA18_2>=WS && LA18_2<=HAS)||LA18_2==RPAREN||LA18_2==COMMA) ) {
                    alt18=1;
                }
                else if ( (LA18_2==LPAREN) ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_member_in_comparable624);
                    member26=member();

                    state._fsp--;

                    adaptor.addChild(root_0, member26.getTree());

                    }
                    break;
                case 2 :
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_function_in_comparable630);
                    function27=function();

                    state._fsp--;

                    adaptor.addChild(root_0, function27.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class member_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.member_return member() throws RecognitionException {
        QueryParser.member_return retval = new QueryParser.member_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        QueryParser.item_return item28 = null;

        try {
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_item_in_member645);
            item28=item();

            state._fsp--;

            adaptor.addChild(root_0, item28.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.function_return function() throws RecognitionException {
        QueryParser.function_return retval = new QueryParser.function_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token LPAREN30=null;
        Token RPAREN32=null;
        QueryParser.text_return text29 = null;

        QueryParser.arglist_return arglist31 = null;

        CommonTree LPAREN30_tree=null;
        CommonTree RPAREN32_tree=null;
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleSubtreeStream stream_arglist=new RewriteRuleSubtreeStream(adaptor,"rule arglist");
        RewriteRuleSubtreeStream stream_text=new RewriteRuleSubtreeStream(adaptor,"rule text");
        try {
            {
            pushFollow(FOLLOW_text_in_function662);
            text29=text();

            state._fsp--;

            stream_text.add(text29.getTree());
            LPAREN30=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_function664);
            stream_LPAREN.add(LPAREN30);

            pushFollow(FOLLOW_arglist_in_function666);
            arglist31=arglist();

            state._fsp--;

            stream_arglist.add(arglist31.getTree());
            RPAREN32=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_function668);
            stream_RPAREN.add(RPAREN32);

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            {
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTION, "FUNCTION"), root_1);

                adaptor.addChild(root_1, stream_text.nextTree());
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ARGS, "ARGS"), root_2);

                adaptor.addChild(root_2, stream_arglist.nextTree());

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class arglist_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.arglist_return arglist() throws RecognitionException {
        QueryParser.arglist_return retval = new QueryParser.arglist_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        QueryParser.arg_return arg33 = null;

        QueryParser.sep_return sep34 = null;

        QueryParser.arg_return arg35 = null;

        RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");
        RewriteRuleSubtreeStream stream_sep=new RewriteRuleSubtreeStream(adaptor,"rule sep");
        try {
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==RPAREN) ) {
                alt20=1;
            }
            else if ( (LA20_0==LPAREN||(LA20_0>=FIX && LA20_0<=QUOTE)) ) {
                alt20=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    }
                    break;
                case 2 :
                    {
                    pushFollow(FOLLOW_arg_in_arglist703);
                    arg33=arg();

                    state._fsp--;

                    stream_arg.add(arg33.getTree());
                    loop19:
                    do {
                        int alt19=2;
                        int LA19_0 = input.LA(1);

                        if ( (LA19_0==WS||LA19_0==COMMA) ) {
                            alt19=1;
                        }

                        switch (alt19) {
                    	case 1 :
                    	    {
                    	    pushFollow(FOLLOW_sep_in_arglist706);
                    	    sep34=sep();

                    	    state._fsp--;

                    	    stream_sep.add(sep34.getTree());
                    	    pushFollow(FOLLOW_arg_in_arglist708);
                    	    arg35=arg();

                    	    state._fsp--;

                    	    stream_arg.add(arg35.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop19;
                        }
                    } while (true);

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        while ( stream_arg.hasNext() ) {
                            adaptor.addChild(root_0, stream_arg.nextTree());

                        }
                        stream_arg.reset();

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class arg_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.arg_return arg() throws RecognitionException {
        QueryParser.arg_return retval = new QueryParser.arg_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        QueryParser.comparable_return comparable36 = null;

        QueryParser.composite_return composite37 = null;

        try {
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( ((LA21_0>=FIX && LA21_0<=QUOTE)) ) {
                alt21=1;
            }
            else if ( (LA21_0==LPAREN) ) {
                alt21=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_comparable_in_arg729);
                    comparable36=comparable();

                    state._fsp--;

                    adaptor.addChild(root_0, comparable36.getTree());

                    }
                    break;
                case 2 :
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_composite_in_arg735);
                    composite37=composite();

                    state._fsp--;

                    adaptor.addChild(root_0, composite37.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class andOp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.andOp_return andOp() throws RecognitionException {
        QueryParser.andOp_return retval = new QueryParser.andOp_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token WS38=null;
        Token AND39=null;
        Token WS40=null;

        CommonTree WS38_tree=null;
        CommonTree AND39_tree=null;
        CommonTree WS40_tree=null;

        try {
            {
            root_0 = (CommonTree)adaptor.nil();

            int cnt22=0;
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==WS) ) {
                    alt22=1;
                }

                switch (alt22) {
            	case 1 :
            	    {
            	    WS38=(Token)match(input,WS,FOLLOW_WS_in_andOp749);
            	    WS38_tree = (CommonTree)adaptor.create(WS38);
            	    adaptor.addChild(root_0, WS38_tree);

            	    }
            	    break;

            	default :
            	    if ( cnt22 >= 1 ) break loop22;
                        EarlyExitException eee =
                            new EarlyExitException(22, input);
                        throw eee;
                }
                cnt22++;
            } while (true);

            AND39=(Token)match(input,AND,FOLLOW_AND_in_andOp752);
            AND39_tree = (CommonTree)adaptor.create(AND39);
            adaptor.addChild(root_0, AND39_tree);

            int cnt23=0;
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==WS) ) {
                    alt23=1;
                }

                switch (alt23) {
            	case 1 :
            	    {
            	    WS40=(Token)match(input,WS,FOLLOW_WS_in_andOp754);
            	    WS40_tree = (CommonTree)adaptor.create(WS40);
            	    adaptor.addChild(root_0, WS40_tree);

            	    }
            	    break;

            	default :
            	    if ( cnt23 >= 1 ) break loop23;
                        EarlyExitException eee =
                            new EarlyExitException(23, input);
                        throw eee;
                }
                cnt23++;
            } while (true);

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class orOp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.orOp_return orOp() throws RecognitionException {
        QueryParser.orOp_return retval = new QueryParser.orOp_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token WS41=null;
        Token OR42=null;
        Token WS43=null;

        CommonTree WS41_tree=null;
        CommonTree OR42_tree=null;
        CommonTree WS43_tree=null;

        try {
            {
            root_0 = (CommonTree)adaptor.nil();

            int cnt24=0;
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==WS) ) {
                    alt24=1;
                }

                switch (alt24) {
            	case 1 :
            	    {
            	    WS41=(Token)match(input,WS,FOLLOW_WS_in_orOp769);
            	    WS41_tree = (CommonTree)adaptor.create(WS41);
            	    adaptor.addChild(root_0, WS41_tree);

            	    }
            	    break;

            	default :
            	    if ( cnt24 >= 1 ) break loop24;
                        EarlyExitException eee =
                            new EarlyExitException(24, input);
                        throw eee;
                }
                cnt24++;
            } while (true);

            OR42=(Token)match(input,OR,FOLLOW_OR_in_orOp772);
            OR42_tree = (CommonTree)adaptor.create(OR42);
            adaptor.addChild(root_0, OR42_tree);

            int cnt25=0;
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==WS) ) {
                    alt25=1;
                }

                switch (alt25) {
            	case 1 :
            	    {
            	    WS43=(Token)match(input,WS,FOLLOW_WS_in_orOp774);
            	    WS43_tree = (CommonTree)adaptor.create(WS43);
            	    adaptor.addChild(root_0, WS43_tree);

            	    }
            	    break;

            	default :
            	    if ( cnt25 >= 1 ) break loop25;
                        EarlyExitException eee =
                            new EarlyExitException(25, input);
                        throw eee;
                }
                cnt25++;
            } while (true);

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class notOp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.notOp_return notOp() throws RecognitionException {
        QueryParser.notOp_return retval = new QueryParser.notOp_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal44=null;
        Token NOT45=null;
        Token WS46=null;

        CommonTree char_literal44_tree=null;
        CommonTree NOT45_tree=null;
        CommonTree WS46_tree=null;

        try {
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==MINUS) ) {
                alt27=1;
            }
            else if ( (LA27_0==NOT) ) {
                alt27=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal44=(Token)match(input,MINUS,FOLLOW_MINUS_in_notOp789);
                    char_literal44_tree = (CommonTree)adaptor.create(char_literal44);
                    adaptor.addChild(root_0, char_literal44_tree);

                    }
                    break;
                case 2 :
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    NOT45=(Token)match(input,NOT,FOLLOW_NOT_in_notOp795);
                    NOT45_tree = (CommonTree)adaptor.create(NOT45);
                    adaptor.addChild(root_0, NOT45_tree);

                    int cnt26=0;
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( (LA26_0==WS) ) {
                            alt26=1;
                        }

                        switch (alt26) {
                    	case 1 :
                    	    {
                    	    WS46=(Token)match(input,WS,FOLLOW_WS_in_notOp797);
                    	    WS46_tree = (CommonTree)adaptor.create(WS46);
                    	    adaptor.addChild(root_0, WS46_tree);

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt26 >= 1 ) break loop26;
                                EarlyExitException eee =
                                    new EarlyExitException(26, input);
                                throw eee;
                        }
                        cnt26++;
                    } while (true);

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class sep_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.sep_return sep() throws RecognitionException {
        QueryParser.sep_return retval = new QueryParser.sep_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token WS47=null;
        Token COMMA48=null;
        Token WS49=null;

        CommonTree WS47_tree=null;
        CommonTree COMMA48_tree=null;
        CommonTree WS49_tree=null;

        try {
            {
            root_0 = (CommonTree)adaptor.nil();

            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==WS) ) {
                    alt28=1;
                }

                switch (alt28) {
            	case 1 :
            	    {
            	    WS47=(Token)match(input,WS,FOLLOW_WS_in_sep812);
            	    WS47_tree = (CommonTree)adaptor.create(WS47);
            	    adaptor.addChild(root_0, WS47_tree);

            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);

            COMMA48=(Token)match(input,COMMA,FOLLOW_COMMA_in_sep815);
            COMMA48_tree = (CommonTree)adaptor.create(COMMA48);
            adaptor.addChild(root_0, COMMA48_tree);

            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==WS) ) {
                    alt29=1;
                }

                switch (alt29) {
            	case 1 :
            	    {
            	    WS49=(Token)match(input,WS,FOLLOW_WS_in_sep817);
            	    WS49_tree = (CommonTree)adaptor.create(WS49);
            	    adaptor.addChild(root_0, WS49_tree);

            	    }
            	    break;

            	default :
            	    break loop29;
                }
            } while (true);

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class composite_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.composite_return composite() throws RecognitionException {
        QueryParser.composite_return retval = new QueryParser.composite_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token LPAREN50=null;
        Token WS51=null;
        Token WS53=null;
        Token RPAREN54=null;
        QueryParser.expression_return expression52 = null;

        CommonTree LPAREN50_tree=null;
        CommonTree WS51_tree=null;
        CommonTree WS53_tree=null;
        CommonTree RPAREN54_tree=null;
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_WS=new RewriteRuleTokenStream(adaptor,"token WS");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            {
            LPAREN50=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_composite833);
            stream_LPAREN.add(LPAREN50);

            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==WS) ) {
                    alt30=1;
                }

                switch (alt30) {
            	case 1 :
            	    {
            	    WS51=(Token)match(input,WS,FOLLOW_WS_in_composite835);
            	    stream_WS.add(WS51);

            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);

            pushFollow(FOLLOW_expression_in_composite838);
            expression52=expression();

            state._fsp--;

            stream_expression.add(expression52.getTree());
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( (LA31_0==WS) ) {
                    alt31=1;
                }

                switch (alt31) {
            	case 1 :
            	    {
            	    WS53=(Token)match(input,WS,FOLLOW_WS_in_composite840);
            	    stream_WS.add(WS53);

            	    }
            	    break;

            	default :
            	    break loop31;
                }
            } while (true);

            RPAREN54=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_composite843);
            stream_RPAREN.add(RPAREN54);

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            {
                adaptor.addChild(root_0, stream_expression.nextTree());

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class item_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.item_return item() throws RecognitionException {
        QueryParser.item_return retval = new QueryParser.item_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token FIX55=null;
        Token REWRITE57=null;
        QueryParser.value_return value56 = null;

        QueryParser.value_return value58 = null;

        QueryParser.value_return value59 = null;

        CommonTree FIX55_tree=null;
        CommonTree REWRITE57_tree=null;
        RewriteRuleTokenStream stream_REWRITE=new RewriteRuleTokenStream(adaptor,"token REWRITE");
        RewriteRuleTokenStream stream_FIX=new RewriteRuleTokenStream(adaptor,"token FIX");
        RewriteRuleSubtreeStream stream_value=new RewriteRuleSubtreeStream(adaptor,"rule value");
        try {
            int alt32=3;
            switch ( input.LA(1) ) {
            case FIX:
                {
                alt32=1;
                }
                break;
            case REWRITE:
                {
                alt32=2;
                }
                break;
            case TEXT:
            case QUOTE:
                {
                alt32=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    {
                    FIX55=(Token)match(input,FIX,FOLLOW_FIX_in_item863);
                    stream_FIX.add(FIX55);

                    pushFollow(FOLLOW_value_in_item865);
                    value56=value();

                    state._fsp--;

                    stream_value.add(value56.getTree());

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LITERAL, "LITERAL"), root_1);

                        adaptor.addChild(root_1, stream_value.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    {
                    REWRITE57=(Token)match(input,REWRITE,FOLLOW_REWRITE_in_item879);
                    stream_REWRITE.add(REWRITE57);

                    pushFollow(FOLLOW_value_in_item881);
                    value58=value();

                    state._fsp--;

                    stream_value.add(value58.getTree());

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUZZY, "FUZZY"), root_1);

                        adaptor.addChild(root_1, stream_value.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 3 :
                    {
                    pushFollow(FOLLOW_value_in_item895);
                    value59=value();

                    state._fsp--;

                    stream_value.add(value59.getTree());

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        adaptor.addChild(root_0, stream_value.nextTree());

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class value_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.value_return value() throws RecognitionException {
        QueryParser.value_return retval = new QueryParser.value_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        QueryParser.text_return text60 = null;

        QueryParser.phrase_return phrase61 = null;

        RewriteRuleSubtreeStream stream_phrase=new RewriteRuleSubtreeStream(adaptor,"rule phrase");
        RewriteRuleSubtreeStream stream_text=new RewriteRuleSubtreeStream(adaptor,"rule text");
        try {
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==TEXT) ) {
                alt33=1;
            }
            else if ( (LA33_0==QUOTE) ) {
                alt33=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    {
                    pushFollow(FOLLOW_text_in_value913);
                    text60=text();

                    state._fsp--;

                    stream_text.add(text60.getTree());

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(VALUE, "VALUE"), root_1);

                        adaptor.addChild(root_1, (CommonTree)adaptor.create(TEXT, "TEXT"));
                        adaptor.addChild(root_1, stream_text.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    {
                    pushFollow(FOLLOW_phrase_in_value929);
                    phrase61=phrase();

                    state._fsp--;

                    stream_phrase.add(phrase61.getTree());

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    {
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(VALUE, "VALUE"), root_1);

                        adaptor.addChild(root_1, (CommonTree)adaptor.create(STRING, "STRING"));
                        adaptor.addChild(root_1, stream_phrase.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class text_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.text_return text() throws RecognitionException {
        QueryParser.text_return retval = new QueryParser.text_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token TEXT62=null;

        CommonTree TEXT62_tree=null;

        try {
            {
            root_0 = (CommonTree)adaptor.nil();

            TEXT62=(Token)match(input,TEXT,FOLLOW_TEXT_in_text953);
            TEXT62_tree = (CommonTree)adaptor.create(TEXT62);
            adaptor.addChild(root_0, TEXT62_tree);

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    public static class phrase_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    public final QueryParser.phrase_return phrase() throws RecognitionException {
        QueryParser.phrase_return retval = new QueryParser.phrase_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token QUOTE63=null;
        Token set64=null;
        Token QUOTE65=null;

        CommonTree QUOTE63_tree=null;
        CommonTree set64_tree=null;
        CommonTree QUOTE65_tree=null;

        try {
            {
            root_0 = (CommonTree)adaptor.nil();

            QUOTE63=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_phrase967);
            QUOTE63_tree = (CommonTree)adaptor.create(QUOTE63);
            adaptor.addChild(root_0, QUOTE63_tree);

            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( ((LA34_0>=ARGS && LA34_0<=TEXT)||(LA34_0>=UNICODE_ESC && LA34_0<=EXCLAMATION)) ) {
                    alt34=1;
                }

                switch (alt34) {
            	case 1 :
            	    {
            	    set64=(Token)input.LT(1);
            	    if ( (input.LA(1)>=ARGS && input.LA(1)<=TEXT)||(input.LA(1)>=UNICODE_ESC && input.LA(1)<=EXCLAMATION) ) {
            	        input.consume();
            	        adaptor.addChild(root_0, (CommonTree)adaptor.create(set64));
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);

            QUOTE65=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_phrase973);
            QUOTE65_tree = (CommonTree)adaptor.create(QUOTE65);
            adaptor.addChild(root_0, QUOTE65_tree);

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

          catch (RecognitionException e) {
            reportError(e);
            throw e;
          }
        finally {
        }
        return retval;
    }

    protected DFA4 dfa4 = new DFA4(this);
    protected DFA6 dfa6 = new DFA6(this);
    protected DFA5 dfa5 = new DFA5(this);
    protected DFA9 dfa9 = new DFA9(this);
    protected DFA8 dfa8 = new DFA8(this);
    protected DFA11 dfa11 = new DFA11(this);
    protected DFA10 dfa10 = new DFA10(this);
    protected DFA14 dfa14 = new DFA14(this);
    static final String DFA4_eotS =
        "\4\uffff";
    static final String DFA4_eofS =
        "\2\2\2\uffff";
    static final String DFA4_minS =
        "\2\20\2\uffff";
    static final String DFA4_maxS =
        "\2\46\2\uffff";
    static final String DFA4_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA4_specialS =
        "\4\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\1\7\uffff\1\3\3\uffff\1\3\1\uffff\4\3\4\uffff\1\3",
            "\1\1\7\uffff\1\3\3\uffff\1\3\1\uffff\4\3\4\uffff\1\3",
            "",
            ""
    };

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "68:1: query : ( ( WS )* EOF -> ^( EMPTY ) | ( WS )* expression ( WS )* EOF -> expression );";
        }
    }
    static final String DFA6_eotS =
        "\4\uffff";
    static final String DFA6_eofS =
        "\2\2\2\uffff";
    static final String DFA6_minS =
        "\2\20\2\uffff";
    static final String DFA6_maxS =
        "\1\31\1\32\2\uffff";
    static final String DFA6_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA6_specialS =
        "\4\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\1\10\uffff\1\2",
            "\1\1\10\uffff\1\2\1\3",
            "",
            ""
    };

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "76:16: ( -> sequence | ( andOp sequence )+ -> ^( CONJUNCTION ( sequence )+ ) )";
        }
    }
    static final String DFA5_eotS =
        "\4\uffff";
    static final String DFA5_eofS =
        "\2\2\2\uffff";
    static final String DFA5_minS =
        "\2\20\2\uffff";
    static final String DFA5_maxS =
        "\1\31\1\32\2\uffff";
    static final String DFA5_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA5_specialS =
        "\4\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\1\10\uffff\1\2",
            "\1\1\10\uffff\1\2\1\3",
            "",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "()+ loopback of 78:11: ( andOp sequence )+";
        }
    }
    static final String DFA9_eotS =
        "\4\uffff";
    static final String DFA9_eofS =
        "\2\2\2\uffff";
    static final String DFA9_minS =
        "\2\20\2\uffff";
    static final String DFA9_maxS =
        "\1\31\1\46\2\uffff";
    static final String DFA9_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA9_specialS =
        "\4\uffff}>";
    static final String[] DFA9_transitionS = {
            "\1\1\10\uffff\1\2",
            "\1\1\7\uffff\1\3\2\2\1\uffff\1\3\1\uffff\4\3\4\uffff\1\3",
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
            return "85:14: ( -> factor | ( ( WS )+ factor )+ -> ^( SEQUENCE ( factor )+ ) )";
        }
    }
    static final String DFA8_eotS =
        "\4\uffff";
    static final String DFA8_eofS =
        "\2\2\2\uffff";
    static final String DFA8_minS =
        "\2\20\2\uffff";
    static final String DFA8_maxS =
        "\1\31\1\46\2\uffff";
    static final String DFA8_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA8_specialS =
        "\4\uffff}>";
    static final String[] DFA8_transitionS = {
            "\1\1\10\uffff\1\2",
            "\1\1\7\uffff\1\3\2\2\1\uffff\1\3\1\uffff\4\3\4\uffff\1\3",
            "",
            ""
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "()+ loopback of 87:11: ( ( WS )+ factor )+";
        }
    }
    static final String DFA11_eotS =
        "\4\uffff";
    static final String DFA11_eofS =
        "\2\2\2\uffff";
    static final String DFA11_minS =
        "\2\20\2\uffff";
    static final String DFA11_maxS =
        "\1\31\1\46\2\uffff";
    static final String DFA11_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA11_specialS =
        "\4\uffff}>";
    static final String[] DFA11_transitionS = {
            "\1\1\10\uffff\1\2",
            "\1\1\7\uffff\3\2\1\3\1\2\1\uffff\4\2\4\uffff\1\2",
            "",
            ""
    };

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "94:12: ( -> term | ( orOp term )+ -> ^( DISJUNCTION ( term )+ ) )";
        }
    }
    static final String DFA10_eotS =
        "\4\uffff";
    static final String DFA10_eofS =
        "\2\2\2\uffff";
    static final String DFA10_minS =
        "\2\20\2\uffff";
    static final String DFA10_maxS =
        "\1\31\1\46\2\uffff";
    static final String DFA10_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA10_specialS =
        "\4\uffff}>";
    static final String[] DFA10_transitionS = {
            "\1\1\10\uffff\1\2",
            "\1\1\7\uffff\3\2\1\3\1\2\1\uffff\4\2\4\uffff\1\2",
            "",
            ""
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
            return "()+ loopback of 96:11: ( orOp term )+";
        }
    }
    static final String DFA14_eotS =
        "\4\uffff";
    static final String DFA14_eofS =
        "\2\2\2\uffff";
    static final String DFA14_minS =
        "\2\20\2\uffff";
    static final String DFA14_maxS =
        "\1\31\1\46\2\uffff";
    static final String DFA14_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA14_specialS =
        "\4\uffff}>";
    static final String[] DFA14_transitionS = {
            "\1\1\7\3\1\uffff\1\2",
            "\1\1\7\3\5\2\1\uffff\4\2\4\uffff\1\2",
            "",
            ""
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        public String getDescription() {
            return "120:16: ( -> ^( HAS GLOBAL comparable ) | comparator arg -> ^( comparator comparable arg ) )";
        }
    }

    public static final BitSet FOLLOW_WS_in_query145 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_EOF_in_query148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WS_in_query177 = new BitSet(new long[]{0x00000043D1010000L});
    public static final BitSet FOLLOW_expression_in_query180 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_WS_in_query182 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_EOF_in_query185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_expression208 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_andOp_in_expression245 = new BitSet(new long[]{0x00000043D1000000L});
    public static final BitSet FOLLOW_sequence_in_expression247 = new BitSet(new long[]{0x00000043D1010002L});
    public static final BitSet FOLLOW_factor_in_sequence285 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_WS_in_sequence321 = new BitSet(new long[]{0x00000043D1010000L});
    public static final BitSet FOLLOW_factor_in_sequence324 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_term_in_factor365 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_orOp_in_factor397 = new BitSet(new long[]{0x00000043D1000000L});
    public static final BitSet FOLLOW_term_in_factor399 = new BitSet(new long[]{0x00000043D1010002L});
    public static final BitSet FOLLOW_primitive_in_term433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_notOp_in_term439 = new BitSet(new long[]{0x00000003C1000000L});
    public static final BitSet FOLLOW_primitive_in_term441 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_restriction_in_primitive467 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_composite_in_primitive473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparable_in_restriction490 = new BitSet(new long[]{0x0000000000FF0002L});
    public static final BitSet FOLLOW_comparator_in_restriction525 = new BitSet(new long[]{0x00000003C1000000L});
    public static final BitSet FOLLOW_arg_in_restriction527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WS_in_comparator557 = new BitSet(new long[]{0x0000000000FF0000L});
    public static final BitSet FOLLOW_LE_in_comparator563 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_LESSTHAN_in_comparator569 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_GE_in_comparator575 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_GT_in_comparator581 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_NE_in_comparator587 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_EQ_in_comparator593 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_HAS_in_comparator599 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_WS_in_comparator602 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_member_in_comparable624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_comparable630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_item_in_member645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_text_in_function662 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_LPAREN_in_function664 = new BitSet(new long[]{0x00000003C3000000L});
    public static final BitSet FOLLOW_arglist_in_function666 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_RPAREN_in_function668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arg_in_arglist703 = new BitSet(new long[]{0x0000000020010002L});
    public static final BitSet FOLLOW_sep_in_arglist706 = new BitSet(new long[]{0x00000003C1000000L});
    public static final BitSet FOLLOW_arg_in_arglist708 = new BitSet(new long[]{0x0000000020010002L});
    public static final BitSet FOLLOW_comparable_in_arg729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_composite_in_arg735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WS_in_andOp749 = new BitSet(new long[]{0x0000000004010000L});
    public static final BitSet FOLLOW_AND_in_andOp752 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_WS_in_andOp754 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_WS_in_orOp769 = new BitSet(new long[]{0x0000000008010000L});
    public static final BitSet FOLLOW_OR_in_orOp772 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_WS_in_orOp774 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_MINUS_in_notOp789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notOp795 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_WS_in_notOp797 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_WS_in_sep812 = new BitSet(new long[]{0x0000000020010000L});
    public static final BitSet FOLLOW_COMMA_in_sep815 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_WS_in_sep817 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_LPAREN_in_composite833 = new BitSet(new long[]{0x00000043D1010000L});
    public static final BitSet FOLLOW_WS_in_composite835 = new BitSet(new long[]{0x00000043D1010000L});
    public static final BitSet FOLLOW_expression_in_composite838 = new BitSet(new long[]{0x0000000002010000L});
    public static final BitSet FOLLOW_WS_in_composite840 = new BitSet(new long[]{0x0000000002010000L});
    public static final BitSet FOLLOW_RPAREN_in_composite843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FIX_in_item863 = new BitSet(new long[]{0x00000003C0000000L});
    public static final BitSet FOLLOW_value_in_item865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REWRITE_in_item879 = new BitSet(new long[]{0x00000003C0000000L});
    public static final BitSet FOLLOW_value_in_item881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_value_in_item895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_text_in_value913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_phrase_in_value929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEXT_in_text953 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUOTE_in_phrase967 = new BitSet(new long[]{0x00007FFFFFFFFFF0L});
    public static final BitSet FOLLOW_set_in_phrase969 = new BitSet(new long[]{0x00007FFFFFFFFFF0L});
    public static final BitSet FOLLOW_QUOTE_in_phrase973 = new BitSet(new long[]{0x0000000000000002L});

}
