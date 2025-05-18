package edu.ezip.ing1.pds.business.enums;

public enum GoalEnum {
    lose("Perdre du poids"),
    maintain("Maintenir le poids"),
    gain("Prendre du poids");

    private final String labelFr;

    GoalEnum(String labelFr) {
        this.labelFr = labelFr;
    }

    public String getLabelFr() {
        return labelFr;
    }
}

