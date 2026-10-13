package ni.edu.uam.gestion_productos.controller;

import ni.edu.uam.gestion_productos.entity.Categoria;
import ni.edu.uam.gestion_productos.repository.CategoriaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaRepository repository;

    public CategoriaController(CategoriaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Categoria> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerPorId(@PathVariable Integer id) {
        Categoria categoria = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada con ID: " + id));
        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    public ResponseEntity<Categoria> guardar(@RequestBody Categoria categoria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(@PathVariable Integer id, @RequestBody Categoria datos) {
        Categoria existente = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada con ID: " + id));
        existente.setNombre(datos.getNombre());
        existente.setActiva(datos.isActiva());
        return ResponseEntity.ok(repository.save(existente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Categoría no encontrada con ID: " + id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
