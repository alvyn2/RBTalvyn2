public class Node {
  int key;

  Node left;
  Node right;
  Node parent;

  boolean color;

  public Node(int key) {
    this.key = key;
  }
}