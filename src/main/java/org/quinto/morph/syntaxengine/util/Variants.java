package org.quinto.morph.syntaxengine.util;

import java.util.Collection;

public class Variants< T > extends ExtList< T > {
  public Variants( T... list ) {
    super( list );
  }

  public Variants() {
  }

  public Variants( int initialCapacity ) {
    super( initialCapacity );
  }

  public Variants( Collection< ? extends T > c ) {
    super( c );
  }

  @Override
  public Variants< T > with( T... elems ) {
    return ( Variants< T > )super.with( elems );
  }
}