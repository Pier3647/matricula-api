# language: es
@P3
Característica: Calificación de matrículas
  Como docente
  Quiero registrar la nota de mis alumnos
  Para que se actualice su historial

  Antecedentes:
    Dado que Rosa consulta la API de matricula
    Y inicia sesion con el usuario "i202610005" y la clave "Cibertec2026"

  Esquema del escenario: El docente registra la nota <nota> y el historial se actualiza
    Dado obtiene el id de su matricula del curso "PI5620"
    Y que Marco consulta la API de matricula
    Y inicia sesion con el usuario "D0001" y la clave "Cibertec2026"
    Cuando registra la nota <nota> en la matricula de Rosa
    Entonces el codigo de respuesta es 200
    Cuando Rosa consulta su historial
    Entonces el codigo de respuesta es 200
    Y la respuesta cumple el esquema "historial.json"
    Y el curso "PI5620" figura con la condicion "<condicion>"
    Y sus creditos aprobados son <creditos> y su promedio ponderado es <promedio>

    Ejemplos:
      | nota | condicion   | creditos | promedio |
      | 14   | APROBADO    | 13       | 14,77    |
      | 12,5 | DESAPROBADO | 7        | 14,08    |

    @punto-decimal
    Ejemplos: con punto
      | nota | condicion   | creditos | promedio |
      | 12.5 | DESAPROBADO | 7        | 14,08    |

  Escenario: Un docente que no dicta el curso intenta registrar la nota
    Dado obtiene el id de su matricula del curso "PI5620"
    Y que Lucia consulta la API de matricula
    Y inicia sesion con el usuario "D0002" y la clave "Cibertec2026"
    Cuando registra la nota 15 en la matricula de Rosa
    Entonces la API responde 403 con el codigo de error "ACCESO_DENEGADO"

  Escenario: No se puede calificar una matrícula que no está pagada
    Dado obtiene el id de su matricula del curso "PS5511"
    Y que Marco consulta la API de matricula
    Y inicia sesion con el usuario "D0001" y la clave "Cibertec2026"
    Cuando registra la nota 15 en la matricula de Rosa
    Entonces la API responde 409 con el codigo de error "MATRICULA_NO_PAGADA"
