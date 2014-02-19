package edu.uah.itsc.workflow.methodDragAndDrop;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ResourceTransfer;

import edu.uah.itsc.workflow.programDropHandler.ProgramDropHandler;
import edu.uah.itsc.workflow.variableHolder.CopyOfVariablePoJo;
import edu.uah.itsc.workflow.variableHolder.POJOHolder;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * This method sets the drop target for the methods tree
 * 
 * @author Rohith Samudrala
 * 
 */
public class FavoritesDropTarget extends DropTargetAdapter {
	Point p = null;
	CompositeWrapper childComposite_WorkSpace;
	int operations = DND.DROP_MOVE | DND.DROP_COPY;

	public FavoritesDropTarget(CompositeWrapper childComposite_WorkSpace) {
		super();
		this.childComposite_WorkSpace = childComposite_WorkSpace;
		DropTarget target = new DropTarget(childComposite_WorkSpace, operations);
		target.setTransfer(new Transfer[] { TextTransfer.getInstance(),
				ResourceTransfer.getInstance() });
		target.addDropListener(new DropTargetListener() {

			@Override
			public void dropAccept(DropTargetEvent event) {

			}

			@Override
			public void drop(DropTargetEvent event) {

				System.out.println("x = " + event.x + "y = " + event.y);

				// Method1_Events method1EventsObj = new Method1_Events(p);
				// method1EventsObj.handleMethod1_Events(event.data);
				// MethodEvents obj = new MethodEvents(event.x, event.y);
				// obj.MethodEventsHandler(event.data);
				
				String editorName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
				CopyOfVariablePoJo dataObj = (POJOHolder.getInstance().getEditorsmap().get(editorName));
				Point cursorLocation = dataObj.getChildCreatorObject().getChildComposite_WorkSpace().getDisplay().getCursorLocation();
//				Point cursorLocation = display.getCurrent().getCursorLocation();
//				Point relativeCursorLocation = Display.getCurrent().getFocusControl().toControl(cursorLocation);
//				System.out.println("RELATIVE CURSOR LOCATION X: " + relativeCursorLocation.x);
//				System.out.println("RELATIVE CURSOR LOCATION Y: " + relativeCursorLocation.y);
				
				Point p = dataObj.getChildCreatorObject().getChildComposite_WorkSpace().toControl(cursorLocation);
				int relativeX = p.x;
				int relativeY = p.y;
//				PlatformUI.getWorkbench().getDisplay().getCursorLocation();
				
				System.out.println("RELATIVE CURSOR LOCATION X: " + p.x);
				System.out.println("RELATIVE CURSOR LOCATION Y: " + p.y);

				ProgramDropHandler handlerObject = new ProgramDropHandler();
				try {
//					System.out.println("event x " + event.x);
//					System.out.println("event y " + event.y);
					handlerObject.handleDrop(relativeX, relativeY, event.data);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String test = event.data.toString();
				System.out.println("Program selected is :" + test + "...");

			}

			@Override
			public void dragOver(DropTargetEvent event) {

			}

			@Override
			public void dragOperationChanged(DropTargetEvent event) {

			}

			@Override
			public void dragLeave(DropTargetEvent event) {

			}

			@Override
			public void dragEnter(DropTargetEvent event) {

			}
		});
	}

}
