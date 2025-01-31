//The following is one possible RedBlackTree implementation.
//Much of this code is from Sven Woltmann's public GitHub repository. Thank you Mr. Woltmann for making your code available for educational purposes.

import java.util.ArrayList;

public class RedBlackTree{

  static final boolean RED = false;
  static final boolean BLACK = true;
  private Node root;
  private NilNode terminator = new NilNode();
  
  public Node search(int key) {
    Node node = root;
    while (node != null) {
      if (key == node.key) {
        return node;
      } else if (key < node.key) {
        node = node.left;
      } else {
        node = node.right;
      }
    }

    return null;
  }

  // -- Insertion ----------------------------------------------------------------------------------


  public void insert(int key) {
    Node node = root;
    Node parent = null;

    // Traverse the tree to the left or right depending on the key
    while (node != null) {
      parent = node;
      if (key < node.key) {
        node = node.left;
      } else if (key > node.key) {
        node = node.right;
      } else {
        throw new IllegalArgumentException("BST already contains a node with key " + key);
      }
    }

    // Insert new node
    Node newNode = new Node(key);
    newNode.color = RED;
    if (parent == null) {
      root = newNode;
    } else if (key < parent.key) {
      parent.left = newNode;
    } else {
      parent.right = newNode;
    }
    newNode.parent = parent;

    fixRedBlackPropertiesAfterInsert(newNode);
  }

  private void fixRedBlackPropertiesAfterInsert(Node node) {
    Node parent = node.parent;

    // Case 1: Parent is null, we've reached the root, the end of the recursion
    if (parent == null) {
      node.color = BLACK;
      return;
    }

    // Parent is black --> nothing to do
    if (parent.color == BLACK) {
      return;
    }

    // From here on, parent is red
    Node grandparent = parent.parent;

    // Get the uncle (may be null/nil, in which case its color is BLACK)
    Node uncle = getUncle(parent);

    // Case 3: Uncle is red -> recolor parent, grandparent and uncle
    if (uncle != null && uncle.color == RED) {
      parent.color = BLACK;
      grandparent.color = RED;
      uncle.color = BLACK;

      // Call recursively for grandparent, which is now red.
      // It might be root or have a red parent, in which case we need to fix more...
      fixRedBlackPropertiesAfterInsert(grandparent);
    }

    // Parent is left child of grandparent
    else if (parent == grandparent.left) {
      // Case 4a: Uncle is black and node is left->right "inner child" of its grandparent
      if (node == parent.right) {
        rotateLeft(parent);

        // Let "parent" point to the new root node of the rotated sub-tree.
        // It will be recolored in the next step, which we're going to fall-through to.
        parent = node;
      }

      // Case 5a: Uncle is black and node is left->left "outer child" of its grandparent
      rotateRight(grandparent);

      // Recolor original parent and grandparent
      parent.color = BLACK;
      grandparent.color = RED;
    }

    // Parent is right child of grandparent
    else {
      // Case 4b: Uncle is black and node is right->left "inner child" of its grandparent
      if (node == parent.left) {
        rotateRight(parent);

        // Let "parent" point to the new root node of the rotated sub-tree.
        // It will be recolored in the next step, which we're going to fall-through to.
        parent = node;
      }

      // Case 5b: Uncle is black and node is right->right "outer child" of its grandparent
      rotateLeft(grandparent);

      // Recolor original parent and grandparent
      parent.color = BLACK;
      grandparent.color = RED;
    }
  }

  private Node getUncle(Node parent) {
    Node grandparent = parent.parent;
    if (grandparent.left == parent) {
      return grandparent.right;
    } else if (grandparent.right == parent) {
      return grandparent.left;
    } else {
      throw new IllegalStateException("Parent is not a child of its grandparent");
    }
  }

  // -- Deletion -----------------------------------------------------------------------------------

