package org.quinto.morph.syntaxengine;

import java.util.Arrays;
import org.quinto.morph.syntaxengine.util.Sequence;

public class Splitter {
  public static Sequence< String > split( String sentence ) {
    char text[] = sentence.toCharArray();
    StringBuilder token = new StringBuilder();
    Sequence< String > ret = new Sequence<>();
    for ( int i = 0; i < text.length; i++ ) {
      char c = text[ i ];
      if ( Character.isLetterOrDigit( c ) )
        token.append( c );
      else {
        if ( token.length() > 0 )
          ret.add( token.toString() );
        if ( !Character.isWhitespace( c ) )
          ret.add( String.valueOf( c ) );
        token = new StringBuilder();
      }
    }
    if ( token.length() > 0 )
      ret.add( token.toString() );
    return ret;
  }
  
  public static Sequence< String > splitAtEachChar( String sentence ) {
    return sentence.isEmpty() ? new Sequence<>() : new Sequence<>( Arrays.asList( sentence.split( "" ) ) );
  }
}