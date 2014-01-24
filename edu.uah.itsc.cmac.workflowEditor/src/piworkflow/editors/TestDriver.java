package piworkflow.editors;

public class TestDriver {
	
	public static void main(String[] args) {
		
		String str = "[L/scattering/rohit/test1/new_file1.json]";
		EditorMethods em = new EditorMethods();
		em.extractFileName(str);
		
	}

}
