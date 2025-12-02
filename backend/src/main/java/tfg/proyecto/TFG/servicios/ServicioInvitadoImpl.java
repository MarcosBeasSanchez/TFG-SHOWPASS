package tfg.proyecto.TFG.servicios;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tfg.proyecto.TFG.dtos.DTOInvitadoBajada;
import tfg.proyecto.TFG.dtos.DTOInvitadoSubida;
import tfg.proyecto.TFG.modelo.Evento;
import tfg.proyecto.TFG.modelo.Invitado;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioInvitado;

/**
 * Implementación del servicio {@link IServicioInvitado}.
 * 
 * <p>Se encarga de gestionar los invitados de un evento, incluyendo:</p>
 * <ul>
 *     <li>Guardar o reemplazar la lista de invitados.</li>
 *     <li>Obtener la lista de invitados de un evento.</li>
 *     <li>Eliminar todos los invitados de un evento.</li>
 *     <li>Procesamiento de imágenes de invitados mediante {@link ServicioImagenImpl}.</li>
 * </ul>
 */
@Service
public class ServicioInvitadoImpl implements IServicioInvitado{

	@Autowired private RepositorioInvitado invitadoDAO;
    @Autowired private RepositorioEvento eventoDAO;
    @Autowired private ServicioImagenImpl servicioImagen;
	

    /**
     * Guarda o reemplaza los invitados de un evento.
     *
     * <p>El método realiza las siguientes operaciones:</p>
     * <ol>
     *     <li>Busca el evento por su ID usando {@link RepositorioEvento}.</li>
     *     <li>Elimina los invitados existentes asociados al evento mediante {@link RepositorioInvitado#deleteByEventoId}.</li>
     *     <li>Guarda los nuevos invitados:
     *         <ul>
     *             <li>Procesa la imagen del invitado usando {@link ServicioImagenImpl#guardarImagenBase64} (maneja Base64, URL o placeholder).</li>
     *             <li>Construye la entidad {@link Invitado} y la guarda con {@link RepositorioInvitado#save}.</li>
     *             <li>Construye y retorna el {@link DTOInvitadoBajada} correspondiente.</li>
     *         </ul>
     *     </li>
     * </ol>
     *
     * @param eventoId ID del evento al que se asignan los invitados
     * @param invitadosDto lista de invitados a guardar (DTO de subida)
     * @return lista de {@link DTOInvitadoBajada} con los invitados guardados
     * @throws RuntimeException si el evento no existe o ocurre un error al guardar la imagen
     */
    @Transactional
    @Override
    public List<DTOInvitadoBajada> guardarInvitados(Long eventoId, List<DTOInvitadoSubida> invitadosDto) {
        Evento evento = eventoDAO.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        //  Eliminar los invitados anteriores del evento
        invitadoDAO.deleteByEventoId(eventoId);

        //  Guardar los nuevos invitados
        return invitadosDto.stream().map(dto -> {
            try {
                //  Guardar la imagen (ya maneja Base64, URL y placeholder internamente)
            	String rutaFoto = servicioImagen.guardarImagenBase64(
            	        dto.getFotoURL(),
            	        "invitados/" + eventoId
            	);
            	System.out.println("[BACKEND] Invitado imagen guardada: " + rutaFoto);

                Invitado invitado = Invitado.builder()
                        .nombre(dto.getNombre())
                        .apellidos(dto.getApellidos())
                        .descripcion(dto.getDescripcion())
                        .fotoURL(rutaFoto)
                        .evento(evento)
                        .build();

                invitadoDAO.save(invitado);

                // Construir DTO de respuesta
                return DTOInvitadoBajada.builder()
                        .id(invitado.getId())
                        .nombre(invitado.getNombre())
                        .apellidos(invitado.getApellidos())
                        .descripcion(invitado.getDescripcion())
                        .fotoURL(rutaFoto)
                        .build();

            } catch (IOException e) {
                throw new RuntimeException("Error guardando imagen del invitado", e);
            }
        }).collect(Collectors.toList());
    }

    /**
     * Obtiene todos los invitados de un evento.
     *
     * <p>Se consulta {@link RepositorioInvitado#findByEventoId} para obtener las entidades,
     * luego se convierten a {@link DTOInvitadoBajada} usando builder.</p>
     *
     * @param eventoId ID del evento
     * @return lista de {@link DTOInvitadoBajada} asociados al evento
     */
    @Override    
    public List<DTOInvitadoBajada> obtenerInvitados(Long eventoId) {
        return invitadoDAO.findByEventoId(eventoId).stream()
                .map(inv -> DTOInvitadoBajada.builder()
                        .id(inv.getId())
                        .nombre(inv.getNombre())
                        .apellidos(inv.getApellidos())
                        .descripcion(inv.getDescripcion())
                        .fotoURL(inv.getFotoURL())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Elimina todos los invitados asociados a un evento.
     *
     * <p>Se utiliza {@link RepositorioInvitado#deleteByEventoId} para eliminar
     * todos los registros relacionados con el evento.</p>
     *
     * @param eventoId ID del evento cuyos invitados se eliminarán
     */
    @Transactional
    @Override
    public void eliminarInvitados(Long eventoId) {
        invitadoDAO.deleteByEventoId(eventoId);
    }

}
