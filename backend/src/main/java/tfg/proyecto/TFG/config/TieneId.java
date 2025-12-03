package tfg.proyecto.TFG.config;

/**
 * Interfaz funcional que garantiza que una clase provea un identificador único.
 * <p>
 * Esta interfaz es útil para operaciones genéricas, mapeos de DTOs o colecciones
 * donde se requiere obtener el ID de un objeto de manera uniforme.
 */
public interface TieneId {
	/**
     * Devuelve el identificador único del objeto.
     *
     * @return ID del objeto como {@link Long}
     */
	Long getId();
}
