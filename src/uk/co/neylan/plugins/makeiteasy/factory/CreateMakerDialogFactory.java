package uk.co.neylan.plugins.makeiteasy.factory;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPackage;
import pl.mjedynak.idea.plugins.builder.factory.ReferenceEditorComboWithBrowseButtonFactory;
import uk.co.neylan.plugins.makeiteasy.gui.CreateMakerDialog;
import pl.mjedynak.idea.plugins.builder.gui.helper.GuiHelper;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;

public class CreateMakerDialogFactory {

    static final String MAKER_SUFFIX = "Maker";
    static final String METHOD_PREFIX = "with";
    private static final String DIALOG_NAME = "CreateMaker";
    private PsiHelper psiHelper;
    private ReferenceEditorComboWithBrowseButtonFactory referenceEditorComboWithBrowseButtonFactory;
    private GuiHelper guiHelper;


    public CreateMakerDialogFactory(PsiHelper psiHelper,
                                    ReferenceEditorComboWithBrowseButtonFactory referenceEditorComboWithBrowseButtonFactory,
                                    GuiHelper guiHelper) {
        this.psiHelper = psiHelper;
        this.referenceEditorComboWithBrowseButtonFactory = referenceEditorComboWithBrowseButtonFactory;
        this.guiHelper = guiHelper;
    }

    public CreateMakerDialog createMakerDialog(PsiClass sourceClass, Project project, PsiPackage srcPackage) {
        return new CreateMakerDialog(project, DIALOG_NAME, sourceClass, sourceClass.getName() + MAKER_SUFFIX, METHOD_PREFIX, srcPackage, psiHelper, guiHelper,
                referenceEditorComboWithBrowseButtonFactory);
    }
}
