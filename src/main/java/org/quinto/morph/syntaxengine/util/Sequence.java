package org.quinto.morph.syntaxengine.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Sequence< T > extends ArrayList< T > {
  public Sequence( T... list ) {
    super( list.length );
    for ( T t : list )
      add( t );
  }
  
  public Sequence() {
  }

  public Sequence( int initialCapacity ) {
    super( initialCapacity );
  }

  public Sequence( Collection< ? extends T > c ) {
    super( c );
  }
  
  public Sequence< T > with( Iterable< T > elems ) {
    if ( elems instanceof Collection )
      addAll( ( Collection< T > )elems );
    else
      for ( T t : elems )
        add( t );
    return this;
  }
  
  public Sequence< T > with( T... elems ) {
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

  public static < T > Variants< Sequence< T > > toVariantsOfSequences( Sequence< Variants< T > > seq ) {
    Variants< Sequence< T > > ret = new Variants<>();
    Sequence< T > seqElem = new Sequence<>( Collections.nCopies( seq.size(), null ) );
    addSequences( seq, ret, 0, seqElem );
    return ret;
  }
  
  private static < T > void addSequences( Sequence< Variants< T > > seq, Variants< Sequence< T > > ret, int from, Sequence< T > seqElem ) {
    if ( from >= seq.size() ) {
      ret.add( new Sequence<>( seqElem ) );
      return;
    }
    Variants< T > vars = seq.get( from );
    for ( T t : vars ) {
      seqElem.set( from, t );
      addSequences( seq, ret, from + 1, seqElem );
    }
  }
}