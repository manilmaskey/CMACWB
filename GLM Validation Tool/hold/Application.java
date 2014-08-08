package hold;


import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	
	private static final String EXTENSION_ID = "GLMExtension";
	public Object start(IApplicationContext context) throws Exception {
		
		
		Display display = PlatformUI.createDisplay();
		runGliderExtension();
		try {
			
			// WorkbenchAdvisor workbenchAdvisor = new GliderWorkbenchAdvisor();
			 
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			// int returnCode = PlatformUI.createAndRunWorkbench(display, workbenchAdvisor);
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return;
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
	
	private void runGliderExtension() {
		try {
			System.out.println("Ready to run GLM Validation Tool Extensions");
			
//			import java.lang.management.ManagementFactory;
//			import java.lang.management.MemoryMXBean;
//			import java.lang.management.MemoryPoolMXBean;
//			import java.lang.management.MemoryUsage;
//			import java.util.List;
//
//			import javax.management.Notification;
//			import javax.management.NotificationEmitter;
//			import javax.management.NotificationListener;
////			Can a Java program detect that it's running low on heap space?
//			final MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
//			final NotificationEmitter ne = (NotificationEmitter) memBean;
//			 
//			ne.addNotificationListener(new NotificationListener(){
//
//				@Override
//				public void handleNotification(Notification notification,
//						Object handback) {
//					// TODO Auto-generated method stub
//					System.err.println("Memory usage exceeds 80%");
//					
//				}}, null, null);
//			 
//			final List<MemoryPoolMXBean> memPools = ManagementFactory
//			    .getMemoryPoolMXBeans();
//			for (final MemoryPoolMXBean mp : memPools) {
//			  if (mp.isUsageThresholdSupported()) {
//			    final MemoryUsage mu = mp.getUsage();
//			    final long max = mu.getMax();
//			    final long alert = (max * 80) / 100;
//			    mp.setUsageThreshold(alert);
//			 
//			  }
//			}
			IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID);
			for (IConfigurationElement e : config) {
				Object o = e.createExecutableExtension("class");
				if (o instanceof IExtension) {
					((IExtension) o).refresh();
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}	
}
