package org.quinto.morph.syntaxengine;

import org.quinto.morph.syntaxengine.util.Variants;

public class ParseResult {
  public Variants< TreeNode > success;
  public ParseException failure;

  public ParseResult( Variants< TreeNode > success ) {
    this.success = success;
  }

  public ParseResult( ParseException failure ) {
    this.failure = failure;
  }

  public boolean isFailed() {
    return success == null || success.isEmpty();
  }
}