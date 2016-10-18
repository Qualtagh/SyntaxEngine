package org.quinto.morph.syntaxengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.quinto.morph.syntaxengine.util.Sequence;
import org.quinto.morph.syntaxengine.util.Variants;

public class TreeNode {
  public Object data;
  public List< TreeNode > children = new ArrayList<>();
  
  public TreeNode() {
  }
  
  public TreeNode( Object data ) {
    this.data = data;
  }

  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();
    print( ret, 0 );
    return ret.toString();
  }
  
  private void print( StringBuilder ret, int shift ) {
    int s = shift;
    while ( s-- > 0 )
      ret.append( "| " );
    ret.append( "t: " ).append( data ).append( '\n' );
    for ( TreeNode child : children )
      child.print( ret, shift + 1 );
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 67 * hash + Objects.hashCode( data );
    hash = 67 * hash + Objects.hashCode( children );
    return hash;
  }

  @Override
  public boolean equals( Object obj ) {
    if ( obj == this )
      return true;
    if ( !( obj instanceof TreeNode ) )
      return false;
    TreeNode other = ( TreeNode )obj;
    return Objects.equals( data, other.data ) && Objects.equals( children, other.children );
  }
  
  public TreeNode withChildren( TreeNode... children ) {
    this.children.addAll( Arrays.asList( children ) );
    return this;
  }
  
  public static Variants< TreeNode > toVariantsOfTreeNodes( Variants< Sequence< TreeNode > > vars, boolean forceWithParent ) {
    Variants< TreeNode > ret = new Variants<>( vars.size() );
    for ( Sequence< TreeNode > s : vars ) {
      TreeNode node;
      if ( s.size() == 1 && !forceWithParent )
        node = s.get( 0 );
      else {
        node = new TreeNode();
        node.children.addAll( s );
      }
      ret.add( node );
    }
    return ret;
  }
}