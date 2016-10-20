package org.quinto.morph.syntaxengine;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TreeNodeTest {
  @Test
  public void print() {
    TreeNode root = new TreeNode().withTag( "root" );
    TreeNode h1 = new TreeNode().withTag( "h1" );
    TreeNode m1 = new TreeNode().withTag( "m1" );
    TreeNode s1 = new TreeNode().withTag( "s1" );
    TreeNode s2 = new TreeNode().withTag( "s2" );
    TreeNode s3 = new TreeNode().withTag( "s3" );
    m1.children.addAll( Arrays.asList( s1, s2, s3 ) );
    TreeNode m2 = new TreeNode().withTag( "m2" );
    TreeNode s4 = new TreeNode().withTag( "s4" );
    TreeNode s5 = new TreeNode().withTag( "s5" );
    TreeNode s6 = new TreeNode().withTag( "s6" );
    m2.children.addAll( Arrays.asList( s4, s5, s6 ) );
    TreeNode m3 = new TreeNode().withTag( "m3" );
    h1.children.addAll( Arrays.asList( m1, m2, m3 ) );
    TreeNode h2 = new TreeNode().withTag( "h2" );
    root.children.addAll( Arrays.asList( h1, h2 ) );
    String expected =
      "\nt: [ root ]\n" +
      "| t: [ h1 ]\n" +
      "| | t: [ m1 ]\n" +
      "| | | t: [ s1 ]\n" +
      "| | | t: [ s2 ]\n" +
      "| | | t: [ s3 ]\n" +
      "| | t: [ m2 ]\n" +
      "| | | t: [ s4 ]\n" +
      "| | | t: [ s5 ]\n" +
      "| | | t: [ s6 ]\n" +
      "| | t: [ m3 ]\n" +
      "| t: [ h2 ]";
    assertEquals( expected, root.toString() );
  }
}