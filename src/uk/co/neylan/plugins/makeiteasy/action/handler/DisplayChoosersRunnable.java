package uk.co.neylan.plugins.makeiteasy.action.handler;

import com.intellij.codeInsight.generation.PsiElementClassMember;
import com.intellij.ide.util.MemberChooser;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiPackage;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;
import pl.mjedynak.idea.plugins.builder.psi.model.PsiFieldsForMaker;
import uk.co.neylan.plugins.makeiteasy.factory.CreateMakerDialogFactory;
import uk.co.neylan.plugins.makeiteasy.factory.MemberChooserDialogFactory;
import uk.co.neylan.plugins.makeiteasy.factory.PsiFieldsForMakerFactory;
import uk.co.neylan.plugins.makeiteasy.gui.CreateMakerDialog;
import uk.co.neylan.plugins.makeiteasy.model.PropertyCase;
import uk.co.neylan.plugins.makeiteasy.psi.PsiFieldSelector;
import uk.co.neylan.plugins.makeiteasy.writer.MakerWriter;

import java.util.List;

public class DisplayChoosersRunnable implements Runnable {


    private PsiClass psiClassFromEditor;
    private Project project;
    private Editor editor;
    private PsiHelper psiHelper;
    private CreateMakerDialogFactory createMakerDialogFactory;
    private PsiFieldSelector psiFieldSelector;
    private MemberChooserDialogFactory memberChooserDialogFactory;
    private MakerWriter makerWriter;
    private PsiFieldsForMakerFactory psiFieldsForMakerFactory;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public DisplayChoosersRunnable(PsiHelper psiHelper, CreateMakerDialogFactory createMakerDialogFactory,
                                   PsiFieldSelector psiFieldSelector, MemberChooserDialogFactory memberChooserDialogFactory,
                                   MakerWriter makerWriter, PsiFieldsForMakerFactory psiFieldsForMakerFactory) {
        this.psiHelper = psiHelper;
        this.createMakerDialogFactory = createMakerDialogFactory;
        this.psiFieldSelector = psiFieldSelector;
        this.memberChooserDialogFactory = memberChooserDialogFactory;
        this.makerWriter = makerWriter;
        this.psiFieldsForMakerFactory = psiFieldsForMakerFactory;
    }

    @Override
    public void run() {
        CreateMakerDialog createMakerDialog = showDialog();
        if (createMakerDialog.isOK()) {
            PsiDirectory targetDirectory = createMakerDialog.getTargetDirectory();
            String className = createMakerDialog.getClassName();
            PropertyCase propertyCase = createMakerDialog.getPropertyCase();
            List<PsiElementClassMember> fieldsToDisplay = getFieldsToIncludeInMaker(psiClassFromEditor);
            MemberChooser<PsiElementClassMember> memberChooserDialog = memberChooserDialogFactory.getMemberChooserDialog(fieldsToDisplay, project);
            memberChooserDialog.show();
            writeMakerIfNecessary(targetDirectory, className, propertyCase, memberChooserDialog);
        }
    }

    private void writeMakerIfNecessary(PsiDirectory targetDirectory,
                                       String className,
                                       PropertyCase propertyCase,
                                       MemberChooser<PsiElementClassMember> memberChooserDialog) {
        if (memberChooserDialog.isOK()) {
            List<PsiElementClassMember> selectedElements = memberChooserDialog.getSelectedElements();
            PsiFieldsForMaker psiFieldsForMaker = psiFieldsForMakerFactory.createPsiFieldsForMaker(selectedElements,
                    psiClassFromEditor);
            makerWriter.writeMaker(project,
                    psiFieldsForMaker, targetDirectory, className, psiClassFromEditor, propertyCase);
        }
    }

    private CreateMakerDialog showDialog() {
        PsiDirectory srcDir = psiHelper.getPsiFileFromEditor(editor, project).getContainingDirectory();
        PsiPackage srcPackage = psiHelper.getPackage(srcDir);
        CreateMakerDialog dialog = createMakerDialogFactory.createMakerDialog(psiClassFromEditor, project, srcPackage);
        dialog.show();
        return dialog;
    }

    private List<PsiElementClassMember> getFieldsToIncludeInMaker(PsiClass clazz) {
        return psiFieldSelector.selectFieldsToIncludeInMaker(clazz);
    }

    public void setPsiClassFromEditor(PsiClass psiClassFromEditor) {
        this.psiClassFromEditor = psiClassFromEditor;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }
}
