package org.quinto.morph.syntaxengine.util;

import java.util.Collection;
import java.util.Collections;

public class Sequence< T > extends ExtList< T > {
  public Sequence( T... list ) {
    super( list );
  }

  public Sequence() {
  }

  public Sequence( int initialCapacity ) {
    super( initialCapacity );
  }

  public Sequence( Collection< ? extends T > c ) {
    super( c );
  }

  @Override
  public Sequence< T > with( T... elems ) {
    return ( Sequence< T > )super.with( elems );
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