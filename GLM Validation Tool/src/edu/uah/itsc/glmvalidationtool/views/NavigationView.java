package edu.uah.itsc.glmvalidationtool.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

public class NavigationView extends CommonNavigator {

	private CommonViewer viewer;

	public static final String ID = "NavigationView";

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
//		createHandlers();
//		hookDoubleClickCommand();
	}

//	private void createHandlers() {
//		IHandlerService handlerService = (IHandlerService) getSite()
//				.getService(IHandlerService.class);
//		AbstractHandler myHandler1 = new AbstractHandler() {
//			public Object execute(ExecutionEvent event)
//					throws ExecutionException {
//				System.out.println("Execute");
//				// viewer is an instance variable of
//				IWorkbenchWindow window = HandlerUtil
//						.getActiveWorkbenchWindow(event);
//				IWorkbenchPage page = window.getActivePage();
//				// NavigatorView view = (NavigatorView)
//				// page.findView(edu.uah.itsc.glider.NavigatorView.ID);
//				// Get the selection
//				ISelection selection = getSite().getSelectionProvider()
//						.getSelection();
//				if (selection != null
//						&& selection instanceof IStructuredSelection) {
//					Object obj = ((IStructuredSelection) selection)
//							.getFirstElement();
//					// If we had a selection lets open the editor
//					if (obj != null) {
//						File gliderFile = (File) obj;
//						GliderMetadata gliderMetadata = new GliderMetadata(
//								gliderFile);
//						MetadataEditorInput input = new MetadataEditorInput(
//								gliderMetadata);
//						try {
//							page.openEditor(input, MetadataEditor.ID);
//						} catch (PartInitException e) {
//							System.out.println("PartinitException "
//									+ e.getMessage() + e.getStackTrace());
//						}
//					}
//				}
//				return null;
//			}
//		};
//		handlerService.activateHandler("edu.uah.itsc.glider.viewMetadata",
//				myHandler1);
//
//		AbstractHandler myHandler2 = new AbstractHandler() {
//			public Object execute(ExecutionEvent event)
//					throws ExecutionException {
//				// viewer is an instance variable of
//				IWorkbenchWindow window = HandlerUtil
//						.getActiveWorkbenchWindow(event);
//				IWorkbenchPage page = window.getActivePage();
//				// NavigatorView view = (NavigatorView)
//				// page.findView(edu.uah.itsc.glider.NavigatorView.ID);
//				// Get the selection
//				ISelection selection = getSite().getSelectionProvider()
//						.getSelection();
//				if (selection != null
//						&& selection instanceof IStructuredSelection) {
//					Object obj = ((IStructuredSelection) selection)
//							.getFirstElement();
//					// If we had a selection lets open the editor
//					if (obj != null) {
//						if (obj.getClass().getName().toString() != "org.eclipse.core.internal.resources.Project") {
//							
//						File file = (File) obj;
//						GliderText text = new GliderText(file);
//						TextEditorInput input = new TextEditorInput(text);
//						try {
//
//							IFileStore fileStore = null;
//							try {
//
//								fileStore = EFS.getStore(text.getFile()
//										.getLocationURI());
//							} catch (CoreException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							page.openEditor(
//									new FileStoreEditorInput(fileStore),
//									EditorsUI.DEFAULT_TEXT_EDITOR_ID);
//
//							// page.openEditor(input, TextEditor.ID);
//						} catch (PartInitException e) {
//							System.out.println("PartinitException "
//									+ e.getMessage() + e.getStackTrace());
//						}
//						}
//					}
//				}
//				return null;
//			}
//		};
//		handlerService.activateHandler("edu.uah.itsc.glider.viewText",
//				myHandler2);
//
//	}
//
//	private void hookDoubleClickCommand() {
//		viewer = super.getCommonViewer();
//
//		// viewer.addOpenListener(new IOpenListener()
//		// {
//		// public void open(OpenEvent event)
//		// {
//		// System.err.println("Open event on viewer");
//		// }
//		// });
//
//		viewer.addDoubleClickListener(new IDoubleClickListener() {
//			@SuppressWarnings("restriction")
//			public void doubleClick(DoubleClickEvent event) {
//				System.out.println("double clicked on viewer");
//
//				IHandlerService handlerService = (IHandlerService) getSite()
//						.getService(IHandlerService.class);
//				;
//
//				boolean isImageFile = false;
//				ISelection selection = event.getSelection();
//				if (selection != null
//						&& selection instanceof IStructuredSelection) {
//					Object obj = ((IStructuredSelection) selection)
//							.getFirstElement();
//					if (obj != null) {
//						if (obj.getClass().getName().toString() != "org.eclipse.core.internal.resources.Project") {
//							File gliderFile = (File) obj;
//
//							if ("gld".equalsIgnoreCase(gliderFile
//									.getFileExtension())
//									|| "gsf".equalsIgnoreCase(gliderFile
//											.getFileExtension())) {
//								isImageFile = true;
//							}
//						}
//					}
//				}
//
//				try {
//					// handlerService = (IHandlerService)
//					// getSite().getService(IHandlerService.class);
//					if (isImageFile) {
//						handlerService.executeCommand(
//								"edu.uah.itsc.glider.viewMetadata", null);
//					} else {
//						handlerService.executeCommand(
//								"edu.uah.itsc.glider.viewText", null);
//					}
//				} catch (ExecutionException ex) {
//					// throw new
//					// RuntimeException("edu.uah.itsc.glider.viewMetadata not found execution exception "+ex.getMessage());
//				} catch (NotDefinedException ex) {
//					// throw new
//					// RuntimeException("edu.uah.itsc.glider.viewMetadata not found NotDefinedException exception "+ex.getMessage());
//				} catch (NotEnabledException ex) {
//					// throw new
//					// RuntimeException("edu.uah.itsc.glider.viewMetadata not found NotEnabledException exception "+ex.getMessage());
//				} catch (NotHandledException ex) {
//					// throw new
//					// RuntimeException("edu.uah.itsc.glider.viewMetadata not found NotHandledException exception "+ex.getMessage());
//				}
//			}
//		});
//	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}
