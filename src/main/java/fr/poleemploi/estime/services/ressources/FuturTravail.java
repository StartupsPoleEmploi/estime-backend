package fr.poleemploi.estime.services.ressources;

public class FuturTravail {
    
    private float distanceKmDomicileTravail;
    private float nombreHeuresTravailleesSemaine;
    private Integer nombreMoisContratCDD;
    private int nombreTrajetsDomicileTravail;
    private Salaire salaire;  
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
    public Salaire getSalaire() {
        return salaire;
    }
    public void setSalaire(Salaire salaire) {
        this.salaire = salaire;
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
                + nombreMoisContratCDD + ", nombreTrajetsDomicileTravail=" + nombreTrajetsDomicileTravail + ", salaire="
                + salaire + ", typeContrat=" + typeContrat + "]";
    }
}
