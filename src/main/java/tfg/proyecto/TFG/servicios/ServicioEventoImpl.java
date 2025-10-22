package tfg.proyecto.TFG.servicios;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tfg.proyecto.TFG.config.DtoConverter;
import tfg.proyecto.TFG.dtos.DTOEventoImagenSubida;
import tfg.proyecto.TFG.dtos.DTOeventoBajada;
import tfg.proyecto.TFG.dtos.DTOeventoSubida;
import tfg.proyecto.TFG.modelo.Categoria;
import tfg.proyecto.TFG.modelo.Evento;
import tfg.proyecto.TFG.modelo.Rol;
import tfg.proyecto.TFG.modelo.Usuario;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioEventoImagen;
import tfg.proyecto.TFG.repositorio.RepositorioUsuario;

@Service
public class ServicioEventoImpl implements IServicioEvento{
	
	@Autowired
	RepositorioEvento eventoDAO;
	
	@Autowired
	DtoConverter dtoConverter;
	

@Autowired  RepositorioEventoImagen eventoImagenDAO;
	    @Autowired  RepositorioUsuario usuarioDAO;
	    @Autowired  ServicioImagenImpl servicioImagen;
	    @Autowired  ServicioEventoImagenImpl servicioEventoImagen;
	    @Autowired ServicioInvitadoImpl servicioInvitado;


	    @Override
	    @Transactional
	    public DTOeventoBajada insert(DTOeventoSubida dto) {
	        // 🔸 Buscar el vendedor
	        Usuario vendedor = usuarioDAO.findById(dto.getVendedorId())
	                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));

	        if (vendedor.getRol() != Rol.VENDEDOR) {
	            throw new RuntimeException("El usuario no tiene rol de vendedor");
	        }

	        try {
	            //  Procesar imagen principal
	            String imagenFinal = null;
	            if (dto.getImagen() != null && !dto.getImagen().isBlank()) {
	                if (dto.getImagen().length() > 200) {
	                    imagenFinal = servicioImagen.guardarImagenBase64(dto.getImagen(), "eventos/portadas");
	                } else {
	                    imagenFinal = dto.getImagen(); // URL directa
	                }
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

	            // 🔹 Crear DTO de bajada con builder (sin usar setters)
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
	@Override
	public List<DTOeventoBajada> obtenerTodosLosEventos() {
		// TODO Auto-generated method stub
		return dtoConverter.mapAll((List<Evento>)eventoDAO.findAll(), DTOeventoBajada.class);

		}
	

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
            if (dto.getImagen() != null) {
                if (dto.getImagen().length() > 200) {
                    String ruta = servicioImagen.guardarImagenBase64(dto.getImagen(), "eventos/portadas");
                    evento.setImagen(ruta);
                } else {
                    evento.setImagen(dto.getImagen());
                }
            }

            // Carrusel (se reemplaza completamente)
            if (dto.getImagenesCarruselUrls() != null && !dto.getImagenesCarruselUrls().isEmpty()) {
                servicioEventoImagen.eliminarCarrusel(id);
                List<DTOEventoImagenSubida> lista = dto.getImagenesCarruselUrls().stream()
                        .map(img -> DTOEventoImagenSubida.builder().url(img).build())
                        .collect(Collectors.toList());
                servicioEventoImagen.guardarCarrusel(id, lista);
            }
            
            if (dto.getInvitados() != null && !dto.getInvitados().isEmpty()) {
                servicioInvitado.eliminarInvitados(id);
                servicioInvitado.guardarInvitados(id, dto.getInvitados());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Evento actualizado = eventoDAO.save(evento);

        DTOeventoBajada dtoBajada = dtoConverter.map(actualizado, DTOeventoBajada.class);
        dtoBajada.setImagenesCarruselUrls(eventoImagenDAO.findUrlsByEventoId(actualizado.getId()));
        dtoBajada.setInvitados(servicioInvitado.obtenerInvitados(evento.getId()));

        return dtoBajada;
    }


	@Override
	public boolean eliminarEvento(Long id) {
		// TODO Auto-generated method stub
        if (eventoDAO.existsById(id)) {
            eventoDAO.deleteById(id);
            return true;
        }
        return false;
    }

	

	@Override
	public DTOeventoBajada obtnerPorElNombre(String nombre) {
		// TODO Auto-generated method stub
		Evento evento = eventoDAO.findByNombre(nombre)
	            .orElseThrow(() -> new RuntimeException(nombre + "no encontrado"));
	    return dtoConverter.map(evento, DTOeventoBajada.class);
	}

	@Override
	public List<DTOeventoBajada> obtenerPorCategoria(Categoria categoria) {
		// TODO Auto-generated method stub
		
		return eventoDAO.findByCategoria(categoria).stream().map(e -> {
            DTOeventoBajada dto = dtoConverter.map(e, DTOeventoBajada.class);
            dto.setImagenesCarruselUrls(eventoImagenDAO.findUrlsByEventoId(e.getId()));
            return dto;
        }).collect(Collectors.toList());
	}

	@Override
	public List<DTOeventoBajada> buscarPorNombreConteniendo(String nombre) {
		return eventoDAO.findByNombreContainingIgnoreCase(nombre).stream()
                .map(e -> {
                    DTOeventoBajada dto = dtoConverter.map(e, DTOeventoBajada.class);
                    dto.setImagenesCarruselUrls(eventoImagenDAO.findUrlsByEventoId(e.getId()));
                    return dto;
                }).collect(Collectors.toList());
	}

	@Override
	public DTOeventoBajada obtnerPorElId(Long id) {
		return dtoConverter.map(eventoDAO.findById(id),DTOeventoBajada.class);
	}

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
	
	
}
