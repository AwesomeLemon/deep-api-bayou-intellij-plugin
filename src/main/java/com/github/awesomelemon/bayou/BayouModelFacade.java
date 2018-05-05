package com.github.awesomelemon.bayou;

import com.github.awesomelemon.deepapi.ApiCallSequence;
import com.github.awesomelemon.Utils;
import com.intellij.openapi.application.ApplicationManager;
//import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import kotlin.Unit;
import com.github.awesomelemon.CodeUtils;
import com.github.awesomelemon.PsiUtils;

import java.util.ArrayList;
import java.util.List;
//import tanvd.bayou.prototype.annotations.*;
//import tanvd.bayou.prototype.BayouProgressIndicatorWrapper;


public class BayouModelFacade implements Runnable {
    Project project;
    ApiCallSequence input;
    PsiMethod method;

    public Project getProject() {
        return project;
    }

    public BayouModelFacade(Project project, ApiCallSequence input, PsiMethod method) {
        this.project = project;
        this.input = input;
        this.method = method;
    }

//    public static void generate(ApiCallSequence input,PsiMethod method, Project project) {
//        ProgressManager.getInstance().runProcessWithProgressSynchronously(
//                new BayouModelFacade(project, input, method), "abc", true, project);
//
//    }

    public boolean invokeBayou(PsiMethod method, ApiCallSequence apiCallSequence, ProgressIndicator indicator) {
        BayouSynthesizerType model = BayouSynthesizerType.StdLib;
//        BayouSynthesizerType model = BayouSynthesizerType.Android;

//        val apiCalls = ArrayList<String>()
//        val apiTypes = ArrayList<String>()
//
//        model = processAnnotations(method, model, contextClasses, apiCalls, apiTypes)
//
//        if (fixMistakes(model, contextClasses, apiCalls, apiTypes, method)) return true

        List<BayouRequest.InputParameter> inputParams = new ArrayList<>();
        ReadAction.run(() -> getMethodInputParameters(method, inputParams));

//        if (!apiCallSequence.getApiMethods().isEmpty()) {
            BayouResponse response = BayouSynthesizer.get().invoke(model, new BayouRequest(inputParams, apiCallSequence),
                    new BayouProgressIndicatorWrapper(indicator));
            final PsiCodeBlock codeBlock;
            if (response != null) {
                String code = response.getCode();
                List<String> imports = response.getImports();
                String qualifiedCode = CodeUtils.INSTANCE.qualifyWithImports(code, imports);
                codeBlock = PsiUtils.INSTANCE.createImportsShortenedBlock("{\n " + qualifiedCode + " \n}", project);
            } else {
                codeBlock = PsiUtils.INSTANCE.createCodeBlock("{\n // Something went wrong. Please try again with other input.\n}", project);
            }
            ApplicationManager.getApplication().invokeLater(() ->
                    PsiUtils.INSTANCE.executeWriteAction(project, method.getContainingFile(), () -> {
                                PsiCodeBlock body = method.getBody();
                                if (body != null) body.add(codeBlock);
                                PsiUtils.INSTANCE.reformatFile(method.getContainingFile(), project);
                                return Unit.INSTANCE;
                            }
                    ));
//        }
        return false;
    }

    private void getMethodInputParameters(PsiMethod method, List<BayouRequest.InputParameter> inputParams ) {
        for (PsiParameter param : method.getParameterList().getParameters()) {
            if (param.getName() != null && Utils.getQualifiedTypeName(param.getType()) != null) {
                inputParams.add(new BayouRequest.InputParameter(param.getName(), param.getType().getCanonicalText()));
            }
        }
    }

    @Override
    public void run() {
        ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
        invokeBayou(method, input, indicator);
    }

}
