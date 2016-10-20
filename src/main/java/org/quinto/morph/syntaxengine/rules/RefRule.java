package org.quinto.morph.syntaxengine.rules;

import org.quinto.morph.syntaxengine.Parser;

public class RefRule extends Rule {
  public final String name;
  public Rule delegate;
  
  public RefRule( Parser parser, String name ) {
    super( parser, false );
    this.name = name;
    action = scope -> delegate.apply( scope );
  }

  @Override
  public String toString() {
    return "RefRule " + name;
  }
}