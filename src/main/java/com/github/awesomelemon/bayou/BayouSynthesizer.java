package com.github.awesomelemon.bayou;

import com.github.awesomelemon.Utils;
import tanvd.bayou.implementation.BayouClient;
import tanvd.bayou.implementation.facade.SynthesisProgress;
import tanvd.bayou.implementation.model.SynthesizingModel;

import java.util.HashMap;
import java.util.Map;

public class BayouSynthesizer {
    private BayouSynthesizer() {

    }

    static BayouSynthesizer _synthesizer = null;

    public static BayouSynthesizer get() {
        if (_synthesizer == null) {
            _synthesizer = new BayouSynthesizer();
        }

        return _synthesizer;
    }

    Map<BayouSynthesizerType, SynthesizingModel> models = new HashMap<>();

    BayouResponse invoke(BayouSynthesizerType type, BayouRequest request, SynthesisProgress progress) {
        if (!models.containsKey(type)) {
            System.out.println("not contains");
            System.out.println(type.name().toLowerCase()+"/config.json");
            SynthesizingModel model = BayouClient.INSTANCE.getConfigurableModel(
                    Utils.convertStreamToString(
                            BayouSynthesizer.class.getResourceAsStream("/" + type.name().toLowerCase()+"/config.json")));
            models.put(type, model);
        }
        SynthesizingModel model = models.get(type);

//        BayouTextConverter.INSTANCE.fromProgramText(
        for (String synthesized : model.synthesize(
                BayouTextConverter.INSTANCE.toProgramText(request),
                100, progress)) {
            if (Utils.isNotBlank(synthesized)) {
                System.gc();
                return BayouTextConverter.INSTANCE.fromProgramText(synthesized);
            }
        }
        return null;
    }
}