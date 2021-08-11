package testsunitaires.commun.utile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.commun.utile.IndividuUtile;

class IndividuUtileTests {

    private IndividuUtile individuUtile = new IndividuUtile();

    @Test
    void isPopulationAutoriseeTest1() {

        //si l'individu est population AAH
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(true);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(false);
        detailIndemnisationESD.setBeneficiaireAideSolidarite(true);
        detailIndemnisationESD.setBeneficiaireRSA(false);
        detailIndemnisationESD.setCodeIndemnisation(null);

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationESD);

        //Alors l'individu est dans la population autorisée
        assertThat(isPopulationAutorisee).isTrue();
    }

    @Test
    void isPopulationAutoriseeTest2() {

        //si l'individu est population ASS
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(false);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationESD.setBeneficiaireAideSolidarite(false);
        detailIndemnisationESD.setBeneficiaireRSA(false);
        detailIndemnisationESD.setCodeIndemnisation("ASS");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationESD);

        //Alors l'individu est dans la population autorisée
        assertThat(isPopulationAutorisee).isTrue();
    }

    @Test
    void isPopulationAutoriseeTest3() {

        //si l'individu est population RSA
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(false);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(false);
        detailIndemnisationESD.setBeneficiaireAideSolidarite(true);
        detailIndemnisationESD.setBeneficiaireRSA(true);
        detailIndemnisationESD.setCodeIndemnisation(null);

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationESD);

        //Alors l'individu est dans la population autorisée
        assertThat(isPopulationAutorisee).isTrue();
    }

    @Test
    void isPopulationAutoriseeTest4() {

        //si l'individu est population AAH et ASS
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(true);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationESD.setBeneficiaireAideSolidarite(false);
        detailIndemnisationESD.setBeneficiaireRSA(false);
        detailIndemnisationESD.setCodeIndemnisation("ASS");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationESD);

        //Alors l'individu est dans la population autorisée
        assertThat(isPopulationAutorisee).isTrue();
    }

    @Test
    void isPopulationAutoriseeTest5() {

        //si l'individu est population AAH et RSA
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(true);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(false);
        detailIndemnisationESD.setBeneficiaireAideSolidarite(false);
        detailIndemnisationESD.setBeneficiaireRSA(true);
        detailIndemnisationESD.setCodeIndemnisation(null);

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationESD);

        //Alors l'individu est dans la population autorisée
        assertThat(isPopulationAutorisee).isTrue();
    }

    @Test
    void isPopulationAutoriseeTest6() {

        //si l'individu est population ASS et RSA
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(false);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationESD.setBeneficiaireAideSolidarite(false);
        detailIndemnisationESD.setBeneficiaireRSA(true);
        detailIndemnisationESD.setCodeIndemnisation("ASS");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationESD);

        //Alors l'individu est dans la population autorisée
        assertThat(isPopulationAutorisee).isTrue();
    }

    @Test
    void isPopulationAutoriseeTest7() {

        //si l'individu est population ASS, AAH et RSA
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(true);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationESD.setBeneficiaireAideSolidarite(false);
        detailIndemnisationESD.setBeneficiaireRSA(true);
        detailIndemnisationESD.setCodeIndemnisation("ASS");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationESD);

        //Alors l'individu est dans la population autorisée
        assertThat(isPopulationAutorisee).isTrue();
    }

    @Test
    void isPopulationAutoriseeTest8() {

        //si l'individu est population ARE
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(false);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationESD.setBeneficiaireAideSolidarite(false);
        detailIndemnisationESD.setBeneficiaireRSA(false);
        detailIndemnisationESD.setCodeIndemnisation("ARE");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationESD);

        //Alors l'individu n'est pas dans la population autorisée
        assertThat(isPopulationAutorisee).isFalse();
    }

    @Test
    void isPopulationAutoriseeTest9() {

        //si l'individu est population ARE + AAH
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(true);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationESD.setBeneficiaireAideSolidarite(true);
        detailIndemnisationESD.setBeneficiaireRSA(false);
        detailIndemnisationESD.setCodeIndemnisation("ARE");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationESD);

        //Alors l'individu n'est pas dans la population autorisée
        assertThat(isPopulationAutorisee).isFalse();
    }

    @Test
    void isPopulationAutoriseeTest10() {

        //si l'individu est population ARE + RSA
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(false);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationESD.setBeneficiaireAideSolidarite(true);
        detailIndemnisationESD.setBeneficiaireRSA(true);
        detailIndemnisationESD.setCodeIndemnisation("ARE");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationESD);

        //Alors l'individu n'est pas dans la population autorisée
        assertThat(isPopulationAutorisee).isFalse();
    }

    @Test
    void isPopulationAutoriseeTest11() {

        //si l'individu est population ARE + RSA + AAH
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(true);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationESD.setBeneficiaireAideSolidarite(true);
        detailIndemnisationESD.setBeneficiaireRSA(true);
        detailIndemnisationESD.setCodeIndemnisation("ARE");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationESD);

        //Alors l'individu n'est pas dans la population autorisée
        assertThat(isPopulationAutorisee).isFalse();
    }

    @Test
    void isPopulationAutoriseeTest12() {

        //si l'individu est population candidat
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(false);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(false);
        detailIndemnisationESD.setBeneficiaireAideSolidarite(false);
        detailIndemnisationESD.setBeneficiaireRSA(false);
        detailIndemnisationESD.setCodeIndemnisation(null);

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationESD);

        //Alors l'individu n'est pas dans la population autorisée
        assertThat(isPopulationAutorisee).isFalse();
    }

}
