package kz.synapse.enums;

/** к какому периоду относится балл за урок. */
public enum AttestationPeriod {
    ATT1,   // первые ~7 недель  → макс 30 баллов
    ATT2,   // следующие ~7 недель → макс 30 баллов
    FINAL   // финальный экзамен   → макс 40 баллов, только Teacher
}
