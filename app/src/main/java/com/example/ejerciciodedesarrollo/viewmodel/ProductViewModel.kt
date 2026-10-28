package com.example.ejerciciodedesarrollo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ejerciciodedesarrollo.data.ProductModel
import com.example.ejerciciodedesarrollo.data.ProductRepository
import kotlinx.coroutines.launch

data class ProductUiState(
    val productos: List<ProductModel> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)

class ProductViewModel(
    private val repositorio: ProductRepository = ProductRepository()
) : ViewModel() {

    var uiState by mutableStateOf(ProductUiState())
        private set

    // Mensaje con el verbo HTTP y el código de respuesta, se muestra en un Snackbar
    var mensaje by mutableStateOf<String?>(null)
        private set

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            uiState = uiState.copy(cargando = true, error = null)
            try {
                val respuesta = repositorio.listar()
                uiState = uiState.copy(productos = respuesta.products, cargando = false)
                mensaje = "GET /products → 200 · ${respuesta.products.size} de ${respuesta.total} productos"
            } catch (e: Exception) {
                uiState = uiState.copy(cargando = false, error = "No se pudo conectar con el API: ${e.message}")
            }
        }
    }

    fun buscar(id: Int): ProductModel? = uiState.productos.find { it.id == id }

    fun mensajeMostrado() {
        mensaje = null
    }
}