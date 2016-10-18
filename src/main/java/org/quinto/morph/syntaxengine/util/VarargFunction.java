package org.quinto.morph.syntaxengine.util;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface VarargFunction< T, R > {
  R apply( T... t );

  default < V > VarargFunction< T, V > andThen( Function< ? super R, ? extends V > after ) {
    Objects.requireNonNull( after );
    return ( T... t ) -> after.apply( apply( t ) );
  }
}