  public void delete(int key) {
    Node node = root;

    // Find the node to be deleted
    while (node != null && node.key != key) {
      // Traverse the tree to the left or right depending on the key
      if (key < node.key) {
        node = node.left;
      } else {
        node = node.right;
      }
    }

    // Node not found?
    if (node == null) {
      return;
    }

    // At this point, "node" is the node to be deleted

    // In this variable, we'll store the node at which we're going to start to fix the R-B
    // properties after deleting a node.
    Node movedUpNode;
    boolean deletedNodeColor;

    // Node has zero or one child
    if (node.left == null || node.right == null) {
      movedUpNode = deleteNodeWithZeroOrOneChild(node);
      deletedNodeColor = node.color;
    }

    // Node has two children
    else {
      // Find minimum node of right subtree ("inorder successor" of current node)
      Node inOrderSuccessor = findMinimum(node.right);

      // Copy inorder successor's data to current node (keep its color!)
      node.key = inOrderSuccessor.key;

      // Delete inorder successor just as we would delete a node with 0 or 1 child
      movedUpNode = deleteNodeWithZeroOrOneChild(inOrderSuccessor);
      deletedNodeColor = inOrderSuccessor.color;
    }

    if (deletedNodeColor == BLACK) {
      fixRedBlackPropertiesAfterDelete(movedUpNode);

      // Remove the temporary NIL node
      if (movedUpNode.getClass() == NilNode.class) {
        replaceParentsChild(movedUpNode.parent, movedUpNode, null);
      }
    }
  }

  private Node deleteNodeWithZeroOrOneChild(Node node) {
    // Node has ONLY a left child --> replace by its left child
    if (node.left != null) {
      replaceParentsChild(node.parent, node, node.left);
      return node.left; // moved-up node
    }

    // Node has ONLY a right child --> replace by its right child
    else if (node.right != null) {
      replaceParentsChild(node.parent, node, node.right);
      return node.right; // moved-up node
    }

    // Node has no children -->
    // * node is red --> just remove it
    // * node is black --> replace it by a temporary NIL node (needed to fix the R-B rules)
    else {
      Node newChild = node.color == BLACK ? new NilNode() : null;
      replaceParentsChild(node.parent, node, newChild);
      return newChild;
    }
  }

  private Node findMinimum(Node node) {
    while (node.left != null) {
      node = node.left;
    }
    return node;
  }

  private void fixRedBlackPropertiesAfterDelete(Node node) {
    // Case 1: Examined node is root, end of recursion
    if (node == root) {
      // Uncomment the following line if you want to enforce black roots (rule 2):
      // node.color = BLACK;
      return;
    }

    Node sibling = getSibling(node);

    // Case 2: Red sibling
    if (sibling.color == RED) {
      handleRedSibling(node, sibling);
      sibling = getSibling(node); // Get new sibling for fall-through to cases 3-6
    }

    // Cases 3+4: Black sibling with two black children
    if (isBlack(sibling.left) && isBlack(sibling.right)) {
      sibling.color = RED;

      // Case 3: Black sibling with two black children + red parent
      if (node.parent.color == RED) {
        node.parent.color = BLACK;
      }

      // Case 4: Black sibling with two black children + black parent
      else {
        fixRedBlackPropertiesAfterDelete(node.parent);
      }
    }

    // Case 5+6: Black sibling with at least one red child
    else {
      handleBlackSiblingWithAtLeastOneRedChild(node, sibling);
    }
  }

  private void handleRedSibling(Node node, Node sibling) {
    // Recolor...
    sibling.color = BLACK;
    node.parent.color = RED;

    // ... and rotate
    if (node == node.parent.left) {
      rotateLeft(node.parent);
    } else {
      rotateRight(node.parent);
    }
  }

  private void handleBlackSiblingWithAtLeastOneRedChild(Node node, Node sibling) {
    boolean nodeIsLeftChild = node == node.parent.left;

    // Case 5: Black sibling with at least one red child + "outer nephew" is black
    // --> Recolor sibling and its child, and rotate around sibling
    if (nodeIsLeftChild && isBlack(sibling.right)) {
      sibling.left.color = BLACK;
      sibling.color = RED;
      rotateRight(sibling);
      sibling = node.parent.right;
    } else if (!nodeIsLeftChild && isBlack(sibling.left)) {
      sibling.right.color = BLACK;
      sibling.color = RED;
      rotateLeft(sibling);
      sibling = node.parent.left;
    }

    // Fall-through to case 6...

    // Case 6: Black sibling with at least one red child + "outer nephew" is red
    // --> Recolor sibling + parent + sibling's child, and rotate around parent
    sibling.color = node.parent.color;
    node.parent.color = BLACK;
    if (nodeIsLeftChild) {
      sibling.right.color = BLACK;
      rotateLeft(node.parent);
    } else {
      sibling.left.color = BLACK;
      rotateRight(node.parent);
    }
  }

  private Node getSibling(Node node) {
    Node parent = node.parent;
    if (node == parent.left) {
      return parent.right;
    } else if (node == parent.right) {
      return parent.left;
    } else {
      throw new IllegalStateException("Parent is not a child of its grandparent");
    }
  }

