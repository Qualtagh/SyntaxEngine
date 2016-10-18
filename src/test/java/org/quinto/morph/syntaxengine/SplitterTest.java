package org.quinto.morph.syntaxengine;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.quinto.morph.syntaxengine.util.Sequence;

public class SplitterTest {
  @Test
  public void empty() {
    assertEquals( new Sequence<>(), Splitter.split( "" ) );
  }
  
  @Test
  public void oneWord() {
    assertEquals( new Sequence<>( "123" ), Splitter.split( "123" ) );
  }
  
  @Test
  public void phrase() {
    assertEquals( new Sequence<>( "A", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog" ), Splitter.split( "A quick brown fox jumps over the lazy dog" ) );
  }
  
  @Test
  public void sentence() {
    assertEquals( new Sequence<>( "Hello", ",", "world", "!" ), Splitter.split( "Hello, world!" ) );
  }
  
  @Test
  public void splitAtEachChar() {
    assertEquals( new Sequence<>( "2", "5", "+", "1", "2", " ", "4" ), Splitter.splitAtEachChar( "25+12 4" ) );
  }
  
  @Test
  public void splitAtEachCharEmpty() {
    assertEquals( new Sequence<>(), Splitter.splitAtEachChar( "" ) );
  }
  
  @Test
  public void splitAtEachCharOneChar() {
    assertEquals( new Sequence<>( " " ), Splitter.splitAtEachChar( " " ) );
  }
}