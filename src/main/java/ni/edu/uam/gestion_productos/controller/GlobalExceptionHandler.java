package ni.edu.uam.gestion_productos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> manejarAutenticacion(AuthenticationException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.UNAUTHORIZED.value());
        respuesta.put("error", "No autorizado");
        respuesta.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(NoSuchElementException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.NOT_FOUND.value());
        respuesta.put("error", "Recurso no encontrado");
        respuesta.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());
        respuesta.put("error", "Validación fallida");
        respuesta.put("errores", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarGeneral(Exception ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        respuesta.put("error", "Error interno");
        respuesta.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
    }
}
