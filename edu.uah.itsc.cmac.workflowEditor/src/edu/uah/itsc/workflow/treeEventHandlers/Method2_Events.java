package edu.uah.itsc.workflow.treeEventHandlers;
//package edu.uah.itsc.workflow.treeEventHandlers;
//
//
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.widgets.Event;
//import org.eclipse.swt.widgets.Listener;
//
//import edu.uah.itsc.workflow.actionHandler.InputHandler;
//import edu.uah.itsc.workflow.actionHandler.Method2_TreeHandler;
//import edu.uah.itsc.workflow.actionHandler.OutputHandler;
//import edu.uah.itsc.workflow.movementTrackers.InputCompositeTracker;
//import edu.uah.itsc.workflow.movementTrackers.MethodCompositeTracker;
//import edu.uah.itsc.workflow.movementTrackers.OutputCompositeTracker;
//import edu.uah.itsc.workflow.variableHolder.VariablePoJo;
//import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;
//
///**
// * This class handles any events i,.e double clicks etc on method 2
// * 
// * @author Rohith Samudrala
// * 
// */
//public class Method2_Events {
//
//	public void handleMethod1_Events(Object obj) {
//
//		if (obj.toString().equals("[Method2]")) {
//
//			// Get an object of the method handler class
//			Method2_TreeHandler methodHandlerObj = new Method2_TreeHandler();
//
//			methodHandlerObj.setChildComposite_WorkSpace(VariablePoJo
//					.getInstance().getChildCreatorObject()
//					.getChildComposite_WorkSpace());
//			methodHandlerObj.setCompositeList(VariablePoJo.getInstance()
//					.getCompositeList());
////			methodHandlerObj.setConnectorList(VariablePoJo.getInstance()
////					.getConnectorList());
//			methodHandlerObj.setConnectorList(VariablePoJo.getInstance()
//					.getConnectorList());
//			// methodHandlerObj.setConnectorObj(MultiPageEditor.getConnectorObj());
//			methodHandlerObj.setParentComposite(VariablePoJo.getInstance()
//					.getParentComposite());
//			// methodHandlerObj.setEndingComposite(mpeObj.getEndingComposite());
//
//			methodHandlerObj.methodTreeCompositeCreator();
//
//			// Get the composite created by the method composite creator
//			final CompositeWrapper methodComposite = methodHandlerObj
//					.getMethodComposite();
//
//			// adding mouse listener to the method composite to enable the
//			// it to move
//			methodComposite.addListener(SWT.MouseDown, new Listener() {
//				public void handleEvent(Event e) {
//
//					MethodCompositeTracker methodTrackerObject = new MethodCompositeTracker();
//					methodTrackerObject.setCompositeList(VariablePoJo
//							.getInstance().getCompositeList());
//					methodTrackerObject.setMethodComposite(methodComposite);
//					methodTrackerObject
//							.setChildComposite_WorkSpace(VariablePoJo
//									.getInstance().getChildCreatorObject()
//									.getChildComposite_WorkSpace());
//					methodTrackerObject.setParentComposite(VariablePoJo
//							.getInstance().getParentComposite());
////					methodTrackerObject.setConnectorList(VariablePoJo
////							.getInstance().getConnectorList());
//					methodTrackerObject.setConnectorList(VariablePoJo
//							.getInstance().getConnectorList());
//					methodTrackerObject.methodTracker();
//				}
//			});
//
//		} else if (obj.toString() == "Inputs") {
//			System.out.println("in root -> parent1 i am working");
//		} else if (obj.toString() == "Outputs") {
//			System.out.println("in root -> parent2 i am working");
//		} else if (obj.toString().equals("[Input 1]")) {
//			System.out.println("in root -> parent1 -> leaf1 is am working");
//			// Set the required data
//			final InputHandler inputHandlerObj = new InputHandler();
//			inputHandlerObj.setChildComposite_WorkSpace(VariablePoJo
//					.getInstance().getChildCreatorObject()
//					.getChildComposite_WorkSpace());
//			inputHandlerObj.setCompositeList(VariablePoJo.getInstance()
//					.getCompositeList());
////			inputHandlerObj.setConnectorList(VariablePoJo.getInstance()
////					.getConnectorList());
//			inputHandlerObj.setConnectorList(VariablePoJo.getInstance()
//					.getConnectorList());
//
//			// Create the input composite
//			inputHandlerObj.inputCompositeCreator();
//
//			inputHandlerObj.getInputComposite().addListener(SWT.MouseDown,
//					new Listener() {
//						public void handleEvent(Event e) {
//							InputCompositeTracker inputTrackerObject = new InputCompositeTracker();
//							inputTrackerObject.setCompositeList(VariablePoJo
//									.getInstance().getCompositeList());
//							inputTrackerObject
//									.setInputComposite(inputHandlerObj
//											.getInputComposite());
//							inputTrackerObject
//									.setChildComposite_WorkSpace(VariablePoJo
//											.getInstance()
//											.getChildCreatorObject()
//											.getChildComposite_WorkSpace());
//							inputTrackerObject
//									.setParentComposite((CompositeWrapper) VariablePoJo
//											.getInstance()
//											.getChildCreatorObject()
//											.getChildComposite_WorkSpace()
//											.getParent());
////							inputTrackerObject.setConnectorList(VariablePoJo
////									.getInstance().getConnectorList());
//							inputTrackerObject.setConnectorList(VariablePoJo
//									.getInstance().getConnectorList());
//							inputTrackerObject.inputTracker();
//
//						}
//					});
//
//		} else if (obj.toString().equals("[Input 2]")) {
//			System.out.println("in root -> parent1 -> leaf2 i am working");
//			// Set the required data
//			final InputHandler inputHandlerObj = new InputHandler();
//			inputHandlerObj.setChildComposite_WorkSpace(VariablePoJo
//					.getInstance().getChildCreatorObject()
//					.getChildComposite_WorkSpace());
//			inputHandlerObj.setCompositeList(VariablePoJo.getInstance()
//					.getCompositeList());
////			inputHandlerObj.setConnectorList(VariablePoJo.getInstance()
////					.getConnectorList());
//			inputHandlerObj.setConnectorList(VariablePoJo.getInstance()
//					.getConnectorList());
//
//			// Create the input composite
//			inputHandlerObj.inputCompositeCreator();
//
//			inputHandlerObj.getInputComposite().addListener(SWT.MouseDown,
//					new Listener() {
//						public void handleEvent(Event e) {
//							InputCompositeTracker inputTrackerObject = new InputCompositeTracker();
//							inputTrackerObject.setCompositeList(VariablePoJo
//									.getInstance().getCompositeList());
//							inputTrackerObject
//									.setInputComposite(inputHandlerObj
//											.getInputComposite());
//							inputTrackerObject
//									.setChildComposite_WorkSpace(VariablePoJo
//											.getInstance()
//											.getChildCreatorObject()
//											.getChildComposite_WorkSpace());
//							inputTrackerObject
//									.setParentComposite((CompositeWrapper) VariablePoJo
//											.getInstance()
//											.getChildCreatorObject()
//											.getChildComposite_WorkSpace()
//											.getParent());
////							inputTrackerObject.setConnectorList(VariablePoJo
////									.getInstance().getConnectorList());
//							inputTrackerObject.setConnectorList(VariablePoJo
//									.getInstance().getConnectorList());
//							inputTrackerObject.inputTracker();
//
//						}
//					});
//
//		} else if (obj.toString().equals("[Output 1]")) {
//			System.out.println("in root -> parent2 -> leaf4 i am working");
//			// get and object of the output handler object
//						final OutputHandler outputHandlerObj = new OutputHandler();
//
//						// Set all the required data
//						outputHandlerObj.setChildComposite_WorkSpace(VariablePoJo
//								.getInstance().getChildCreatorObject()
//								.getChildComposite_WorkSpace());
//						outputHandlerObj.setCompositeList(VariablePoJo.getInstance()
//								.getCompositeList());
////						outputHandlerObj.setConnectorList(VariablePoJo.getInstance()
////								.getConnectorList());
//						outputHandlerObj.setConnectorList(VariablePoJo.getInstance()
//								.getConnectorList());
//						outputHandlerObj.setParentComposite((CompositeWrapper) VariablePoJo
//								.getInstance().getChildCreatorObject()
//								.getChildComposite_WorkSpace().getParent());
//
//						// Call the method to create the composite on clicking the
//						outputHandlerObj.outputCompositeCreator();
//
//						// get the composite created by the output handler class and add
//						// listener to it to track movement
//						outputHandlerObj.getOutputComposite().addListener(SWT.MouseDown,
//								new Listener() {
//									public void handleEvent(Event e) {
//
//										OutputCompositeTracker outputTrackerObject = new OutputCompositeTracker();
//										outputTrackerObject.setCompositeList(VariablePoJo
//												.getInstance().getCompositeList());
//										outputTrackerObject
//												.setOutputComposite(outputHandlerObj
//														.getOutputComposite());
//										outputTrackerObject
//												.setChildComposite_WorkSpace(VariablePoJo
//														.getInstance()
//														.getChildCreatorObject()
//														.getChildComposite_WorkSpace());
//										outputTrackerObject
//												.setParentComposite((CompositeWrapper) VariablePoJo
//														.getInstance()
//														.getChildCreatorObject()
//														.getChildComposite_WorkSpace()
//														.getParent());
////										outputTrackerObject.setConnectorList(VariablePoJo
////												.getInstance().getConnectorList());
//										outputTrackerObject.setConnectorList(VariablePoJo
//												.getInstance().getConnectorList());
//										outputTrackerObject.outputTracker();
//									}
//								});
//
//
//		} else if (obj.toString().equals("[Output 2]")) {
//			System.out.println("in root -> parent2 -> leaf4 i am working");
//			// get and object of the output handler object
//						final OutputHandler outputHandlerObj = new OutputHandler();
//
//						// Set all the required data
//						outputHandlerObj.setChildComposite_WorkSpace(VariablePoJo
//								.getInstance().getChildCreatorObject()
//								.getChildComposite_WorkSpace());
//						outputHandlerObj.setCompositeList(VariablePoJo.getInstance()
//								.getCompositeList());
////						outputHandlerObj.setConnectorList(VariablePoJo.getInstance()
////								.getConnectorList());
//						outputHandlerObj.setConnectorList(VariablePoJo.getInstance()
//								.getConnectorList());
//						outputHandlerObj.setParentComposite((CompositeWrapper) VariablePoJo
//								.getInstance().getChildCreatorObject()
//								.getChildComposite_WorkSpace().getParent());
//
//						// Call the method to create the composite on clicking the
//						outputHandlerObj.outputCompositeCreator();
//
//						// get the composite created by the output handler class and add
//						// listener to it to track movement
//						outputHandlerObj.getOutputComposite().addListener(SWT.MouseDown,
//								new Listener() {
//									public void handleEvent(Event e) {
//
//										OutputCompositeTracker outputTrackerObject = new OutputCompositeTracker();
//										outputTrackerObject.setCompositeList(VariablePoJo
//												.getInstance().getCompositeList());
//										outputTrackerObject
//												.setOutputComposite(outputHandlerObj
//														.getOutputComposite());
//										outputTrackerObject
//												.setChildComposite_WorkSpace(VariablePoJo
//														.getInstance()
//														.getChildCreatorObject()
//														.getChildComposite_WorkSpace());
//										outputTrackerObject
//												.setParentComposite((CompositeWrapper) VariablePoJo
//														.getInstance()
//														.getChildCreatorObject()
//														.getChildComposite_WorkSpace()
//														.getParent());
////										outputTrackerObject.setConnectorList(VariablePoJo
////												.getInstance().getConnectorList());
//										outputTrackerObject.setConnectorList(VariablePoJo
//												.getInstance().getConnectorList());
//										outputTrackerObject.outputTracker();
//									}
//								});
//
//
//		}
//
//	}
//
//}
