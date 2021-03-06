import java.util.Scanner;
import java.util.LinkedList;

public class Menu {
	
	private LinkedList<String> options;
	private String title;
	private String prompt;
	
	public Menu (String title, String prompt) {
		this.options = new LinkedList<String> ();
		this.title = title;
		this.prompt = prompt;
	}
	
	public boolean add (String option) {
		if (this.options.contains(option)) {
			return false;
		}
		this.options.add(option);
		return true;
	}
	
	public boolean remove (String option) {
		if (!this.options.contains(option)) {
			return false;
		}
		return this.options.remove(option);
	}

	public void showOptions () {
		int i = 0;
		System.out.println(this.title);
		for (String option : this.options) {
			System.out.printf("%d. %s\n", i++, option);
		}
	}

	public int runSelection () {
		Scanner in = new Scanner (System.in);
		int out = 0;
		int i = 0;
		do {
			if (i++%3==0) {
				this.showOptions();
			}
			System.out.print(this.prompt);
			out = in.nextInt();
		} while (out >= this.options.size() || out < 0);
		return out;
	}
	
}
