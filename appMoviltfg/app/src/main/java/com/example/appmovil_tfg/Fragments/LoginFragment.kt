package com.example.appmovil_tfg.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.appmovil_tfg.Api.ApiClient
import com.example.appmovil_tfg.Models.LoginRequest
import com.example.appmovil_tfg.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(email, password)

            // Llamada al backend usando Coroutines
            lifecycleScope.launch {
                try {
                    val response = ApiClient.api.login(request)
                    Toast.makeText(requireContext(), "Bienvenido ${response.nombre}", Toast.LENGTH_SHORT).show()
                    // Aquí podrías guardar token o navegar a la pantalla principal
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error al iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvGoRegister.setOnClickListener {
            // Navegar al fragmento de registro
            parentFragmentManager.beginTransaction()
                .replace((view.parent as ViewGroup).id, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}