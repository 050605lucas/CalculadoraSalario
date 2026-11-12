package com.damunir.calculadorasalario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Control de qué pantalla se ve (1 = Formulario, 2 = Resultados)
            var pantalla by remember { mutableStateOf(1) }

            // Variables para los campos del formulario
            var salarioBrutoTexto by remember { mutableStateOf("") }
            var edadTexto by remember { mutableStateOf("") }
            var pagasTexto by remember { mutableStateOf("12") }
            var grupoTexto by remember { mutableStateOf("") }
            var discapacidadTexto by remember { mutableStateOf("0") }
            var estadoCivilTexto by remember { mutableStateOf("") }
            var hijosTexto by remember { mutableStateOf("0") }
            var mensajeAviso by remember { mutableStateOf("") }

            // Variables para guardar los resultados del cálculo
            var brutoCalculado by remember { mutableStateOf(0.0) }
            var netoAnual by remember { mutableStateOf(0.0) }
            var netoMensual by remember { mutableStateOf(0.0) }
            var tipoIRPF by remember { mutableStateOf(0.0) }
            var retencionDinero by remember { mutableStateOf(0.0) }
            var deduccionTotal by remember { mutableStateOf(0.0) }
            var pagasResultado by remember { mutableStateOf(12) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {
                when (pantalla) {
                    // PANTALLA 1: FORMULARIO
                    1 -> {
                        Text(
                            text = "Calculadora de Salario Neto",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )

                        OutlinedTextField(
                            value = salarioBrutoTexto,
                            onValueChange = { salarioBrutoTexto = it },
                            label = { Text("Salario bruto anual (€)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = edadTexto,
                            onValueChange = { edadTexto = it },
                            label = { Text("Edad") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = pagasTexto,
                            onValueChange = { pagasTexto = it },
                            label = { Text("Número de pagas (12 o 14)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = grupoTexto,
                            onValueChange = { grupoTexto = it },
                            label = { Text("Grupo profesional (Ej. Técnico, Oficial)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = discapacidadTexto,
                            onValueChange = { discapacidadTexto = it },
                            label = { Text("Discapacidad % (0 si no aplica)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = estadoCivilTexto,
                            onValueChange = { estadoCivilTexto = it },
                            label = { Text("Estado civil (Soltero, Casado...)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = hijosTexto,
                            onValueChange = { hijosTexto = it },
                            label = { Text("Número de hijos") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (mensajeAviso.isNotEmpty()) {
                            Text(
                                text = mensajeAviso,
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }

                        Button(
                            onClick = {
                                val bruto = salarioBrutoTexto.toDoubleOrNull() ?: 0.0
                                val edad = edadTexto.toIntOrNull() ?: 0
                                val pagas = pagasTexto.toIntOrNull() ?: 12
                                val discapacidad = discapacidadTexto.toIntOrNull() ?: 0
                                val hijos = hijosTexto.toIntOrNull() ?: 0

                                if (bruto <= 0.0 || edad <= 0) {
                                    mensajeAviso = "Por favor, introduce al menos salario y edad válidos."
                                } else {
                                    mensajeAviso = ""

                                    // 1. Tramo base IRPF
                                    val baseIRPF = when {
                                        bruto <= 12450.0 -> 19.0
                                        bruto <= 20200.0 -> 24.0
                                        bruto <= 35200.0 -> 30.0
                                        bruto <= 60000.0 -> 37.0
                                        else -> 45.0
                                    }

                                    // 2. Deducciones
                                    var deduccion = hijos * 1.0
                                    if (discapacidad >= 33) deduccion += 2.0
                                    if (edad >= 65) deduccion += 1.5

                                    // 3. IRPF final (mínimo 2%)
                                    var irpfFinal = baseIRPF - deduccion
                                    if (irpfFinal < 2.0) {
                                        irpfFinal = 2.0
                                    }

                                    // Guardar valores calculados
                                    brutoCalculado = bruto
                                    tipoIRPF = irpfFinal
                                    deduccionTotal = deduccion
                                    retencionDinero = bruto * (irpfFinal / 100.0)
                                    netoAnual = bruto - retencionDinero
                                    pagasResultado = if (pagas == 14) 14 else 12
                                    netoMensual = netoAnual / pagasResultado

                                    // Pasamos a la pantalla 2
                                    pantalla = 2
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Calcular Salario Neto", fontSize = 18.sp)
                        }
                    }

                    // PANTALLA 2: RESULTADOS
                    2 -> {
                        Text(
                            text = "Resultado del Cálculo",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )

                        Text("Salario Bruto: " + brutoCalculado + " €", fontSize = 18.sp)
                        Text("Deducciones aplicadas: " + deduccionTotal + " %", fontSize = 16.sp)
                        Text("Retención IRPF (" + tipoIRPF + " %): -" + retencionDinero + " €", fontSize = 16.sp, color = Color.Red)

                        Text(
                            text = "Salario Neto Anual: " + netoAnual + " €",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )

                        Text(
                            text = "Neto al mes (" + pagasResultado + " pagas): " + netoMensual + " €/mes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF166534)
                        )

                        Text(
                            text = "Perfil: " + grupoTexto + " (" + estadoCivilTexto + ")",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Button(
                            onClick = { pantalla = 1 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Volver a calcular", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}