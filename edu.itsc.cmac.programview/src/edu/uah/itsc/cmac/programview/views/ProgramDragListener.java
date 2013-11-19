//package edu.uah.itsc.cmac.programview.views;
//
//import java.util.ArrayList;
//
//import org.eclipse.jface.viewers.IStructuredSelection;
//import org.eclipse.jface.viewers.TableViewer;
//import org.eclipse.swt.dnd.DragSourceEvent;
//import org.eclipse.swt.dnd.DragSourceListener;
//
//import edu.uah.itsc.cmac.portal.Program;
//
//public class ProgramDragListener implements DragSourceListener {
//
//	private final TableViewer mytableviewer;
//	private ArrayList<Program> program;
//
//	public ProgramDragListener(TableViewer viewer, ArrayList<Program> progs) {
//		this.mytableviewer = viewer;
//		this.program = progs;
//	}
//
//	@Override
//	public void dragFinished(DragSourceEvent event) {
//
//		// mytableviewer.refresh();
//	}
//
//	@Override
//	public void dragSetData(DragSourceEvent event) {
//		// System.out.println(" ---------------------- Drag set data  ---------------------- ");
//		IStructuredSelection selection = (IStructuredSelection) mytableviewer
//				.getSelection();
//
//		String currentelement = selection.getFirstElement().toString();
//		for (int i = 0; i < program.size(); i++) {
//			if (program.get(i).getTitle().equals(currentelement)) // System.out.println(values[i][1]);
//				event.data = program.get(i).getTitle() + "\t-\t"
//						+ program.get(i).getUri() + "\n";
//		}
//
//		// event.data = currentelement + " -i [Input] -o [Output] \n";
//		// System.out.println("test adam " + adam.length);
//		// System.out.println("Event data is : "+event.data);
//
//		// Todo firstElement = (Todo) selection.getFirstElement();
//
//		/*
//		 * if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
//		 * event.data = firstElement.getShortDescription() + " " +
//		 * firstElement.getLongDescription(); }
//		 */
//
//	}
//
//	@Override
//	public void dragStart(DragSourceEvent event) {
//
//		event.doit = true;
//
//	}
//
//}
