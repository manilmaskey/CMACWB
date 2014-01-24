package edu.uah.itsc.cmac.actions;

import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NewWindow {

	protected Shell shell;
	private Text text;
	String filename;
	boolean correct = false;

	/**
	 * Launch the application.
	 * @param args
	 */
	public void getfilename (){
//	public static void main(String[] args){
		try {
			NewWindow window = new NewWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 254);
		
		Composite composite = new Composite(shell, SWT.BORDER);
		composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		composite.setBounds(0, 0, 434, 64);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		FontData[] fD = lblNewLabel.getFont().getFontData();
		fD[0].setHeight(10);
		fD[0].setStyle(SWT.BOLD);
		lblNewLabel.setFont(new Font(composite.getDisplay(), fD));
		lblNewLabel.setBounds(10, 10, 83, 19);
		lblNewLabel.setText("CMAC Editor");
		
		final Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		lblNewLabel_1.setBounds(20, 30, 400, 30);
		lblNewLabel_1.setText("This wizard creates a new file with *.json extension that can be opened by a \r\nCMAC Editor.");
		
		Composite composite_1 = new Composite(shell, SWT.BORDER);
		composite_1.setBounds(0, 64, 434, 108);
		
		Label lblFile = new Label(composite_1, SWT.CENTER);
		lblFile.setBounds(23, 38, 73, 21);
		lblFile.setText("File name:");
		
		text = new Text(composite_1, SWT.BORDER);
		text.setBounds(106, 38, 279, 21);
		
		Composite composite_2 = new Composite(shell, SWT.BORDER);
		composite_2.setBounds(1, 172, 434, 44);
		
		Button btnFinish = new Button(composite_2, SWT.NONE);
		btnFinish.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(text.getText());
				checkextension(text.getText());
				
				if (correct == true){
					CreateFile cf = new CreateFile();
					cf.createfile(filename);
					shell.dispose();
				}else {
					// do not close it 
				}
			}

			private void checkextension(String text) {
				StringTokenizer st = new StringTokenizer(text, ".");
				String key = st.nextToken();
				String val;
				
				if (st.hasMoreTokens()) {
					val = st.nextToken();
					
					if (val.equals("json")){
						// create a file with the given name 
						System.out.println("extension is json ");
						lblNewLabel_1.setText("This wizard creates a new file with *.json extension that can be opened by a \r\nCMAC Editor.");
						filename = text;
						correct = true;
					}else{
						// don't take it 
						System.out.println("extension is not json ");
						lblNewLabel_1.setText("Only allowed extension is .json");
						correct = false;
					}
				} else {
						// append .json to the text
						String newfilename = text + ".json";
						System.out.println("default extension is json ");
						System.out.println("the filename now is: " + newfilename);
						lblNewLabel_1.setText("This wizard creates a new file with *.json extension that can be opened by a \r\nCMAC Editor.");
						filename = newfilename;
						correct = true;
				}
			}
		});
		btnFinish.setBounds(268, 10, 75, 21);
		btnFinish.setText("Finish");
		
		Button btnCancel = new Button(composite_2, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		btnCancel.setBounds(349, 10, 75, 21);
		btnCancel.setText("Cancel");

	}
}
