package com.github.awesomelemon;

public class DeepApiModelFacade {
    private String generate(String input) {
        return "Random.nextInt";
    }

    public ApiCallSequence generateBayouInput(String input) {
        String apiCallSequence = generate(input);
        return new ApiCallSequence(apiCallSequence);
    }
}
