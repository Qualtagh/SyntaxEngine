package org.quinto.morph.syntaxengine.rules;

import java.util.Collections;
import org.quinto.morph.syntaxengine.ParseResult;
import org.quinto.morph.syntaxengine.Parser;
import org.quinto.morph.syntaxengine.Scope;
import org.quinto.morph.syntaxengine.TreeNode;
import org.quinto.morph.syntaxengine.util.Sequence;
import org.quinto.morph.syntaxengine.util.Variants;

public class RepRule extends Rule {
  private static final int MAX_REPETITIONS = 64;
  
  public RepRule( Parser parser, Rule rule ) {
    this( parser, 0, MAX_REPETITIONS, rule );
  }
  
  public RepRule( Parser parser, int repFrom, Rule rule ) {
    this( parser, repFrom, MAX_REPETITIONS, rule );
  }
  
  public RepRule( Parser parser, int repFrom, int repTo, Rule rule ) {
    super( parser );
    final int repsFrom = repFrom < 0 ? 0 : repFrom;
    final int repsTo = repTo > MAX_REPETITIONS ? MAX_REPETITIONS : repTo;
    if ( repsFrom > repsTo )
      throw new IllegalArgumentException( "Number of repetitions is out of bounds: " + repsFrom + " > " + repsTo );
    action = scope -> {
      int repetitionsFrom = repsFrom;
      if ( repsFrom == 0 ) {
        if ( scope.from == scope.to )
          return new ParseResult( new Variants<>( new TreeNode() ) );
        if ( repsTo == 0 )
          return failure( scope, "Empty sequence, non-empty input" );
        repetitionsFrom = 1;
      } else if ( scope.from == scope.to ) {
        ParseResult res = rule.apply( new Scope( scope.context, scope.from, scope.from ) );
        if ( res.isFailed() )
          return res;
        Variants< Sequence< TreeNode > > vars = Sequence.toVariantsOfSequences( new Sequence<>( Collections.nCopies( repetitionsFrom, res.success ) ) );
        return new ParseResult( TreeNode.toVariantsOfTreeNodes( vars, repsTo > 1 ) );
      }
      int size = scope.to - scope.from + 1;
      Boolean visited[][] = new Boolean[ size ][ size ];
      return findPath( scope, size - 1, repetitionsFrom, repsTo, rule, visited, new Sequence<>(), new Sequence<>() );
    };
  }
  
  private ParseResult findPath( Scope scope, int pathTo, int minRep, int maxRep, Rule rule, Boolean visited[][], Sequence< Variants< TreeNode > > path, Sequence< Integer > pathDelimiters ) {
    if ( maxRep <= 0 )
      return failure( scope, "Max number of repetitions reached" );
    ParseResult lastFailure = null;
    Variants< TreeNode > success = new Variants<>();
    Boolean vis[] = visited[ pathTo ];
    for ( int pathFrom = 0; pathFrom < pathTo; pathFrom++ ) {
      Boolean v = vis[ pathFrom ];
      if ( v == null || v ) {
        ParseResult res = rule.apply( new Scope( scope.context, scope.from + pathFrom, scope.from + pathTo ) );
        if ( vis[ pathFrom ] = !res.isFailed() ) {
          if ( pathFrom > 0 ) {
            res = findPath( scope, pathFrom, minRep, maxRep - 1, rule, visited, new Sequence<>( path ).with( res.success ), new Sequence<>( pathDelimiters ).with( pathFrom ) );
            if ( !res.isFailed() )
              success.addAll( res.success );
          } else {
            path.add( res.success );
            path.reverse();
            if ( path.size() < minRep ) {
              // Try zero-length items.
              pathDelimiters.add( 0 );
              pathDelimiters.reverse();
              pathDelimiters.add( visited.length - 1 );
              res = null;
              for ( int i = pathDelimiters.size() - 1; i >= 0; i-- ) {
                int delimiter = pathDelimiters.get( i );
                res = rule.apply( new Scope( scope.context, scope.from + delimiter, scope.from + delimiter ) );
                if ( res.isFailed() )
                  res = null;
                else {
                  path.addAll( i, Collections.nCopies( minRep - path.size(), res.success ) );
                  break;
                }
              }
              if ( res == null )
                return failure( scope, "Path found with " + path.size() + " repetitions, minimum: " + minRep );
            }
            Variants< Sequence< TreeNode > > vars = Sequence.toVariantsOfSequences( path );
            success.addAll( TreeNode.toVariantsOfTreeNodes( vars, maxRep + path.size() > 2 ) );
          }
        }
        if ( res.isFailed() )
          lastFailure = res;
      }
    }
    return success.isEmpty() ? lastFailure == null ? failure( scope, "No path found" ) : lastFailure : new ParseResult( success );
  }
}