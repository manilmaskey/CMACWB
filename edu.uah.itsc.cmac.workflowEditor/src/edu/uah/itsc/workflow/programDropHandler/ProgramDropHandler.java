package edu.uah.itsc.workflow.programDropHandler;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import edu.uah.itsc.uah.programview.programObjects.ProgramPOJO;
import edu.uah.itsc.workflow.actionHandler.CompositeClickHandler;
import edu.uah.itsc.workflow.movementTrackers.MethodCompositeTracker;
import edu.uah.itsc.workflow.relayComposites.RelayComposites;
import edu.uah.itsc.workflow.variableHolder.VariablePoJo;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class ProgramDropHandler {

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

	public void handleDrop(int x, int y, Object obj) throws Exception {
		VariablePoJo instance = VariablePoJo.getInstance();
		List<ProgramPOJO> programsList = instance.getProgram_List();

		for (int i = 0; i < programsList.size(); i++) {
			if (obj.toString().equals(
					"[" + programsList.get(i).getTitle() + "]")) {
				System.out.println("Program Found ...");

				CreateProgram creatorObject = new CreateProgram();
				creatorObject.createMethod(x, y, obj);

				// Add tracker to the method
				method = creatorObject.getMethodComposite();
				in = creatorObject.getInflow();
				out = creatorObject.getOutflow();

				method.addListener(SWT.MouseDown, new Listener() {
					public void handleEvent(Event e) {
						MethodCompositeTracker methodTrackerObject = new MethodCompositeTracker();
						methodTrackerObject.setCompositeList(VariablePoJo
								.getInstance().getCompositeList());
						methodTrackerObject.setMethodComposite(method);
						methodTrackerObject
								.setChildComposite_WorkSpace(VariablePoJo
										.getInstance().getChildCreatorObject()
										.getChildComposite_WorkSpace());
						methodTrackerObject.setParentComposite(VariablePoJo
								.getInstance().getParentComposite());
						methodTrackerObject.setConnectorList(VariablePoJo
								.getInstance().getConnectorList());
						methodTrackerObject.methodTracker();
					}
				});

				method.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						if (e.keyCode == SWT.DEL) {
							DeleteProgram dpObj = new DeleteProgram();
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
						
						RelayComposites rc = new RelayComposites();
						rc.reDraw();

						if (VariablePoJo.getInstance().getSelected_composite() != null) {

							if (VariablePoJo.getInstance()
									.getSelected_composite().getCompositeID()
									.equals(method.getCompositeID())) {
								CompositeWrapper composite = VariablePoJo
										.getInstance().getSelected_composite();
								composite
										.setBackground(VariablePoJo
												.getInstance()
												.getChildCreatorObject()
												.getChildComposite_WorkSpace()
												.getDisplay()
												.getSystemColor(
														SWT.COLOR_WIDGET_NORMAL_SHADOW));
								VariablePoJo.getInstance()
										.setSelected_composite(null);
							} else {
								if (!(VariablePoJo.getInstance()
										.getSelected_composite().isDisposed())) {
									CompositeWrapper composite = VariablePoJo
											.getInstance()
											.getSelected_composite();
									composite
											.setBackground(VariablePoJo
													.getInstance()
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

						method.setBackground(VariablePoJo
								.getInstance()
								.getChildCreatorObject()
								.getChildComposite_WorkSpace()
								.getDisplay()
								.getSystemColor(
										SWT.COLOR_TITLE_INACTIVE_BACKGROUND));

						VariablePoJo.getInstance()
								.setSelected_composite(method);
						VariablePoJo.getInstance().setTitleLabel(
								method.getTitleLabel());

					}

					@Override
					public void mouseDoubleClick(MouseEvent e) {
						CompositeClickHandler handlerObject = new CompositeClickHandler();

						try {
							for (int i = 0; i < VariablePoJo.getInstance()
									.getCompositeList().size(); i++) {
								if (VariablePoJo.getInstance()
										.getCompositeList().get(i)
										.getCompositeID()
										.equals(method.getCompositeID())) {
									handlerObject.handleCompositeClick(i);
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

}
