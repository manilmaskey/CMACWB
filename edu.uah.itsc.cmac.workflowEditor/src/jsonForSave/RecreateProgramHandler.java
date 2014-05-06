package jsonForSave;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

import edu.uah.itsc.uah.programview.programObjects.ProgramPOJO;
import edu.uah.itsc.workflow.actionHandler.CompositeClickHandler;
import edu.uah.itsc.workflow.actionHandler.DeleteProgram;
import edu.uah.itsc.workflow.movementTrackers.MethodCompositeTracker;
import edu.uah.itsc.workflow.programDropHandler.CreateProgram;
import edu.uah.itsc.workflow.relayComposites.RelayComposites;
import edu.uah.itsc.workflow.variableHolder.CopyOfVariablePoJo;
import edu.uah.itsc.workflow.variableHolder.POJOHolder;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class RecreateProgramHandler implements MouseListener {

	CompositeWrapper method;
	Label in;
	Label out;

	public Label getIn() {
		return in;
	}

	public void setIn(Label in) {
		this.in = in;
	}

	public Label getOut() {
		return out;
	}

	public void setOut(Label out) {
		this.out = out;
	}

	public CompositeWrapper getMethod() {
		return method;
	}

	public void setMethod(CompositeWrapper method) {
		this.method = method;
	}

	public void handleDrop(int x, int y, Object obj, final String filename)
			throws Exception {

		// String editorName =
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
		final CopyOfVariablePoJo dataobj = (POJOHolder.getInstance()
				.getEditorsmap().get(filename));

		// VariablePoJo instance = VariablePoJo.getInstance();
		List<ProgramPOJO> programsList = dataobj.getProgram_List();

		for (int i = 0; i < programsList.size(); i++) {
			if (obj.toString().equals(
					"[" + programsList.get(i).getTitle() + "]")) {
				System.out.println("Program Found ...");

				CreateProgram creatorObject = new CreateProgram(filename);
				creatorObject.createMethod(x, y, obj);

				// Add tracker to the method
				method = creatorObject.getMethodComposite();
				in = creatorObject.getInflow();
				out = creatorObject.getOutflow();

				method.addListener(SWT.MouseDown, new Listener() {
					public void handleEvent(Event e) {
						MethodCompositeTracker methodTrackerObject = new MethodCompositeTracker();
						methodTrackerObject.setCompositeList(dataobj
								.getCompositeList());
						methodTrackerObject.setMethodComposite(method);
						methodTrackerObject.setChildComposite_WorkSpace(dataobj
								.getChildCreatorObject()
								.getChildComposite_WorkSpace());
						methodTrackerObject.setParentComposite(dataobj
								.getParentComposite());
						methodTrackerObject.setConnectorList(dataobj
								.getConnectorList());
						methodTrackerObject.methodTracker();
					}
				});

				method.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						if (e.keyCode == SWT.DEL) {
							DeleteProgram dpObj = new DeleteProgram(filename);
							dpObj.delete_selected_program(method);
						}
					}
				});

				method.addMouseListener(new MouseListener() {

					@Override
					public void mouseUp(MouseEvent e) {
					}

					@Override
					public void mouseDown(MouseEvent e) {

						System.out.println("CLICK COUNT = " + e.count);
						if (e.count < 2) { // if less than 2 clicks then
							RelayComposites rc = new RelayComposites();
							rc.reDraw();

							if (dataobj.getSelected_composite() != null) {

								if (dataobj.getSelected_composite()
										.getCompositeID()
										.equals(method.getCompositeID())) {
									CompositeWrapper composite = dataobj
											.getSelected_composite();
									composite
											.setBackground(dataobj
													.getChildCreatorObject()
													.getChildComposite_WorkSpace()
													.getDisplay()
													.getSystemColor(
															SWT.COLOR_WIDGET_NORMAL_SHADOW));
									dataobj.setSelected_composite(null);
								} else {
									if (!(dataobj.getSelected_composite()
											.isDisposed())) {
										CompositeWrapper composite = dataobj
												.getSelected_composite();
										composite
												.setBackground(dataobj
														.getChildCreatorObject()
														.getChildComposite_WorkSpace()
														.getDisplay()
														.getSystemColor(
																SWT.COLOR_WIDGET_NORMAL_SHADOW));
									}
								}
							}

							method.forceFocus();
							if (method.forceFocus() == true) {
								System.out.println("force focus = " + true);
							} else {
								System.out.println("force focus = " + false);
							}

							method.setBackground(dataobj
									.getChildCreatorObject()
									.getChildComposite_WorkSpace()
									.getDisplay()
									.getSystemColor(
											SWT.COLOR_TITLE_INACTIVE_BACKGROUND));

							dataobj.setSelected_composite(method);
							dataobj.setTitleLabel(method.getTitleLabel());
						} else {
							// mouse double click detected
							CompositeClickHandler handlerObject = new CompositeClickHandler();
							try {
								for (int i = 0; i < dataobj.getCompositeList()
										.size(); i++) {
									if (dataobj.getCompositeList().get(i)
											.getCompositeID()
											.equals(method.getCompositeID())) {
										handlerObject.handleCompositeClick(i,
												e.display.getCursorLocation());
									}
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}

						}

					}

					@Override
					public void mouseDoubleClick(MouseEvent e) {
						CompositeClickHandler handlerObject = new CompositeClickHandler();
						try {
							for (int i = 0; i < dataobj.getCompositeList()
									.size(); i++) {
								if (dataobj.getCompositeList().get(i)
										.getCompositeID()
										.equals(method.getCompositeID())) {
									handlerObject.handleCompositeClick(i,
											e.display.getCursorLocation());
								}
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}

					}
				});

			}

		}

	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDown(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseUp(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
