package com.github.awesomelemon.deepapi;

import java.util.ArrayList;
import java.util.HashSet;
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
        HashSet<String> apiTypes = new HashSet<>();
        HashSet<String> apiMethods = new HashSet<>();
        for (String separatedCall : separatedCalls) {
            String[] typeAndMethod = separatedCall.split("\\.");
            assert typeAndMethod.length == 2;
            if (!(typeAndMethod[0].equals("List") || typeAndMethod[0].equals("ArrayList")
                    || typeAndMethod[0].equals("LinkedList") || typeAndMethod[0].equals("String"))) {
                if (apiTypes.size() >= 3) continue;
                apiTypes.add(typeAndMethod[0]);
            }
            if (!typeAndMethod[1].equals("new")) {
                if (apiMethods.size() >= 3) continue;
                apiMethods.add(typeAndMethod[1]);
            }
        }
        this.apiMethods = new ArrayList<>(apiMethods);
        this.apiTypes = new ArrayList<>(apiTypes);
    }

}
