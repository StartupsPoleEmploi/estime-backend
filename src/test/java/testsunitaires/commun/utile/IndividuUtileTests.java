package testsunitaires.commun.utile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.commun.utile.IndividuUtile;

class IndividuUtileTests {

    private IndividuUtile individuUtile = new IndividuUtile();

    @Test
    void isPopulationAutoriseeTest1() {

        //si l'individu est population AAH
        DetailIndemnisationPEIO detailIndemnisationPEIO = new DetailIndemnisationPEIO();
        detailIndemnisationPEIO.setBeneficiaireAAH(true);
        detailIndemnisationPEIO.setBeneficiaireAssuranceChomage(false);
        detailIndemnisationPEIO.setBeneficiaireAideSolidarite(true);
        detailIndemnisationPEIO.setBeneficiaireRSA(false);
        detailIndemnisationPEIO.setCodeIndemnisation(null);

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationPEIO);

        //Alors l'individu est dans la population autorisée
        assertThat(isPopulationAutorisee).isTrue();
    }

    @Test
    void isPopulationAutoriseeTest2() {

        //si l'individu est population ASS
        DetailIndemnisationPEIO detailIndemnisationPEIO = new DetailIndemnisationPEIO();
        detailIndemnisationPEIO.setBeneficiaireAAH(false);
        detailIndemnisationPEIO.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationPEIO.setBeneficiaireAideSolidarite(false);
        detailIndemnisationPEIO.setBeneficiaireRSA(false);
        detailIndemnisationPEIO.setCodeIndemnisation("ASS");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationPEIO);

        //Alors l'individu est dans la population autorisée
        assertThat(isPopulationAutorisee).isTrue();
    }

    @Test
    void isPopulationAutoriseeTest3() {

        //si l'individu est population RSA
        DetailIndemnisationPEIO detailIndemnisationPEIO = new DetailIndemnisationPEIO();
        detailIndemnisationPEIO.setBeneficiaireAAH(false);
        detailIndemnisationPEIO.setBeneficiaireAssuranceChomage(false);
        detailIndemnisationPEIO.setBeneficiaireAideSolidarite(true);
        detailIndemnisationPEIO.setBeneficiaireRSA(true);
        detailIndemnisationPEIO.setCodeIndemnisation(null);

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationPEIO);

        //Alors l'individu est dans la population autorisée
        assertThat(isPopulationAutorisee).isTrue();
    }

    @Test
    void isPopulationAutoriseeTest4() {

        //si l'individu est population AAH et ASS
        DetailIndemnisationPEIO detailIndemnisationPEIO = new DetailIndemnisationPEIO();
        detailIndemnisationPEIO.setBeneficiaireAAH(true);
        detailIndemnisationPEIO.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationPEIO.setBeneficiaireAideSolidarite(false);
        detailIndemnisationPEIO.setBeneficiaireRSA(false);
        detailIndemnisationPEIO.setCodeIndemnisation("ASS");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationPEIO);

        //Alors l'individu est dans la population autorisée
        assertThat(isPopulationAutorisee).isTrue();
    }

    @Test
    void isPopulationAutoriseeTest5() {

        //si l'individu est population AAH et RSA
        DetailIndemnisationPEIO detailIndemnisationPEIO = new DetailIndemnisationPEIO();
        detailIndemnisationPEIO.setBeneficiaireAAH(true);
        detailIndemnisationPEIO.setBeneficiaireAssuranceChomage(false);
        detailIndemnisationPEIO.setBeneficiaireAideSolidarite(false);
        detailIndemnisationPEIO.setBeneficiaireRSA(true);
        detailIndemnisationPEIO.setCodeIndemnisation(null);

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationPEIO);

        //Alors l'individu est dans la population autorisée
        assertThat(isPopulationAutorisee).isTrue();
    }

    @Test
    void isPopulationAutoriseeTest6() {

        //si l'individu est population ASS et RSA
        DetailIndemnisationPEIO detailIndemnisationPEIO = new DetailIndemnisationPEIO();
        detailIndemnisationPEIO.setBeneficiaireAAH(false);
        detailIndemnisationPEIO.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationPEIO.setBeneficiaireAideSolidarite(false);
        detailIndemnisationPEIO.setBeneficiaireRSA(true);
        detailIndemnisationPEIO.setCodeIndemnisation("ASS");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationPEIO);

        //Alors l'individu est dans la population autorisée
        assertThat(isPopulationAutorisee).isTrue();
    }

    @Test
    void isPopulationAutoriseeTest7() {

        //si l'individu est population ASS, AAH et RSA
        DetailIndemnisationPEIO detailIndemnisationPEIO = new DetailIndemnisationPEIO();
        detailIndemnisationPEIO.setBeneficiaireAAH(true);
        detailIndemnisationPEIO.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationPEIO.setBeneficiaireAideSolidarite(false);
        detailIndemnisationPEIO.setBeneficiaireRSA(true);
        detailIndemnisationPEIO.setCodeIndemnisation("ASS");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationPEIO);

        //Alors l'individu est dans la population autorisée
        assertThat(isPopulationAutorisee).isTrue();
    }

    @Test
    void isPopulationAutoriseeTest8() {

        //si l'individu est population ARE
        DetailIndemnisationPEIO detailIndemnisationPEIO = new DetailIndemnisationPEIO();
        detailIndemnisationPEIO.setBeneficiaireAAH(false);
        detailIndemnisationPEIO.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationPEIO.setBeneficiaireAideSolidarite(false);
        detailIndemnisationPEIO.setBeneficiaireRSA(false);
        detailIndemnisationPEIO.setCodeIndemnisation("ARE");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationPEIO);

        //Alors l'individu n'est pas dans la population autorisée
        assertThat(isPopulationAutorisee).isFalse();
    }

    @Test
    void isPopulationAutoriseeTest9() {

        //si l'individu est population ARE + AAH
        DetailIndemnisationPEIO detailIndemnisationPEIO = new DetailIndemnisationPEIO();
        detailIndemnisationPEIO.setBeneficiaireAAH(true);
        detailIndemnisationPEIO.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationPEIO.setBeneficiaireAideSolidarite(true);
        detailIndemnisationPEIO.setBeneficiaireRSA(false);
        detailIndemnisationPEIO.setCodeIndemnisation("ARE");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationPEIO);

        //Alors l'individu n'est pas dans la population autorisée
        assertThat(isPopulationAutorisee).isFalse();
    }

    @Test
    void isPopulationAutoriseeTest10() {

        //si l'individu est population ARE + RSA
        DetailIndemnisationPEIO detailIndemnisationPEIO = new DetailIndemnisationPEIO();
        detailIndemnisationPEIO.setBeneficiaireAAH(false);
        detailIndemnisationPEIO.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationPEIO.setBeneficiaireAideSolidarite(true);
        detailIndemnisationPEIO.setBeneficiaireRSA(true);
        detailIndemnisationPEIO.setCodeIndemnisation("ARE");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationPEIO);

        //Alors l'individu n'est pas dans la population autorisée
        assertThat(isPopulationAutorisee).isFalse();
    }

    @Test
    void isPopulationAutoriseeTest11() {

        //si l'individu est population ARE + RSA + AAH
        DetailIndemnisationPEIO detailIndemnisationPEIO = new DetailIndemnisationPEIO();
        detailIndemnisationPEIO.setBeneficiaireAAH(true);
        detailIndemnisationPEIO.setBeneficiaireAssuranceChomage(true);
        detailIndemnisationPEIO.setBeneficiaireAideSolidarite(true);
        detailIndemnisationPEIO.setBeneficiaireRSA(true);
        detailIndemnisationPEIO.setCodeIndemnisation("ARE");

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationPEIO);

        //Alors l'individu n'est pas dans la population autorisée
        assertThat(isPopulationAutorisee).isFalse();
    }

    @Test
    void isPopulationAutoriseeTest12() {

        //si l'individu est population candidat
        DetailIndemnisationPEIO detailIndemnisationPEIO = new DetailIndemnisationPEIO();
        detailIndemnisationPEIO.setBeneficiaireAAH(false);
        detailIndemnisationPEIO.setBeneficiaireAssuranceChomage(false);
        detailIndemnisationPEIO.setBeneficiaireAideSolidarite(false);
        detailIndemnisationPEIO.setBeneficiaireRSA(false);
        detailIndemnisationPEIO.setCodeIndemnisation(null);

        //Lorsque l'on appele la méthode isPopulationAutorisee
        boolean isPopulationAutorisee = individuUtile.isPopulationAutorisee(detailIndemnisationPEIO);

        //Alors l'individu n'est pas dans la population autorisée
        assertThat(isPopulationAutorisee).isFalse();
    }

}
