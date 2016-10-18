package org.quinto.morph.syntaxengine.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ExtList< T > extends ArrayList< T > {
  public ExtList( T... list ) {
    super( list.length );
    for ( T t : list )
      add( t );
  }
  
  public ExtList() {
  }

  public ExtList( int initialCapacity ) {
    super( initialCapacity );
  }

  public ExtList( Collection< ? extends T > c ) {
    super( c );
  }
  
  public ExtList< T > with( T... elems ) {
    if ( elems != null && elems.length != 0 ) {
      if ( elems.length == 1 )
        add( elems[ 0 ] );
      else
        addAll( Arrays.asList( elems ) );
    }
    return this;
  }
  
  public void reverse() {
    final int size = size();
    for ( int i = 0; i < size / 2; i++ )
      set( i, set( size - i - 1, get( i ) ) );
  }
}