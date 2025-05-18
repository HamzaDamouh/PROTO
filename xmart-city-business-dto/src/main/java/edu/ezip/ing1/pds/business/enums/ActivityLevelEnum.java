package edu.ezip.ing1.pds.business.enums;


public enum ActivityLevelEnum {
    low("Faible"),
    moderate("Modéré"),
    high("Élevé");

    private final String labelFr;

    ActivityLevelEnum(String labelFr) {
        this.labelFr = labelFr;
    }

    public String getLabelFr() {
        return labelFr;
    }
}
