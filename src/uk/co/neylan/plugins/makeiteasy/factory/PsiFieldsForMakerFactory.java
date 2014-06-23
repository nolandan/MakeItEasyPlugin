package uk.co.neylan.plugins.makeiteasy.factory;

import com.intellij.codeInsight.generation.PsiElementClassMember;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import pl.mjedynak.idea.plugins.builder.psi.model.PsiFieldsForMaker;
import pl.mjedynak.idea.plugins.builder.verifier.PsiFieldVerifier;

import java.util.ArrayList;
import java.util.List;

public class PsiFieldsForMakerFactory {

    private PsiFieldVerifier psiFieldVerifier;

    public PsiFieldsForMakerFactory(PsiFieldVerifier psiFieldVerifier) {
        this.psiFieldVerifier = psiFieldVerifier;
    }

    public PsiFieldsForMaker createPsiFieldsForMaker(List<PsiElementClassMember> psiElementClassMembers,
                                                     PsiClass psiClass) {
        List<PsiField> psiFieldsForSetters = new ArrayList<PsiField>();
        List<PsiField> psiFieldsForConstructor = new ArrayList<PsiField>();
        for (PsiElementClassMember psiElementClassMember : psiElementClassMembers) {
            PsiElement psiElement = psiElementClassMember.getPsiElement();
            if (psiElement instanceof PsiField) {
                if (psiFieldVerifier.isSetInSetterMethod((PsiField) psiElement, psiClass)) {
                    psiFieldsForSetters.add((PsiField) psiElement);
                } else if (psiFieldVerifier.isSetInConstructor((PsiField) psiElement, psiClass)) {
                    psiFieldsForConstructor.add((PsiField) psiElement);
                }
            }
        }
        return new PsiFieldsForMaker(psiFieldsForSetters, psiFieldsForConstructor);
    }
}
