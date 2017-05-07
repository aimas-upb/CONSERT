package conserttest.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;

public class MyNewWizard extends Wizard implements INewWizard {
	protected WizardPage firstPage;
	protected WizardPage secondPage;

	public MyNewWizard() {
		super();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		IWorkbenchWizard wizard = new BasicNewFileResourceWizard();
		wizard.init(workbench, selection);
		WizardDialog wizardDialog = new WizardDialog(this.getShell(), wizard);
		setNeedsProgressMonitor(true);
		if (wizardDialog.open() == Window.OK) {
			System.out.println("Ok pressed");
		} else {
			System.out.println("Cancel pressed");
		}
	}

	@Override
	public String getWindowTitle() {
		return "Export My Data";
	}

	@Override
	public void addPages() {
		firstPage = new MyPageOne();
		secondPage = new MyPageTwo();
		addPage(firstPage);
		addPage(secondPage);
	}

	@Override
	public boolean performFinish() {
		System.out.println(((MyPageOne) firstPage).getText1());
		System.out.println(((MyPageTwo) secondPage).getText1());
		return true;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
		if (currentPage == firstPage) {
			return secondPage;
		}
		return null;
	}

}
