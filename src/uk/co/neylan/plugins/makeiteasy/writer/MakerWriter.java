package uk.co.neylan.plugins.makeiteasy.writer;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import pl.mjedynak.idea.plugins.builder.gui.helper.GuiHelper;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;
import pl.mjedynak.idea.plugins.builder.psi.model.PsiFieldsForMaker;
import uk.co.neylan.plugins.makeiteasy.model.PropertyCase;
import uk.co.neylan.plugins.makeiteasy.psi.MakerPsiClassBuilder;

public class MakerWriter {

    static final String CREATE_MAKER_STRING = "Create Maker";
    private MakerPsiClassBuilder makerPsiClassBuilder;
    private PsiHelper psiHelper;
    private GuiHelper guiHelper;

    public MakerWriter(MakerPsiClassBuilder makerPsiClassBuilder, PsiHelper psiHelper, GuiHelper guiHelper) {
        this.makerPsiClassBuilder = makerPsiClassBuilder;
        this.psiHelper = psiHelper;
        this.guiHelper = guiHelper;
    }

    public void writeMaker(Project project,
                           PsiFieldsForMaker psiFieldsForMaker,
                           PsiDirectory targetDirectory,
                           String className,
                           PsiClass psiClassFromEditor,
                           PropertyCase propertyCase) {
        CommandProcessor commandProcessor = psiHelper.getCommandProcessor();
        commandProcessor.executeCommand(project,
                new MakerWriterRunnable(makerPsiClassBuilder, project,
                        psiFieldsForMaker, targetDirectory, className, psiClassFromEditor, psiHelper, guiHelper, propertyCase),
                CREATE_MAKER_STRING, this);
    }
}
