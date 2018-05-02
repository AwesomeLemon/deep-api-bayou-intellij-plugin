package com.github.awesomelemon;

import com.github.awesomelemon.bayou.BayouModelFacade;
import com.github.awesomelemon.deepapi.ApiCallSequence;
import com.github.awesomelemon.deepapi.DeepApiModelFacade;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.formatting.WhiteSpace;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.*;

public class GenerateIntentionAction extends PsiElementBaseIntentionAction implements IntentionAction {

    private static final String INTENTION_PREFIX = "//";
    private BayouModelFacade bayouModelFacade;

    @NotNull
    public String getText() {
        return "Generate code";
    }

    @NotNull
    public String getFamilyName() {
        return getText();
    }

    private PsiComment asComment(@NotNull  PsiElement element) {
        if (!(element instanceof PsiComment)) {
            element = PsiTreeUtil.skipSiblingsBackward(element, WhiteSpace.class);
            if (!(element instanceof PsiComment)) return null;
        }
        return (PsiComment) element;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @Nullable PsiElement element) {
        if (element == null || getContainingMethod(element) == null) return false;

        PsiComment comment = asComment(element);
        return comment != null;
    }

    private PsiMethod getContainingMethod(PsiElement element) {
        while (element != null && !(element instanceof PsiMethod)) element = element.getParent();
        return (PsiMethod) element;
    }

    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
//        ProgressManager.getInstance().run(new Task.Backgroundable(project, "daf") {
//            public void run(ProgressIndicator indicator) {
//                indicator.setText("5 kilos of mushrooms and cellphone");
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                indicator.setFraction(0.5);  // halfway done
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        PsiComment comment = asComment(element);
        String request = comment.getText().substring(INTENTION_PREFIX.length());
        System.out.println(request);
        PsiMethod containingMethod = getContainingMethod(comment);

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "DeepAPI-Bayou Code Generation", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                DeepApiModelFacade deepApiModelFacade = DeepApiModelFacade.load(project);
                ApiCallSequence bayouInput = deepApiModelFacade.generateBayouInput(request);
                System.out.println(bayouInput.getApiMethods());
                System.out.println(bayouInput.getApiTypes());
                new BayouModelFacade(project, bayouInput, containingMethod).run();
            }
        });
        System.out.println("done!");
    }
}