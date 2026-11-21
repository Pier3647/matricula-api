package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.screenplay.Actor;
import pe.edu.cibertec.matricula_api.screenplay.questions.ElCodigoDeError;
import pe.edu.cibertec.matricula_api.screenplay.questions.LaRespuesta;
import pe.edu.cibertec.matricula_api.screenplay.task.*;

import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.Matchers.*;
import static pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion.REFRESH_TOKEN;

public class SeguridadStepDefinitions {

    private static final String REFRESH_ORIGINAL = "refreshTokenOriginal";

    @Dado("inicia sesion con el usuario {string} y la clave {string}")
    public void iniciaSesion(String usuario, String clave) {
        Actor actor = theActorInTheSpotlight();
        actor.attemptsTo(IniciarSesion.con(usuario, clave));
        actor.should(seeThat(LaRespuesta.codigoDeEstado(), equalTo(200)));
        actor.remember(REFRESH_ORIGINAL, actor.<String>recall(REFRESH_TOKEN));
    }

    @Cuando("intenta ingresar con el usuario {string} y la clave {string}")
    public void intentaIngresar(String usuario, String clave) {
        theActorInTheSpotlight().attemptsTo(IniciarSesion.con(usuario, clave));
    }

    @Cuando("renueva sus tokens con el refresh token")
    public void renuevaSusTokens() {
        theActorInTheSpotlight().attemptsTo(RenovarToken.conElRefreshTokenVigente());
    }

    @Cuando("intenta renovar otra vez con el refresh token original")
    public void intentaRenovarConElOriginal() {
        theActorInTheSpotlight().attemptsTo(RenovarToken.conElRefreshTokenGuardadoComo(REFRESH_ORIGINAL));
    }

    @Cuando("cierra su sesion")
    public void cierraSuSesion() {
        theActorInTheSpotlight().attemptsTo(CerrarSesion.actual());
    }

    @Cuando("consulta su perfil")
    public void consultaSuPerfil() {
        theActorInTheSpotlight().attemptsTo(ConsultarPerfil.conSuAccessToken());
    }

    @Cuando("lista todos los alumnos")
    public void listaTodosLosAlumnos() {
        theActorInTheSpotlight().attemptsTo(ListarAlumnos.todos());
    }

    @Cuando("ingresa con el usuario {string} una clave incorrecta en cada intento:")
    public void ingresaConClaveIncorrecta(String usuario, DataTable intentos) {
        Actor actor = theActorInTheSpotlight();
        for (Map<String, String> intento : intentos.asMaps()) {
            actor.attemptsTo(IniciarSesion.con(usuario, intento.get("clave")));
            actor.should(
                    seeThat("estado del intento " + intento.get("intento"),
                            LaRespuesta.codigoDeEstado(), equalTo(Integer.parseInt(intento.get("status")))),
                    seeThat("codigo del intento " + intento.get("intento"),
                            ElCodigoDeError.deLaRespuesta(), equalTo(intento.get("codigo"))),
                    seeThatResponse("el intento " + intento.get("intento") + " cumple error.json",
                            res -> res.body(matchesJsonSchemaInClasspath("schemas/error.json")))
            );
        }
    }

    @Entonces("la API responde {int} con el codigo de error {string}")
    public void laApiRespondeConElCodigoDeError(int status, String codigo) {
        theActorInTheSpotlight().should(
                seeThat(LaRespuesta.codigoDeEstado(), equalTo(status)),
                seeThat(ElCodigoDeError.deLaRespuesta(), equalTo(codigo)),
                seeThatResponse("la respuesta cumple error.json",
                        res -> res.body(matchesJsonSchemaInClasspath("schemas/error.json")))
        );
    }
}
