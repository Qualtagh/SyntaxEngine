package org.quinto.morph.syntaxengine.rules;

import java.util.function.Predicate;
import org.quinto.morph.syntaxengine.ParseResult;
import org.quinto.morph.syntaxengine.Parser;
import org.quinto.morph.syntaxengine.TreeNode;
import org.quinto.morph.syntaxengine.util.Variants;

public class FilterRule extends Rule {
  public FilterRule( Parser parser, Rule rule, Predicate< TreeNode > condition ) {
    super( parser, false );
    action = scope -> {
      ParseResult res = rule.apply( scope );
      if ( res.isFailed() )
        return res;
      Variants< TreeNode > success = new Variants<>();
      for ( TreeNode node : res.success )
        if ( condition.test( node ) )
          success.add( node );
      return success.isEmpty() ? failure( scope, "No results meet the condition" ) : new ParseResult( success );
    };
  }
}