  private boolean isBlack(Node node) {
    return node == null || node.color == BLACK;
  }

  // -- All leaves will be connected to a NilNode (Note that these are black)
  private static class NilNode extends Node {
    private NilNode() {
      super(0);
      this.color = BLACK;
    }
  }

  // -- Helpers for insertion and deletion ---------------------------------------------------------

  private void rotateRight(Node node) {
    Node parent = node.parent;
    Node leftChild = node.left;

    node.left = leftChild.right;
    if (leftChild.right != null) {
      leftChild.right.parent = node;
    }

    leftChild.right = node;
    node.parent = leftChild;

    replaceParentsChild(parent, node, leftChild);
  }

  private void rotateLeft(Node node) {
    Node parent = node.parent;
    Node rightChild = node.right;

    node.right = rightChild.left;
    if (rightChild.left != null) {
      rightChild.left.parent = node;
    }

    rightChild.left = node;
    node.parent = rightChild;

    replaceParentsChild(parent, node, rightChild);
  }

  private void replaceParentsChild(Node parent, Node oldChild, Node newChild) {
    if (parent == null) {
      root = newChild;
    } else if (parent.left == oldChild) {
      parent.left = newChild;
    } else if (parent.right == oldChild) {
      parent.right = newChild;
    } else {
      throw new IllegalStateException("Node is not a child of its parent");
    }

    if (newChild != null) {
      newChild.parent = parent;
    }
  }

  
  
  
   //------- printing
  public static void showTrunks(Trunk p)
  {
      if (p == null) {
          return;
      }

      showTrunks(p.prev);
      System.out.print(p.str);
  }

  public void printTree(){
      printTree(root, null, false);
  }

  private void printTree(Node root, Trunk prev, boolean isLeft)
  {
      if (root == null) {
    	  System.out.println(" " + "NIL "+"B");
          return;
      }

      String prev_str = "    ";
      Trunk trunk = new Trunk(prev, prev_str);

      printTree(root.right, trunk, true);

      if (prev == null) {
          trunk.str = "---";
      }
      else if (isLeft) {
          trunk.str = ".---";
          prev_str = "   |";
      }
      else {
          trunk.str = "`---";
          prev.str = prev_str;
      }

      showTrunks(trunk);
      System.out.println(" " + root.key+(root.color==BLACK ? "B" : "R"));

      if (prev != null) {
          prev.str = prev_str;
      }
      trunk.str = "   |";

      printTree(root.left, trunk, false);
  }
  
  
  
  //---- Your part:
  // This should check for the four rules that ensure the tree is a Red Black Tree.
  // 1 Every node is either red or black.-unnecessary color is an attribute of node
  // 2 The root is black.-necessary
  // 3 Every leaf (nil) is black.-done
  // 4 If a node is red, then both its children are black.-done
  // 5 For each node, all paths from the node to descendant leaves contain the same number of black nodes.
  // To receive full credit you must explicitly check for each property! You may not assume anything based on the above implementation (which does ensure all these rules are followed)
  // you may wish to add some helper functions here.
  
//precondition: root is not null
//postcondition:returns true if tree is a red black tree and false if not
  public boolean isRedBlack() {
    boolean is=true;
    // 2 The root is black.-necessary
    if(isBlack(root)==true){
      System.out.println("blackroot rule true");
  // 4 If a node is red, then both its children are black.
  Node n=root;

      if(checkChildren(n)){
        is=true;
      }else{
        return false;
      }
      
      System.out.println("red has black children rule true");

  // 5 For each node, all paths from the node to descendant leaves contain the same number of black nodes.
  n=root;
  while (n!=null){    
    if(blackHeight(n.left)==blackHeight(n.right)){
      is=true;
  System.out.println("equal black height true");
    }else{
      System.out.println("left"+blackHeight(n.left)+"right"+blackHeight(n.right));
      System.out.println("while loop ended inside");
      return false;
    }
    if(n.left!=null&& n.right!=null){
      if(equalBlackHeight(n.left)){
        n=n.right;
      }else{
      System.out.println("while loop ended inside helper");
        return false;
      }
    }else if(n.left!=null){
      n=n.left;
    }else if(n.right!=null){
      n=n.right;
    }else{
      n=null;
    }
  }
  System.out.println("while loop ended");

    }else{
      return false;
    }

	  return is;
  }

//isRed Black Helper functions
  private Boolean equalBlackHeight(Node n){
    if(blackHeight(n.left)==blackHeight(n.right)){
      
  System.out.println("equal black height true");
      return true;
    }else{
      System.out.println("equal black height false");
      return false;
    }
    //return n;
  }
     
  
  //checks that if a node is red both its children are black
  private boolean checkChildren(Node n){
    boolean check=false;
  if (n==null){
  return true;
}
    if(isBlack(n)==false){//is red
      if(isBlack(n.left)&&isBlack(n.right)){
        check= true;
      }else{
        return false;
      }
    }else{
      check= true;
    }

    //if(n.left!=null && n.right!=null){
    if(check==true){
      if(checkChildren(n.left) && checkChildren(n.right)){
       // System.out.println("children checked returned true");
        return true;
      }else{
        System.out.println("children checked returned false");
        return false;
      } 
    }
    
    /* }if(n.left!=null){
      check=checkChildren(n.left);
    }else if(n.right!=null){
      check=checkChildren(n.right);
    }
   //else{
     // return true;//null
    //}

*/
    return check;
  }
  
//precondition: method is called
//postcondition:  should return a string of comma separated keys that represents the shortest height path through the tree.

