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

@Service
public class ServicioInvitadoImpl implements IServicioInvitado{

	@Autowired private RepositorioInvitado invitadoDAO;
    @Autowired private RepositorioEvento eventoDAO;
    @Autowired private ServicioImagenImpl servicioImagen;
	

	/**
     * Guarda o reemplaza los invitados de un evento
     */
    @Transactional
    @Override
    public List<DTOInvitadoBajada> guardarInvitados(Long eventoId, List<DTOInvitadoSubida> invitadosDto) {
        Evento evento = eventoDAO.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // 🔹 Eliminar los invitados anteriores del evento
        invitadoDAO.deleteByEventoId(eventoId);

        // 🔹 Guardar los nuevos invitados
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

                // 🔹 Construir DTO de respuesta
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
     * Obtiene los invitados de un evento
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
     * Elimina todos los invitados de un evento
     */
    @Transactional
    @Override
    public void eliminarInvitados(Long eventoId) {
        invitadoDAO.deleteByEventoId(eventoId);
    }

}
