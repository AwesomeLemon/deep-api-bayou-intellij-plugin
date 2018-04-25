package com.github.awesomelemon;

import com.github.awesomelemon.Bayou.BayouModelFacade;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.formatting.WhiteSpace;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.*;

public class GenerateIntentionAction extends PsiElementBaseIntentionAction implements IntentionAction {

    private static final String INTENTION_PREFIX = "///";

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
        if (element == null) return false;
        PsiComment comment = asComment(element);
        if (comment == null) return false;
//        if (!element.isWritable()) return false;

//        PsiElement comment = PsiTreeUtil.skipSiblingsBackward(currentPosition, PsiWhiteSpace.class);
        if (comment.getText().startsWith(INTENTION_PREFIX)) {
            return true;
        }


//        if (element instanceof PsiJavaToken) {
//            final PsiJavaToken token = (PsiJavaToken) element;
//            if (token.getTokenType() != JavaTokenType.QUEST) return false;
//            if (token.getParent() instanceof PsiConditionalExpression) {
//                final PsiConditionalExpression conditionalExpression = (PsiConditionalExpression) token.getParent();
//                if (conditionalExpression.getThenExpression() == null
//                        || conditionalExpression.getElseExpression() == null) {
//                    return false;
//                }
//                return true;
//            }
//            return false;
//        }
        return false;
    }

    private PsiMethod getContainingMethod(PsiElement element) {
        while (!(element instanceof PsiMethod)) element = element.getParent();
        return (PsiMethod) element;
    }

    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        PsiComment comment = asComment(element);
        String request = comment.getText().substring(INTENTION_PREFIX.length());
        System.out.println(request);
        BayouModelFacade bayouModelFacade = new BayouModelFacade(project);
        DeepApiModelFacade deepApiModelFacade = new DeepApiModelFacade();
        ApiCallSequence bayouInput = deepApiModelFacade.generateBayouInput(request);
        System.out.println(bayouInput.getApiMethods());
        System.out.println(bayouInput.getApiTypes());
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> bayouModelFacade.generate(bayouInput, getContainingMethod(comment)),
                "dsjall", true, project);
//        final int offset = editor.getCaretModel().getOffset();
//        PsiConditionalExpression conditionalExpression = PsiTreeUtil.getParentOfType(element,
//                PsiConditionalExpression.class, false);
//        if (conditionalExpression == null) return;
//        if (conditionalExpression.getThenExpression() == null || conditionalExpression.getElseExpression() == null) return;
//
//        final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
//
//        PsiElement originalStatement = PsiTreeUtil.getParentOfType(conditionalExpression, PsiStatement.class, false);
//        while (originalStatement instanceof PsiForStatement) {
//            originalStatement = PsiTreeUtil.getParentOfType(originalStatement, PsiStatement.class, true);
//        }
//        if (originalStatement == null) return;
//
//        // Maintain declrations
//        if (originalStatement instanceof PsiDeclarationStatement) {
//            final PsiDeclarationStatement declaration = (PsiDeclarationStatement) originalStatement;
//            final PsiElement[] declaredElements = declaration.getDeclaredElements();
//            PsiLocalVariable variable = null;
//            for (PsiElement declaredElement : declaredElements) {
//                if (declaredElement instanceof PsiLocalVariable &&
//                        PsiTreeUtil.isAncestor(declaredElement, conditionalExpression, true)) {
//                    variable = (PsiLocalVariable) declaredElement;
//                    break;
//                }
//            }
//            if (variable == null) return;
//            variable.normalizeDeclaration();
//            final Object marker = new Object();
//            PsiTreeUtil.mark(conditionalExpression, marker);
//            PsiExpressionStatement statement =
//                    (PsiExpressionStatement) factory.createStatementFromText(variable.getName() + " = 0;", null);
//            statement = (PsiExpressionStatement) CodeStyleManager.getInstance(project).reformat(statement);
//            ((PsiAssignmentExpression) statement.getExpression()).getRExpression().replace(variable.getInitializer());
//            variable.getInitializer().delete();
//            final PsiElement variableParent = variable.getParent();
//            originalStatement = variableParent.getParent().addAfter(statement, variableParent);
//            conditionalExpression = (PsiConditionalExpression) PsiTreeUtil.releaseMark(originalStatement, marker);
//        }
//
//        // create then and else branches
//        final PsiElement[] originalElements = new PsiElement[]{originalStatement, conditionalExpression};
//        final PsiExpression condition = (PsiExpression) conditionalExpression.getCondition().copy();
//        final PsiElement[] thenElements = PsiTreeUtil.copyElements(originalElements);
//        final PsiElement[] elseElements = PsiTreeUtil.copyElements(originalElements);
//        thenElements[1].replace(conditionalExpression.getThenExpression());
//        elseElements[1].replace(conditionalExpression.getElseExpression());

//        PsiIfStatement statement = (PsiIfStatement) factory.createStatementFromText("if (true) { a = b } else { c = d }",
//                null);
//        statement = (PsiIfStatement) CodeStyleManager.getInstance(project).reformat(statement);
//        statement.getCondition().replace(condition);
//        statement = (PsiIfStatement) originalStatement.replace(statement);
//
//        ((PsiBlockStatement) statement.getThenBranch()).getCodeBlock().getStatements()[0].replace(thenElements[0]);
//        ((PsiBlockStatement) statement.getElseBranch()).getCodeBlock().getStatements()[0].replace(elseElements[0]);
        System.out.println("done!");
    }

    public boolean startInWriteAction() {
        return true;
    }
}