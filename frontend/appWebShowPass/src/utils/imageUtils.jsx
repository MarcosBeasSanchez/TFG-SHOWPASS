import config from "../config/config"; // Asume que la ruta es correcta

/**
 * Devuelve la fuente (src) correcta para mostrar una imagen.
 */
export const getImageSrc = (img) => {
  if (!img) return "";
  if (img.startsWith("data:image/")) return img;
  if (img.startsWith("http://") || img.startsWith("https://")) return img;
  if (img.startsWith("/uploads/")) return `${config.apiBaseUrl}${img}`;
  return `data:image/png;base64,${img}`;
};