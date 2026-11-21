package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Y;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import pe.edu.cibertec.matricula_api.screenplay.questions.ElHistorial;
import pe.edu.cibertec.matricula_api.screenplay.questions.LaRespuesta;
import pe.edu.cibertec.matricula_api.screenplay.task.CalificarMatricula;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarHistorial;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarMatriculas;

import java.util.List;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CalificacionStepDefinitions {

    private static final String ID_MATRICULA = "idMatricula";
    private static final double TOLERANCIA = 0.001;

    @Y("obtiene el id de su matricula del curso {string}")
    public void obtieneElIdDeSuMatricula(String curso) {
        Actor alumna = theActorInTheSpotlight();
        alumna.attemptsTo(ConsultarMatriculas.delCurso(curso));
        alumna.should(seeThat(LaRespuesta.codigoDeEstado(), equalTo(200)));
        List<Object> ids = SerenityRest.lastResponse().jsonPath().getList("id");
        assertThat("la alumna debe tener una matricula en " + curso, ids, not(empty()));
        alumna.remember(ID_MATRICULA, Long.valueOf(String.valueOf(ids.get(0))));
    }

    @Cuando("registra la nota {double} en la matricula de {word}")
    public void registraLaNota(double nota, String alumna) {
        Actor docente = theActorInTheSpotlight();
        Long id = theActorCalled(alumna).recall(ID_MATRICULA);
        theActorCalled(docente.getName());
        docente.attemptsTo(CalificarMatricula.conNota(nota, id));
    }

    @Cuando("{word} consulta su historial")
    public void consultaSuHistorial(String alumna) {
        theActorCalled(alumna).attemptsTo(ConsultarHistorial.propio());
    }

    @Y("el curso {string} figura con la condicion {string}")
    public void elCursoFiguraConLaCondicion(String curso, String condicion) {
        theActorInTheSpotlight().should(seeThat(ElHistorial.condicionDelCurso(curso), equalTo(condicion)));
    }

    @Y("sus creditos aprobados son {int} y su promedio ponderado es {double}")
    public void susCreditosYPromedio(int creditos, double promedio) {
        theActorInTheSpotlight().should(
                seeThat(ElHistorial.creditosAprobados(), equalTo(creditos)),
                seeThat(ElHistorial.promedioPonderado(), closeTo(promedio, TOLERANCIA))
        );
    }
}
