package conserttest.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class SampleE4Handler {

	@Execute
	public void execute(Shell shell) {
		MessageDialog.openInformation(shell, "First", "Hello, e4 API world emilia");
	}
}