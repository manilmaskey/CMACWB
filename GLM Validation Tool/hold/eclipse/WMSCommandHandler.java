package edu.uah.itsc.worldwind.eclipse;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class WMSCommandHandler implements IHandler
{
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		System.err.println("Trigger = " + event.getTrigger().getClass());
		try
		{
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			activePage.showView(WMSLayerManager.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public void addHandlerListener(IHandlerListener handlerListener)
	{
	}
	
	public void removeHandlerListener(IHandlerListener handlerListener)
	{
	}
	
	public void dispose()
	{
	}
	
	public boolean isEnabled()
	{
		return true;
	}
	
	public boolean isHandled()
	{
		return true;
	}
}
