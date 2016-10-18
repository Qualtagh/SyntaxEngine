package org.quinto.morph.syntaxengine.rules;

import java.util.function.Function;
import org.quinto.morph.syntaxengine.ParseResult;
import org.quinto.morph.syntaxengine.Parser;
import org.quinto.morph.syntaxengine.TreeNode;
import org.quinto.morph.syntaxengine.util.VarargFunction;
import org.quinto.morph.syntaxengine.util.Variants;

public class MapRule extends Rule {
  public < T > MapRule( Parser parser, Rule rule, VarargFunction< T, Object > mapping ) {
    this( parser, ( Function< TreeNode, TreeNode > )node -> {
      TreeNode ret;
      if ( node.data == null && !node.children.isEmpty() )
        ret = new TreeNode( mapping.apply( ( T[] )node.children.stream().map( child -> child.data ).toArray() ) );
      else {
        ret = new TreeNode( mapping.apply( ( T[] )new Object[]{ node.data } ) );
        ret.children = node.children;
      }
      return ret;
    }, rule );
  }
  
  public < T > MapRule( Parser parser, Rule rule, Function< T, Object > mapping ) {
    this( parser, rule, ( Object... ops ) -> mapping.apply( ( T )ops[ 0 ] ) );
  }
  
  public MapRule( Parser parser, Function< TreeNode, TreeNode > mapping, Rule rule ) {
    super( parser );
    action = scope -> {
      ParseResult res = rule.apply( scope );
      if ( res.isFailed() )
        return res;
      Variants< TreeNode > success = new Variants<>( res.success.size() );
      for ( TreeNode node : res.success )
        success.add( mapping.apply( node ) );
      return new ParseResult( success );
    };
  }
}