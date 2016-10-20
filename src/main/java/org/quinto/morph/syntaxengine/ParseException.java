package org.quinto.morph.syntaxengine;

import org.quinto.morph.syntaxengine.rules.Rule;

public class ParseException extends Exception {
  public Rule rule;
  public Scope scope;
  
  public ParseException( String message ) {
    super( message );
  }

  public ParseException( Rule rule, Scope scope, String message ) {
    super( message );
    this.rule = rule;
    this.scope = scope;
  }

  public boolean isAlreadyOnStackFailure() {
    return "Already on stack".equals( getMessage() );
  }
}