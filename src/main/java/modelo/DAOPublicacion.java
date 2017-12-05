package modelo;

import java.util.LinkedList;

public class DAOPublicacion {
	DataBase db;
	public DAOPublicacion() {
		db = DataBase.get();
	}
	public boolean crearPublicacion(Publicacion p) {
		return db.createPublicacion(p);
	}
	public Publicacion leerPublicacion(String username, String fecha) {
		return db.readPublicacion(username, fecha);
	}
	public LinkedList<Publicacion> leerPublicaciones(String username) {
		return db.readPublicaciones(username);
	}
	public LinkedList<Publicacion> leerTodasPublicaciones() {
		return db.readAllPublicaciones();
	}
	public LinkedList<Publicacion> leerTodasPublicacionesUser(Persona user) {
		return db.readAllPublicacionesUser(user);
	}
	public boolean actualizaPublicacion(Publicacion antigua, Publicacion nueva) {
		return db.updatePublicacion(antigua, nueva);
	}
	public boolean borrarPublicacion(Publicacion pub) {
		return db.deletePublicacion(pub);
	}
	public boolean borrarPublicacionExacta(String username, String fecha) {
		return db.deletePublicacionExacta(username,fecha);
	}
	public boolean borrarPublicacionesUsuario(String username) {
		return db.deletePublicacionesUsuario(username);
	}
	public boolean borrarTodasPublicaciones() {
		return db.deleteAllPublicaciones();
	}
}