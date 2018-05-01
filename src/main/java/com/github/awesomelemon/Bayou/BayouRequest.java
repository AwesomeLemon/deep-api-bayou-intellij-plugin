package com.github.awesomelemon.Bayou;

import com.github.awesomelemon.DeepAPI.ApiCallSequence;

import java.util.List;

public class BayouRequest {
    private List<InputParameter> inputParameters;
    private ApiCallSequence apiCallSequence;

    public List<InputParameter> getInputParameters() {
        return inputParameters;
    }

    public ApiCallSequence getApiCallSequence() {
        return apiCallSequence;
    }

    public BayouRequest(List<InputParameter> inputParameters, ApiCallSequence apiCallSequence) {
        this.inputParameters = inputParameters;
        this.apiCallSequence = apiCallSequence;
    }

    static class InputParameter {
        private String name;
        private String klass;

        public String getName() {
            return name;
        }

        public String getKlass() {
            return klass;
        }

        public InputParameter(String name, String klass) {
            this.name = name;
            this.klass = klass;
        }
    }
}
