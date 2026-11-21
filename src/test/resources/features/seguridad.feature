# language: es
@P2
Característica: Seguridad de la sesión
  Como usuario de la API
  Quiero que mi sesion sea segura
  Para que nadie use mi cuenta

  Escenario: Rotación del refresh token
    Dado que Carla consulta la API de matricula
    Y inicia sesion con el usuario "i202610003" y la clave "Cibertec2026"
    Cuando renueva sus tokens con el refresh token
    Entonces el codigo de respuesta es 200
    Y la respuesta cumple el esquema "token.json"
    Cuando intenta renovar otra vez con el refresh token original
    Entonces la API responde 401 con el codigo de error "REFRESH_REUTILIZADO"
    Cuando consulta su perfil
    Entonces la API responde 401 con el codigo de error "TOKEN_INVALIDO"

  Escenario: Cierre de sesión
    Dado que Carla consulta la API de matricula
    Y inicia sesion con el usuario "i202610003" y la clave "Cibertec2026"
    Cuando cierra su sesion
    Entonces el codigo de respuesta es 204
    Cuando consulta su perfil
    Entonces la API responde 401 con el codigo de error "TOKEN_INVALIDO"

  Escenario: Bloqueo de cuenta por intentos fallidos
    Dado que Jorge consulta la API de matricula
    Cuando ingresa con el usuario "i202610004" una clave incorrecta en cada intento:
      | intento | clave     | status | codigo                 |
      | 1       | Errada001 | 401    | CREDENCIALES_INVALIDAS |
      | 2       | Errada002 | 401    | CREDENCIALES_INVALIDAS |
      | 3       | Errada003 | 401    | CREDENCIALES_INVALIDAS |
      | 4       | Errada004 | 401    | CREDENCIALES_INVALIDAS |
      | 5       | Errada005 | 423    | CUENTA_BLOQUEADA       |
    Y intenta ingresar con el usuario "i202610004" y la clave "Cibertec2026"
    Entonces la API responde 423 con el codigo de error "CUENTA_BLOQUEADA"

  Escenario: Alumno sin permiso de administrador
    Dado que Carla consulta la API de matricula
    Y inicia sesion con el usuario "i202610003" y la clave "Cibertec2026"
    Cuando lista todos los alumnos
    Entonces la API responde 403 con el codigo de error "ACCESO_DENEGADO"
