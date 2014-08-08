package edu.uah.itsc.worldwind.eclipse;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import edu.uah.itsc.worldwind.eclipse.glider.GliderWorldWindow;

public class ExtendedGliderWorldWindow extends GliderWorldWindow
{
	private boolean isDrawableRealized = false;
	private boolean isGLInitEventFired = false;

	public ExtendedGliderWorldWindow()
	{
		addGLEventListener(new GLEventListener()
		{
			public void init(GLAutoDrawable drawable)
			{
				System.err.println("***** GL Init Event fired ****** No of layers = " 
						+ getModel().getLayers().size());
				ExtendedGliderWorldWindow.this.isGLInitEventFired = true;
			}
			public void display(GLAutoDrawable drawable) {}
			public void displayChanged(GLAutoDrawable drawable,
					boolean modeChanged, boolean deviceChanged) {}
			public void reshape(GLAutoDrawable drawable, int x, int y,
					int width, int height) {}
			@Override
			public void dispose(GLAutoDrawable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		isDrawableRealized = true;
	}

	public boolean isGLInitEventFired()
	{
		return isGLInitEventFired;
	}

	public boolean isDrawableRealized()
	{
		return isDrawableRealized;
	}
}
