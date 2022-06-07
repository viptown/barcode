package com.example.barcodescanner.screens

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.barcodescanner.BarCodeAnalyser
import com.example.barcodescanner.data.remote.LocationService
import com.example.barcodescanner.data.remote.dto.LocationRequest
import com.example.barcodescanner.data.remote.dto.LocationResponse
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.common.util.concurrent.ListenableFuture
import io.ktor.client.statement.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

        Button(
            onClick = {
                cameraPermissionState.launchPermissionRequest()
            }
        ) {
            Text(text = "Camera Permission")
        }

        Spacer(modifier = Modifier.height(10.dp))

        CameraPreview()


    }
}

@Composable
fun CameraPreview() {

    //get json
    val service = LocationService.create()
    val posts = produceState<List<LocationResponse>>(
        initialValue = emptyList(),
        producer ={
            value = service.getLocations()
        }
    )
//selected index dropmenuItem
    var  seletedItemDropMenuIndex  = remember { "" }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var preview by remember { mutableStateOf<Preview?>(null) }
    val barCodeVal = remember { mutableStateOf("") }

    var barcodeText by remember { mutableStateOf("") }

    //drop down menu
    var expanded by remember { mutableStateOf(false) }
    //val suggestions = listOf("Item1","Item2","Item3")
    val suggestions = posts.value
    var selectedText by remember { mutableStateOf("") }

    var textfieldSize by remember { mutableStateOf(Size.Zero)}

    val icon = if (expanded)
        //Icons.Filled.ArrowDropUp //it requires androidx.compose.material:material-icons-extended
        Icons.Filled.ArrowDropUp
    else
        Icons.Filled.ArrowDropDown


    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(
            factory = { AndroidViewContext ->
                PreviewView(AndroidViewContext).apply {
                    this.scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
//            modifier = Modifier
//                .fillMaxSize(),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f),

            update = { previewView ->
                val cameraSelector: CameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
                val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
                    ProcessCameraProvider.getInstance(context)

                cameraProviderFuture.addListener({
                    preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                    val barcodeAnalyser = BarCodeAnalyser { barcodes ->
                        barcodes.forEach { barcode ->
                            barcode.rawValue?.let { barcodeValue ->
                                barCodeVal.value = barcodeValue
                                barcodeText =  barcodeValue
                                Toast.makeText(context, barcodeValue, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor, barcodeAnalyser)
                        }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.d("TAG", "CameraPreview: ${e.localizedMessage}")
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            placeholder = { Text(text = "바코드 입력해주세요.") },
            label = { Text(text = "코드") },
            value = barcodeText, onValueChange = {
            barcodeText = it
        })

        //drop down menu
        Column() {
            OutlinedTextField(

                value = selectedText,
                onValueChange = { selectedText = it },
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        //This value is used to assign to the DropDown the same width
                        textfieldSize = coordinates.size.toSize()
                    },
                label = {Text("Label")},
                trailingIcon = {
                    Icon(icon,"contentDescription",
                        Modifier.clickable { expanded = !expanded })
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current){textfieldSize.width.toDp()})
            ) {
//                suggestions.forEach { label ->
//                    DropdownMenuItem(onClick = {
//                        selectedText = label
//                        expanded = false
//                    }) {
//                        Text(text = label)
//                    }
//                }
                suggestions.forEach { locationResponse ->
                    DropdownMenuItem(onClick = {
                        seletedItemDropMenuIndex = locationResponse.position_id
                        selectedText = locationResponse.position_name
                        expanded = false
                    }) {
                        Text(text = locationResponse.position_name)
                    }
                }
            }
            Box(modifier = Modifier.align(Alignment.End)) {
                Button(
                    onClick = {

                        GlobalScope.launch (Dispatchers.Main) {

                            val response: LocationResponse? =
                                service.createLocation(LocationRequest(barcodeText, seletedItemDropMenuIndex))
                            Toast.makeText(context, response?.toString() ?: "NO DATA", Toast.LENGTH_SHORT).show()
                        }

                              },
                    modifier = Modifier.align(Alignment.TopEnd))
                {
                    Text("SAVE")
                }
            }


        }
    }
}
