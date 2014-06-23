package uk.co.neylan.plugins.makeiteasy.finder;

import com.intellij.psi.PsiClass;
import pl.mjedynak.idea.plugins.builder.finder.ClassFinder;

public class MakerFinder {

    static final String SEARCH_PATTERN = "Maker";
    public static final String EMPTY_STRING = "";

    private ClassFinder classFinder;

    public MakerFinder(ClassFinder classFinder) {
        this.classFinder = classFinder;
    }

    public PsiClass findMakerForClass(PsiClass psiClass) {
        String searchName = psiClass.getName() + SEARCH_PATTERN;
        return findClass(psiClass, searchName);
    }

    public PsiClass findClassForMaker(PsiClass psiClass) {
        String searchName = psiClass.getName().replaceFirst(SEARCH_PATTERN, EMPTY_STRING);
        return findClass(psiClass, searchName);
    }

    private PsiClass findClass(PsiClass psiClass, String searchName) {
        PsiClass result = null;
        if (typeIsCorrect(psiClass)) {
            result = classFinder.findClass(searchName, psiClass.getProject());
        }
        return result;
    }

    private boolean typeIsCorrect(PsiClass psiClass) {
        return !psiClass.isAnnotationType() && !psiClass.isEnum() && !psiClass.isInterface();
    }
}
