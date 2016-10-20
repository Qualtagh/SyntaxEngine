package org.quinto.morph.syntaxengine.rules;

import java.util.function.Predicate;
import org.quinto.morph.syntaxengine.ParseResult;
import org.quinto.morph.syntaxengine.Parser;
import org.quinto.morph.syntaxengine.TreeNode;
import org.quinto.morph.syntaxengine.util.Variants;

public class MatchRule< T > extends Rule {
  public MatchRule( Parser parser, Predicate< T > pattern ) {
    super( parser, false );
    action = scope -> {
      if ( scope.from != scope.to - 1 )
        return failure( scope, "Only one token allowed" );
      Object token = scope.context.input.get( scope.from );
      Variants< T > vars;
      if ( token instanceof Variants )
        vars = ( Variants< T > )token;
      else
        vars = new Variants<>( ( T )token );
      Variants< TreeNode > success = new Variants<>();
      ParseResult lastFailure = null;
      for ( T t : vars ) {
        try {
          if ( pattern.test( t ) )
            success.add( t instanceof TreeNode ? ( TreeNode )t : new TreeNode( t ) );
          else if ( lastFailure == null )
            lastFailure = failure( scope, "Token " + t + " doesn't match pattern " + pattern );
        } catch ( ClassCastException e ) {
          if ( lastFailure == null )
            lastFailure = failure( scope, "Token " + t + " of wrong type", e );
        }
      }
      return success.isEmpty() ? lastFailure == null ? failure( scope, "Empty variants set" ) : lastFailure : new ParseResult( success );
    };
  }
}