  public String shortestTruePath() {
    Node n=root;
    ArrayList<Node> list=new ArrayList<>();
    String out="";

      list=shortPathHelper(n,list);
    
    for(Node i:list){
      
      out+=i.key+", ";
    }

	  return out;
  }
  //Perhaps this would be easier to do with some helper functions?
  //shortestTruePath helper function
  private ArrayList<Node> shortPathHelper(Node n,ArrayList<Node> l){
    int h;
    l.add(n);
  if(n==null){
    System.out.println("pathhelperended");
    return (l);
  }else if(n.left!=null && n.right!=null){
    h=Math.min(heightHelper(n.left),heightHelper(n.right));
    if(h==heightHelper(n.left)){
      return shortPathHelper(n.left,l);
    }else if(h==heightHelper(n.right)){
      return shortPathHelper(n.right,l);
    }
  }else if(n.left!=null){
    return shortPathHelper(n.left,l);
  }else if(n.right!=null){
    return shortPathHelper(n.right,l);
  }else{
    System.out.println("pathhelperended");
    return l;
  }
  System.out.println("shortpathhelper missed");
    return l;
  }
  
//precondition: method is called
//postcondition: returns the absolute value of the difference between the real height of the tree and its black height. 
  public int trueHeightDiff(){
    return height()-blackHeight();
	  //return 0;
  }

//precondition
//postcondition
public int blackHeight(){
  return blackHeight(root);
}

private int blackHeight(Node n){
  int h=1;
  if(n==null){
    return 1;
  }else if(n.left==null && n.right==null){// base case
    if(isBlack(n)){
      System.out.println("bh base case black");
      return 1;
    }else{
      
      System.out.println("bh base case red");
      return 0;
    }
  }else if(n.left!=null&& n.right!=null){
    int left;
    int right;
    if(isBlack(n)){
      left=1+blackHeight(n.left);
      right=1+blackHeight(n.right);
    }else{
      left=blackHeight(n.left);
      right=blackHeight(n.right);
    }
   /* if(isBlack(n.left)){
      left=1+blackHeight(n.left);
    }else{
      left=blackHeight(n.left);
    }
    if(isBlack(n.right)){
      right=1+blackHeight(n.right);
    }else{
      right=blackHeight(n.right);
    }*/
    return Math.max(left,right);
  }else if(n.left!=null){
    if(isBlack(n.left)){
    return 1+blackHeight(n.left);
  }else{
    return blackHeight(n.left);
  }
  }else if(n.right!=null){
    if(isBlack(n.left)){
      return 1+blackHeight(n.right);
    }else{
      return blackHeight(n.right);
    }
  }else{
    return h;
  }
}
//precondition:no conditions
//postcondition: height of the longest path to leaves is returned
public int height(){
  Node n=root;

  return heightHelper(n);

}
//recursively finds the height of the node
private int heightHelper(Node n){
  int h=1;
  if(n==null){
    return 0;
  }else if(n.left==null && n.right==null){
    return 1;
  }else if(n.left!=null&& n.right!=null){
    return Math.max(h+heightHelper(n.left),h+heightHelper(n.right));
  }else if(n.left!=null){
    return h+heightHelper(n.left);
  }else if(n.right!=null){
    return h+heightHelper(n.right);
  }else{
    return h;
  }
  //return 1;
}

//3 points for comments, conventions and formatting your code cleanly.

}
