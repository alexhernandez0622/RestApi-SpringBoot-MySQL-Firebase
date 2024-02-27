package com.example.RestApiSpringBootMySQLFirebase.Controller;

import com.example.RestApiSpringBootMySQLFirebase.Entity.Usuario;
import com.example.RestApiSpringBootMySQLFirebase.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> obtenerUsuarios() {

        return usuarioService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Optional<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {

        return usuarioService.obtenerPorId(id);
    }

    @PostMapping
    public Usuario crearUsuario(@RequestBody Usuario usuario) {

        return usuarioService.guardarUsuario(usuario);
    }

    @PutMapping("/{id}")
    public Usuario actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        // Verifica si el usuario existe en MySQL
        Optional<Usuario> usuarioExistente = usuarioService.obtenerPorId(id);
        if (usuarioExistente.isPresent()) {
            usuario.setId(id);
            return usuarioService.guardarUsuario(usuario);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
    }

    @DeleteMapping("/{id}")
    public void eliminarUsuario(@PathVariable Long id) {

        usuarioService.eliminarUsuario(id);
    }
}
