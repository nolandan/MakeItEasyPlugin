package uk.co.neylan.plugins.makeiteasy.main;

import com.intellij.openapi.editor.actionSystem.EditorAction;
import pl.mjedynak.idea.plugins.builder.action.handler.DisplayChoosersRunnable;
import pl.mjedynak.idea.plugins.builder.factory.PopupListFactory;
import pl.mjedynak.idea.plugins.builder.finder.BuilderFinder;
import pl.mjedynak.idea.plugins.builder.gui.displayer.PopupDisplayer;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;
import pl.mjedynak.idea.plugins.builder.verifier.BuilderVerifier;

public class GoToMakerAction extends EditorAction {

    protected GoToMakerAction() {
        super(new MakerEditorActionHandler(
                new PsiHelper(),
                new BuilderVerifier(),
                new PopupListFactory(),
                new PopupDisplayer(null),
                new BuilderFinder(null),
                new DisplayChoosersRunnable(null, null, null, null, null, null)));
    }

}
