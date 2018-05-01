package com.github.awesomelemon.DeepAPI;

import com.github.awesomelemon.ModelProvider;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import tanvd.bayou.implementation.model.configurable.synthesizer.Bayou;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeepApiModelFacade {

    private final SavedModelBundle model;

    public DeepApiModelFacade() {
        ModelProvider modelProvider = new ModelProvider();
        TensorFlow.loadLibrary(modelProvider.getBeamOpsPath());
        model = SavedModelBundle.load(modelProvider.getExportedModelPath(), "serve");
    }

//    private int findMax(int a1, int a2) {
//        ///Pattern.matches
//
//    }

    private String generate(String input) {
//        if (true) return input;
        String[] tokens = input.split(" ");
        byte[][][] matrix = new byte[1][tokens.length][];
        for (int i = 0; i < tokens.length; i++) {
            try {
                matrix[0][i] = tokens[i].getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        Tensor<String> inputCallsTensor = Tensor.create(matrix, String.class);

        int[] ints = {tokens.length};
        Tensor<?> inputLengthTensor = Tensor.create(ints);

        Session session = model.session();
        Session.Runner runner = session.runner();
        runner.feed("Placeholder_1:0", inputLengthTensor);
        runner.feed("Placeholder:0", inputCallsTensor);
        List<Tensor<?>> results = runner.fetch("seq2seq/index_to_string_Lookup:0").run();
        inputCallsTensor.close();
        inputLengthTensor.close();

        Tensor<?> resultTensor = results.get(0);
        long[] shape = resultTensor.shape();
        int alternativesNum = (int) shape[1];
        int resultingTokensNum = (int) shape[2];
        byte[][][][] outMatr = new byte[1][alternativesNum][resultingTokensNum][];
        resultTensor.copyTo(outMatr);
        resultTensor.close();

        for (int i = 0; i < alternativesNum; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < resultingTokensNum; j++) {
                String word = new String(outMatr[0][i][j]);
                if (word.equals("</s>")) break;
                stringBuilder.append(word);
                stringBuilder.append(" ");
            }
            return stringBuilder.toString();
        }
        return null;
    }

    public ApiCallSequence generateBayouInput(String input) {
        String apiCallSequence = generate(input);
        return new ApiCallSequence(apiCallSequence);
    }
}
