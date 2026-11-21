package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

import static pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion.ACCESS_TOKEN;
import static pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion.REFRESH_TOKEN;

public class RenovarToken implements Task {

    private final String claveEnMemoria;

    public RenovarToken(String claveEnMemoria) {
        this.claveEnMemoria = claveEnMemoria;
    }

    public static RenovarToken conElRefreshTokenVigente() {
        return new RenovarToken(REFRESH_TOKEN);
    }

    public static RenovarToken conElRefreshTokenGuardadoComo(String claveEnMemoria) {
        return new RenovarToken(claveEnMemoria);
    }

    @Override
    @Step("{0} renueva sus tokens con el refresh token '#claveEnMemoria'")
    public <T extends Actor> void performAs(T actor) {
        String refreshToken = actor.recall(claveEnMemoria);
        actor.attemptsTo(
                Post.to("/api/auth/refresh")
                        .with(req -> req.contentType(ContentType.JSON)
                                .body(Map.of("refreshToken", refreshToken)))
        );
        if (SerenityRest.lastResponse().statusCode() == 200) {
            actor.remember(ACCESS_TOKEN, SerenityRest.lastResponse().jsonPath().getString("accessToken"));
            actor.remember(REFRESH_TOKEN, SerenityRest.lastResponse().jsonPath().getString("refreshToken"));
        }
    }
}
