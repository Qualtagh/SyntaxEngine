package org.quinto.morph.syntaxengine.rules;

import org.quinto.morph.syntaxengine.ParseResult;
import org.quinto.morph.syntaxengine.Parser;
import org.quinto.morph.syntaxengine.Scope;
import org.quinto.morph.syntaxengine.TreeNode;
import org.quinto.morph.syntaxengine.util.Sequence;
import org.quinto.morph.syntaxengine.util.Variants;

public class SeqRule extends Rule {
  public SeqRule( Parser parser, Rule... rules ) {
    super( parser );
    action = scope -> {
      if ( rules == null || rules.length == 0 ) {
        if ( scope.from == scope.to )
          return new ParseResult( new Variants<>( new TreeNode() ) );
        return failure( scope, "Empty sequence, non-empty input" );
      }
      return match( scope, scope.from, rules, 0, new int[ rules.length - 1 ] );
    };
  }
  
  private ParseResult match( Scope scope, int fromScope, Rule rules[], int fromRule, int delimiters[] ) {
    Variants< TreeNode > success = new Variants<>();
    ParseResult lastFailure = null;
    if ( fromRule + 1 < rules.length ) {
      for ( int from = fromScope; from <= scope.to; from++ ) {
        delimiters[ fromRule ] = from;
        ParseResult res = match( scope, from, rules, fromRule + 1, delimiters );
        if ( res.isFailed() )
          lastFailure = res;
        else
          success.addAll( res.success );
      }
    } else {
      Sequence< Variants< TreeNode > > seq = new Sequence<>();
      for ( int i = 0; i < rules.length; i++ ) {
        ParseResult res = rules[ i ].apply( new Scope( scope.context, i == 0 ? scope.from : delimiters[ i - 1 ], i == rules.length - 1 ? scope.to : delimiters[ i ] ) );
        if ( res.isFailed() )
          return res;
        seq.add( res.success );
      }
      Variants< Sequence< TreeNode > > variants = Sequence.toVariantsOfSequences( seq );
      success.addAll( TreeNode.toVariantsOfTreeNodes( variants, true ) );
    }
    return success.isEmpty() ? lastFailure : new ParseResult( success );
  }
}