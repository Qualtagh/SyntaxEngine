package org.quinto.morph.syntaxengine.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.quinto.morph.syntaxengine.ParseResult;
import org.quinto.morph.syntaxengine.Parser;
import org.quinto.morph.syntaxengine.TreeNode;
import org.quinto.morph.syntaxengine.util.Permutations;
import org.quinto.morph.syntaxengine.util.Variants;

public class UnorderedSeqRule extends Rule {
  public UnorderedSeqRule( Parser parser, Rule... rules ) {
    super( parser );
    action = scope -> {
      if ( rules == null || rules.length <= 1 )
        return new SeqRule( parser, rules ).apply( scope );
      ParseResult lastFailure = null;
      Variants< TreeNode > success = new Variants<>();
      for ( Integer rulesIndexes[] : Permutations.from( IntStream.range( 0, rules.length ).boxed().toArray( Integer[]::new ) ) ) {
        Rule rs[] = new Rule[ rules.length ];
        for ( int i = 0; i < rs.length; i++ )
          rs[ i ] = rules[ rulesIndexes[ i ] ];
        ParseResult res = new SeqRule( parser, rs ).apply( scope );
        if ( res.isFailed() )
          lastFailure = res;
        else {
          int reverseIndex[] = new int[ rulesIndexes.length ];
          for ( int i = 0; i < rulesIndexes.length; i++ )
            reverseIndex[ rulesIndexes[ i ] ] = i;
          for ( TreeNode node : res.success ) {
            List< TreeNode > newChildren = new ArrayList<>();
            for ( int i = 0; i < reverseIndex.length; i++ )
              newChildren.add( node.children.get( reverseIndex[ i ] ) );
            node.children = newChildren;
          }
          success.addAll( res.success );
        }
      }
      return success.isEmpty() ? lastFailure : new ParseResult( success );
    };
  }
}