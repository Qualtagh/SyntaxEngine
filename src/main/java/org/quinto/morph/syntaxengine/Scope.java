package org.quinto.morph.syntaxengine;

import java.util.Objects;

public class Scope {
  public Context context;
  public int from;
  public int to;

  public Scope( Context context, int from, int to ) {
    if ( from > to )
      throw new IllegalArgumentException( "Index out of bounds: " + from + " > " + to );
    if ( from < 0 )
      throw new IllegalArgumentException( "Index out of bounds: " + from + " < 0" );
    if ( to > context.input.size() )
      throw new IllegalArgumentException( "Index out of bounds: " + to + " > " + context.input.size() );
    this.context = context;
    this.from = from;
    this.to = to;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 59 * hash + Objects.hashCode( context );
    hash = 59 * hash + from;
    hash = 59 * hash + to;
    return hash;
  }

  @Override
  public boolean equals( Object obj ) {
    if ( obj == this )
      return true;
    if ( !( obj instanceof Scope ) )
      return false;
    Scope other = ( Scope )obj;
    return from == other.from && to == other.to && Objects.equals( context, other.context );
  }

  @Override
  public String toString() {
    return context.input.subList( from, to ) + " [ " + from + ", " + to + " ] " + context.input;
  }
}