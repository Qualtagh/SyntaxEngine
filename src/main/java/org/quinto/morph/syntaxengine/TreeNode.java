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

public class TreeNode implements Cloneable {
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
    ret.append( '\n' );
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
    ret.append( "]" );
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

  @Override
  protected TreeNode clone() {
    TreeNode node;
    try {
      node = ( TreeNode )super.clone();
    } catch ( CloneNotSupportedException e ) {
      node = new TreeNode();
    }
    node.data = data instanceof TreeNode ? ( ( TreeNode )data ).clone() : data;
    node.tags = new LinkedHashMap<>( tags );
    node.children = new ArrayList<>( children.size() );
    for ( TreeNode child : children )
      node.children.add( child.clone() );
    return node;
  }
  
  public TreeNode withChildren( Iterable< TreeNode > children ) {
    TreeNode clone = clone();
    if ( children instanceof Collection )
      clone.children.addAll( ( Collection< TreeNode > )children );
    else
      for ( TreeNode node : children )
        clone.children.add( node );
    return clone;
  }
  
  public TreeNode withChildren( TreeNode... children ) {
    TreeNode clone = clone();
    clone.children.addAll( Arrays.asList( children ) );
    return clone;
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
    if ( tags.containsKey( tagName ) )
      return this;
    TreeNode clone = clone();
    clone.tags.put( tagName, null );
    return clone;
  }
  
  public TreeNode withTag( String tagName, String value ) {
    if ( hasTag( tagName, value ) )
      return this;
    TreeNode clone = clone();
    clone.tags.put( tagName, value );
    return clone;
  }
  
  public TreeNode withTagsOf( TreeNode node ) {
    TreeNode clone = clone();
    clone.tags.putAll( node.tags );
    return clone;
  }
  
  public TreeNode tagChildren( String tagName ) {
    TreeNode clone = clone();
    for ( TreeNode child : clone.children )
      if ( !child.tags.containsKey( tagName ) )
        child.tags.put( tagName, null );
    return clone;
  }
  
  public TreeNode tagChildren( String tagName, String value ) {
    TreeNode clone = clone();
    for ( TreeNode child : clone.children )
      child.tags.put( tagName, value );
    return clone;
  }
  
  public TreeNode tagChild( int childIdx, String tagName ) {
    if ( children.get( childIdx ).tags.containsKey( tagName ) )
      return this;
    TreeNode clone = clone();
    clone.children.get( childIdx ).tags.put( tagName, null );
    return clone;
  }
  
  public TreeNode tagChild( int childIdx, String tagName, String value ) {
    if ( children.get( childIdx ).hasTag( tagName, value ) )
      return this;
    TreeNode clone = clone();
    clone.children.get( childIdx ).tags.put( tagName, value );
    return clone;
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
    TreeNode clone = clone();
    TreeNode ret = clone.children.remove( childIdx );
    ret.children.addAll( clone.children );
    return ret;
  }

  public TreeNode setChildAsMainFromEnd( int childIdx ) {
    return setChildAsMain( children.size() - 1 - childIdx );
  }
}