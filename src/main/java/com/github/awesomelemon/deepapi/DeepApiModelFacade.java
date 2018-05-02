package com.github.awesomelemon.deepapi;

import com.github.awesomelemon.ModelProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class DeepApiModelFacade {

    private final SavedModelBundle model;
    private static DeepApiModelFacade INSTANCE = null;

    private DeepApiModelFacade() {
        ModelProvider modelProvider = new ModelProvider();
//        TensorFlow.loadLibrary(modelProvider.getBeamOpsPath());
        model = SavedModelBundle.load(modelProvider.getExportedModelPath(), "serve");
    }

    public static DeepApiModelFacade load(Project project) {
        if (INSTANCE == null) {
//            ProgressManager instance = ProgressManager.getInstance();
//            instance.run(new Task.Backgroundable(project, "DeepAPI Model Download", true) {
//                @Override
//                public void run(@NotNull ProgressIndicator indicator) {
                    INSTANCE = new DeepApiModelFacade();
//                    ApplicationManager.getApplication().invokeLater()
//                }
//            });
        }
        return INSTANCE;
    }

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
