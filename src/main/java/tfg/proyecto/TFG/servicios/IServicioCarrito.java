package tfg.proyecto.TFG.servicios;

import tfg.proyecto.TFG.dtos.DTOCarritoBajada;
import tfg.proyecto.TFG.dtos.DTOCarritoSubida;
import tfg.proyecto.TFG.dtos.DTOeventoSubida;

public interface IServicioCarrito {
	
	DTOCarritoBajada obtenerCarritoPorUsuario(Long Id);
	DTOCarritoBajada agregarEvento(Long usuarioId, DTOCarritoSubida request);
	DTOCarritoBajada eliminarEvento(Long usuarioId, Long eventoId);
	DTOCarritoBajada vaciarCarrito(Long usuarioId);
	double calcularTotal(Long usuarioId);
	DTOCarritoBajada finalizarCompra(Long usuarioId);
}
