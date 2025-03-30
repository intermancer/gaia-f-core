# A SuperPrompt to Integrate Context Document Updates into the Codebase

I tried to create a good prompt to update my existing code.  I'm preserving it here for posterity.  It didn't work well, and I'm moving the valuable stuff over to the gen-ai-prompts.md doc.

> You are a professional back-end Java developer.  The context document `@gen-ai-context/OrganismDomainObjects.md` has changed.

> First use Git to compare this version to the previous committed version.

> Identify and list the various updated files, new files, and / or deleted files that are affected by the changes, along with the code changes that are necessary.

When this prompt was run, CoPilot created:
- com.intermancer.organism.gene.basic.BaseSingleDataPointGene
- com.intermancer.organism.gene.basic.BaseSingleDataPointGeneTest
- com.intermancer.organism.gene.basic.AdditionGene

I asked it to create AdditionGeneTest and it did so.