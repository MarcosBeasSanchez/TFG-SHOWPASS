package tfg.proyecto.TFG.servicios;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.transaction.Transactional;
import tfg.proyecto.TFG.config.DtoConverter;
import tfg.proyecto.TFG.dtos.DTOEventoImagenSubida;
import tfg.proyecto.TFG.dtos.DTOeventoBajada;
import tfg.proyecto.TFG.dtos.DTOeventoSubida;
import tfg.proyecto.TFG.dtos.EventoRecomendadoDTO;
import tfg.proyecto.TFG.modelo.Categoria;
import tfg.proyecto.TFG.modelo.Evento;
import tfg.proyecto.TFG.modelo.Rol;
import tfg.proyecto.TFG.modelo.Usuario;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioEventoImagen;
import tfg.proyecto.TFG.repositorio.RepositorioInvitado;
import tfg.proyecto.TFG.repositorio.RepositorioUsuario;

/**
 * Implementación del servicio {@link IServicioEvento}.
 * 
 * Gestiona la lógica de negocio relacionada con los eventos, incluyendo:
 * <ul>
 *     <li>Creación, actualización y eliminación de eventos.</li>
 *     <li>Manejo de imágenes principales y carrusel de eventos.</li>
 *     <li>Gestión de invitados.</li>
 *     <li>Búsquedas avanzadas incluyendo microservicio de IA para recomendaciones.</li>
 *     <li>Obtención de eventos por vendedor, categoría o búsqueda por nombre.</li>
 *     <li>Obtención de recomendaciones y eventos similares para un usuario.</li>
 * </ul>
 */
@Service
public class ServicioEventoImpl implements IServicioEvento{
	
	
		/*
	     * URL del microservicio de recomendación/IA.
	     * Se puede inyectar vía properties, con valor por defecto.
	     */
		@Value("${microservicio.recomendacion.url:http://recomendacion-servicio:8000}")
	    private  String microServicioURL;  
	
		/*
	     * Cliente HTTP para llamadas REST al microservicio.
	     */
	    private final RestTemplate restTemplate = new RestTemplate();
	
	    /*
	     * Repositorios y servicios auxiliares.
	     * - eventoImagenDAO: manejo de imágenes asociadas a eventos.
	     * - usuarioDAO: consulta de vendedores y validación de roles.
	     * - servicioImagen: almacenamiento de imágenes físicas.
	     * - servicioEventoImagen: manejo de carrusel de imágenes.
	     * - servicioInvitado: gestión de invitados a un evento.
	     * - invitadoDAO: persistencia de invitados.
	     * - eventoDAO: Permite operaciones CRUD y consultas personalizadas como findByNombre, findByCategoria.
	     */
    	@Autowired  RepositorioEventoImagen eventoImagenDAO;
	    @Autowired  RepositorioUsuario usuarioDAO;
	    @Autowired  ServicioImagenImpl servicioImagen;
	    @Autowired  ServicioEventoImagenImpl servicioEventoImagen;
	    @Autowired ServicioInvitadoImpl servicioInvitado;
	    @Autowired RepositorioInvitado invitadoDAO;
	    @Autowired RepositorioEvento eventoDAO;	
	    
