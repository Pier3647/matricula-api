# language: es
@P1
Característica: Matrícula del alumno
  Como alumno
  Quiero matricularme y pagar mi curso
  Para llevarlo en el ciclo

  Antecedentes:
    Dado que el alumno "i202610003" inicia sesion con la clave "Cibertec2026"

  Escenario: Cotizar el curso TS5514
    Cuando cotiza el curso "TS5514"
    Entonces el codigo HTTP es 200
    Y la cotizacion tiene 4 creditos, subtotal 480,0, descuento de 0 por ciento y total 480,0

  Escenario: Matricularse y pagar el curso TS5514
    Dado cotiza el curso "TS5514"
    Cuando se matricula en el curso "TS5514"
    Entonces el codigo HTTP es 201
    Y la matricula queda en estado "REGISTRADA"
    Y el monto de la matricula es 480,0 y coincide con el total cotizado
    Y el cuerpo cumple el esquema "matricula.json"
    Cuando paga la matricula con el metodo "YAPE"
    Entonces el codigo HTTP es 200
    Y la matricula queda en estado "PAGADA"
    Y la fecha de pago tiene valor
    Cuando consulta sus pagos
    Entonces el codigo HTTP es 200
    Y existe el pago de su matricula con el metodo "YAPE" y numero de operacion con formato OP-000000

  Escenario: Regla de negocio que debe fallar al matricularse en AD5101
    Cuando se matricula en el curso "AD5101"
    Entonces el codigo HTTP es 422
    Y el error tiene el codigo "CURSO_DE_OTRA_CARRERA"
    Y el cuerpo cumple el esquema "error.json"
