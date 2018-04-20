package com.github.awesomelemon;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;

public class Utils {
    private Utils(){}
    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String getQualifiedTypeName(PsiType type) {
        PsiClass psiClass = PsiUtil.resolveClassInType(type);
        if (psiClass == null) return null;
        return psiClass.getQualifiedName();
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    static boolean isBlank(String s) {
        if (s.length() == 0) return true;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) return false;
        }
        return true;
    }
}
