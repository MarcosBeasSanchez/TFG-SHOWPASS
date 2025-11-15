package tfg.proyecto.TFG.servicios;


import java.util.List;


import tfg.proyecto.TFG.dtos.DTOUsuarioReportado;
import tfg.proyecto.TFG.dtos.DTOeventoBajada;
import tfg.proyecto.TFG.dtos.DTOusuarioBajada;

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
	
	DTOusuarioBajada registerConDatos(DTOusuarioSubida usuarioDto); //crear usuario con todos los datos
	DTOusuarioBajada register(DTOusuarioSubidaMinimo usuarioDto); //crear usuario con datos minimos
	DTOusuarioLoginBajada login(DTOusuarioLogin dtologin); //login usuario
	List<DTOeventoBajada> findAllEventosCreadosPorUnUsuario(Long id); //Busca todos los eventos creados
	DTOUsuarioReportado findByEmail(String email);
	List<DTOUsuarioReportado>findAllReportados();
	DTOUsuarioReportado  reportarUsuario(String email);
	DTOUsuarioReportado  quitarReport(String email);
	
}
