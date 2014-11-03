package edu.uah.itsc.cmac.versionview.views;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
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

import edu.uah.itsc.cmac.model.VersionViewInterface;
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
				git.close();
			}
			createHeadVersion(git, selectedFolder);
		}
		git.getRepository().close();
		git.close();
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

	private void createHeadVersion(final Git git, final IFolder selectedFolder) {
		try {
			final Ref HEAD = git.getRepository().getRef("refs/heads/master");
			Composite composite = new Composite(bar, SWT.NONE);
			composite.setLayout(new GridLayout(1, true));
			Label headVersionLabel = new Label(composite, SWT.NONE);
			headVersionLabel
				.setText("Master HEAD version. This version is the main branch and is not related to any version.");

			Button resetButton = new Button(composite, SWT.PUSH);
			resetButton.setText("Get HEAD version");
			resetButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					super.widgetSelected(e);
					String stringRef = HEAD.getTarget().getName();
					try {
						git.checkout().setName(HEAD.getName()).call();
						selectedFolder.refreshLocal(IFolder.DEPTH_INFINITE, null);
					}
					catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});

			ExpandItem item = new ExpandItem(bar, SWT.NONE, 0);
			item.setText("Master HEAD version");
			item.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			item.setControl(composite);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createVersionBar(final Git git, final Ref ref, final IFolder selectedFolder) {
		RevTag tag = getTag(git, ref);
		if (tag == null)
			return;

		Composite composite = new Composite(bar, SWT.FILL);
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.horizontalSpacing = 30;
		gridLayout.verticalSpacing = 20;
		composite.setLayout(gridLayout);

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
				// GITUtility.hardReset(git, stringRef);
				try {
					git.checkout().setName(ref.getName()).call();
					selectedFolder.refreshLocal(IFolder.DEPTH_INFINITE, null);
				}
				catch (Exception e1) {
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
		tag = null;
	}

	/**
	 * @param revWalk
	 * @param ref
	 * @return
	 */
	private RevTag getTag(Git git, Ref ref) {
		RevWalk revWalk = new RevWalk(git.getRepository());
		RevTag tag = null;
		ObjectId id = ref.getObjectId();

		try {
			RevObject object = revWalk.parseAny(id);
			// This is lightweight tag not annotated, skip this
			if (object instanceof RevCommit)
				return null;
			// The call to parseTag seems to lock the pack files in the objects db of git directory.
			// Have not been able to find any way to release those locks.
			// The eclipse instance has to be restarted to be able to remove these locks
			// There is no documentation on how to release lock yet.
			tag = revWalk.parseTag(id);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		revWalk.release();
		revWalk.dispose();
		revWalk = null;
		return tag;
	}

	@Override
	public void setFocus() {

	}

}