package org.quinto.morph.syntaxengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.quinto.morph.syntaxengine.util.Sequence;
import org.quinto.morph.syntaxengine.util.Variants;

public class TreeNode {
  public Object data;
  public Map< String, String > tags = new LinkedHashMap<>();
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
    ret.append( "t: " );
    boolean first = true;
    for ( Map.Entry< String, String > tag : tags.entrySet() ) {
      ret.append( first ? '[' : ',' );
      first = false;
      ret.append( ' ' ).append( tag.getKey() );
      if ( tag.getValue() != null )
        ret.append( " = " ).append( tag.getValue() );
    }
    ret.append( first ? '[' : ' ' );
    ret.append( "]\n" );
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
  
  public TreeNode withChildren( Iterable< TreeNode > children ) {
    if ( children instanceof Collection )
      this.children.addAll( ( Collection< TreeNode > )children );
    else
      for ( TreeNode node : children )
        this.children.add( node );
    return this;
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
  
  public TreeNode withTag( String tagName ) {
    if ( !tags.containsKey( tagName ) )
      tags.put( tagName, null );
    return this;
  }
  
  public TreeNode withTag( String tagName, String value ) {
    tags.put( tagName, value );
    return this;
  }
  
  public TreeNode withTagsOf( TreeNode node ) {
    tags.putAll( node.tags );
    return this;
  }
  
  public TreeNode tagChildren( String tagName ) {
    for ( TreeNode child : children )
      child.withTag( tagName );
    return this;
  }
  
  public TreeNode tagChildren( String tagName, String value ) {
    for ( TreeNode child : children )
      child.withTag( tagName, value );
    return this;
  }
  
  public TreeNode tagChild( int childIdx, String tagName ) {
    children.get( childIdx ).withTag( tagName );
    return this;
  }
  
  public TreeNode tagChild( int childIdx, String tagName, String value ) {
    children.get( childIdx ).withTag( tagName, value );
    return this;
  }
  
  public TreeNode tagChildFromEnd( int childIdx, String tagName ) {
    return tagChild( children.size() - 1 - childIdx, tagName );
  }
  
  public TreeNode tagChildFromEnd( int childIdx, String tagName, String value ) {
    return tagChild( children.size() - 1 - childIdx, tagName, value );
  }
  
  public boolean hasTag( String tagName ) {
    return tags.containsKey( tagName );
  }
  
  public boolean hasTag( String tagName, String value ) {
    String current = tags.get( tagName );
    return ( current != null || tags.containsKey( tagName ) ) && Objects.equals( current, value );
  }
  
  public boolean hasTag( String tagName, Predicate< String > valueCondition ) {
    String current = tags.get( tagName );
    return ( current != null || tags.containsKey( tagName ) ) && valueCondition.test( current );
  }
  
  public boolean hasTag( Predicate< String > tagNameSelector ) {
    return tags.keySet().stream().anyMatch( tagNameSelector );
  }
  
  public boolean hasTag( Predicate< String > tagNameSelector, Predicate< String > valueCondition ) {
    return tags.entrySet().stream().anyMatch( e -> tagNameSelector.test( e.getKey() ) && valueCondition.test( e.getValue() ) );
  }

  public TreeNode setChildAsMain( int childIdx ) {
    return children.remove( childIdx ).withChildren( children );
  }

  public TreeNode setChildAsMainFromEnd( int childIdx ) {
    return setChildAsMain( children.size() - 1 - childIdx );
  }
}