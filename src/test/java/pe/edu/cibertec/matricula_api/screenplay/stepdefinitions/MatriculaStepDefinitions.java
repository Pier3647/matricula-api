package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.model.environment.SystemEnvironmentVariables;

import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MatriculaStepDefinitions {

    private static final double TOLERANCIA = 0.001;

    private final String baseUrl = EnvironmentSpecificConfiguration
            .from(SystemEnvironmentVariables.currentEnvironmentVariables())
            .getProperty("restapi.baseurl");

    private String alumno;
    private String token;
    private Response respuesta;
    private Long idMatricula;
    private Double totalCotizado;

    private RequestSpecification api() {
        return SerenityRest.given().baseUri(baseUrl).contentType(ContentType.JSON);
    }

    private RequestSpecification conToken() {
        return api().header("Authorization", "Bearer " + token);
    }

    @Dado("que el alumno {string} inicia sesion con la clave {string}")
    public void queElAlumnoIniciaSesion(String codigo, String clave) {
        alumno = codigo;
        respuesta = api()
                .body(Map.of("username", codigo, "password", clave))
                .post("/api/auth/login");
        respuesta.then().statusCode(200);
        token = respuesta.jsonPath().getString("accessToken");
        assertThat("el login debe devolver un accessToken", token, not(emptyOrNullString()));
    }

    @Cuando("cotiza el curso {string}")
    public void cotizaElCurso(String curso) {
        cotizar(List.of(curso));
    }

    private void cotizar(List<String> cursos) {
        respuesta = conToken()
                .body(Map.of("codigoAlumno", alumno, "cursos", cursos))
                .post("/api/matriculas/cotizacion");
        if (respuesta.statusCode() == 200) {
            totalCotizado = respuesta.jsonPath().getDouble("total");
        }
    }

    @Y("la cotizacion tiene {int} creditos, subtotal {double}, descuento de {int} por ciento y total {double}")
    public void laCotizacionTiene(int creditos, double subtotal, int descuento, double total) {
        assertThat(respuesta.jsonPath().getInt("creditos"), equalTo(creditos));
        assertThat(respuesta.jsonPath().getDouble("subtotal"), closeTo(subtotal, TOLERANCIA));
        assertThat(respuesta.jsonPath().getInt("descuento"), equalTo(descuento));
        assertThat(respuesta.jsonPath().getDouble("total"), closeTo(total, TOLERANCIA));
    }

    @Cuando("se matricula en el curso {string}")
    public void seMatriculaEnElCurso(String curso) {
        respuesta = conToken()
                .body(Map.of("codigoAlumno", alumno, "codigoCurso", curso))
                .post("/api/matriculas");
        if (respuesta.statusCode() == 201) {
            idMatricula = respuesta.jsonPath().getLong("id");
        }
    }

    @Y("la matricula queda en estado {string}")
    public void laMatriculaQuedaEnEstado(String estado) {
        assertThat(respuesta.jsonPath().getString("estado"), equalTo(estado));
    }

    @Y("el monto de la matricula es {double} y coincide con el total cotizado")
    public void elMontoDeLaMatriculaEs(double monto) {
        double montoMatricula = respuesta.jsonPath().getDouble("monto");
        assertThat(montoMatricula, closeTo(monto, TOLERANCIA));
        assertThat("primero se debe cotizar", totalCotizado, notNullValue());
        assertThat(montoMatricula, closeTo(totalCotizado, TOLERANCIA));
    }

    @Cuando("paga la matricula con el metodo {string}")
    public void pagaLaMatriculaConElMetodo(String metodo) {
        respuesta = conToken()
                .pathParam("id", idMatricula)
                .body(Map.of("metodo", metodo))
                .patch("/api/matriculas/{id}/pagar");
    }

    @Y("la fecha de pago tiene valor")
    public void laFechaDePagoTieneValor() {
        assertThat(respuesta.jsonPath().getString("fechaPago"), not(emptyOrNullString()));
    }

    @Cuando("consulta sus pagos")
    public void consultaSusPagos() {
        respuesta = conToken().get("/api/pagos");
    }

    @Y("existe el pago de su matricula con el metodo {string} y numero de operacion con formato OP-000000")
    public void existeElPagoDeSuMatricula(String metodo) {
        Map<String, Object> pago = respuesta.jsonPath().getMap("find { it.idMatricula == " + idMatricula + " }");
        assertThat("debe existir un pago para la matricula " + idMatricula, pago, notNullValue());
        assertThat(pago.get("metodo"), equalTo(metodo));
        assertThat(String.valueOf(pago.get("numeroOperacion")), matchesPattern("^OP-\\d{6}$"));
    }

    @Entonces("el codigo HTTP es {int}")
    public void elCodigoHttpEs(int status) {
        assertThat(respuesta.statusCode(), equalTo(status));
    }

    @Y("el error tiene el codigo {string}")
    public void elErrorTieneElCodigo(String codigo) {
        assertThat(respuesta.jsonPath().getString("codigo"), equalTo(codigo));
    }

    @Y("el cuerpo cumple el esquema {string}")
    public void elCuerpoCumpleElEsquema(String esquema) {
        respuesta.then().body(matchesJsonSchemaInClasspath("schemas/" + esquema));
    }
}
