package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    private static final String ALUMNO = "Pier Anderson Olivares Sanchez";
    private static final String CODIGO = "i202505314";
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Before(order = 0)
    public void imprimirDatosDelAlumno(Scenario escenario) {
        System.out.println("==============================================================");
        System.out.println(" Alumno       : " + ALUMNO);
        System.out.println(" Codigo       : " + CODIGO);
        System.out.println(" Fecha y hora : " + LocalDateTime.now().format(FORMATO));
        System.out.println(" Escenario    : " + escenario.getName());
        System.out.println("==============================================================");
    }

    @Before(order = 1)
    public void beforeScenario() {
        OnStage.setTheStage(new OnlineCast());
    }
}
