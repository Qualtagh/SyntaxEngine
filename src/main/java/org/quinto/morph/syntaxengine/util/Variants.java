package org.quinto.morph.syntaxengine.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

public class Variants< T > extends LinkedHashSet< T > {
  public Variants( T... list ) {
    super( list.length );
    for ( T t : list )
      add( t );
  }
  
  public Variants() {
  }

  public Variants( int initialCapacity ) {
    super( initialCapacity );
  }

  public Variants( Collection< ? extends T > c ) {
    super( c );
  }
  
  public Variants< T > with( Iterable< T > elems ) {
    if ( elems instanceof Collection )
      addAll( ( Collection< T > )elems );
    else
      for ( T t : elems )
        add( t );
    return this;
  }
  
  public Variants< T > with( T... elems ) {
    if ( elems != null && elems.length != 0 ) {
      if ( elems.length == 1 )
        add( elems[ 0 ] );
      else
        addAll( Arrays.asList( elems ) );
    }
    return this;
  }
}