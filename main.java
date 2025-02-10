
public class main {

	public static void main(String[] args) {
		System.out.println("running");
		//your test code here
		RedBlackTree tree= new RedBlackTree();
	
	//	tree.insert(1);
	//	tree.insert(2);
		tree.insert(3);
		tree.insert(4);
		tree.insert(5);
	
		tree.insert(111);

	//	System.out.println("start");
	//	tree.printTree();
		//System.out.println("tree printed");
	//	System.out.println(tree.height());
	//	System.out.println(tree.blackHeight());
	//	System.out.println(tree.trueHeightDiff());
		//System.out.println("shortesttruePath:" + tree.shortestTruePath());
		//System.out.println("end");
		//System.out.println("isRedBlack:");
		//System.out.println("isRedBlack:"+tree.isRedBlack());
		//System.out.println("end");




		RedBlackTree test = new RedBlackTree();
        test.insert(100);
        test.insert(80);
        test.insert(90);
        test.insert(40);
        test.insert(89);
        test.insert(55);
        test.insert(56);
        test.insert(12);
        test.printTree();
		System.out.println(tree.height());
		System.out.println(tree.blackHeight());
		System.out.println(tree.trueHeightDiff());
	}
}
