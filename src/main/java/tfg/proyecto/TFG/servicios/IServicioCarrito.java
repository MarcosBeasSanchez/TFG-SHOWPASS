package tfg.proyecto.TFG.servicios;

import tfg.proyecto.TFG.dtos.DTOCarritoBajada;

public interface IServicioCarrito {
	
	 DTOCarritoBajada obtenerCarritoPorUsuario(Long usuarioId);
	    DTOCarritoBajada agregarItemAlCarrito(Long usuarioId, Long eventoId, int cantidad);
	    DTOCarritoBajada actualizarItem(Long usuarioId, Long itemId, int cantidad);
	    DTOCarritoBajada eliminarItem(Long usuarioId, Long itemId);
	    DTOCarritoBajada vaciarCarrito(Long usuarioId);
	    double calcularTotal(Long usuarioId);
	    DTOCarritoBajada finalizarCompra(Long usuarioId);
}