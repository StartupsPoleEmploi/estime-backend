package fr.poleemploi.estime.commun.enumerations;

public enum NombresJoursIndemnises {

    HEURES_HEBDO_TRAVAILLEES_1H_A_7H(1, 7, 4),
    HEURES_HEBDO_TRAVAILLEES_8H_A_10H(8, 10, 5),
    HEURES_HEBDO_TRAVAILLEES_11H_A_12H(11, 12, 6),
    HEURES_HEBDO_TRAVAILLEES_13H_A_14H(13, 14, 7),
    HEURES_HEBDO_TRAVAILLEES_15H_A_16H(15, 16, 8),
    HEURES_HEBDO_TRAVAILLEES_17H(17, 17, 9),
    HEURES_HEBDO_TRAVAILLEES_18H_A_19H(18, 19, 10),
    HEURES_HEBDO_TRAVAILLEES_20H_A_21H(20, 21, 11),
    HEURES_HEBDO_TRAVAILLEES_22H_A_23H(22, 23, 12),
    HEURES_HEBDO_TRAVAILLEES_24H_A_25H(24, 25, 13),
    HEURES_HEBDO_TRAVAILLEES_26H_A_27H(26, 27, 14),
    HEURES_HEBDO_TRAVAILLEES_28H_A_29H(28, 29, 15),
    HEURES_HEBDO_TRAVAILLEES_30H_A_31H(30, 31, 16),
    HEURES_HEBDO_TRAVAILLEES_32H(32, 32, 17),
    HEURES_HEBDO_TRAVAILLEES_33H(33, 33, 18),
    HEURES_HEBDO_TRAVAILLEES_34H(34, 34, 19);
    
    
    private int nombreHeuresMinHebdo;
    private int nombreHeuresMaxHebdo;
    private int nombreRepasIndemnises; 
    
    NombresJoursIndemnises(int nombreHeuresMinHebdo, int nombreHeuresMaxHebdo, int nombreRepasIndemnises) {
        this.nombreHeuresMinHebdo = nombreHeuresMinHebdo;
        this.nombreHeuresMaxHebdo = nombreHeuresMaxHebdo;
        this.nombreRepasIndemnises = nombreRepasIndemnises;
    }

    public int getNombreHeuresMinHebdo() {
        return nombreHeuresMinHebdo;
    }

    public int getNombreHeuresMaxHebdo() {
        return nombreHeuresMaxHebdo;
    }

    public int getNombreRepasIndemnises() {
        return nombreRepasIndemnises;
    }
}
