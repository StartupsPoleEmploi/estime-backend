package fr.poleemploi.estime.services.ressources;

public class FuturTravail {
    
    private float distanceKmDomicileTravail;
    private float nombreHeuresTravailleesSemaine;
    private Integer nombreMoisContratCDD;
    private int nombreTrajetsDomicileTravail;
    private float salaireMensuelBrut;    
    private float salaireMensuelNet;
    private String typeContrat;
    
    public int getNombreTrajetsDomicileTravail() {
        return nombreTrajetsDomicileTravail;
    }
    public void setNombreTrajetsDomicileTravail(int nombreTrajetsDomicileTravail) {
        this.nombreTrajetsDomicileTravail = nombreTrajetsDomicileTravail;
    }
    public String getTypeContrat() {
        return typeContrat;
    }
    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
    }    
    public float getSalaireMensuelBrut() {
        return salaireMensuelBrut;
    }
    public void setSalaireMensuelBrut(float salaireMensuelBrut) {
        this.salaireMensuelBrut = salaireMensuelBrut;
    }
    public float getSalaireMensuelNet() {
        return salaireMensuelNet;
    }
    public void setSalaireMensuelNet(float salaireMensuelNet) {
        this.salaireMensuelNet = salaireMensuelNet;
    }
    public float getNombreHeuresTravailleesSemaine() {
        return nombreHeuresTravailleesSemaine;
    }
    public void setNombreHeuresTravailleesSemaine(float nombreHeuresTravailleesSemaine) {
        this.nombreHeuresTravailleesSemaine = nombreHeuresTravailleesSemaine;
    }
    public float getDistanceKmDomicileTravail() {
        return distanceKmDomicileTravail;
    }
    public void setDistanceKmDomicileTravail(float distanceKmDomicileTravail) {
        this.distanceKmDomicileTravail = distanceKmDomicileTravail;
    }
    public Integer getNombreMoisContratCDD() {
        return nombreMoisContratCDD;
    }
    public void setNombreMoisContratCDD(Integer nombreMoisContratCDD) {
        this.nombreMoisContratCDD = nombreMoisContratCDD;
    }
    @Override
    public String toString() {
        return "FuturTravail [distanceKmDomicileTravail=" + distanceKmDomicileTravail
                + ", nombreHeuresTravailleesSemaine=" + nombreHeuresTravailleesSemaine + ", nombreMoisContratCDD="
                + nombreMoisContratCDD + ", nombreTrajetsDomicileTravail=" + nombreTrajetsDomicileTravail
                + ", salaireMensuelBrut=" + salaireMensuelBrut + ", salaireMensuelNet=" + salaireMensuelNet
                + ", typeContrat=" + typeContrat + "]";
    }
}
