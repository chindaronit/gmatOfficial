package com.gmat.ui.screens.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gmat.R
import com.gmat.ui.components.CenterBar
import com.gmat.ui.components.CustomToast
import com.gmat.ui.events.UserEvents
import com.gmat.ui.state.UserState
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

@Composable
fun EditProfileDetails(
    modifier: Modifier = Modifier,
    navController: NavController,
    userState: UserState,
    onUserEvents: (UserEvents) -> Unit,
    authToken: String
) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    var canConfirm by remember { mutableStateOf(true) }
    var isUploaded by remember { mutableStateOf(false) }
    var downloadUrl by remember { mutableStateOf<String?>(null) }

    // List to store all uploaded image URLs
    val uploadedImageUrls = remember { mutableStateListOf<String>() }

    val imagePickerLauncher: ActivityResultLauncher<Intent> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    isUploaded=false
                    uploadImageToFirebase(
                        uri,
                        storageRef,
                        onSuccess = { url ->
                            uploadedImageUrls.add(url)
                            downloadUrl = url
                            onUserEvents(UserEvents.OnProfileChange(downloadUrl!!))
                        },
                        onFailure = { exception ->
                            Log.e("Firebase", "Error uploading image: ${exception.message}")
                        }
                    )
                }
            } else {
                canConfirm = true
            }
        }
    )

    LaunchedEffect(key1 = userState.newProfile) {
        if (userState.newProfile.isNotBlank()) {
            canConfirm = true
            isUploaded = true
        }
    }

    Scaffold(
        topBar = {
            CenterBar(
                onClick = {
                    onUserEvents(UserEvents.ClearNewProfile)
                    navController.navigateUp()
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.edit_profile),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    if (canConfirm) {
                        IconButton(onClick = {
                            if(userState.user!!.profile.isNotBlank()){
                                deleteFromFirebase(userState.user.profile)
                            }

                            uploadedImageUrls.dropLast(1).forEach { oldImageUrl ->
                                val oldImageRef =
                                    FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl)
                                oldImageRef.delete().addOnSuccessListener {
                                    Log.d("Firebase", "Old image $oldImageUrl deleted successfully")
                                }.addOnFailureListener { exception ->
                                    Log.e(
                                        "Firebase",
                                        "Failed to delete old image: ${exception.message}"
                                    )
                                }
                            }
                            onUserEvents(UserEvents.UpdateUser)
                            onUserEvents(
                                UserEvents.UpdateRoom(
                                    user = userState.user,
                                    verificationId = userState.verificationId,
                                    authToken
                                )
                            )
                            navController.navigateUp()
                        }) {
                            Icon(imageVector = Icons.Rounded.Check, contentDescription = null)
                        }
                    }
                })
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 10.dp, end = 10.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isUploaded) {
                    AsyncImage(
                        model = userState.newProfile,
                        contentDescription = null,
                        modifier = modifier
                            .padding(top = 10.dp)
                            .size(150.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = null,
                        modifier = modifier.size(150.dp)
                    )
                }

                Button(
                    onClick = {
                        val intent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        canConfirm = false
                        imagePickerLauncher.launch(intent)

                    },
                    enabled = canConfirm
                ) {
                    Text(
                        text = stringResource(id = R.string.upload_photo),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = modifier.height(20.dp))
                OutlinedTextField(
                    value = userState.newName,
                    onValueChange = {
                        onUserEvents(UserEvents.OnNameChange(it))
                    },
                    modifier = modifier.fillMaxWidth(),
                    label = {
                        Text(
                            "Name",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }
            CustomToast(
                modifier = Modifier.align(Alignment.BottomCenter),
                message = "Uploading",
                isVisible = !canConfirm
            )
        }
    }
}

private fun uploadImageToFirebase(
    uri: Uri,
    storageRef: StorageReference,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    //    val fileRef = storageRef.child("images/${uri.lastPathSegment}")
    val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "image"
    val newFileName = "${System.currentTimeMillis()}_$fileName"
    val fileRef = storageRef.child("images/$newFileName")
    val uploadTask = fileRef.putFile(uri)
    uploadTask.addOnSuccessListener {
        // Get the download URL after a successful upload
        fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
//            Log.d("Firebase", "Upload successful! Download URL: $downloadUrl")
            onSuccess(downloadUrl.toString())
        }.addOnFailureListener { exception ->
//            Log.e("Firebase", "Failed to get download URL: ${exception.message}")
            onFailure(exception)
        }
    }.addOnFailureListener { exception ->
        Log.e("Firebase", "Upload failed: ${exception.message}")
        onFailure(exception)
    }
}


private fun deleteFromFirebase(url: String) {
    val oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
    oldImageRef.delete().addOnSuccessListener {
        Log.d("Firebase", "Old image $url deleted successfully")
    }.addOnFailureListener { exception ->
        Log.e("Firebase", "Failed to delete old image: ${exception.message}")
    }
}
