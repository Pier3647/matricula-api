package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion.ACCESS_TOKEN;

public class ConsultarMatriculas implements Task {

    private final String curso;

    public ConsultarMatriculas(String curso) {
        this.curso = curso;
    }

    public static ConsultarMatriculas delCurso(String curso) {
        return new ConsultarMatriculas(curso);
    }

    @Override
    @Step("{0} consulta sus matriculas del curso #curso")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall(ACCESS_TOKEN);
        actor.attemptsTo(
                Get.resource("/api/matriculas")
                        .with(req -> req.header("Authorization", "Bearer " + token).queryParam("curso", curso))
        );
    }
}
