
public class main {

	public static void main(String[] args) {
		System.out.println("running");
		//your test code here
		RedBlackTree tree= new RedBlackTree();
		tree.insert(500);
		tree.insert(200);
		tree.insert(90);
		tree.insert(280);
		tree.insert(400);
		tree.insert(105);
		tree.insert(410);
		tree.insert(600);
		tree.insert(800);
		tree.insert(1000);
		tree.insert(120);
		tree.insert(290);
		tree.insert(590);
		tree.insert(150);
		tree.insert(50);
		tree.insert(230);
		tree.insert(510);
		tree.insert(210);
		tree.insert(520);
		tree.insert(220);
		tree.insert(530);
		tree.insert(240);
		tree.insert(540);
		tree.insert(250);
		tree.insert(550);
		tree.insert(260);
		tree.insert(560);
		tree.insert(270);


		tree.printTree();
		System.out.println(tree.height());
	}
}
