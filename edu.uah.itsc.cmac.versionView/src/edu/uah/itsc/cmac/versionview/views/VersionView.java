package edu.uah.itsc.cmac.versionview.views;

import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import edu.uah.itsc.cmac.models.VersionViewInterface;
import edu.uah.itsc.cmac.util.GITUtility;

public class VersionView extends ViewPart implements VersionViewInterface {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String	ID	= "edu.uah.itsc.cmac.versionview.views.VersionView";
	private ExpandBar			bar;

	@Override
	public void createPartControl(Composite parent) {
		bar = new ExpandBar(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		bar.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	@Override
	public void accept(IFolder selectedFolder, String repoName, String repoPath) {
		/*
		 * Get all expand items currently in the bar. Dispose all the items. Note that the item should set expanded
		 * value to false, otherwise you will notice weird problems when the items are disposed
		 */

		ExpandItem[] items = bar.getItems();
		for (ExpandItem item : items) {
			item.setExpanded(false);
			item.dispose();
		}

		Git git = GITUtility.getGit(repoName, repoPath);
		List<Ref> tags = null;
		try {
			tags = git.tagList().call();
		}
		catch (GitAPIException e) {
			e.printStackTrace();
		}
		if (tags == null || tags.isEmpty())
			createNoVersion();
		else {
			for (Ref ref : tags) {
				createVersionBar(git, ref, selectedFolder);
			}
		}

	}

	private void createNoVersion() {
		Composite composite = new Composite(bar, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		Label noVersionLabel = new Label(composite, SWT.NONE);
		noVersionLabel.setText("There are no versions for the selected workflow");
		ExpandItem item = new ExpandItem(bar, SWT.NONE, 0);
		item.setText("No versions available");
		item.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item.setControl(composite);
	}

	private void createVersionBar(final Git git, final Ref ref, final IFolder selectedFolder) {
		Composite composite = new Composite(bar, SWT.FILL);
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.horizontalSpacing = 30;
		gridLayout.verticalSpacing = 20;
		composite.setLayout(gridLayout);

		RevTag tag = getTag(git, ref);

		Label versionLabel = new Label(composite, SWT.NONE);
		versionLabel.setText("Version: " + tag.getTagName());

		Label creatorLabel = new Label(composite, SWT.NONE);
		creatorLabel.setText("Creator: " + tag.getTaggerIdent().getName());

		Label dateLabel = new Label(composite, SWT.NONE);
		dateLabel.setText("Created At: " + tag.getTaggerIdent().getWhen().toString());

		Button resetButton = new Button(composite, SWT.PUSH);
		resetButton.setText("Get this version");
		resetButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				String stringRef = ref.getTarget().getName();
				GITUtility.hardReset(git, stringRef);
				try {
					selectedFolder.refreshLocal(IFolder.DEPTH_INFINITE, null);
				}
				catch (CoreException e1) {
					e1.printStackTrace();
				}
			}
		});

		GridData gData = new GridData();
		gData.horizontalSpan = 4;

		Text description = new Text(composite, SWT.NONE | SWT.WRAP);
		description.setEditable(false);
		description.setText("Comment: \n" + tag.getFullMessage());
		description.setLayoutData(gData);

		ExpandItem item = new ExpandItem(bar, SWT.NONE, 0);
		item.setText("Version: " + tag.getTagName() + " - " + tag.getTaggerIdent().getName());
		item.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item.setControl(composite);
	}

	/**
	 * @param revWalk
	 * @param ref
	 * @return
	 */
	private RevTag getTag(Git git, Ref ref) {
		RevWalk revWalk = new RevWalk(git.getRepository());
		ObjectId id = ref.getObjectId();
		try {
			RevTag tag = revWalk.parseTag(id);
			return tag;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void setFocus() {

	}

}