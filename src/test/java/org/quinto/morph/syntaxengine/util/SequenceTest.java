package org.quinto.morph.syntaxengine.util;

import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SequenceTest {
  @Test
  public void toVariantsOfSequences() {
    Sequence< Variants< String > > seq = new Sequence<>();
    seq.add( new Variants<>( "Jack", "Bob", "Sally" ) );
    seq.add( new Variants<>( "seeks", "searches", "looks for" ) );
    seq.add( new Variants<>( "vegetables", "fruits" ) );
    seq.add( new Variants<>( "in" ) );
    seq.add( new Variants<>( "a supermarket", "a mall" ) );
    int size = seq.stream().mapToInt( v -> v.size() ).reduce( 1, ( a, b ) -> a * b );
    Variants< Sequence< String > > variants = Sequence.toVariantsOfSequences( seq );
    assertEquals( size, variants.size() );
    for ( Sequence< String > var : variants )
      assertEquals( seq.size(), var.size() );
    Sequence< String > first = seq.stream().map( v -> v.iterator().next() ).collect( Collectors.toCollection( Sequence::new ) );
    assertEquals( first, variants.iterator().next() );
    Sequence< String > last = seq.stream().map( v -> v.stream().skip( v.size() - 1 ).findFirst().get() ).collect( Collectors.toCollection( Sequence::new ) );
    assertEquals( last, variants.stream().skip( variants.size() - 1 ).findFirst().get() );
    seq = new Sequence<>();
    variants = Sequence.toVariantsOfSequences( seq );
    assertEquals( 1, variants.size() );
    assertEquals( 0, variants.iterator().next().size() );
  }
  
  @Test
  public void reverse() {
    Sequence< String > seq = new Sequence<>();
    seq.reverse();
    assertEquals( new Sequence<>(), seq );
    seq.add( "123" );
    seq.reverse();
    assertEquals( new Sequence<>().with( "123" ), seq );
    seq.add( "456" );
    seq.reverse();
    assertEquals( new Sequence<>().with( "456", "123" ), seq );
    seq.add( "789" );
    seq.reverse();
    assertEquals( new Sequence<>().with( "789", "123", "456" ), seq );
    seq.add( "123" );
    seq.reverse();
    assertEquals( new Sequence<>().with( "123", "456", "123", "789" ), seq );
    seq.add( "456" );
    seq.reverse();
    assertEquals( new Sequence<>().with( "456", "789", "123", "456", "123" ), seq );
    seq.add( "789" );
    seq.reverse();
    assertEquals( new Sequence<>().with( "789", "123", "456", "123", "789", "456" ), seq );
  }
}