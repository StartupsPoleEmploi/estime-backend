package fr.poleemploi.estime.clientsexternes.emploistoredev.ressources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AideESD {

    private String name;
    private String what;
    private String slug;
    @JsonProperty("short_description")
    private String shortDescription;
    @JsonProperty("how_much")
    private String howMuch;
    @JsonProperty("additionnal_conditions")
    private String additionnaConditions;
    @JsonProperty("how_and_when")
    private String howAndWhen;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getWhat() {
        return what;
    }
    public void setWhat(String what) {
        this.what = what;
    }
    public String getSlug() {
        return slug;
    }
    public void setSlug(String slug) {
        this.slug = slug;
    }
    public String getShortDescription() {
        return shortDescription;
    }
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    public String getHowMuch() {
        return howMuch;
    }
    public void setHowMuch(String howMuch) {
        this.howMuch = howMuch;
    }
    public String getAdditionnaConditions() {
        return additionnaConditions;
    }
    public void setAdditionnaConditions(String additionnaConditions) {
        this.additionnaConditions = additionnaConditions;
    }
    public String getHowAndWhen() {
        return howAndWhen;
    }
    public void setHowAndWhen(String howAndWhen) {
        this.howAndWhen = howAndWhen;
    }
    
    @Override
    public String toString() {
        return "AideSociale [name=" + name + ", what=" + what + ", slug=" + slug + ", shortDescription="
                + shortDescription + ", howMuch=" + howMuch + ", additionnaConditions=" + additionnaConditions
                + ", howAndWhen=" + howAndWhen + "]";
    }
}
