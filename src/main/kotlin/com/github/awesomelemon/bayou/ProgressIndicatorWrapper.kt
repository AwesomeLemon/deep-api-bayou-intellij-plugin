package com.github.awesomelemon.bayou

/* author: tanvd */

import com.intellij.openapi.progress.ProgressIndicator
import tanvd.bayou.implementation.facade.SynthesisPhase
import tanvd.bayou.implementation.facade.SynthesisProgress

class ProgressIndicatorWrapper(val p: ProgressIndicator): SynthesisProgress {
    override var fraction: Double = 0.0
        set(value) {
            field = value
            p.fraction = value
        }
    override var phase: SynthesisPhase = SynthesisPhase.IDLE
        set(value) {
            field = value
            p.text = when (value) {
                SynthesisPhase.IDLE -> "Writing down"
                SynthesisPhase.Started -> "Started"
                SynthesisPhase.Parsing -> "Parsing Evidences"
                SynthesisPhase.Embedding -> "Wrangling Evidences"
                SynthesisPhase.SketchGeneration -> "Generating Sketches"
                SynthesisPhase.Concretization -> "Concretizating Sketches"
                SynthesisPhase.Finished -> "Done"
            }
        }

}
