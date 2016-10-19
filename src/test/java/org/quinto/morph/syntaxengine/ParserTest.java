package org.quinto.morph.syntaxengine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.quinto.morph.syntaxengine.rules.Rule;
import org.quinto.morph.syntaxengine.util.Sequence;
import org.quinto.morph.syntaxengine.util.Variants;

public class ParserTest {
  @Test
  public void eq() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, eq( "123" ) );
      }
    };
    assertEquals( new Variants<>( new TreeNode( "123" ) ), parser.parse( Splitter.split( "123" ) ) );
    try {
      parser.parse( Splitter.split( "1234" ) );
      fail( "Expected exception" );
    } catch ( ParseException e ) {
    }
  }
  
  @Test
  public void match() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, match( ( String s ) -> s.startsWith( "1" ) ) );
      }
    };
    assertEquals( new Variants<>( new TreeNode( "123" ) ), parser.parse( Splitter.split( "123" ) ) );
    try {
      parser.parse( Splitter.split( "234" ) );
      fail( "Expected exception" );
    } catch ( ParseException e ) {
    }
    try {
      parser.parse( new Sequence<>( new StringBuilder( "123" ) ) );
      fail( "Expected exception" );
    } catch ( ParseException e ) {
      assertTrue( e.getMessage().contains( "wrong type" ) );
    }
  }
  
  @Test
  public void seq() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, seq( eq( "123" ), eq( "asd" ) ) );
      }
    };
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "123" ), new TreeNode( "asd" ) ) ),
      parser.parse( Splitter.split( "123 asd" ) ) );
    try {
      parser.parse( Splitter.split( "123 123" ) );
      fail( "Expected exception" );
    } catch ( ParseException e ) {
    }
  }
  
  @Test
  public void unorderedSeq() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, seqUnordered( eq( "123" ), eq( "asd" ) ) );
      }
    };
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "123" ), new TreeNode( "asd" ) ) ),
      parser.parse( Splitter.split( "123 asd" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "123" ), new TreeNode( "asd" ) ) ),
      parser.parse( Splitter.split( "asd 123" ) ) );
    try {
      parser.parse( Splitter.split( "123 123" ) );
      fail( "Expected exception" );
    } catch ( ParseException e ) {
    }
  }
  
  @Test
  public void empty() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, empty() );
      }
    };
    assertEquals( new Variants<>( new TreeNode() ),
      parser.parse( Splitter.split( "" ) ) );
    try {
      parser.parse( Splitter.split( "234" ) );
      fail( "Expected exception" );
    } catch ( ParseException e ) {
    }
  }
  
  @Test
  public void opt() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, opt( eq( "123" ) ) );
      }
    };
    assertEquals( new Variants<>( new TreeNode( "123" ) ),
      parser.parse( Splitter.split( "123" ) ) );
    assertEquals( new Variants<>( new TreeNode() ),
      parser.parse( Splitter.split( "" ) ) );
    try {
      parser.parse( Splitter.split( "234" ) );
      fail( "Expected exception" );
    } catch ( ParseException e ) {
    }
  }
  
  @Test
  public void rep() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, rep( eq( "123" ) ) );
      }
    };
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "123" ), new TreeNode( "123" ) ) ),
      parser.parse( Splitter.split( "123 123" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "123" ) ) ),
      parser.parse( Splitter.split( "123" ) ) );
    assertEquals( new Variants<>( new TreeNode() ),
      parser.parse( Splitter.split( "" ) ) );
    try {
      parser.parse( Splitter.split( "234" ) );
      fail( "Expected exception" );
    } catch ( ParseException e ) {
    }
    try {
      parser.parse( Splitter.split( "123 234" ) );
      fail( "Expected exception" );
    } catch ( ParseException e ) {
    }
  }
  
  @Test
  public void rep1() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, rep1( eq( "123" ) ) );
      }
    };
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "123" ), new TreeNode( "123" ) ) ),
      parser.parse( Splitter.split( "123 123" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "123" ) ) ),
      parser.parse( Splitter.split( "123" ) ) );
    try {
      parser.parse( Splitter.split( "234" ) );
      fail( "Expected exception" );
    } catch ( ParseException e ) {
    }
    try {
      parser.parse( Splitter.split( "" ) );
      fail( "Expected exception" );
    } catch ( ParseException e ) {
    }
    try {
      parser.parse( Splitter.split( "123 234" ) );
      fail( "Expected exception" );
    } catch ( ParseException e ) {
    }
  }
  
  @Test
  public void rep35() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, rep( 3, 5, eq( "123" ) ) );
      }
    };
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "123" ), new TreeNode( "123" ), new TreeNode( "123" ) ) ),
      parser.parse( Splitter.split( "123 123 123" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "123" ), new TreeNode( "123" ), new TreeNode( "123" ), new TreeNode( "123" ) ) ),
      parser.parse( Splitter.split( "123 123 123 123" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "123" ), new TreeNode( "123" ), new TreeNode( "123" ), new TreeNode( "123" ), new TreeNode( "123" ) ) ),
      parser.parse( Splitter.split( "123 123 123 123 123" ) ) );
    for ( String s : new String[]{
      "",
      "123",
      "123 123",
      "234",
      "123 234",
      "123 123 123 123 123 123"
    } ) {
      try {
        parser.parse( Splitter.split( s ) );
        fail( "Expected exception: " + s );
      } catch ( ParseException e ) {
      }
    }
  }
  
  @Test
  public void rep35opt() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, rep( 3, 5, opt( eq( "123" ) ) ) );
      }
    };
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode(), new TreeNode(), new TreeNode() ) ),
      parser.parse( Splitter.split( "" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "123" ), new TreeNode(), new TreeNode() ) ),
      parser.parse( Splitter.split( "123" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "123" ), new TreeNode( "123" ), new TreeNode() ) ),
      parser.parse( Splitter.split( "123 123" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "123" ), new TreeNode( "123" ), new TreeNode( "123" ) ) ),
      parser.parse( Splitter.split( "123 123 123" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "123" ), new TreeNode( "123" ), new TreeNode( "123" ), new TreeNode( "123" ) ) ),
      parser.parse( Splitter.split( "123 123 123 123" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "123" ), new TreeNode( "123" ), new TreeNode( "123" ), new TreeNode( "123" ), new TreeNode( "123" ) ) ),
      parser.parse( Splitter.split( "123 123 123 123 123" ) ) );
    for ( String s : new String[]{
      "234",
      "123 234",
      "123 123 123 123 123 123"
    } ) {
      try {
        parser.parse( Splitter.split( s ) );
        fail( "Expected exception: " + s );
      } catch ( ParseException e ) {
      }
    }
  }
  
  @Test
  public void or() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, or( eq( "123" ), eq( "345" ), eq( "567" ) ) );
      }
    };
    assertEquals( new Variants<>( new TreeNode( "123" ) ), parser.parse( Splitter.split( "123" ) ) );
    assertEquals( new Variants<>( new TreeNode( "345" ) ), parser.parse( Splitter.split( "345" ) ) );
    assertEquals( new Variants<>( new TreeNode( "567" ) ), parser.parse( Splitter.split( "567" ) ) );
    for ( String s : new String[]{
      "234",
      "123 345",
      "123 123 123",
      "",
      "1234"
    } ) {
      try {
        parser.parse( Splitter.split( s ) );
        fail( "Expected exception: " + s );
      } catch ( ParseException e ) {
      }
    }
  }
  
  @Test
  public void ref() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, or( eq( "123" ), ref( "letters" ) ) );
        def( "letters", seq( eq( "abc" ), or( eq( "qwe" ), eq( "zxc" ) ) ) );
      }
    };
    assertEquals( new Variants<>( new TreeNode( "123" ) ),
      parser.parse( Splitter.split( "123" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "abc" ), new TreeNode( "qwe" ) ) ),
      parser.parse( Splitter.split( "abc qwe" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "abc" ), new TreeNode( "zxc" ) ) ),
      parser.parse( Splitter.split( "abc zxc" ) ) );
    for ( String s : new String[]{
      "234",
      "123 345",
      "123 123 123",
      "",
      "1234",
      "123 qwe",
      "123 zxc",
      "abc",
      "abc 123"
    } ) {
      try {
        parser.parse( Splitter.split( s ) );
        fail( "Expected exception: " + s );
      } catch ( ParseException e ) {
      }
    }
  }
  
  @Test
  public void refImmediately() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, ref( "letters" ) );
        def( "letters", seq( eq( "abc" ), or( eq( "qwe" ), eq( "zxc" ) ) ) );
      }
    };
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "abc" ), new TreeNode( "qwe" ) ) ),
      parser.parse( Splitter.split( "abc qwe" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren( new TreeNode( "abc" ), new TreeNode( "zxc" ) ) ),
      parser.parse( Splitter.split( "abc zxc" ) ) );
    for ( String s : new String[]{
      "123",
      "234",
      "123 345",
      "123 123 123",
      "",
      "1234",
      "123 qwe",
      "123 zxc",
      "abc",
      "abc 123"
    } ) {
      try {
        parser.parse( Splitter.split( s ) );
        fail( "Expected exception: " + s );
      } catch ( ParseException e ) {
      }
    }
  }
  
  @Test
  public void pumpingLemma() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, filter( seq( rep( eq( "a" ) ), rep( eq( "b" ) ), rep( eq( "c" ) ) ), node -> {
          int size = node.children.get( 0 ).children.size();
          for ( int i = node.children.size() - 1; i >= 1; i-- )
            if ( node.children.get( i ).children.size() != size )
              return false;
          return true;
        } ) );
      }
    };
    assertEquals( new Variants<>( new TreeNode().withChildren(
      new TreeNode().withChildren( new TreeNode( "a" ), new TreeNode( "a" ), new TreeNode( "a" ) ),
      new TreeNode().withChildren( new TreeNode( "b" ), new TreeNode( "b" ), new TreeNode( "b" ) ),
      new TreeNode().withChildren( new TreeNode( "c" ), new TreeNode( "c" ), new TreeNode( "c" ) )
    ) ), parser.parse( Splitter.splitAtEachChar( "aaabbbccc" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren(
      new TreeNode().withChildren( new TreeNode( "a" ), new TreeNode( "a" ) ),
      new TreeNode().withChildren( new TreeNode( "b" ), new TreeNode( "b" ) ),
      new TreeNode().withChildren( new TreeNode( "c" ), new TreeNode( "c" ) )
    ) ), parser.parse( Splitter.splitAtEachChar( "aabbcc" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren(
      new TreeNode().withChildren( new TreeNode( "a" ) ),
      new TreeNode().withChildren( new TreeNode( "b" ) ),
      new TreeNode().withChildren( new TreeNode( "c" ) )
    ) ), parser.parse( Splitter.splitAtEachChar( "abc" ) ) );
    assertEquals( new Variants<>( new TreeNode().withChildren(
      new TreeNode(),
      new TreeNode(),
      new TreeNode()
    ) ), parser.parse( Splitter.splitAtEachChar( "" ) ) );
    for ( String s : new String[]{
      "a",
      "b",
      "c",
      "ab",
      "abcc",
      "aabc",
      "aaabbbcccc",
      "abcd",
      "d",
      "   ",
      " "
    } ) {
      try {
        parser.parse( Splitter.splitAtEachChar( s ) );
        fail( "Expected exception: " + s );
      } catch ( ParseException e ) {
      }
    }
  }
  
  @Test
  public void arithmetic() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, ref( "expr" ) );
        def( "expr", or( map( seq( ref( "expr" ), eq( "*" ), ref( "expr" ) ), ( Object... ops ) -> ( Integer )ops[ 0 ] * ( Integer )ops[ 2 ] ),
                         map( seq( ref( "expr" ), eq( "/" ), ref( "expr" ) ), ( Object... ops ) -> ( Integer )ops[ 0 ] / ( Integer )ops[ 2 ] ),
                         map( seq( ref( "expr" ), eq( "+" ), ref( "expr" ) ), ( Object... ops ) -> ( Integer )ops[ 0 ] + ( Integer )ops[ 2 ] ),
                         map( seq( ref( "expr" ), eq( "-" ), ref( "expr" ) ), ( Object... ops ) -> ( Integer )ops[ 0 ] - ( Integer )ops[ 2 ] ),
                         map( seq( eq( "(" ), ref( "expr" ), eq( ")" ) ), ( Object... ops ) -> ops[ 1 ] ),
                         map( seq( eq( "-" ), ref( "expr" ) ), ( String op, Integer number ) -> -number ),
                         map( match( ( String s ) -> s.matches( "\\d+" ) ), ( String number ) -> Integer.parseInt( number ) ) ) );
      }
    };
    assertEquals( new HashSet<>( Arrays.asList( -9, -7, 3, 5 ) ), parser.parse( Splitter.split( "-1 + 2 * 3" ) ).stream().map( node -> node.data ).collect( Collectors.toSet() ) );
    assertEquals( new HashSet<>( Arrays.asList( 5 ) ), parser.parse( Splitter.split( "(-1) + (2 * 3)" ) ).stream().map( node -> node.data ).collect( Collectors.toSet() ) );
  }
  
  @Test
  public void arithmeticVararg() throws ParseException {
    Parser parser = new Parser() {
      {
        def( Rule.ROOT, ref( "expr" ) );
        def( "expr", or( map( seq( ref( "expr" ), eq( "*" ), ref( "expr" ) ), ( Object... ops ) -> ( Integer )ops[ 0 ] * ( Integer )ops[ 2 ] ),
                         map( seq( ref( "expr" ), eq( "/" ), ref( "expr" ) ), ( Object... ops ) -> ( Integer )ops[ 0 ] / ( Integer )ops[ 2 ] ),
                         map( seq( ref( "expr" ), eq( "+" ), ref( "expr" ) ), ( Object... ops ) -> ( Integer )ops[ 0 ] + ( Integer )ops[ 2 ] ),
                         map( seq( ref( "expr" ), eq( "-" ), ref( "expr" ) ), ( Object... ops ) -> ( Integer )ops[ 0 ] - ( Integer )ops[ 2 ] ),
                         map( seq( eq( "(" ), ref( "expr" ), eq( ")" ) ), ( Object... ops ) -> ops[ 1 ] ),
                         map( seq( eq( "-" ), ref( "expr" ) ), ( Object... ops ) -> -( Integer )ops[ 1 ] ),
                         map( match( ( String s ) -> s.matches( "\\d+" ) ), ( Object... ops ) -> Integer.parseInt( ( String )ops[ 0 ] ) ) ) );
      }
    };
    assertEquals( new HashSet<>( Arrays.asList( -9, -7, 3, 5 ) ), parser.parse( Splitter.split( "-1 + 2 * 3" ) ).stream().map( node -> node.data ).collect( Collectors.toSet() ) );
    assertEquals( new HashSet<>( Arrays.asList( 5 ) ), parser.parse( Splitter.split( "(-1) + (2 * 3)" ) ).stream().map( node -> node.data ).collect( Collectors.toSet() ) );
  }
}