package uk.co.neylan.plugins.makeiteasy.writer;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import pl.mjedynak.idea.plugins.builder.gui.helper.GuiHelper;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;
import pl.mjedynak.idea.plugins.builder.psi.model.PsiFieldsForMaker;
import uk.co.neylan.plugins.makeiteasy.psi.MakerPsiClassBuilder;

public class MakerWriterComputable implements Computable<PsiElement> {

    private MakerPsiClassBuilder makerPsiClassBuilder;
    private Project project;
    private PsiFieldsForMaker psiFieldsForMaker;
    private PsiDirectory targetDirectory;
    private String className;
    private PsiClass psiClassFromEditor;
    private GuiHelper guiHelper;
    private PsiHelper psiHelper;
    private String methodPrefix;

    public MakerWriterComputable(MakerPsiClassBuilder makerPsiClassBuilder,
                                 Project project,
                                 PsiFieldsForMaker psiFieldsForMaker,
                                 PsiDirectory targetDirectory,
                                 String className,
                                 PsiClass psiClassFromEditor,
                                 PsiHelper psiHelper,
                                 GuiHelper guiHelper,
                                 String methodPrefix) {
        this.makerPsiClassBuilder = makerPsiClassBuilder;
        this.project = project;
        this.psiFieldsForMaker = psiFieldsForMaker;
        this.targetDirectory = targetDirectory;
        this.className = className;
        this.psiClassFromEditor = psiClassFromEditor;
        this.psiHelper = psiHelper;
        this.guiHelper = guiHelper;
        this.methodPrefix = methodPrefix;

    }

    @Override
    public PsiElement compute() {
        return createBuilder(project, psiFieldsForMaker, targetDirectory, className, psiClassFromEditor, methodPrefix);
    }

    private PsiElement createBuilder(Project project, PsiFieldsForMaker psiFieldsForMaker, PsiDirectory targetDirectory, String className, PsiClass psiClassFromEditor, String methodPrefix) {
        try {
            guiHelper.includeCurrentPlaceAsChangePlace(project);
            PsiClass targetClass = getBuilderPsiClass(project,
                    psiFieldsForMaker, targetDirectory, className, psiClassFromEditor, methodPrefix);
            navigateToClassAndPositionCursor(project, targetClass);
            return targetClass;
        } catch (IncorrectOperationException e) {
            showErrorMessage(project, className);
            e.printStackTrace();
            return null;
        }
    }

    private PsiClass getBuilderPsiClass(Project project, PsiFieldsForMaker psiFieldsForMaker, PsiDirectory targetDirectory, String className, PsiClass psiClassFromEditor, String methodPrefix) {
        MakerPsiClassBuilder builder = makerPsiClassBuilder.aBuilder(project, targetDirectory, psiClassFromEditor, className,
                psiFieldsForMaker)
                .withFields();
        return builder.build();
    }

    private void navigateToClassAndPositionCursor(Project project, PsiClass targetClass) {
        guiHelper.positionCursor(project, targetClass.getContainingFile(), targetClass.getLBrace());
    }

    private void showErrorMessage(Project project, String className) {
        Application application = psiHelper.getApplication();
        application.invokeLater(new MakerWriterErrorRunnable(project, className));
    }
}