package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion.ACCESS_TOKEN;
import static pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion.USUARIO;

public class ConsultarHistorial implements Task {

    public static ConsultarHistorial propio() {
        return new ConsultarHistorial();
    }

    @Override
    @Step("{0} consulta su historial academico")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall(ACCESS_TOKEN);
        String codigo = actor.recall(USUARIO);
        actor.attemptsTo(
                Get.resource("/api/alumnos/{codigo}/historial")
                        .with(req -> req.header("Authorization", "Bearer " + token).pathParam("codigo", codigo))
        );
    }
}
