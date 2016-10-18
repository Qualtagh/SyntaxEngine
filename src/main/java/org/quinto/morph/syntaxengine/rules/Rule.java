package org.quinto.morph.syntaxengine.rules;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.quinto.morph.syntaxengine.ParseException;
import org.quinto.morph.syntaxengine.ParseResult;
import org.quinto.morph.syntaxengine.Parser;
import org.quinto.morph.syntaxengine.Scope;

public class Rule {
  private static final AtomicInteger IDS = new AtomicInteger();
  public static final String ROOT = "root";
  public Function< Scope, ParseResult > action;
  public final Parser parser;
  public final int id;
  
  public Rule( Parser parser ) {
    this.parser = parser;
    id = IDS.getAndIncrement();
  }
  
  public ParseResult failure( Scope scope, String message ) {
    return new ParseResult( new ParseException( this, scope, message ) );
  }
  
  public ParseResult failure( Scope scope, String message, Exception cause ) {
    ParseException e = new ParseException( this, scope, message );
    e.addSuppressed( cause );
    return new ParseResult( e );
  }
  
  public ParseResult apply( Scope scope ) {
    ParseResult res = scope.context.getFromCache( scope, this );
    if ( res != null )
      return res;
    if ( scope.context.isOnStack( scope, this ) )
      return failure( scope, "Already on stack" );
    scope.context.putOnStack( scope, this );
    try {
      return res = action.apply( scope );
    } finally {
      scope.context.removeFromStack( scope, this );
      scope.context.putToCache( scope, this, res );
    }
  }
}