package uk.co.neylan.plugins.makeiteasy.main;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.Nullable;
import uk.co.neylan.plugins.makeiteasy.action.handler.DisplayChoosersRunnable;
import uk.co.neylan.plugins.makeiteasy.factory.PopupListFactory;
import uk.co.neylan.plugins.makeiteasy.finder.MakerFinder;
import pl.mjedynak.idea.plugins.builder.gui.displayer.PopupDisplayer;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;
import uk.co.neylan.plugins.makeiteasy.verifier.MakerVerifier;

import javax.swing.*;

class MakerEditorActionHandler extends EditorActionHandler {
    private PsiHelper psiHelper;
    private MakerVerifier makerVerifier;
    private PopupListFactory popupListFactory;
    private PopupDisplayer popupDisplayer;
    private MakerFinder makerFinder;
    private DisplayChoosersRunnable displayChoosersRunnable;

    public MakerEditorActionHandler(PsiHelper psiHelper,
                                    MakerVerifier makerVerifier,
                                    PopupListFactory popupListFactory,
                                    PopupDisplayer popupDisplayer,
                                    MakerFinder makerFinder,
                                    DisplayChoosersRunnable displayChoosersRunnable) {
        this.psiHelper = psiHelper;
        this.makerVerifier = makerVerifier;
        this.popupListFactory = popupListFactory;
        this.popupDisplayer = popupDisplayer;
        this.makerFinder = makerFinder;
        this.displayChoosersRunnable = displayChoosersRunnable;
    }

    @Override
    protected void doExecute(Editor editor, @Nullable Caret caret, DataContext dataContext) {
        Project project = (Project) dataContext.getData(DataKeys.PROJECT.getName());
        PsiClass psiClassFromEditor = psiHelper.getPsiClassFromEditor(editor, project);
        if (psiClassFromEditor != null) {
            navigateOrDisplay(editor, psiClassFromEditor, dataContext);
        }
    }

    private void navigateOrDisplay(Editor editor, PsiClass psiClassFromEditor, DataContext dataContext) {
        boolean isMaker = makerVerifier.isMaker(psiClassFromEditor);
        PsiClass classToGo = findClassToGo(psiClassFromEditor, isMaker);
        if (classToGo != null) {
            psiHelper.navigateToClass(classToGo);
        } else if (!isMaker) {
            displayPopup(editor, psiClassFromEditor, dataContext);
        }
    }

    private void displayPopup(final Editor editor, final PsiClass psiClassFromEditor, final DataContext dataContext) {
        JList popupList = popupListFactory.getPopupList();
        Project project = (Project) dataContext.getData(DataKeys.PROJECT.getName());
        displayChoosersRunnable.setEditor(editor);
        displayChoosersRunnable.setProject(project);
        displayChoosersRunnable.setPsiClassFromEditor(psiClassFromEditor);
        popupDisplayer.displayPopupChooser(editor, popupList, displayChoosersRunnable);
    }

    private PsiClass findClassToGo(PsiClass psiClassFromEditor, boolean isMaker) {
        if (isMaker) {
            return makerFinder.findClassForMaker(psiClassFromEditor);
        }
        return makerFinder.findMakerForClass(psiClassFromEditor);
    }
}
