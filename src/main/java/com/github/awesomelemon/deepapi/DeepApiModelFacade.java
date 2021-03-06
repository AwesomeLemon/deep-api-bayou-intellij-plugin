package com.github.awesomelemon.deepapi;

import com.github.awesomelemon.ModelProvider;
import com.intellij.openapi.progress.ProgressIndicator;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class DeepApiModelFacade {

    private final SavedModelBundle model;
    private static DeepApiModelFacade INSTANCE = null;

    private DeepApiModelFacade(ProgressIndicator progressIndicator) {
        ModelProvider modelProvider = new ModelProvider(new DeepApiDownloadProgressIndicatorWrapper(progressIndicator));
        model = SavedModelBundle.load(modelProvider.getExportedModelPath(), "serve");
    }

    public static DeepApiModelFacade load(ProgressIndicator progressIndicator) {
        if (INSTANCE == null) {
            INSTANCE = new DeepApiModelFacade(progressIndicator);
        }
        return INSTANCE;
    }

    private String generate(String input) {
//        if (true) return input;
        String[] tokens = input.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        byte[][][] matrix = new byte[1][tokens.length][];
        for (int i = 0; i < tokens.length; i++) {
            try {
                matrix[0][i] = tokens[i].getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
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

    public ApiCallSequence generateApiCallSequence(String request) {
        String generated = generate(request);
        if (generated == null) throw new RuntimeException("DeepAPI could not generate any sequences");
        return new ApiCallSequence(generated);
    }
}
