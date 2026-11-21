package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion.ACCESS_TOKEN;

public class ConsultarPerfil implements Task {

    public static ConsultarPerfil conSuAccessToken() {
        return new ConsultarPerfil();
    }

    @Override
    @Step("{0} consulta su perfil")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall(ACCESS_TOKEN);
        actor.attemptsTo(
                Get.resource("/api/auth/perfil")
                        .with(req -> req.header("Authorization", "Bearer " + token))
        );
    }
}
