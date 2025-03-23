# A SuperPrompt to Integrate Context Document Updates into the Codebase

You are a professional back-end Java developer.  The context document `@gen-ai-context/OrganismDomainObjects.md` has changed.

First use Git to compare this version to the previous committed version.

Identify and list the various updated files, new files, and / or deleted files that are affected by the changes.  Ask me if I agree before proceeding.

# A single-shot approach

Keep issuing this command in a fresh context, and editing BasicArithmeticGenes.md until it is right.  Then execute on that.

> @OrganismDomainObjecst.md describes the java files in the com.intermancer.gaiaf.core.organism package. @BasicArithmeticGenes.md supplements @OrganismDomainObjects.md and describes java files in the com.intermancer.gaiaf.core.organism.gene.basic package. Using the current workspace, create the java interfaces, classes, and test classes to implement the Single-DataPoint Genes described in @BasicArithmeticGenes.md.