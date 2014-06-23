package uk.co.neylan.plugins.makeiteasy.psi;

import com.google.common.base.Predicate;
import com.intellij.codeInsight.generation.PsiElementClassMember;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import pl.mjedynak.idea.plugins.builder.factory.PsiElementClassMemberFactory;
import pl.mjedynak.idea.plugins.builder.verifier.PsiFieldVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Iterables.filter;

public class PsiFieldSelector {

    private PsiElementClassMemberFactory psiElementClassMemberFactory;
    private PsiFieldVerifier psiFieldVerifier;

    public PsiFieldSelector(PsiElementClassMemberFactory psiElementClassMemberFactory, PsiFieldVerifier psiFieldVerifier) {
        this.psiElementClassMemberFactory = psiElementClassMemberFactory;
        this.psiFieldVerifier = psiFieldVerifier;
    }

    public List<PsiElementClassMember> selectFieldsToIncludeInMaker(final PsiClass psiClass) {
        List<PsiElementClassMember> result = new ArrayList<PsiElementClassMember>();
        List<PsiField> psiFields = Arrays.asList(psiClass.getAllFields());
        Iterable<PsiField> filtered = filter(psiFields, new Predicate<PsiField>() {
            @Override
            public boolean apply(PsiField psiField) {
                return isAppropriate(psiClass, psiField);
            }
        });

        for (PsiField psiField : filtered) {
            result.add(psiElementClassMemberFactory.createPsiElementClassMember(psiField));
        }
        return result;
    }

    private boolean isAppropriate(PsiClass psiClass, PsiField psiField) {
        return psiFieldVerifier.isSetInSetterMethod(psiField, psiClass) || psiFieldVerifier.isSetInConstructor(psiField, psiClass);

    }


}
