package edu.ezip.ing1.pds.business.enums;

public enum SexEnum {
    male("Homme"),
    female("Femme");

    private final String labelFr;

    SexEnum(String labelFr) {
        this.labelFr = labelFr;
    }

    public String getLabelFr() {
        return labelFr;
    }
}
