package modelo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class DataBase {
	private MongoClient client;
	private MongoDatabase db;
	
	private DataBase() {
		MongoClientURI uri  = new MongoClientURI("mongodb://equipo03:pis03equipo@ds113935.mlab.com:13935/equipo03"); 
        this.client = new MongoClient(uri);
        this.db = client.getDatabase(uri.getDatabase());
	}
	
	public static class SingletonHolder{
        static DataBase singleton = new DataBase();
    }
	
	public static DataBase get(){
        return SingletonHolder.singleton;
    }
	
	protected boolean create(Persona p) {
		Document doc = null;
		try {
			doc=new Document("email",p.getEmail())
					.append("clave", p.getPassword())
					.append("username", p.getUsername())
					.append("nombre", p.getNombre())
					.append("apellidos", p.getApellidos())
					.append("direccion", p.getDireccion())
					.append("telefono", p.getTelefono())
					.append("foto", p.getFoto())
					.append("esAdmin", p.isEsAdmin())
					.append("amigos", p.getAmigos())
					.append("peticiones",p.getPeticiones())
					.append("peticionesenviadas", p.getPeticionesenviadas())
					.append("fecha", p.getFecha())
					.append("fecha_ultimo_login", p.getFecha_ultimo_login())
					.append("ultimo_equipo", p.getUltimo_equipo());
			
			
			this.db.getCollection("usuarios").insertOne(doc);
			return true;
		}catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	protected boolean existeEmail(String email) {
		Document doc = null;
		boolean existe = false;
		MongoCursor<Document> elementos = this.db.getCollection("usuarios").find().iterator();
		while(elementos.hasNext()) {
			doc=elementos.next();
			if(doc.get("email").toString().equalsIgnoreCase(email))existe=true;
		}
		return existe;
	}
	
	protected boolean existeUsername(String username) {
		Document doc= null;
		boolean existe = false;
		MongoCursor<Document> elementos = db.getCollection("usuarios").find().iterator();
		while(elementos.hasNext()) {
			doc=elementos.next();
			if(doc.get("username").toString().equalsIgnoreCase(username))existe=true;
		}
		return existe;
	}
	
	protected boolean login(Persona p) throws Exception {
		Document doc=null;
		boolean logueado = false;
		MongoCursor<Document> elementos = db.getCollection("usuarios").find().iterator();
		while(elementos.hasNext()) {
			doc=elementos.next();
			if((doc.get("username").toString().equalsIgnoreCase(p.getUsername()))&&
			   (doc.get("clave").toString().equalsIgnoreCase(p.getPassword()))) {
				logueado=true;
			}
		}
		return logueado;
	}
	
	protected boolean delete(Persona p) {
		Document aux=null;
		boolean borrado= false;
		try {
			MongoCursor<Document> elementos = db.getCollection("usuarios").find().iterator();
			while(elementos.hasNext()) {
				aux=elementos.next();
				if((aux.get("username").toString().equalsIgnoreCase(p.getUsername()))) {
					db.getCollection("usuarios").deleteOne(aux);
					borrado=true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return borrado;
	}

	protected boolean deleteEmail(String email) {
		Document aux=null;
		MongoCursor<Document> elementos = db.getCollection("usuarios").find().iterator();
		
		while(elementos.hasNext()) {
			aux=elementos.next();
			if((aux.get("email").toString().equalsIgnoreCase(email))) {
				db.getCollection("usuarios").deleteOne(aux);
			
			}
		}
		return true;
	}
	
	protected boolean update(Persona p){		
		deleteEmail(p.getEmail());
		create(p);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	protected Persona getPersona(String username) {
		Document doc=null;
		Persona p=null;
		MongoCursor<Document> elementos = db.getCollection("usuarios").find().iterator();
		while(elementos.hasNext()) {
			doc=elementos.next();
			if((doc.get("username").toString().equalsIgnoreCase(username))) {
				p = new Persona(doc.getString("nombre"), doc.getString("apellidos"), doc.getString("username"), doc.getString("email"), doc.getString("clave"), doc.getString("direccion"), doc.getString("telefono"), doc.getString("foto"), doc.getBoolean("esAdmin"),(ArrayList<String>) doc.get("amigos"),(ArrayList<String>) doc.get("peticiones"),(ArrayList<String>) doc.get("peticionesenviadas"));
				p.setFecha(doc.getDate("fecha"));
				p.setUltimo_equipo("ultimo_equipo");
			}
		}		
		return p;
	}
	
	protected Persona getPersonaByEmail(String email) {
		Persona p = null;
		Document doc=null;
		MongoCursor<Document> elementos = db.getCollection("usuarios").find().iterator();
		while(elementos.hasNext()) {
			doc=elementos.next();
			if((doc.get("email").toString().equalsIgnoreCase(email))) {
				p = new Persona(doc.getString("nombre"), doc.getString("apellidos"), doc.getString("username"), doc.getString("email"), doc.getString("clave"), doc.getString("direccion"), doc.getString("telefono"), doc.getString("foto"), doc.getBoolean("esAdmin"),(ArrayList<String>) doc.get("amigos"),(ArrayList<String>) doc.get("peticiones"),(ArrayList<String>) doc.get("peticionesenviadas"));
			}
		}		
		return p;
	}
	
	@SuppressWarnings("unchecked")
	protected ArrayList<Persona> getAllPersonas(){
		Document doc=null;
		ArrayList<Persona> personas = new ArrayList<Persona>();
		Persona p = null;
		MongoCursor<Document> elementos = db.getCollection("usuarios").find().iterator();
		while(elementos.hasNext()) {
			doc=elementos.next();
			p = new Persona(doc.getString("nombre"), doc.getString("apellidos"), doc.getString("username"), doc.getString("email"), doc.getString("clave"), doc.getString("direccion"), doc.getString("telefono"), doc.getString("foto"), doc.getBoolean("esAdmin"), (ArrayList<String>) doc.get("amigos"),(ArrayList<String>) doc.get("peticiones"),(ArrayList<String>) doc.get("peticionesenviadas"));
			personas.add(p);
		}		
		return personas;
	}

protected boolean createPublicacion(Publicacion p) {
	Document doc=null;
	try {
	      doc=new Document("username", p.getUsername())
	          .append("mensaje", p.getMensaje())
	          .append("compartir", p.getCompartirCon())
	          .append("adjuntos", p.getAdjuntos())
	          .append("fecha", p.getFecha().toString());
	      db.getCollection("publicaciones").insertOne(doc);
	      return true;
	    }catch(Exception ex) {
	      ex.printStackTrace();
	      return false;
	    }
  }
  
  protected Publicacion readPublicacion(String username, String fecha) {
	  Document aux=null;
      Publicacion pub = null;

	  try {
		  MongoCursor<Document> elementos = db.getCollection("publicaciones").find().iterator();
      while(elementos.hasNext()) {
        aux = elementos.next();
        if(aux.get("username").toString().equalsIgnoreCase(username)&&(aux.get("fecha").toString().equalsIgnoreCase(fecha))) {
          List<String>els=(List<String>)aux.get("adjuntos");
          LinkedList<String> adjs=new LinkedList<String>();
          for(int i=0; i<els.size();i++) {
            adjs.add(els.get(i));
          }
          pub=new Publicacion(aux.get("username").toString(), aux.get("mensaje").toString(), aux.get("compartir").toString(), adjs, aux.get("fecha").toString());
        }
      }
    }catch(Exception ex) {
      ex.printStackTrace();
    }
    return pub;
  }
  
  protected boolean updatePublicacion(Publicacion antigua, Publicacion nueva) {
    try {
      deletePublicacion(antigua);
      createPublicacion(nueva);
      return true;
    }catch(Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }
  protected boolean deletePublicacion(Publicacion pub) {
	  Document aux = null;
    boolean borrado= false;
    MongoCursor<Document> elementos = db.getCollection("publicaciones").find().iterator();
    while(elementos.hasNext()) {
      aux=elementos.next();
      System.out.println("Entra: "+aux.get("username").toString()+" "+aux.get("fecha").toString());
      if((aux.get("username").toString().equalsIgnoreCase(pub.getUsername()))&&
         (aux.get("fecha").toString().equalsIgnoreCase(pub.getFecha().toString()))) {
        db.getCollection("publicaciones").deleteOne(aux);
        borrado=true;
      }
    }
    return borrado;
  }
  
  protected boolean deletePublicacionExacta(String username, String fecha) {
    Document aux=null;
	MongoCursor<Document> elementos = db.getCollection("publicaciones").find().iterator();
    while(elementos.hasNext()) {
      aux=elementos.next();
      if((aux.get("username").toString().equalsIgnoreCase(username))&&(aux.get("fecha").toString().equalsIgnoreCase(fecha))) {
        db.getCollection("publicaciones").deleteOne(aux);
      }
    }
    return true;
  }
  protected boolean deletePublicacionesUsuario(String username) {
	  Document aux=null;
	    MongoCursor<Document> elementos = db.getCollection("publicaciones").find().iterator();
	    while(elementos.hasNext()) {
	      aux=elementos.next();
	      if((aux.get("username").toString().equalsIgnoreCase(username))) {
	        db.getCollection("publicaciones").deleteOne(aux);
	      }
	    }
	    return true;
	  }
  
  protected boolean deleteAllPublicaciones() {
	  Document aux=null;
    MongoCursor<Document> elementos = db.getCollection("publicaciones").find().iterator();
    while(elementos.hasNext()) {
      aux=elementos.next();
      db.getCollection("publicaciones").deleteOne(aux);
    }
    return true;
  }
  
  protected LinkedList<Publicacion> readPublicaciones(String username) {
    LinkedList<Publicacion>pubs = new LinkedList<Publicacion>();
    Document aux=null;
    try {
      MongoCursor<Document> elementos = db.getCollection("publicaciones").find().iterator();
      while(elementos.hasNext()) {
        aux = elementos.next();
        if(aux.get("username").toString().equalsIgnoreCase(username)) {
          List<String>els=(List<String>)aux.get("adjuntos");
          LinkedList<String> adjs=new LinkedList<String>();
          for(int i=0; i<els.size();i++) {
            adjs.add(els.get(i));
          }
          pubs.add(new Publicacion(aux.get("username").toString(), aux.get("mensaje").toString(), aux.get("compartir").toString(), adjs, aux.get("fecha").toString()));
        }
      }
    }catch(Exception ex) {
      ex.printStackTrace();
    }
    return pubs;
  }
  
  protected LinkedList<Publicacion> readAllPublicaciones() {
    LinkedList<Publicacion>pubs = new LinkedList<Publicacion>();
    Document aux=null;
    try {
      MongoCursor<Document> elementos = db.getCollection("publicaciones").find().iterator();
      while(elementos.hasNext()) {
        aux = elementos.next();
        List<String>els=(List<String>)aux.get("adjuntos");
        LinkedList<String> adjs=new LinkedList<String>();
        for(int i=0; i<els.size();i++) {
          adjs.add(els.get(i));
        }
        System.out.println(aux.get("mensaje").toString());
        System.out.println(aux.get("fecha").toString());
        System.out.println(aux.get("username").toString());
        System.out.println(aux.get("compartir").toString());
        pubs.add(new Publicacion(aux.get("username").toString(), aux.get("mensaje").toString(), aux.get("compartir").toString(), adjs, aux.get("fecha").toString()));
      }
    }catch(Exception ex) {
      ex.printStackTrace();
    }
    return pubs;
  }
  
  public LinkedList<Publicacion> readAllPublicacionesUser(Persona user) {
	    LinkedList<Publicacion>pubs = new LinkedList<Publicacion>();

	    String username = user.getUsername();
	    Document aux = null;
    
	    try {
	    	MongoCursor<Document> elementos = db.getCollection("publicaciones").find().iterator();
	      while(elementos.hasNext()) {
	        aux = elementos.next();
	        if(!aux.get("username").toString().equalsIgnoreCase(username) && 
	        		aux.get("compartir").toString().equals("privado"))
	        	continue;
	        else if(!aux.get("username").toString().equalsIgnoreCase(username) && 
	        		aux.get("compartir").toString().equals("amigos") && 
	        		!user.isAmigo(aux.get("username").toString()))
	        	continue;
	        	
	        LinkedList<String> adjs=new LinkedList<String>();
	        
	        System.out.println(aux.get("mensaje").toString());
	        System.out.println(aux.get("fecha").toString());
	        System.out.println(aux.get("username").toString());
	        System.out.println(aux.get("compartir").toString());
	        pubs.add(new Publicacion(aux.get("username").toString(), aux.get("mensaje").toString(), aux.get("compartir").toString(), adjs, aux.get("fecha").toString()));
	      }
	    }catch(Exception ex) {
	      System.out.println("Error al cargar publicaciones");
	    }
	    return pubs;
	  }


	
}
