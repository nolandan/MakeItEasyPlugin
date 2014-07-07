package uk.co.neylan.plugins.makeiteasy.writer;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import pl.mjedynak.idea.plugins.builder.gui.helper.GuiHelper;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;
import pl.mjedynak.idea.plugins.builder.psi.model.PsiFieldsForMaker;
import uk.co.neylan.plugins.makeiteasy.model.PropertyCase;
import uk.co.neylan.plugins.makeiteasy.psi.MakerPsiClassBuilder;

public class MakerWriterRunnable implements Runnable {

    private MakerPsiClassBuilder makerPsiClassBuilder;
    private Project project;
    private PsiFieldsForMaker psiFieldsForMaker;
    private PsiDirectory targetDirectory;
    private String className;
    private PsiClass psiClassFromEditor;
    private PsiHelper psiHelper;
    private GuiHelper guiHelper;
    private PropertyCase propertyCase;

    public MakerWriterRunnable(MakerPsiClassBuilder makerPsiClassBuilder,
                               Project project,
                               PsiFieldsForMaker psiFieldsForMaker,
                               PsiDirectory targetDirectory,
                               String className,
                               PsiClass psiClassFromEditor,
                               PsiHelper psiHelper,
                               GuiHelper guiHelper,
                               PropertyCase propertyCase) {
        this.makerPsiClassBuilder = makerPsiClassBuilder;
        this.project = project;
        this.psiFieldsForMaker = psiFieldsForMaker;
        this.targetDirectory = targetDirectory;
        this.className = className;
        this.psiClassFromEditor = psiClassFromEditor;
        this.psiHelper = psiHelper;
        this.guiHelper = guiHelper;
        this.propertyCase = propertyCase;
    }

    @Override
    public void run() {
        Application application = psiHelper.getApplication();
        application.runWriteAction(new MakerWriterComputable(makerPsiClassBuilder, project,
                psiFieldsForMaker, targetDirectory, className, psiClassFromEditor, psiHelper, guiHelper, propertyCase));
    }
}