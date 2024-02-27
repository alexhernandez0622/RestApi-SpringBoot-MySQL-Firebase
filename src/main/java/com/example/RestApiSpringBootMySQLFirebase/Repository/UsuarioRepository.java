package com.example.RestApiSpringBootMySQLFirebase.Repository;

import com.example.RestApiSpringBootMySQLFirebase.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
