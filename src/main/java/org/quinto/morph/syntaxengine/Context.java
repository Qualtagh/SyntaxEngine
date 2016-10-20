package org.quinto.morph.syntaxengine;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.quinto.morph.syntaxengine.rules.Rule;
import org.quinto.morph.syntaxengine.util.Sequence;

public class Context {
  public Sequence< ? > input;
  public final Map< Triple< Integer, Integer, Integer >, Pair< Rule, Scope > > stack = new LinkedHashMap<>();
  public final Map< Triple< Integer, Integer, Integer >, ParseResult > resultsCache = new HashMap<>();

  public Context( Sequence< ? > input ) {
    this.input = input;
  }

  public boolean isOnStack( Scope scope, Rule rule ) {
    Triple< Integer, Integer, Integer > key = Triple.of( rule.id, scope.from, scope.to );
    return stack.containsKey( key );
  }

  public void putOnStack( Scope scope, Rule rule ) {
    Triple< Integer, Integer, Integer > key = Triple.of( rule.id, scope.from, scope.to );
    stack.put( key, Pair.of( rule, scope ) );
  }

  public void removeFromStack( Scope scope, Rule rule ) {
    Triple< Integer, Integer, Integer > key = Triple.of( rule.id, scope.from, scope.to );
    stack.remove( key );
  }

  public void putToCache( Scope scope, Rule rule, ParseResult res ) {
    Triple< Integer, Integer, Integer > key = Triple.of( rule.id, scope.from, scope.to );
    resultsCache.put( key, res );
  }

  public ParseResult getFromCache( Scope scope, Rule rule ) {
    Triple< Integer, Integer, Integer > key = Triple.of( rule.id, scope.from, scope.to );
    return resultsCache.get( key );
  }
}