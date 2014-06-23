package uk.co.neylan.plugins.makeiteasy.gui;

import com.intellij.CommonBundle;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.util.IncorrectOperationException;
import pl.mjedynak.idea.plugins.builder.gui.helper.GuiHelper;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;

public class OKActionRunnable implements Runnable {

    private CreateMakerDialog createMakerDialog;
    private PsiHelper psiHelper;
    private GuiHelper guiHelper;
    private Project project;
    private Module module;
    private String packageName;
    private String className;

    public OKActionRunnable(CreateMakerDialog createMakerDialog, PsiHelper psiHelper, GuiHelper guiHelper, Project project, Module module, String packageName, String className) {
        this.createMakerDialog = createMakerDialog;
        this.psiHelper = psiHelper;
        this.guiHelper = guiHelper;
        this.project = project;
        this.module = module;
        this.packageName = packageName;
        this.className = className;
    }

    @Override
    public void run() {
        String errorString = null;
        try {
            PsiDirectory targetDirectory = psiHelper.getDirectoryFromModuleAndPackageName(module, packageName);
            if (targetDirectory != null) {
                createMakerDialog.setTargetDirectory(targetDirectory);
                errorString = psiHelper.checkIfClassCanBeCreated(targetDirectory, className);
            }
        } catch (IncorrectOperationException e) {
            errorString = e.getMessage();
        }
        if (errorString != null) {
            guiHelper.showMessageDialog(project, errorString, CommonBundle.getErrorTitle(), Messages.getErrorIcon());
        }
    }
}
