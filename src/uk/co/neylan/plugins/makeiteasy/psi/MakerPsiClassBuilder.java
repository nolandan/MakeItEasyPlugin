package uk.co.neylan.plugins.makeiteasy.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportHolder;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.text.StringTokenizer;
import org.apache.commons.lang.StringUtils;
import pl.mjedynak.idea.plugins.builder.finder.ClassFinder;
import pl.mjedynak.idea.plugins.builder.psi.MethodNameCreator;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;
import pl.mjedynak.idea.plugins.builder.psi.model.PsiFieldsForMaker;
import uk.co.neylan.plugins.makeiteasy.model.PropertyCase;

import java.util.List;

@SuppressWarnings("PMD.TooManyMethods")
public class MakerPsiClassBuilder {

    private static final String PRIVATE_STRING = "private";
    private static final String SPACE = " ";
    private static final String A_PREFIX = " a";
    private static final String AN_PREFIX = " an";
    private static final String SEMICOLON = ",";
    private static final String FINAL = "final";

    private PsiHelper psiHelper;
    private ClassFinder classFinder;
    private MethodNameCreator methodNameCreator = new MethodNameCreator();

    private Project project = null;
    private PsiDirectory targetDirectory = null;
    private PsiClass srcClass = null;
    private String builderClassName = null;
    private PropertyCase propertyCase;

    private List<PsiField> psiFieldsForSetters = null;
    private List<PsiField> psiFieldsForConstructor = null;

    private PsiClass builderClass = null;
    private PsiElementFactory elementFactory = null;
    private String srcClassName = null;
    private String srcClassFieldName = null;

    public MakerPsiClassBuilder(PsiHelper psiHelper, ClassFinder classFinder) {
        this.psiHelper = psiHelper;
        this.classFinder = classFinder;
    }

    public MakerPsiClassBuilder aBuilder(Project project,
                                         PsiDirectory targetDirectory,
                                         PsiClass psiClass,
                                         String builderClassName,
                                         PsiFieldsForMaker psiFieldsForMaker,
                                         PropertyCase propertyCase) {
        this.project = project;
        this.targetDirectory = targetDirectory;
        this.srcClass = psiClass;
        this.builderClassName = builderClassName;
        this.propertyCase = propertyCase;
        JavaDirectoryService javaDirectoryService = psiHelper.getJavaDirectoryService();
        builderClass = javaDirectoryService.createClass(targetDirectory, builderClassName);
        JavaPsiFacade javaPsiFacade = psiHelper.getJavaPsiFacade(project);
        elementFactory = javaPsiFacade.getElementFactory();
        addImports(project, javaPsiFacade);
        srcClassName = psiClass.getName();
        srcClassFieldName = StringUtils.uncapitalize(srcClassName);
        psiFieldsForSetters = psiFieldsForMaker.getFieldsForSetters();
        psiFieldsForConstructor = psiFieldsForMaker.getFieldsForConstructor();
        return this;
    }

    private void addImports(Project project, JavaPsiFacade javaPsiFacade) {
        PsiFile containingFile = builderClass.getContainingFile();
        if (containingFile instanceof PsiImportHolder) {
            addImport(project, javaPsiFacade, (PsiImportHolder) containingFile, "com.natpryce.makeiteasy.Property");
            addImport(project, javaPsiFacade, (PsiImportHolder) containingFile, "com.natpryce.makeiteasy.Instantiator");
            addImport(project, javaPsiFacade, (PsiImportHolder) containingFile, "com.natpryce.makeiteasy.PropertyLookup");
        }
    }

    private void addImport(Project project,
                           JavaPsiFacade javaPsiFacade,
                           PsiImportHolder containingFile,
                           String propertyClass) {
        PsiClass classToImport = javaPsiFacade.findClass(
                propertyClass,
                GlobalSearchScope.allScope(project));
        if (classToImport != null) {
            containingFile.importClass(classToImport);
        }
    }

    public MakerPsiClassBuilder withFields() {
        checkClassFieldsRequiredForBuilding();
        for (PsiField psiFieldsForSetter : psiFieldsForSetters) {
            removeModifiers(psiFieldsForSetter);
        }
        for (PsiField psiFieldForConstructor : psiFieldsForConstructor) {
            removeModifiers(psiFieldForConstructor);
        }
        return this;
    }

