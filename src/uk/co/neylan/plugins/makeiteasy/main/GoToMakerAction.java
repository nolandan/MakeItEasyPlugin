package uk.co.neylan.plugins.makeiteasy.main;

import com.intellij.openapi.editor.actionSystem.EditorAction;
import pl.mjedynak.idea.plugins.builder.factory.PopupChooserBuilderFactory;
import pl.mjedynak.idea.plugins.builder.factory.PsiElementClassMemberFactory;
import pl.mjedynak.idea.plugins.builder.factory.ReferenceEditorComboWithBrowseButtonFactory;
import pl.mjedynak.idea.plugins.builder.finder.ClassFinder;
import pl.mjedynak.idea.plugins.builder.gui.displayer.PopupDisplayer;
import pl.mjedynak.idea.plugins.builder.gui.helper.GuiHelper;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;
import pl.mjedynak.idea.plugins.builder.verifier.PsiFieldVerifier;
import uk.co.neylan.plugins.makeiteasy.action.handler.DisplayChoosersRunnable;
import uk.co.neylan.plugins.makeiteasy.factory.CreateMakerDialogFactory;
import uk.co.neylan.plugins.makeiteasy.factory.MemberChooserDialogFactory;
import uk.co.neylan.plugins.makeiteasy.factory.PopupListFactory;
import uk.co.neylan.plugins.makeiteasy.factory.PsiFieldsForMakerFactory;
import uk.co.neylan.plugins.makeiteasy.finder.MakerFinder;
import uk.co.neylan.plugins.makeiteasy.psi.MakerPsiClassBuilder;
import uk.co.neylan.plugins.makeiteasy.psi.PsiFieldSelector;
import uk.co.neylan.plugins.makeiteasy.verifier.MakerVerifier;
import uk.co.neylan.plugins.makeiteasy.writer.MakerWriter;

public class GoToMakerAction extends EditorAction {

    private static MakerEditorActionHandler handler;

    static {
        PsiHelper psiHelper = new PsiHelper();
        GuiHelper guiHelper = new GuiHelper();
        PsiFieldVerifier psiFieldVerifier = new PsiFieldVerifier();
        ClassFinder classFinder = new ClassFinder(psiHelper);

        handler = new MakerEditorActionHandler(
                psiHelper,
                new MakerVerifier(),
                new PopupListFactory(),
                new PopupDisplayer(new PopupChooserBuilderFactory()),
                new MakerFinder(classFinder),
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
                                new MakerPsiClassBuilder(psiHelper, classFinder),
                                psiHelper,
                                guiHelper),
                        new PsiFieldsForMakerFactory(psiFieldVerifier)));
    }

    protected GoToMakerAction() {
        super(handler);
    }

}
