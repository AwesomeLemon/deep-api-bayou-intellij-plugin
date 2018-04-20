package com.github.awesomelemon.Bayou;

import com.github.awesomelemon.ApiCallSequence;
import com.github.awesomelemon.Utils;
import com.intellij.openapi.application.ApplicationManager;
//import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import kotlin.Unit;
import tanvd.bayou.prototype.utils.CodeUtils;
import tanvd.bayou.prototype.utils.PsiUtils;

import java.util.ArrayList;
import java.util.List;
//import tanvd.bayou.prototype.annotations.*;
//import tanvd.bayou.prototype.ProgressIndicatorWrapper;


public class BayouModelFacade {
    Project project;

    public BayouModelFacade(Project project) {
        this.project = project;
    }

    public void generate(ApiCallSequence input, PsiMethod method) {
        ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
        processMethodChe(method, input, indicator);

    }

    public boolean processMethodChe(PsiMethod method, ApiCallSequence apiCallSequence, ProgressIndicator indicator) {
        BayouSynthesizerType model = BayouSynthesizerType.StdLib;

//        val apiCalls = ArrayList<String>()
//        val apiTypes = ArrayList<String>()
//
//        model = processAnnotations(method, model, contextClasses, apiCalls, apiTypes)
//
//        if (fixMistakes(model, contextClasses, apiCalls, apiTypes, method)) return true

        ArrayList<BayouRequest.InputParameter> inputParams = new ArrayList<>();
        for (PsiParameter param : method.getParameterList().getParameters()) {
            if (param.getName() != null && Utils.getQualifiedTypeName(param.getType()) != null) {
                inputParams.add(new BayouRequest.InputParameter(param.getName(), param.getType().getCanonicalText()));
            }
        }

        if (!apiCallSequence.getApiMethods().isEmpty()) {
            BayouResponse response = BayouSynthesizer.get().invoke(model, new BayouRequest(inputParams, apiCallSequence), new ProgressIndicatorWrapper(indicator));
            final PsiCodeBlock codeBlock;
            if (response != null) {
                String code = response.getCode();
                List<String> imports = response.getImports();
                String qualifiedCode = CodeUtils.INSTANCE.qualifyWithImports(code, imports);
                codeBlock = PsiUtils.INSTANCE.createImportsShortenedBlock("{\n " + qualifiedCode + " \n}", project);
            } else {
                codeBlock = PsiUtils.INSTANCE.createCodeBlock("{\n // Something went wrong. Try again with other params.\n}", project);
            }
            ApplicationManager.getApplication().invokeLater(() ->
                    PsiUtils.INSTANCE.executeWriteAction(project, method.getContainingFile(), () -> {
                        PsiCodeBlock body = method.getBody();
                        if (body != null) body.replace(codeBlock);
                        PsiUtils.INSTANCE.reformatFile(method.getContainingFile(), project);
                        return Unit.INSTANCE;
                    }
            ));
        }
        return false;
    }
}