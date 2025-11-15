import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'


// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],

  // Acceso desde otras IPs en red local(ej. para pruebas en móviles)
  server: {
    host: '0.0.0.0', // Permite el acceso desde otras IPs en tu red local
    port: 5173,// El puerto predeterminado es 5173 tailwindcss
  },
})
