package za.org.grassroot.language.grammar;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.Tree;
import org.junit.Assert;
import za.org.grassroot.language.ANTLRNoCaseInputStream;
import za.org.grassroot.language.generated.DateLexer;
import za.org.grassroot.language.generated.DateParser;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;

public abstract class AbstractGrammarTest {
  protected String _ruleName;
  
  /**
   * 
   * @param input
   * @param ast
   */
  protected void assertAST(String input, String ast) throws Exception {
    Assert.assertEquals(ast, buildAST(input));
  }
  
  /**
   * 
   * @param value
   * @return
   * @throws Exception
   */
  protected String buildAST(String value) throws Exception {
    DateParser parser = buildParser(value);
    Class<?> klass = Class.forName("za.org.grassroot.language.generated.DateParser");
    Method meth = klass.getMethod(_ruleName, (Class<?>[]) null);
    ParserRuleContext ret = (ParserRuleContext) meth.invoke(parser, (Object[]) null);
    
    Tree tree = ret.getPayload();
    return tree.toStringTree();
  }
  
  /**
   * 
   * @param value
   * @return
   */
  private DateParser buildParser(String value) throws Exception {
    // lex
    ANTLRNoCaseInputStream input = new ANTLRNoCaseInputStream(new ByteArrayInputStream(value.getBytes()));
    DateLexer lexer = new DateLexer(input);
    TokenStream tokens = new CommonTokenStream(lexer);
      
    // parse
    // ParseListener listener = new ParseListener();
    return new DateParser(tokens);
  }
}
