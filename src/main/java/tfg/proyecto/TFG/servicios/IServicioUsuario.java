package tfg.proyecto.TFG.servicios;


import java.util.List;

import tfg.proyecto.TFG.dtos.DTOusuarioBajada;
import tfg.proyecto.TFG.dtos.DTOusuarioModificarSubida;
import tfg.proyecto.TFG.dtos.DTOusuarioSubida;


public interface IServicioUsuario {
	
	DTOusuarioBajada insert(DTOusuarioSubida usuarioDto);
	DTOusuarioBajada update(DTOusuarioModificarSubida usuarioDto);
	Integer deleteById(Long id); //eliminar usuario por id
	DTOusuarioBajada findById(Long id);
	List<DTOusuarioBajada>findAllUsuarios();

}
