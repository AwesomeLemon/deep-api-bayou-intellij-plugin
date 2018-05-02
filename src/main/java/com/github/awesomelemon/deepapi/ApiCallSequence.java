package com.github.awesomelemon.deepapi;

import java.util.ArrayList;
import java.util.List;

public class ApiCallSequence {
    private List<String> apiTypes;
    private List<String> apiMethods;

    public List<String> getApiTypes() {
        return apiTypes;
    }

    public List<String> getApiMethods() {
        return apiMethods;
    }

    public ApiCallSequence(List<String> apiTypes, List<String> apiMethods) {
        this.apiMethods = apiMethods;
        this.apiTypes = apiTypes;
    }

    public ApiCallSequence(String apiSequence) {
        String[] separatedCalls = apiSequence.split(" ");
        ArrayList<String> apiTypes = new ArrayList<>();
        ArrayList<String> apiMethods = new ArrayList<>();
        for (String separatedCall : separatedCalls) {
            String[] typeAndMethod = separatedCall.split("\\.");
            assert typeAndMethod.length == 2;
            apiTypes.add(typeAndMethod[0]);
            if (!typeAndMethod[1].equals("new")) {
                apiMethods.add(typeAndMethod[1]);
            }
        }
        this.apiMethods = apiMethods;
        this.apiTypes = apiTypes;
    }

}
