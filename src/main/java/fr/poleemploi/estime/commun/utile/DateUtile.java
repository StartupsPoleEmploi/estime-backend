package fr.poleemploi.estime.commun.utile;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DateUtile {

    public static final ZoneId ZONE_ID_FRANCE = ZoneId.of("Europe/Paris");
    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";

    public int getNbrMoisEntreDeuxDates(Date dateDebut, Date dateFin) {
	return (int) ChronoUnit.MONTHS.between(dateDebut.toInstant().atZone(ZONE_ID_FRANCE).toLocalDate(), dateFin.toInstant().atZone(ZONE_ID_FRANCE).toLocalDate());
    }

    public int getNbrMoisEntreDeuxLocalDates(LocalDate dateDebut, LocalDate dateFin) {
	return (int) ChronoUnit.MONTHS.between(dateDebut, dateFin);
    }

    public LocalDate getDatePremierJourDuMois(LocalDate dateCourante) {
	YearMonth yearMonth = YearMonth.of(dateCourante.getYear(), dateCourante.getMonthValue());
	return yearMonth.atDay(1);
    }

    public LocalDate getDateDernierJourDuMois(LocalDate dateCourante) {
	YearMonth yearMonth = YearMonth.of(dateCourante.getYear(), dateCourante.getMonthValue());
	return yearMonth.atEndOfMonth();
    }

    public LocalDate getDateJour() {
	return LocalDate.now(ZONE_ID_FRANCE);
    }

    public LocalDate getDateMoisProchain() {
	return LocalDate.now(ZONE_ID_FRANCE).with(TemporalAdjusters.firstDayOfNextMonth());
    }

    public LocalDateTime getDateTimeJour() {
	return LocalDateTime.now(ZONE_ID_FRANCE);
    }

    public String getMonthFromLocalDate(LocalDate localDate) {
	DecimalFormat decimalFormat = new DecimalFormat("00");
	return decimalFormat.format(Double.valueOf(localDate.getMonthValue()));
    }

    public LocalDate convertDateToLocalDate(Date dateToConvert) {
	return dateToConvert.toInstant().atZone(ZONE_ID_FRANCE).toLocalDate();
    }

    public String convertDateToString(LocalDate dateToConvert) {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
	return formatter.format(dateToConvert);
    }

    public String convertDateToStringOpenFisca(LocalDate dateToConvert) {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY_MM_DD);
	return formatter.format(dateToConvert);
    }

    public String convertDateToString(LocalDate dateToConvert, String dateFormat) {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
	return formatter.format(dateToConvert);
    }

    // Fonction qui permet d'ajouter 1 an et 1 mois aux enfants de moins d'1 an
    // pour contourner une erreur dans le calcul des ages des enfants de moins d'un an de l'API AGEPI OpenFisca
    public LocalDate getDateNaissanceModifieeEnfantMoinsDUnAn(LocalDate dateNaissance) {
	if (getAge(dateNaissance) < 1) {
	    return enleverMoisALocalDate(enleverAnneesALocalDate(dateNaissance, 1), 1);
	}
	return dateNaissance;
    }

    public LocalDate enleverAnneesALocalDate(LocalDate localDate, int nombreAnneesAAjouter) {
	return localDate.minusYears(nombreAnneesAAjouter);
    }

    public LocalDate ajouterMoisALocalDate(LocalDate localDate, int nombreMoisAAjouter) {
	return localDate.plusMonths(nombreMoisAAjouter);
    }

    public LocalDate enleverMoisALocalDate(LocalDate localDate, int nombreMoisAEnlever) {
	return localDate.minusMonths(nombreMoisAEnlever);
    }

    public boolean isDateAvant(LocalDate dateToCheck, LocalDate dateLimite) {
	return dateToCheck.isBefore(dateLimite);
    }

    public int getNombreJoursDansLeMois(LocalDate date) {
	YearMonth yearMonthObject = YearMonth.of(date.getYear(), date.getMonthValue());
	return yearMonthObject.lengthOfMonth();
    }

    public int getAge(LocalDate dateNaissance) {
	if ((dateNaissance != null)) {
	    return Period.between(dateNaissance, LocalDate.now()).getYears();
	} else {
	    return 0;
	}
    }

    public LocalDate getDateNaissanceFromAge(int age) {
	return LocalDate.now().minusYears(age);
    }

    public LocalDate getDateNaissanceFromAge(LocalDate dateReference, int age) {
	return dateReference.minusYears(age);
    }
}
