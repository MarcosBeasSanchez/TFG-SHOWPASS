package tfg.proyecto.TFG.servicios;


import java.util.List;

import tfg.proyecto.TFG.dtos.DTOusuarioBajada;
import tfg.proyecto.TFG.dtos.DTOusuarioBajadaMinimo;
import tfg.proyecto.TFG.dtos.DTOusuarioLogin;
import tfg.proyecto.TFG.dtos.DTOusuarioLoginBajada;
import tfg.proyecto.TFG.dtos.DTOusuarioModificarSubida;
import tfg.proyecto.TFG.dtos.DTOusuarioSubida;
import tfg.proyecto.TFG.dtos.DTOusuarioSubidaMinimo;


public interface IServicioUsuario {
	
	DTOusuarioBajada insert(DTOusuarioSubida usuarioDto);
	DTOusuarioBajada update(DTOusuarioModificarSubida usuarioDto);
	Integer deleteById(Long id); //eliminar usuario por id
	DTOusuarioBajada findById(Long id);
	List<DTOusuarioBajada>findAllUsuarios();
	
	DTOusuarioBajada register(DTOusuarioSubidaMinimo usuarioDto);
	DTOusuarioLoginBajada login(DTOusuarioLogin dtologin);

}
