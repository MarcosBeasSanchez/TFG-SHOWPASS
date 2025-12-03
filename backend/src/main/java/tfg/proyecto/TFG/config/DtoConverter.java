package tfg.proyecto.TFG.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import tfg.proyecto.TFG.dtos.DTOusuarioBajada;
import tfg.proyecto.TFG.modelo.Usuario;

/**
 * Bean de utilidad para mapear entidades a DTOs y viceversa usando ModelMapper.
 * <p>
 * Incluye configuración flexible (MatchingStrategies.LOOSE) y mapeos personalizados
 * para colecciones de IDs en ciertas entidades, como {@link Usuario} → {@link DTOusuarioBajada}.
 * <p>
 * Este bean se declara como {@link org.springframework.stereotype.Component} y también puede ser definido como {@link Bean} en configuración.
 */
@Component
public class DtoConverter {
	
	private ModelMapper modelMapper;
	
	/**
     * Constructor por defecto.
     * Inicializa {@link ModelMapper} con estrategia de coincidencia flexible.
     */
	public DtoConverter ()
	{
		  modelMapper = new ModelMapper();
		    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE); // estrategia flexible
		    System.out.println("dtoconverter creado e inicializado...");
		
	}
	

    /**
     * Mapea un objeto de tipo T a un DTO de tipo D.
     *
     * @param input    Objeto fuente a mapear.
     * @param outClass Clase destino.
     * @param <D>      Tipo de DTO.
     * @param <T>      Tipo de entidad u objeto origen.
     * @return DTO mapeado.
     */
	 public <D, T> D map(final T input, Class<D> outClass) {
	        return modelMapper.map(input, outClass);
	 }
	 
	 /**
	     * Mapea una colección de objetos a una lista de DTOs.
	     *
	     * @param inputList Lista de objetos origen.
	     * @param outCLass Clase DTO destino
	     * @param <D>       Tipo de DTO.
	     * @param <T>       Tipo de objeto origen.
	     * @return Lista de DTOs mapeados.
	     */
	 public <D, T> List<D> mapAll(final Collection<T> inputList, Class<D> outCLass)
	 {
			        return inputList.stream()
			                .map(input -> map(input, outCLass))
			                .collect(Collectors.toList());
	 }
	 
	 /**
	     * Método llamado al inicializar el bean.
	     * <p>
	     * Configura mapeos personalizados, especialmente para colecciones:
	     * <ul>
	     *     <li>Convierte List&lt;Entity&gt; a List&lt;Long&gt; obteniendo el ID de cada objeto.</li>
	     *     <li>Aplica este mapeo en {@link Usuario} → {@link DTOusuarioBajada} para tickets y eventos creados.</li>
	     * </ul>
	     */
	 public void inicializarBean () {
		    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

		    // 1. Crear el Converter de List<?> a List<Long> (usando reflexión)
		    // Puede verse por ejemplo en tfg/usuario/findAll
		    Converter<List<?>, List<Long>> listToIdConverter = new Converter<List<?>, List<Long>>() {
		        @Override
		        public List<Long> convert(MappingContext<List<?>, List<Long>> context) {
		            if (context.getSource() == null) {
		                return Collections.emptyList();
		            }
		            return context.getSource().stream()
		                .map(obj -> {
		                    try {
		                        // Asume que la Entidad tiene un método getId()
		                        return (Long) obj.getClass().getMethod("getId").invoke(obj);
		                    } catch (Exception e) {
		                        // Es importante lanzar esta excepción para saber dónde falla.
		                        throw new IllegalStateException("Error al obtener ID de la Entidad", e);
		                    }
		                })
		                .collect(Collectors.toList());
		        }
		    };

		    // 2. Aplicar el mapeo para Usuario -> DTOusuarioBajada
		    modelMapper.createTypeMap(Usuario.class, DTOusuarioBajada.class)
		        .addMappings(mapper -> {
		            // Configuración para ticketsId
		            mapper.using(listToIdConverter)
		                  .map(Usuario::getTickets, DTOusuarioBajada::setTicketsId);
		            
		            // Configuración para eventosCreadosId
		            mapper.using(listToIdConverter)
		                  .map(Usuario::getEventosCreados, DTOusuarioBajada::setEventosCreadosId);
		        });

		    System.out.println("bean dtoConverter inicializado con mapeos personalizados para colecciones.");
		}
	 
	 /**
	     * Método llamado al destruir el bean.
	     * Solo imprime un mensaje de finalización.
	     */
	 public void finalizarBean ()
	 {
		 System.out.println("bean dtoConverter finalizado...");
	 }

}
