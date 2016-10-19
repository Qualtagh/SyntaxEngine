package org.quinto.morph.syntaxengine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import org.quinto.morph.syntaxengine.rules.*;
import org.quinto.morph.syntaxengine.util.Sequence;
import org.quinto.morph.syntaxengine.util.Variants;

public class Parser {
  public final Map< String, RefRule > rules = new HashMap<>();
  
  public Variants< TreeNode > parse( Sequence< ? > sentence ) throws ParseException {
    ParseResult result = getNamedRule( Rule.ROOT ).apply( new Scope( new Context( sentence ), 0, sentence.size() ) );
    if ( result.isFailed() ) {
      ParseException e = new ParseException( result.failure.getMessage() + " at [ " + result.failure.scope.from + ", " + result.failure.scope.to + " ]" );
      e.addSuppressed( result.failure );
      throw e;
    }
    return result.success;
  }
  
  public Rule getNamedRule( String name ) throws ParseException {
    Rule rule = rules.get( name );
    if ( rule == null )
      throw new ParseException( "Rule " + name + " not found" );
    return rule;
  }
  
  public RefRule def( String ruleName, Rule contents ) {
    RefRule rule = rules.get( ruleName );
    if ( rule == null )
      rules.put( ruleName, rule = new RefRule( this, ruleName ) );
    else if ( rule.delegate != null )
      throw new IllegalArgumentException( "Ambiguous rule definition: " + ruleName );
    rule.delegate = contents;
    return rule;
  }
  
  public RefRule ref( String ruleName ) {
    RefRule rule = rules.get( ruleName );
    if ( rule == null )
      rules.put( ruleName, rule = new RefRule( this, ruleName ) );
    return rule;
  }
  
  public MatchRule match( Predicate< ? > pattern ) {
    return new MatchRule( this, pattern );
  }
  
  public MatchRule eq( Object pattern ) {
    return new MatchRule( this, Predicate.isEqual( pattern ) );
  }
  
  public MatchRule hasTag( String tagName ) {
    return new MatchRule< TreeNode >( this, node -> node.hasTag( tagName ) );
  }
  
  public MatchRule hasTag( String tagName, String value ) {
    return new MatchRule< TreeNode >( this, node -> node.hasTag( tagName, value ) );
  }
  
  public MatchRule hasTag( String tagName, Predicate< String > valueCondition ) {
    return new MatchRule< TreeNode >( this, node -> node.hasTag( tagName, valueCondition ) );
  }
  
  public MatchRule hasTag( Predicate< String > tagNameSelector ) {
    return new MatchRule< TreeNode >( this, node -> node.hasTag( tagNameSelector ) );
  }
  
  public MatchRule hasTag( Predicate< String > tagNameSelector, Predicate< String > valueCondition ) {
    return new MatchRule< TreeNode >( this, node -> node.hasTag( tagNameSelector, valueCondition ) );
  }
  
  public SeqRule seq( Rule... rulesSeq ) {
    return new SeqRule( this, rulesSeq );
  }
  
  public UnorderedSeqRule seqUnordered( Rule... rulesSeq ) {
    return new UnorderedSeqRule( this, rulesSeq );
  }
  
  public RepRule rep( Rule rule ) {
    return new RepRule( this, rule );
  }
  
  public RepRule rep( int from, Rule rule ) {
    return new RepRule( this, from, rule );
  }
  
  public RepRule rep( int from, int to, Rule rule ) {
    return new RepRule( this, from, to, rule );
  }
  
  public RepRule rep1( Rule rule ) {
    return new RepRule( this, 1, rule );
  }
  
  public RepRule opt( Rule rule ) {
    return new RepRule( this, 0, 1, rule );
  }
  
  public RepRule empty() {
    return new RepRule( this, 0, 0, null );
  }
  
  public OrRule or( Rule... rules ) {
    return new OrRule( this, rules );
  }
  
  public FilterRule filter( Rule rule, Predicate< TreeNode > condition ) {
    return new FilterRule( this, rule, condition );
  }
  
  public MapRule mapNode( Rule rule, Function< TreeNode, TreeNode > mapping ) {
    return new MapRule( this, mapping, rule );
  }
  
  public MapRule map( Rule rule, Function< ?, Object > mapping ) {
    return new MapRule( this, rule, mapping );
  }
  
  public MapRule map( Rule rule, BiFunction< ?, ?, Object > mapping ) {
    return new MapRule( this, rule, mapping );
  }
}