
public class Temp {
	public static int[] i;
	public static Dot d;

	public static void main(String[] args) {
		i = new int[1];
		System.out.println(i[0]);
		
		d = new Dot(i);
		System.out.println(i[0]);
		
		d.edit(115);
		System.out.println(i[0]);
	}

}
