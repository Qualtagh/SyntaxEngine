package org.quinto.morph.syntaxengine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.quinto.morph.syntaxengine.rules.Rule;

public class ArithmeticGrammarTest {
  private Parser parser;
  
  @Before
  public void setUp() {
    parser = new Parser() {
      {
        def( Rule.ROOT, ref( "expr" ) );
        System.out.println( "0" );
        def( "expr", or( map( seq( ref( "expr" ), eq( "*" ), ref( "expr" ) ), ( Object... ops ) -> ( Integer )ops[ 0 ] * ( Integer )ops[ 2 ] ),
                         map( seq( ref( "expr" ), eq( "/" ), ref( "expr" ) ), ( Object... ops ) -> ( Integer )ops[ 0 ] / ( Integer )ops[ 2 ] ),
                         map( seq( ref( "expr" ), eq( "+" ), ref( "expr" ) ), ( Object... ops ) -> ( Integer )ops[ 0 ] + ( Integer )ops[ 2 ] ),
                         map( seq( ref( "expr" ), eq( "-" ), ref( "expr" ) ), ( Object... ops ) -> ( Integer )ops[ 0 ] - ( Integer )ops[ 2 ] ),
                         map( seq( eq( "(" ), ref( "expr" ), eq( ")" ) ), ( Object... ops ) -> ( Integer )ops[ 1 ] ),
                         map( seq( eq( "-" ), ref( "expr" ) ), ( Object... ops ) -> -( Integer )ops[ 1 ] ),
                         map( match( ( String s ) -> s.matches( "\\d+" ) ), ( Object... ops ) -> Integer.parseInt( ( String )ops[ 0 ] ) ) ) );
        System.out.println( "1" );
      }
    };
  }
  
  @Test
  public void casual() throws ParseException {
    assertEquals( new HashSet<>( Arrays.asList( -9, -7, 3, 5 ) ), parser.parse( Splitter.split( "-1 + 2 * 3" ) ).stream().map( node -> node.data ).collect( Collectors.toSet() ) );
  }
  
  @Test
  public void brackets() throws ParseException {
    assertEquals( new HashSet<>( Arrays.asList( 5 ) ), parser.parse( Splitter.split( "(-1) + (2 * 3)" ) ).stream().map( node -> node.data ).collect( Collectors.toSet() ) );
  }
}