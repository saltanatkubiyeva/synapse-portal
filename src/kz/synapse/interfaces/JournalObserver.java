package kz.synapse.interfaces;

import kz.synapse.models.Journal;
import kz.synapse.models.ResearchPaper;

public interface JournalObserver {
    void update(Journal journal, ResearchPaper paper);
}
