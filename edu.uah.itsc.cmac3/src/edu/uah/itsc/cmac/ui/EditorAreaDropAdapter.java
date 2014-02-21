package edu.uah.itsc.cmac.ui;

import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorInputTransfer;

public class EditorAreaDropAdapter extends DropTargetAdapter {
	public void handleDrop(IWorkbenchPage page, DropTargetEvent event) {
		if (EditorInputTransfer.getInstance().isSupportedType(event.currentDataType)) {
			EditorInputTransfer.EditorInputData[] editorInputs = (EditorInputTransfer.EditorInputData[]) event.data;
			for (int i = 0; i < editorInputs.length; i++) {
				IEditorInput editorInput = editorInputs[i].input;
				String editorId = editorInputs[i].editorId;
				openEditor(page, editorInput, editorId);
			}
		}

	}

	private void openEditor(IWorkbenchPage page, IEditorInput editorInput, String editorId) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, editorId);

		}
		catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
