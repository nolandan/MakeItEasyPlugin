package uk.co.neylan.plugins.makeiteasy.main;

import com.intellij.openapi.editor.actionSystem.EditorAction;
import uk.co.neylan.plugins.makeiteasy.action.handler.DisplayChoosersRunnable;
import uk.co.neylan.plugins.makeiteasy.factory.CreateMakerDialogFactory;
import uk.co.neylan.plugins.makeiteasy.factory.MemberChooserDialogFactory;
import pl.mjedynak.idea.plugins.builder.factory.PopupChooserBuilderFactory;
import uk.co.neylan.plugins.makeiteasy.factory.PopupListFactory;
import pl.mjedynak.idea.plugins.builder.factory.PsiElementClassMemberFactory;
import uk.co.neylan.plugins.makeiteasy.factory.PsiFieldsForMakerFactory;
import pl.mjedynak.idea.plugins.builder.factory.ReferenceEditorComboWithBrowseButtonFactory;
import uk.co.neylan.plugins.makeiteasy.writer.MakerWriter;
import uk.co.neylan.plugins.makeiteasy.finder.MakerFinder;
import pl.mjedynak.idea.plugins.builder.finder.ClassFinder;
import pl.mjedynak.idea.plugins.builder.gui.displayer.PopupDisplayer;
import pl.mjedynak.idea.plugins.builder.gui.helper.GuiHelper;
import uk.co.neylan.plugins.makeiteasy.psi.BuilderPsiClassBuilder;
import uk.co.neylan.plugins.makeiteasy.psi.PsiFieldSelector;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;
import uk.co.neylan.plugins.makeiteasy.verifier.MakerVerifier;
import pl.mjedynak.idea.plugins.builder.verifier.PsiFieldVerifier;

public class GoToMakerAction extends EditorAction {

    private static MakerEditorActionHandler handler;

    static {
        PsiHelper psiHelper = new PsiHelper();
        GuiHelper guiHelper = new GuiHelper();
        PsiFieldVerifier psiFieldVerifier = new PsiFieldVerifier();

        handler = new MakerEditorActionHandler(
                psiHelper,
                new MakerVerifier(),
                new PopupListFactory(),
                new PopupDisplayer(new PopupChooserBuilderFactory()),
                new MakerFinder(new ClassFinder(psiHelper)),
                new DisplayChoosersRunnable(
                        psiHelper,
                        new CreateMakerDialogFactory(
                                psiHelper,
                                new ReferenceEditorComboWithBrowseButtonFactory(),
                                guiHelper
                        ),
                        new PsiFieldSelector(
                                new PsiElementClassMemberFactory(),
                                psiFieldVerifier
                        ),
                        new MemberChooserDialogFactory(),
                        new MakerWriter(
                                new BuilderPsiClassBuilder(psiHelper),
                                psiHelper,
                                guiHelper),
                        new PsiFieldsForMakerFactory(psiFieldVerifier)));
    }

    protected GoToMakerAction() {
        super(handler);
    }

}
