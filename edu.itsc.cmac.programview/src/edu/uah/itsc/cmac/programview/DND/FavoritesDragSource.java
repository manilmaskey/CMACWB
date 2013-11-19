package edu.uah.itsc.cmac.programview.DND;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.part.ResourceTransfer;

/**
 * This method sets the drag source 
 * @author Rohith Samudrala
 *
 */
public class FavoritesDragSource implements DragSourceListener {

	private TableViewer viewer;
	int operations = DND.DROP_MOVE | DND.DROP_COPY;

	/**
	 * Constructor
	 * 
	 * @param tableviewer
	 *            drag source
	 */
	public FavoritesDragSource(TableViewer tableviewer) {
		super();
		this.viewer = tableviewer;
		DragSource source = new DragSource(tableviewer.getControl(), operations);
		source.setTransfer(new Transfer[] { TextTransfer.getInstance(),
				ResourceTransfer.getInstance() });
		source.addDragListener(this);
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		event.doit = !viewer.getSelection().isEmpty();
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = viewer.getSelection().toString();
		}
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		// TODO Auto-generated method stub

	}

}