    private void removeModifiers(PsiField psiField) {
        String fieldName = psiField.getName();
        String fieldType = getFieldType(psiField);

        PsiField field = elementFactory.createFieldFromText(
                "public static final Property<" + srcClassName + ", " + fieldType + "> " + getPropertyName(
                        fieldName) + " = Property.newProperty();",
                psiField
        );

        builderClass.add(field);
    }

    private String getPropertyName(String fieldName) {
        if (propertyCase.equals(PropertyCase.UPPERCASE)) {
            new StringTokenizer(fieldName);
        }
        return fieldName;
    }

    private String getFieldType(PsiField psiField) {
        PsiType psiFieldType = psiField.getType();
        if (psiFieldType instanceof PsiPrimitiveType) {

            PsiClassType boxedType = ((PsiPrimitiveType) psiFieldType).getBoxedType(psiField);
            if (boxedType != null) {
                return boxedType.getPresentableText();
            }
        }
        return psiFieldType.getPresentableText();
    }

    public PsiClass build() {
        checkBuilderField();
        StringBuilder buildInstantiateField = new StringBuilder();
        String constructorParameters = createConstructorParameters();
        buildInstantiateField
                .append("public static Instantiator<").append(srcClassName).append("> ")
                .append(srcClassFieldName)
                .append(" = new Instantiator<").append(srcClassName).append(">")
                .append(" () { ").append("@Override\n" + "        public ").append(srcClassName).append(
                " instantiate(PropertyLookup<").append(srcClassName).append("> lookup) {")
                .append(srcClassName).append(SPACE)
                .append(srcClassFieldName).append(" = new ").append(srcClassName).append("(").append(
                constructorParameters).append(");");

        for (PsiField psiFieldsForSetter : psiFieldsForSetters) {
            String fieldName = psiFieldsForSetter.getName();
            String fieldNameUppercase = StringUtils.capitalize(fieldName);
            buildInstantiateField.append(srcClassFieldName).append(".set").append(fieldNameUppercase).append("(").append(
                    getFieldNameLookup(fieldName, psiFieldsForSetter.getType())).append(");");
        }
        buildInstantiateField.append("return ").append(srcClassFieldName).append(";}").append("};");
        PsiField makerField = elementFactory.createFieldFromText(buildInstantiateField.toString(), srcClass);
//        makerField.getNode().
//        builderClass.getContainingFile().getNode().addChild(ASTFactory.whitespace("\n"));
        builderClass.add(makerField);
        return builderClass;
    }

    private String getFieldNameLookup(String fieldName, PsiType type) {
        return "lookup.valueOf(" + fieldName + ", " + deriveType(type) + ")";
    }

    private String deriveType(PsiType type) {
        if (type.equalsToText("java.lang.String")) {
            return "\"\"";
        }
        String defaultValueOfType = PsiTypesUtil.getDefaultValueOfType(type);
        if (defaultValueOfType.equals("null")) {
            return "(" + type.getPresentableText() + ") " + defaultValueOfType;
        }
        return defaultValueOfType;
    }

    private String createConstructorParameters() {
        StringBuilder sb = new StringBuilder();
        for (PsiField psiField : psiFieldsForConstructor) {
            sb.append(getFieldNameLookup(psiField.getName(), psiField.getType())).append(
                    SEMICOLON);
        }
        removeLastSemicolon(sb);
        return sb.toString();
    }

    private void removeLastSemicolon(StringBuilder sb) {
        if (sb.toString().endsWith(SEMICOLON)) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    private void checkBuilderField() {
        if (builderClass == null) {
            throw new IllegalStateException("Builder field not created. Invoke at least aBuilder() method before.");
        }
    }

    private void checkClassFieldsRequiredForBuilding() {
        if (anyFieldIsNull()) {
            throw new IllegalStateException("Fields not set. Invoke aBuilder() method before.");
        }
    }

    private boolean anyFieldIsNull() {
        return (project == null || targetDirectory == null || srcClass == null || builderClassName == null
                || psiFieldsForSetters == null || psiFieldsForConstructor == null);
    }


}
