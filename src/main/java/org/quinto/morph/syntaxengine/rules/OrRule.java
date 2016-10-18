package org.quinto.morph.syntaxengine.rules;

import org.quinto.morph.syntaxengine.ParseResult;
import org.quinto.morph.syntaxengine.Parser;
import org.quinto.morph.syntaxengine.TreeNode;
import org.quinto.morph.syntaxengine.util.Variants;

public class OrRule extends Rule {
  public OrRule( Parser parser, Rule... rules ) {
    super( parser );
    action = scope -> {
      if ( rules == null || rules.length == 0 ) {
        if ( scope.from == scope.to )
          return new ParseResult( new Variants<>( new TreeNode() ) );
        return failure( scope, "Empty sequence, non-empty input" );
      }
      ParseResult lastFailure = null;
      Variants< TreeNode > success = new Variants<>();
      for ( Rule rule : rules ) {
        ParseResult res = rule.apply( scope );
        if ( res.isFailed() )
          lastFailure = res;
        else
          success.addAll( res.success );
      }
      return success.isEmpty() ? lastFailure : new ParseResult( success );
    };
  }
}