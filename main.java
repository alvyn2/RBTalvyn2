
public class main {

	public static void main(String[] args) {
		System.out.println("running");
		//your test code here
		RedBlackTree tree= new RedBlackTree();
	
		tree.insert(1);
		tree.insert(2);
		tree.insert(3);
		tree.insert(4);
		tree.insert(5);
		tree.insert(9);
		tree.insert(8);
		tree.insert(7);
		tree.insert(6);
		tree.insert(11);
		tree.insert(51);
		tree.insert(52);
		tree.insert(35);
		tree.insert(46);
		tree.insert(57);
		tree.insert(98);
		tree.insert(89);
		tree.insert(70);
		tree.insert(61);
		tree.insert(111);


		tree.printTree();
		//System.out.println(tree.height());
		//System.out.println(tree.blackHeight());
		//System.out.println(tree.trueHeightDiff());
		System.out.println("shortesttruePath:" + tree.shortestTruePath());
	}
}
