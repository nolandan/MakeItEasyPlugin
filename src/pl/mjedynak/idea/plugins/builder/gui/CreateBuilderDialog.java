package pl.mjedynak.idea.plugins.builder.gui;

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.RecentsManager;
import com.intellij.ui.ReferenceEditorComboWithBrowseButton;
import pl.mjedynak.idea.plugins.builder.factory.PackageChooserDialogFactory;
import pl.mjedynak.idea.plugins.builder.factory.ReferenceEditorComboWithBrowseButtonFactory;
import pl.mjedynak.idea.plugins.builder.gui.helper.GuiHelper;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyMethods"})
public class CreateBuilderDialog extends DialogWrapper {

    static final String RECENTS_KEY = "CreateBuilderDialog.RecentsKey";
    private static final int WIDTH = 40;

    private PsiHelper psiHelper;
    private GuiHelper guiHelper;
    private Project project;
    private PsiDirectory targetDirectory;
    private PsiClass sourceClass;
    private JTextField targetClassNameField;
    private JTextField targetMethodPrefix;
    private ReferenceEditorComboWithBrowseButton targetPackageField;

    public CreateBuilderDialog(Project project,
                               String title,
                               PsiClass sourceClass,
                               String targetClassName,
                               String methodPrefix,
                               PsiPackage targetPackage,
                               PsiHelper psiHelper,
                               GuiHelper guiHelper,
                               ReferenceEditorComboWithBrowseButtonFactory referenceEditorComboWithBrowseButtonFactory) {
        super(project, true);
        this.psiHelper = psiHelper;
        this.guiHelper = guiHelper;
        this.project = project;
        this.sourceClass = sourceClass;
        targetClassNameField = new JTextField(targetClassName);
        targetMethodPrefix = new JTextField(methodPrefix);
        setPreferredSize(targetClassNameField);
        setPreferredSize(targetMethodPrefix);

        String targetPackageName = (targetPackage != null) ? targetPackage.getQualifiedName() : "";
        targetPackageField = referenceEditorComboWithBrowseButtonFactory.getReferenceEditorComboWithBrowseButton(project, targetPackageName, RECENTS_KEY);
        targetPackageField.addActionListener(new ChooserDisplayerActionListener(targetPackageField, new PackageChooserDialogFactory(), project));
        setTitle(title);
    }

    @Override
    public void show() {
        super.init();
        super.show();
    }

    private void setPreferredSize(JTextField field) {
        Dimension size = field.getPreferredSize();
        FontMetrics fontMetrics = field.getFontMetrics(field.getFont());
        size.width = fontMetrics.charWidth('a') * WIDTH;
        field.setPreferredSize(size);
    }

    protected Action[] createActions() {
        return new Action[]{getOKAction(), getCancelAction(), getHelpAction()};
    }

    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbConstraints = new GridBagConstraints();

        panel.setBorder(IdeBorderFactory.createBorder());

        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 0;
        gbConstraints.weightx = 0;
        gbConstraints.gridwidth = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Class name"), gbConstraints);

        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 1;
        gbConstraints.weightx = 1;
        gbConstraints.gridwidth = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(targetClassNameField, gbConstraints);

        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 2;
        gbConstraints.weightx = 0;
        gbConstraints.gridwidth = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Method prefix"), gbConstraints);

        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 1;
        gbConstraints.weightx = 1;
        gbConstraints.gridwidth = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(targetMethodPrefix, gbConstraints);

        targetClassNameField.getDocument().addDocumentListener(new DocumentAdapter() {
            protected void textChanged(DocumentEvent e) {
                getOKAction().setEnabled(JavaPsiFacade.getInstance(project).getNameHelper().isIdentifier(getClassName()));
            }
        });

        gbConstraints.gridx = 0;
        gbConstraints.gridy = 3;
        gbConstraints.weightx = 0;
        gbConstraints.gridwidth = 1;
        panel.add(new JLabel(CodeInsightBundle.message("dialog.create.class.destination.package.label")), gbConstraints);

        gbConstraints.gridx = 1;
        gbConstraints.weightx = 1;

        AnAction clickAction = new AnAction() {
            public void actionPerformed(AnActionEvent e) {
                targetPackageField.getButton().doClick();
            }
        };
        clickAction.registerCustomShortcutSet(new CustomShortcutSet(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK)),
                targetPackageField.getChildComponent());

        addInnerPanel(panel, gbConstraints);

        return panel;
    }

    private void addInnerPanel(JPanel panel, GridBagConstraints gbConstraints) {
        JPanel innerPanel = createInnerPanel();
        panel.add(innerPanel, gbConstraints);
    }

    private JPanel createInnerPanel() {
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.add(targetPackageField, BorderLayout.CENTER);
        return innerPanel;
    }

    protected void doOKAction() {
        registerEntry(RECENTS_KEY, targetPackageField.getText());
        Module module = psiHelper.findModuleForPsiClass(sourceClass, project);
        if (module == null) {
            throw new IllegalStateException("Cannot find module for class " + sourceClass.getName());
        }
        OKActionRunnable okActionRunnable = new OKActionRunnable(this, psiHelper, guiHelper, project, module, getPackageName(), getClassName());
        executeCommand(okActionRunnable);
        callSuper();
    }

    void registerEntry(String key, String entry) {
        RecentsManager.getInstance(project).registerRecentEntry(key, entry);
    }

    void callSuper() {
        super.doOKAction();
    }

    void executeCommand(OKActionRunnable okActionRunnable) {
        CommandProcessor.getInstance().executeCommand(project, okActionRunnable, CodeInsightBundle.message("create.directory.command"), null);
    }

    private String getPackageName() {
        String name = targetPackageField.getText();
        return (name != null) ? name.trim() : "";
    }

    public JComponent getPreferredFocusedComponent() {
        return targetClassNameField;
    }

    public String getClassName() {
        return targetClassNameField.getText();
    }

    public String getMethodPrefix() {
        return targetMethodPrefix.getText();
    }

    public PsiDirectory getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(PsiDirectory targetDirectory) {
        this.targetDirectory = targetDirectory;
    }
}
