package org.quinto.morph.syntaxengine.rules;

import java.util.function.Predicate;
import org.quinto.morph.syntaxengine.ParseResult;
import org.quinto.morph.syntaxengine.Parser;
import org.quinto.morph.syntaxengine.TreeNode;
import org.quinto.morph.syntaxengine.util.Variants;

public class MatchRule< T > extends Rule {
  public MatchRule( Parser parser, Predicate< T > pattern ) {
    super( parser );
    action = scope -> {
      if ( scope.from != scope.to - 1 )
        return failure( scope, "Only one token allowed" );
      T token = ( T )scope.context.input.get( scope.from );
      try {
        if ( !pattern.test( token ) )
          return failure( scope, "Token " + token + " doesn't match pattern " + pattern );
      } catch ( ClassCastException e ) {
        return failure( scope, "Token " + token + " of wrong type", e );
      }
      return new ParseResult( new Variants<>( new TreeNode( token ) ) );
    };
  }
}