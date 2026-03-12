package com.intermancer.gaiaf.core.experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.intermancer.gaiaf.core.organism.Gene;
import com.intermancer.gaiaf.core.organism.gene.basic.AdditionGene;
import com.intermancer.gaiaf.core.organism.gene.basic.DataPointAdditionGene;
import com.intermancer.gaiaf.core.organism.gene.basic.DataPointDivisionGene;
import com.intermancer.gaiaf.core.organism.gene.basic.DataPointMultiplicationGene;
import com.intermancer.gaiaf.core.organism.gene.basic.DataPointSubtractionGene;
import com.intermancer.gaiaf.core.organism.gene.basic.DivisionGene;
import com.intermancer.gaiaf.core.organism.gene.basic.MultiplicationGene;
import com.intermancer.gaiaf.core.organism.gene.basic.SineGene;
import com.intermancer.gaiaf.core.organism.gene.basic.SubtractionGene;
import com.intermancer.gaiaf.core.organism.gene.control.AbsoluteValueGateGene;
import com.intermancer.gaiaf.core.organism.gene.control.BoundedScaleGene;
import com.intermancer.gaiaf.core.organism.gene.control.ClampGene;
import com.intermancer.gaiaf.core.organism.gene.control.MagnitudeGateGene;
import com.intermancer.gaiaf.core.organism.gene.control.ScaledBlendGene;
import com.intermancer.gaiaf.core.organism.gene.control.SelectMaxGene;
import com.intermancer.gaiaf.core.organism.gene.control.SelectMinGene;
import com.intermancer.gaiaf.core.organism.gene.control.SignGene;
import com.intermancer.gaiaf.core.organism.gene.control.ThresholdSwitchGene;
import com.intermancer.gaiaf.core.organism.gene.window.DelayGene;
import com.intermancer.gaiaf.core.organism.gene.window.ExponentialMovingAverageGene;
import com.intermancer.gaiaf.core.organism.gene.window.LinearProjectionGene;
import com.intermancer.gaiaf.core.organism.gene.window.MovingAverageGene;
import com.intermancer.gaiaf.core.organism.gene.window.MovingMedianGene;
import com.intermancer.gaiaf.core.organism.gene.window.MomentumGene;
import com.intermancer.gaiaf.core.organism.gene.window.RangeGene;
import com.intermancer.gaiaf.core.organism.gene.window.StandardDeviationGene;
import com.intermancer.gaiaf.core.organism.gene.window.ZScoreGene;

/**
 * Utility class for generating random genes to support mutation activities.
 *
 * <p>All three Gene categories are included in the random pool:
 * Basic Arithmetic (single- and multi-DataPoint), Window, and Control.
 */
public class GeneGenerator {

    private static final Random random = new Random();

    /** Total number of Gene types available. */
    private static final int GENE_TYPE_COUNT = 27;

    /**
     * Returns a single, randomly chosen Gene. If the Gene uses any operational
     * constants, they are randomly generated. Multi-index Genes keep their
     * default index configuration with randomized non-last indices.
     *
     * @return A randomly generated Gene.
     */
    public static Gene getRandomGene() {
        int geneType = random.nextInt(GENE_TYPE_COUNT);

        Gene gene = switch (geneType) {
            // Basic single-DataPoint (0-4)
            case 0  -> new AdditionGene();
            case 1  -> new SubtractionGene();
            case 2  -> new MultiplicationGene();
            case 3  -> new DivisionGene();
            case 4  -> new SineGene();
            // Basic multi-DataPoint (5-8)
            case 5  -> new DataPointAdditionGene();
            case 6  -> new DataPointSubtractionGene();
            case 7  -> new DataPointMultiplicationGene();
            case 8  -> new DataPointDivisionGene();
            // Window (9-16)
            case 9  -> new MovingAverageGene();
            case 10 -> new MovingMedianGene();
            case 11 -> new StandardDeviationGene();
            case 12 -> new MomentumGene();
            case 13 -> new DelayGene();
            case 14 -> new LinearProjectionGene();
            case 15 -> new ZScoreGene();
            case 16 -> new RangeGene();
            case 17 -> new ExponentialMovingAverageGene();
            // Control (18-25)
            case 18 -> new ClampGene();
            case 19 -> new ThresholdSwitchGene();
            case 20 -> new AbsoluteValueGateGene();
            case 21 -> new ScaledBlendGene();
            case 22 -> new SignGene();
            case 23 -> new MagnitudeGateGene();
            case 24 -> new BoundedScaleGene();
            case 25 -> new SelectMaxGene();
            case 26 -> new SelectMinGene();
            default -> new AdditionGene(); // unreachable
        };

        // Set a random ID
        gene.setId(gene.getClass().getSimpleName() + "-" + UUID.randomUUID().toString().substring(0, 8));

        // Randomize operation constants (preserve list size; replace each value)
        if (!gene.getOperationConstantList().isEmpty()) {
            for (int i = 0; i < gene.getOperationConstantList().size(); i++) {
                double randomConstant = 0.1 + (random.nextDouble() * 9.9);
                gene.getOperationConstantList().set(i, randomConstant);
            }
        }

        // Randomize non-last target indices for multi-index Genes
        randomizeTargetIndices(gene);

        return gene;
    }

    /**
     * Randomizes target indices for Genes that have more than one index.
     * The last index is always kept at -1 (chain convention). All prior indices
     * are assigned unique random values in the range [-2, -10].
     *
     * @param gene The Gene whose target indices to randomize.
     */
    private static void randomizeTargetIndices(Gene gene) {
        List<Integer> indexList = gene.getTargetIndexList();
        if (indexList.size() <= 1) {
            return;
        }

        List<Integer> used = new ArrayList<>();
        used.add(-1); // Last index is always -1

        for (int i = 0; i < indexList.size() - 1; i++) {
            int newIndex;
            do {
                newIndex = -(random.nextInt(9) + 2); // Random between -2 and -10
            } while (used.contains(newIndex));
            used.add(newIndex);
            indexList.set(i, newIndex);
        }
        // Ensure last index is -1
        indexList.set(indexList.size() - 1, -1);
    }
}