	    /*
	     * Conversor de entidades a DTOs y viceversa.
	     * Facilita la capa de presentación sin exponer entidades JPA directamente.
	     */
		@Autowired DtoConverter dtoConverter;

		
		/**
	     * Inserta un nuevo evento.
	     * <p>
	     * Procesa la imagen principal y las imágenes del carrusel, guarda invitados,
	     * y genera un DTO de salida con toda la información del evento.
	     * </p>
	     * 
	     * @param dto DTO de subida con los datos del evento
	     * @return DTO de bajada con información completa del evento
	     * @throws RuntimeException si el vendedor no existe o no tiene rol de vendedor, o si falla la carga de imágenes
	     */
	    @Override
	    @Transactional
	    public DTOeventoBajada insert(DTOeventoSubida dto) {
	        //  Buscar el vendedor
	        Usuario vendedor = usuarioDAO.findById(dto.getVendedorId())
	                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));

	        if (vendedor.getRol() != Rol.VENDEDOR) {
	            throw new RuntimeException("El usuario no tiene rol de vendedor");
	        }

	        try {
	            //  Procesar imagen principal
	            
	            String imagenFinal = null;
	            if (dto.getImagen() != null && !dto.getImagen().isBlank()) {
	            	// Guardar la imagen en el servidor y obtener URL
	                imagenFinal = servicioImagen.guardarImagenBase64(dto.getImagen(), "eventos/portadas");
	                System.out.println("[BACKEND] Portada guardada en: " + imagenFinal);
	            } else {
	                    imagenFinal = dto.getImagen(); // URL directa
	                }
	            

	            //  Procesar imágenes de carrusel
	            List<String> urlsCarrusel = new ArrayList<>();
	            if (dto.getImagenesCarruselUrls() != null && !dto.getImagenesCarruselUrls().isEmpty()) {
	                for (String img : dto.getImagenesCarruselUrls()) {
	                    if (img != null && !img.isBlank()) {
	                        String urlFinal = (img.length() > 200)
	                                ? servicioImagen.guardarImagenBase64(img, "eventos/carrusel")
	                                : img;
	                        urlsCarrusel.add(urlFinal);
	                    }
	                }
	            }

	            // Crear la entidad usando el builder (
	            Evento evento = Evento.builder()
	                    .nombre(dto.getNombre())
	                    .localizacion(dto.getLocalizacion())
	                    .inicioEvento(dto.getInicioEvento())
	                    .finEvento(dto.getFinEvento())
	                    .descripcion(dto.getDescripcion())
	                    .precio(dto.getPrecio())
	                    .aforoMax(dto.getAforoMax())
	                    .categoria(dto.getCategoria())
	                    .imagen(imagenFinal)
	                    .imagenesCarruselUrls(urlsCarrusel) //  builder con lista (por @Singular)
	                    .vendedor(vendedor)
	                    .build();

	            //  Guardar evento y cascada crea evento_carrusel automáticamente
	            Evento guardado = eventoDAO.save(evento);

	            //  Guardar invitados si existen
	            if (dto.getInvitados() != null && !dto.getInvitados().isEmpty()) {
	                servicioInvitado.guardarInvitados(guardado.getId(), dto.getInvitados());
	            }

	            //  Crear DTO de bajada con builder (sin usar setters)
	            DTOeventoBajada dtoBajada = DTOeventoBajada.builder()
	                    .id(guardado.getId())
	                    .nombre(guardado.getNombre())
	                    .localizacion(guardado.getLocalizacion())
	                    .inicioEvento(guardado.getInicioEvento())
	                    .finEvento(guardado.getFinEvento())
	                    .descripcion(guardado.getDescripcion())
	                    .precio(guardado.getPrecio())
	                    .aforo(guardado.getAforoMax())
	                    .categoria(guardado.getCategoria())
	                    .imagenPrincipalUrl(guardado.getImagen())
	                    .imagenesCarruselUrls(guardado.getImagenesCarruselUrls()) 
	                    .invitados(servicioInvitado.obtenerInvitados(guardado.getId()))
	                    .vendedorId(guardado.getVendedor().getId())
	                    .build();

	            return dtoBajada;

	        } catch (IOException e) {
	            throw new RuntimeException("Error guardando imágenes del evento", e);
	        }
	    }
	    
	    /**
	     * Obtiene todos los eventos.
	     *
	     * @return lista de DTOs de eventos
	     */
		@Override
		public List<DTOeventoBajada> obtenerTodosLosEventos() {
			// TODO Auto-generated method stub
			return dtoConverter.mapAll((List<Evento>)eventoDAO.findAll(), DTOeventoBajada.class);
	
			}
	

		/**
	     * Actualiza un evento existente con nueva información.
	     * <p>
	     * Procesa imagen principal, carrusel e invitados.
	     * </p>
	     *
	     * @param id ID del evento a actualizar
	     * @param dto DTO con información nueva
	     * @return DTO actualizado
	     */
		@Override
	    @Transactional
	    public DTOeventoBajada actualizarEvento(Long id, DTOeventoSubida dto) {
	        Evento evento = eventoDAO.findById(id)
	                .orElseThrow(() -> new RuntimeException("Evento no encontrado para actualizar"));
	
	        // Actualizamos campos básicos
	        evento.setNombre(dto.getNombre());
	        evento.setLocalizacion(dto.getLocalizacion());
	        evento.setInicioEvento(dto.getInicioEvento());
	        evento.setFinEvento(dto.getFinEvento());
	        evento.setDescripcion(dto.getDescripcion());
	        evento.setPrecio(dto.getPrecio());
	        evento.setAforoMax(dto.getAforoMax());
	        evento.setCategoria(dto.getCategoria());
	
	        try {
	            // Imagen principal
	        	String imagenFinal = null;
	            if (dto.getImagen() != null && !dto.getImagen().isBlank()) {
	                imagenFinal = servicioImagen.guardarImagenBase64(dto.getImagen(), "eventos/portadas");
	                System.out.println("✅ [BACKEND] Portada guardada en: " + imagenFinal);
	            } else {
	                    imagenFinal = dto.getImagen(); // URL directa
	                }
	            
	            evento.setImagen(imagenFinal); //SAVE
	
	            
	            // Carrusel (se reemplaza completamente)
	            	 List<String> urlsCarrusel = new ArrayList<>();
	 	            if (dto.getImagenesCarruselUrls() != null && !dto.getImagenesCarruselUrls().isEmpty()) {
	 	                for (String img : dto.getImagenesCarruselUrls()) {
	 	                    if (img != null && !img.isBlank()) {
	 	                        String urlFinal = (img.length() > 200)
	 	                                ? servicioImagen.guardarImagenBase64(img, "eventos/carrusel")
	 	                                : img;
	 	                        urlsCarrusel.add(urlFinal);
	 	                    }
	 	                }
	 	            }
	 	            
	 	           evento.setImagenesCarruselUrls(urlsCarrusel); //SAVE
	            
	 	       // Actualización de invitados
	            if (dto.getInvitados() != null && !dto.getInvitados().isEmpty()) {
	                servicioInvitado.eliminarInvitados(id);
	                servicioInvitado.guardarInvitados(id, dto.getInvitados());
	            }
	
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	
	        Evento actualizado = eventoDAO.save(evento);
	
	        // Construcción del DTO de bajada
	        DTOeventoBajada dtoBajada = dtoConverter.map(actualizado, DTOeventoBajada.class);
	        dtoBajada.setImagenesCarruselUrls(eventoImagenDAO.findUrlsByEventoId(actualizado.getId()));
	        dtoBajada.setInvitados(servicioInvitado.obtenerInvitados(evento.getId()));
	
	        return dtoBajada;
	    }
		

	    /**
	     * Actualiza un evento desde la versión móvil.
	     * Similar a {@link #actualizarEvento(Long, DTOeventoSubida)}, pero reemplaza todas
	     * las imágenes del carrusel usando {@link ServicioEventoImagenImpl}.
	     *
	     * @param id ID del evento
	     * @param dto datos del evento
	     * @return {@link DTOeventoBajada} actualizado
	     */
		@Override
	    @Transactional
	    public DTOeventoBajada actualizarEventoMovil(Long id, DTOeventoSubida dto) {
			 Evento evento = eventoDAO.findById(id)
			            .orElseThrow(() -> new RuntimeException("Evento no encontrado para actualizar"));
	
			    evento.setNombre(dto.getNombre());
			    evento.setLocalizacion(dto.getLocalizacion());
			    evento.setInicioEvento(dto.getInicioEvento());
			    evento.setFinEvento(dto.getFinEvento());
			    evento.setDescripcion(dto.getDescripcion());
			    evento.setPrecio(dto.getPrecio());
			    evento.setAforoMax(dto.getAforoMax());
			    evento.setCategoria(dto.getCategoria());
	
			    try {
			        // Portada igual que en insert()
			        if (dto.getImagen() != null && !dto.getImagen().isBlank()) {
			            String portadaFinal = (dto.getImagen().length() > 200)
			                    ? servicioImagen.guardarImagenBase64(dto.getImagen(), "eventos/portadas")
			                    : dto.getImagen();
	
			            evento.setImagen(portadaFinal);
			        }
	
			        // Reemplazar TODAS las imágenes de carrusel como insert()
			        servicioEventoImagen.eliminarCarrusel(id);
	
			        if (dto.getImagenesCarruselUrls() != null && !dto.getImagenesCarruselUrls().isEmpty()) {
			            List<String> urlsCarrusel = new ArrayList<>();
	
			            for (String img : dto.getImagenesCarruselUrls()) {
			                if (img != null && !img.isBlank()) {
			                    String urlFinal = (img.length() > 200)
			                            ? servicioImagen.guardarImagenBase64(img, "eventos/carrusel")
			                            : img;
	
			                    urlsCarrusel.add(urlFinal);
			                }
			            }
	
			            // Guardamos carrusel usando servicio como insert()
			            List<DTOEventoImagenSubida> lista = urlsCarrusel.stream()
			                    .map(url -> DTOEventoImagenSubida.builder().url(url).build())
			                    .toList();
	
			            servicioEventoImagen.guardarCarrusel(id, lista);
			            evento.setImagenesCarruselUrls(urlsCarrusel);
			        }
	
			        //  Reemplazar invitados directamente como insert()
			        servicioInvitado.eliminarInvitados(id);
			        if (dto.getInvitados() != null && !dto.getInvitados().isEmpty()) {
			            servicioInvitado.guardarInvitados(id, dto.getInvitados());
			        }
	
			    } catch (IOException e) {
			        throw new RuntimeException("Error actualizando imágenes", e);
			    }
	
			    Evento actualizado = eventoDAO.save(evento);
	
			    // Crear DTO de bajada igual que en insert()
			    DTOeventoBajada dtoBajada = dtoConverter.map(actualizado, DTOeventoBajada.class);
			    dtoBajada.setImagenesCarruselUrls(evento.getImagenesCarruselUrls());
			    dtoBajada.setInvitados(servicioInvitado.obtenerInvitados(id));
			    dtoBajada.setVendedorId(evento.getVendedor().getId());
	
			    return dtoBajada;
		}
	
		/**
	     * Elimina un evento por su ID.
	     *
	     * @param id ID del evento a eliminar
	     * @return true si se eliminó, false si no existía
	     */
		@Override
		public boolean eliminarEvento(Long id) {
			// TODO Auto-generated method stub
	        if (eventoDAO.existsById(id)) {
	            eventoDAO.deleteById(id);
	            return true;
	        }
	        return false;
	    }
	
		
		 /**
	     * Obtiene un evento por su nombre.
	     *
	     * @param nombre nombre del evento
	     * @return {@link DTOeventoBajada} con información del evento
	     * @throws RuntimeException si no se encuentra
	     */
		@Override
		public DTOeventoBajada obtnerPorElNombre(String nombre) {
			// TODO Auto-generated method stub
			Evento evento = eventoDAO.findByNombre(nombre)
		            .orElseThrow(() -> new RuntimeException(nombre + "no encontrado"));
		    return dtoConverter.map(evento, DTOeventoBajada.class);
		}
		
		/**
	     * Obtiene eventos por categoría.
	     *
	     * @param categoria categoría de eventos
	     * @return lista de {@link DTOeventoBajada} de la categoría
	     */
		@Override
		public List<DTOeventoBajada> obtenerPorCategoria(Categoria categoria) {
			// TODO Auto-generated method stub
			
			return eventoDAO.findByCategoria(categoria).stream().map(e -> {
	            DTOeventoBajada dto = dtoConverter.map(e, DTOeventoBajada.class);
	            dto.setImagenesCarruselUrls(eventoImagenDAO.findUrlsByEventoId(e.getId()));
	            return dto;
	        }).collect(Collectors.toList());
		}
	
	
		
		  /**
	     * Busca eventos por nombre utilizando IA y fallback tradicional.
	     *
	     * @param nombre nombre a buscar
	     * @return lista de eventos encontrados
	     */
		@Override
	    public List<DTOeventoBajada> buscarPorNombreConteniendo(String nombre) {
	
	        // 1) BUSCAR PRIMERO EN LA IA
	        
	        List<Long> idsIA = buscarEventosPorIA(nombre);
	
	        if (!idsIA.isEmpty()) {
	            // Convertimos IDs a DTO
	            return idsIA.stream()
	                    .map(id -> eventoDAO.findById(id).orElse(null))
	                    .filter(e -> e != null)
	                    .map(e -> {
	                        DTOeventoBajada dto = dtoConverter.map(e, DTOeventoBajada.class);
	                        dto.setImagenesCarruselUrls(eventoImagenDAO.findUrlsByEventoId(e.getId()));
	                        return dto;
	                    })
	                    .toList();
	        }
	
	        // 2) FALLBACK → BÚSQUEDA NORMAL
	
	        return eventoDAO.findByNombreContainingIgnoreCase(nombre)
	                .stream()
	                .map(e -> {
	                    DTOeventoBajada dto = dtoConverter.map(e, DTOeventoBajada.class);
	                    dto.setImagenesCarruselUrls(eventoImagenDAO.findUrlsByEventoId(e.getId()));
	                    return dto;
	                })
	                .toList();
	    }
		
		
		@Override
		public DTOeventoBajada obtnerPorElId(Long id) {
			return dtoConverter.map(eventoDAO.findById(id),DTOeventoBajada.class);
		}
		
		/**
		 * Obtiene todos los eventos asociados a un vendedor específico.
		 *
		 * <p>El método realiza los siguientes pasos:</p>
		 * <ol>
		 *     <li>Busca al usuario por su ID usando {@link RepositorioUsuario}.</li>
		 *     <li>Verifica que el usuario tenga el rol {@link Rol#VENDEDOR}.</li>
		 *     <li>Obtiene todos los eventos del vendedor mediante {@link RepositorioEvento#findByVendedorId}.</li>
		 *     <li>Convierte cada evento a {@link DTOeventoBajada} usando {@link DtoConverter}.</li>
		 *     <li>Agrega las URLs del carrusel de imágenes consultando {@link RepositorioEventoImagen#findUrlsByEventoId}.</li>
		 * </ol>
		 *
		 * @param vendedorId ID del vendedor cuyos eventos se desean obtener
		 * @return lista de {@link DTOeventoBajada} correspondientes a los eventos del vendedor
		 * @throws RuntimeException si el usuario no existe o no tiene rol de vendedor
		 */
		@Override
		public List<DTOeventoBajada> obtenerPorVendedor(Long vendedorId) {
			// TODO Auto-generated method stub
			Usuario vendedor = usuarioDAO.findById(vendedorId)
	                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
			
	        if (vendedor.getRol() != Rol.VENDEDOR)
	            throw new RuntimeException("El usuario no tiene rol de vendedor");
	
	        return eventoDAO.findByVendedorId(vendedorId).stream().map(e -> {
	            DTOeventoBajada dto = dtoConverter.map(e, DTOeventoBajada.class);
	            dto.setImagenesCarruselUrls(eventoImagenDAO.findUrlsByEventoId(e.getId()));
	            return dto;
	        }).collect(Collectors.toList());
		}
		
		
		
		 /**
	     * Obtiene recomendaciones de eventos para un usuario usando microservicio IA.
	     *
	     * @param userId ID del usuario
	     * @return lista de {@link EventoRecomendadoDTO}
	     */
		@Override
		public List<EventoRecomendadoDTO> obtenerRecomendacionesUsuario(Long userId) {
			// TODO Auto-generated method stub
			 String url = microServicioURL + "/recommendations?userId=" + userId;
	
			 // Llamamos al microservicio  devuelve 
			    Map<String, List<Integer>> response = restTemplate.getForObject(url, Map.class);
			    List<Integer> ids = response.get("eventos_recomendados");
	
			    if (ids == null || ids.isEmpty()) return List.of();
	
			    // Convertimos Integer → Long
			    List<Long> idsLong = ids.stream()
			            .map(Integer::longValue)
			            .collect(Collectors.toList());
	
			    // Consultamos la BD usando los IDs devueltos
			    List<Evento> eventos = (List<Evento>) eventoDAO.findAllById(idsLong);
	
			    // Mapeamos con DtoConverter a EventoRecomendadoDTO
			    return eventos.stream()
			            .map(e -> dtoConverter.map(e, EventoRecomendadoDTO.class))
			            .collect(Collectors.toList());
		}
		
		
		/**
	     * Obtiene eventos similares a uno dado usando microservicio IA.
	     *
	     * @param eventoId ID del evento
	     * @return lista de {@link EventoRecomendadoDTO}
	     */
		@Override
		public List<EventoRecomendadoDTO> obtenerSimilaresEvento(Long eventoId) {
			// TODO Auto-generated method stub
			 String url = microServicioURL + "/recommendations/event?eventoId=" + eventoId;
	
			 Map<String, List<Integer>> response = restTemplate.getForObject(url, Map.class);
			    List<Integer> ids = response.get("eventos_similares");
	
			    if (ids == null || ids.isEmpty()) return List.of();
	
			    List<Long> idsLong = ids.stream()
			            .map(Integer::longValue)
			            .collect(Collectors.toList());
	
			    List<Evento> eventos = (List<Evento>) eventoDAO.findAllById(idsLong);
	
			    return eventos.stream()
			            .map(e -> dtoConverter.map(e, EventoRecomendadoDTO.class))
			            .collect(Collectors.toList());
		}
		
		
		/**
	     * Llama al microservicio de IA para buscar eventos por nombre.
	     *
	     * @param nombre nombre del evento
	     * @return lista de IDs de eventos encontrados
	     */
		@Override
		public List<Long> buscarEventosPorIA(String nombre) {
			// TODO Auto-generated method stub
			 String url = microServicioURL + "/search?nombre=" + nombre;
	
		        // Llamada al microservicio
		        Map response = restTemplate.getForObject(url, Map.class);
	
		        if (response == null || !response.containsKey("eventos_encontrados")) {
		            return List.of();
		        }
	
		        // Convertimos a lista de IDs (Long)
		        List<Integer> ids = (List<Integer>) response.get("eventos_encontrados");
		        return ids.stream().map(Long::valueOf).toList();
		}
	
}
