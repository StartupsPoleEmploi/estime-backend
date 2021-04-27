package fr.poleemploi.estime.clientsexternes.emploistoredev.ressources;

public class DetailAideESD {
  
    private AideESD aid;

    public AideESD getAid() {
        return aid;
    }

    public void setAid(AideESD aid) {
        this.aid = aid;
    }

    @Override
    public String toString() {
        return "DetailAideSociale [aid=" + aid + "]";
    }
}
