package ni.edu.uam.gestion_productos.controller;

import java.util.Map;
import ni.edu.uam.gestion_productos.dto.LoginRequest;
import ni.edu.uam.gestion_productos.dto.LoginResponse;
import ni.edu.uam.gestion_productos.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager manager;
    private final TokenService tokens;

    public AuthController(AuthenticationManager manager,
                          TokenService tokens) {
        this.manager = manager;
        this.tokens = tokens;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest datos) {
        Authentication auth = manager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        datos.username(), datos.password()));
        return ResponseEntity.ok(
                new LoginResponse(tokens.crearToken(auth), "Bearer"));
    }

    @GetMapping("/perfil")
    public ResponseEntity<Map<String, Object>> perfil(Authentication auth) {
        return ResponseEntity.ok(Map.of(
                "username", auth.getName(),
                "roles", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        ));
    }
}

