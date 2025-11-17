package com.example.appmovilshowpass.data.local

import com.example.appmovilshowpass.R

data class Desarrollador(
    val nombre: String,
    val descripcion: String,
    val photo: String, // Usamos Int para un ID de recurso drawable
    val github: String
)

// Objeto para simular tus datosDesarroladores. ¡Asegúrate de cambiar los drawables!
object DatosDesarrolladores {
    // Reemplaza R.drawable.marcos_photo con tu recurso de imagen
    val marcos = Desarrollador(
        nombre = "Marcos",
        descripcion = "Desarrollador Frontend, Backend y UX/UI",
        photo = "https://avatars.githubusercontent.com/u/153531722?s=400&u=2d5304573c9bc68eddfc083b2afcf07efa8ebbc4&v=4",
        github = "https://github.com/MarcosBeasSanchez/TFG"
    )
    // Reemplaza R.drawable.dylan_photo con tu recurso de imagen
    val dylan = Desarrollador(
        nombre = "Dylan",
        descripcion = "Desarrollador Frontend, Backend y Arquitectura",
        photo = "https://avatars.githubusercontent.com/u/153531988?v=4",
        github = "https://github.com/DylanMolinaAmariles"
    )

    val members = listOf(marcos, dylan)
}