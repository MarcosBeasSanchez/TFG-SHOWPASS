package tfg.proyecto.TFG.servicios;

import java.util.List;

import tfg.proyecto.TFG.dtos.DTOeventoBajada;
import tfg.proyecto.TFG.dtos.DTOeventoSubida;
import tfg.proyecto.TFG.dtos.EventoRecomendadoDTO;
import tfg.proyecto.TFG.modelo.Categoria;

public interface IServicioEvento {
	
	DTOeventoBajada insert(DTOeventoSubida dto);
	List<DTOeventoBajada> obtenerTodosLosEventos();
	DTOeventoBajada actualizarEvento(Long id,  DTOeventoSubida eventodto);
	boolean eliminarEvento(Long id);
	DTOeventoBajada obtnerPorElNombre(String nombre);
	List<DTOeventoBajada> obtenerPorCategoria(Categoria categoria);
	List<DTOeventoBajada> buscarPorNombreConteniendo(String nombre);
	DTOeventoBajada obtnerPorElId(Long id);
	List<DTOeventoBajada> obtenerPorVendedor(Long vendedorId);
	
	DTOeventoBajada actualizarEventoMovil(Long id,  DTOeventoSubida eventodto);
	
	List<EventoRecomendadoDTO> obtenerRecomendacionesUsuario(Long userId);
	List<EventoRecomendadoDTO> obtenerSimilaresEvento(Long eventoId);
	
	List<Long> buscarEventosPorIA(String query);
}
