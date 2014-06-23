package uk.co.neylan.plugins.makeiteasy.writer;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import pl.mjedynak.idea.plugins.builder.gui.helper.GuiHelper;
import uk.co.neylan.plugins.makeiteasy.psi.BuilderPsiClassBuilder;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;
import pl.mjedynak.idea.plugins.builder.psi.model.PsiFieldsForMaker;
import uk.co.neylan.plugins.makeiteasy.writer.BuilderWriterRunnable;

public class MakerWriter {

    static final String CREATE_MAKER_STRING = "Create Maker";
    private BuilderPsiClassBuilder builderPsiClassBuilder;
    private PsiHelper psiHelper;
    private GuiHelper guiHelper;

    public MakerWriter(BuilderPsiClassBuilder builderPsiClassBuilder, PsiHelper psiHelper, GuiHelper guiHelper) {
        this.builderPsiClassBuilder = builderPsiClassBuilder;
        this.psiHelper = psiHelper;
        this.guiHelper = guiHelper;
    }

    public void writeMaker(Project project,
                           PsiFieldsForMaker psiFieldsForMaker,
                           PsiDirectory targetDirectory,
                           String className,
                           PsiClass psiClassFromEditor,
                           String methodPrefix) {
        CommandProcessor commandProcessor = psiHelper.getCommandProcessor();
        commandProcessor.executeCommand(project,
                new BuilderWriterRunnable(builderPsiClassBuilder, project,
                        psiFieldsForMaker, targetDirectory, className, psiClassFromEditor, psiHelper, guiHelper, methodPrefix),
                CREATE_MAKER_STRING, this);
    }
}
