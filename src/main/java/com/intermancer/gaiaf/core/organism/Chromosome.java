package com.intermancer.gaiaf.core.organism;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.intermancer.gaiaf.core.experiment.GeneGenerator;
import com.intermancer.gaiaf.core.experiment.MutationCommand;
import com.intermancer.gaiaf.core.experiment.Mutational;

/**
 * Represents a Chromosome, which is an ordered list of Genes.
 * A Chromosome is a DataQuantumConsumer that processes a DataQuantum
 * by passing it to each of its Genes in order.
 */
public class Chromosome implements DataQuantumConsumer, Mutational {
    private List<Gene> genes;

    /**
     * Default constructor for Jackson deserialization.
     */
    public Chromosome() {
        this.genes = new ArrayList<>();
    }

    /**
     * Gets the list of Genes in this Chromosome.
     *
     * @return The list of Genes
     */
    public List<Gene> getGenes() {
        return genes;
    }

    /**
     * Sets the list of Genes for this Chromosome.
     *
     * @param genes The list of Genes to set
     */
    public void setGenes(List<Gene> genes) {
        this.genes = genes;
    }

    /**
     * Processes the given DataQuantum by passing it to each Gene
     * in the list, in order.
     *
     * @param dataQuantum The DataQuantum to process.
     */
    @Override
    public void consume(DataQuantum dataQuantum) {
        for (Gene gene : genes) {
            gene.consume(dataQuantum);
        }
    }
    
    /**
     * Creates a clone of this Chromosome.
     * The clone contains deep copies of all genes in this chromosome.
     *
     * @return A new Chromosome that is a deep copy of this one
     */
    public Chromosome copyOf() {
        Chromosome clone = new Chromosome();
        
        // Clone each gene and add it to the new chromosome
        for (Gene gene : this.genes) {
            clone.getGenes().add(gene.copyOf());
        }
        
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Chromosome other = (Chromosome) obj;
        
        // Compare genes list
        if (genes == null) {
            if (other.genes != null) return false;
        } else if (!genes.equals(other.genes)) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((genes == null) ? 0 : genes.hashCode());
        return result;
    }

/**
     * Implements the Mutational interface. Returns a list of possible mutations 
     * that can be applied to this Chromosome. This includes mutations for the 
     * chromosome itself, as well as the MutationCommands for each of its Genes.
     * 
     * @return List of MutationCommand objects
     */
    @Override
    public List<MutationCommand> getMutationCommandList() {
        List<MutationCommand> mutations = new ArrayList<>();
        Random random = new Random();
        
        // Add mutations for the chromosome itself
        
        // Move a random Gene to a different place in the List
        if (genes.size() > 1) {
            mutations.add(getExchangeGeneMutationCommand(random));
        }
        
        // Delete a random Gene
        if (!genes.isEmpty()) {
            mutations.add(getRemoveRandomGeneMutationCommand(random));
        }
        
        // Add a random Gene
        mutations.add(getAddRandomGeneMutationCommand(random));
        
        // Add mutations from each gene
        for (Gene gene : genes) {
            mutations.addAll(gene.getMutationCommandList());
        }
        
        return mutations;
    }

    private MutationCommand getExchangeGeneMutationCommand(Random random) {
        return new MutationCommand() {
            @Override
            public void execute() {
                int fromIndex = random.nextInt(genes.size());
                int toIndex = random.nextInt(genes.size());
                while (toIndex == fromIndex) {
                    toIndex = random.nextInt(genes.size());
                }
                Gene gene = genes.remove(fromIndex);
                genes.add(toIndex, gene);
            }
            
            @Override
            public String getDescription() {
                return "Move a random gene to a different position";
            }
        };
    }

    private MutationCommand getRemoveRandomGeneMutationCommand(Random random) {
        return new MutationCommand() {
            @Override
            public void execute() {
                int indexToRemove = random.nextInt(genes.size());
                genes.remove(indexToRemove);
            }
            
            @Override
            public String getDescription() {
                return "Delete a random gene";
            }
        };
    }

    private MutationCommand getAddRandomGeneMutationCommand(Random random) {
        return new MutationCommand() {
            @Override
            public void execute() {
                Gene newGene = GeneGenerator.getRandomGene();
                int insertIndex = genes.isEmpty() ? 0 : random.nextInt(genes.size() + 1);
                genes.add(insertIndex, newGene);
            }
            
            @Override
            public String getDescription() {
                return "Add a random gene at a random position";
            }
        };
    }
}