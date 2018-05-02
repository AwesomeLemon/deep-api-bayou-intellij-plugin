package com.github.awesomelemon.bayou;

import java.util.List;

public class BayouResponse {
    List<String> imports;
    String code;

    public BayouResponse(List<String> imports, String code) {
        this.imports = imports;
        this.code = code;
    }

    public List<String> getImports() {
        return imports;
    }

    public String getCode() {
        return code;
    }
}
