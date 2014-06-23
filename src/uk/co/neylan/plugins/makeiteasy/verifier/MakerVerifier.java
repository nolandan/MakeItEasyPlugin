package uk.co.neylan.plugins.makeiteasy.verifier;

import com.intellij.psi.PsiClass;

public class MakerVerifier {

    private static final String SUFFIX = "Maker";

    public boolean isMaker(PsiClass psiClass) {
        return psiClass.getName().endsWith(SUFFIX);
    }
}
