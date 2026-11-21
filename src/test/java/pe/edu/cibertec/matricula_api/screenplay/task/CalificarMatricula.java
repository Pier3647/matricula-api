package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;

import java.util.Map;

import static pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion.ACCESS_TOKEN;

public class CalificarMatricula implements Task {

    private final long idMatricula;
    private final double nota;

    public CalificarMatricula(long idMatricula, double nota) {
        this.idMatricula = idMatricula;
        this.nota = nota;
    }

    public static CalificarMatricula conNota(double nota, long idMatricula) {
        return new CalificarMatricula(idMatricula, nota);
    }

    @Override
    @Step("{0} registra la nota #nota en la matricula #idMatricula")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall(ACCESS_TOKEN);
        actor.attemptsTo(
                Patch.to("/api/matriculas/{id}/nota")
                        .with(req -> req.header("Authorization", "Bearer " + token)
                                .pathParam("id", idMatricula)
                                .contentType(ContentType.JSON)
                                .body(Map.of("nota", nota)))
        );
    }
}
