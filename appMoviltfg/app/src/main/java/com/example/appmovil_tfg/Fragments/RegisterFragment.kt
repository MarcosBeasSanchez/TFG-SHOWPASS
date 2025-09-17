package com.example.appmovil_tfg.Fragments
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appmovil_tfg.Api.ApiClient
import com.example.appmovil_tfg.Models.RegisterRequest
import com.example.appmovil_tfg.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class RegisterFragment : Fragment() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private val TAG = "RegisterFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.etFechaNacimiento.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    // Formato ISO yyyy-MM-dd
                    val formattedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                    binding.etFechaNacimiento.setText(formattedDate)
                },
                year, month, day
            )

            datePicker.show()
        }


        binding.btnRegister.setOnClickListener {
            val nombre = binding.etNombre.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val fecha = binding.etFechaNacimiento.text.toString()

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = RegisterRequest(nombre, email, password, fecha)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = ApiClient.api.register(request)
                    Log.i(TAG, "Usuario registrado: ${response.nombre}")
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(), "Usuario ${response.nombre} registrado", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error al registrar usuario", e)
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()


                    }
                }
            }
        }

        binding.tvGoLogin.setOnClickListener {
            // Navegar al fragmento de login

            parentFragmentManager.beginTransaction()
                .replace((view.parent as ViewGroup).id, LoginFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}