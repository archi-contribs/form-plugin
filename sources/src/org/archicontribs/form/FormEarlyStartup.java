package org.archicontribs.form;

import org.eclipse.ui.IStartup;

public class FormEarlyStartup implements IStartup {

	@Override
	public void earlyStartup() {
		// nothing to do. The FormPlugin class will do all the requested work.
		new FormPlugin();
	}

}
