package uk.co.neylan.plugins.makeiteasy.main;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.Nullable;
import pl.mjedynak.idea.plugins.builder.action.handler.DisplayChoosersRunnable;
import pl.mjedynak.idea.plugins.builder.factory.PopupListFactory;
import pl.mjedynak.idea.plugins.builder.finder.BuilderFinder;
import pl.mjedynak.idea.plugins.builder.gui.displayer.PopupDisplayer;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;
import pl.mjedynak.idea.plugins.builder.verifier.BuilderVerifier;

import javax.swing.*;

class MakerEditorActionHandler extends EditorActionHandler {
    private PsiHelper psiHelper;
    private BuilderVerifier builderVerifier;
    private PopupListFactory popupListFactory;
    private PopupDisplayer popupDisplayer;
    private BuilderFinder builderFinder;
    private DisplayChoosersRunnable displayChoosersRunnable;

    public MakerEditorActionHandler(PsiHelper psiHelper,
                                    BuilderVerifier builderVerifier,
                                    PopupListFactory popupListFactory,
                                    PopupDisplayer popupDisplayer,
                                    BuilderFinder builderFinder,
                                    DisplayChoosersRunnable displayChoosersRunnable) {
        this.psiHelper = psiHelper;
        this.builderVerifier = builderVerifier;
        this.popupListFactory = popupListFactory;
        this.popupDisplayer = popupDisplayer;
        this.builderFinder = builderFinder;
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
        boolean isBuilder = builderVerifier.isBuilder(psiClassFromEditor);
        PsiClass classToGo = findClassToGo(psiClassFromEditor, isBuilder);
        if (classToGo != null) {
            psiHelper.navigateToClass(classToGo);
        } else if (!isBuilder) {
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

    private PsiClass findClassToGo(PsiClass psiClassFromEditor, boolean isBuilder) {
        if (isBuilder) {
            return builderFinder.findClassForBuilder(psiClassFromEditor);
        }
        return builderFinder.findBuilderForClass(psiClassFromEditor);
    }
}
