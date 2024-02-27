package com.example.RestApiSpringBootMySQLFirebase.Service;


import com.example.RestApiSpringBootMySQLFirebase.Config.FirestoreInitializer;
import com.example.RestApiSpringBootMySQLFirebase.Entity.Usuario;
import com.example.RestApiSpringBootMySQLFirebase.Repository.UsuarioRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;




@Service

@Transactional
public class UsuarioService {


    @Autowired
    private UsuarioRepository usuarioRepository;


    @Autowired
    private FirestoreInitializer firestoreInitializer;



    public List<Usuario> obtenerTodos() {
        // Obtener todos los usuarios de MySQL
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Obtener todos los usuarios de Firestore
        List<Usuario> usuariosFirestore = obtenerUsuariosDeFirestore();

        // Combinar usuarios de MySQL y Firestore
        usuarios.addAll(usuariosFirestore);

        return usuarios;
    }


    public Optional<Usuario> obtenerPorId(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);

        if (!usuario.isPresent()) {
            // Buscar usuario en Firestore si no se encuentra en MySQL
            usuario = obtenerUsuarioDeFirestorePorId(id);
        }

        return usuario;
    }


    public Usuario guardarUsuario(Usuario usuario) {
        // Guardar usuario en MySQL
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Guardar usuario en Firestore
        guardarUsuarioEnFirestore(usuario);

        return usuarioGuardado;
    }


    public void eliminarUsuario(Long id) {
        // Eliminar usuario de MySQL
        usuarioRepository.deleteById(id);

        // Eliminar usuario de Firestore
        eliminarUsuarioDeFirestore(id);
    }

    // Métodos para Firestore


    private Firestore getFirestore() {
        return firestoreInitializer.getFirestore();
    }


    private List<Usuario> obtenerUsuariosDeFirestore() {
        // Obtiene una instancia de Firestore
        Firestore firestore = getFirestore();
        // Obtiene una referencia a la colección "usuarios" en Firestore
        CollectionReference usuariosRef = firestore.collection("usuarios");
        // Inicializa una lista para almacenar los usuarios recuperados
        List<Usuario> usuarios = new ArrayList<>();

        // Obtiene los datos de Firestore usando una operación asíncrona
        ApiFuture<QuerySnapshot> future = usuariosRef.get();
        try {
            // Espera a que se completen las operaciones asíncronas y obtiene el resultado
            QuerySnapshot querySnapshot = future.get();
            // Itera sobre cada documento en la colección
            for (QueryDocumentSnapshot document : querySnapshot) {
                // Convierte el documento en un objeto Usuario y lo agrega a la lista
                Usuario usuario = document.toObject(Usuario.class);
                usuarios.add(usuario);
            }
        } catch (InterruptedException | ExecutionException e) {
            // Maneja cualquier excepción lanzada durante la obtención de datos
            e.printStackTrace();
        }

        return usuarios;
    }


    private Optional<Usuario> obtenerUsuarioDeFirestorePorId(Long id) {
        // Obtiene una instancia de Firestore
        Firestore firestore = getFirestore();
        // Obtiene una referencia a la colección "usuarios" en Firestore
        CollectionReference usuariosRef = firestore.collection("usuarios");

        // Realiza una consulta en Firestore para obtener un usuario con el ID especificado
        ApiFuture<QuerySnapshot> future = usuariosRef.whereEqualTo("id", id).get();
        try {
            // Espera a que se complete la operación asíncrona y obtiene el resultado
            QuerySnapshot querySnapshot = future.get();
            // Verifica si el resultado de la consulta no está vacío
            if (!querySnapshot.isEmpty()) {
                // Obtiene el primer documento de la consulta
                QueryDocumentSnapshot document = querySnapshot.getDocuments().get(0);
                // Convierte el documento en un objeto Usuario y lo devuelve dentro de un Optional
                Usuario usuario = document.toObject(Usuario.class);
                return Optional.of(usuario);
            }
        } catch (InterruptedException | ExecutionException e) {
            // Maneja cualquier excepción lanzada durante la obtención de datos
            e.printStackTrace();
        }

        // Devuelve un Optional vacío si no se encuentra ningún usuario con el ID especificado
        return Optional.empty();
    }

    /**
     * Guarda un usuario en Firestore.
     * @param usuario Usuario a guardar en Firestore.
     */
    private void guardarUsuarioEnFirestore(Usuario usuario) {
        // Obtiene una instancia de Firestore.
        Firestore firestore = getFirestore();
        // Obtiene una referencia a la colección "usuarios" en Firestore.
        CollectionReference usuariosRef = firestore.collection("usuarios");
        // Establece los datos del usuario que se van a guardar en Firestore.
        usuariosRef.document(String.valueOf(usuario.getId())).set(usuario);
    }


    private void eliminarUsuarioDeFirestore(Long id) {
        // Obtiene una instancia de Firestore.
        Firestore firestore = getFirestore();
        // Obtiene una referencia a la colección "usuarios" en Firestore.
        CollectionReference usuariosRef = firestore.collection("usuarios");
        // Elimina el documento de Firestore correspondiente al ID especificado.
        usuariosRef.document(String.valueOf(id)).delete();
    }
}
