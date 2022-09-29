package com.olafros.exercise4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.olafros.exercise4.ui.theme.Exercise4Theme

data class ImageObject(val label: String, val url: String)

class ImagesViewModel : ViewModel() {

    var currentImageViewed by mutableStateOf(0)
        private set

    var images = mutableStateListOf(
        ImageObject(
            "Nr 1",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Moraine_Lake_17092005.jpg/1200px-Moraine_Lake_17092005.jpg"
        ),
        ImageObject(
            "Nr 2",
            "https://www.wwf.no/assets/article_images/_1440x648_crop_center-center_none/shutterstock_1550458109-korr-LJ.jpg"
        ),
        ImageObject(
            "Nr 3",
            "https://m.psecn.photoshelter.com/img-get2/I0000H0rIQzSXuNA/sec=/fit=1200x1200/I0000H0rIQzSXuNA.jpg"
        ),
    )
        private set

    fun setShownImage(index: Int) {
        currentImageViewed = index
    }

    fun showNextImage() {
        if (currentImageViewed < images.size - 1) {
            currentImageViewed++
        } else {
            currentImageViewed = 0
        }
    }

    fun showPreviousImage() {
        if (currentImageViewed == 0) {
            currentImageViewed = images.size - 1
        } else {
            currentImageViewed--
        }
    }
}

class MainActivity : ComponentActivity() {
    private val imagesViewModel by viewModels<ImagesViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Exercise4Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Main(
                        imagesViewModel.images,
                        imagesViewModel::setShownImage,
                        imagesViewModel.currentImageViewed,
                        imagesViewModel::showNextImage,
                        imagesViewModel::showPreviousImage
                    )
                }
            }
        }
    }
}

@Composable
fun Main(
    images: List<ImageObject>,
    setShownImage: (Int) -> Unit,
    currentImageViewed: Int,
    showNextImage: () -> Unit,
    showPreviousImage: () -> Unit
) {
    Box {
        Column() {
            Row(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth()
            ) {
                ImageList(images, setShownImage)
            }

            Row(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth()
            ) {
                ImageCarousel(images, currentImageViewed, showNextImage, showPreviousImage)
            }
        }
    }
}

@Composable
fun ImageList(images: List<ImageObject>, setShownImage: (Int) -> Unit) {
    LazyColumn {
        items(images.size) { index ->
            Button(onClick = { setShownImage(index) }, modifier = Modifier.fillMaxWidth()) {
                Text(text = images[index].label)
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
fun ImageCarousel(
    images: List<ImageObject>,
    currentImageViewed: Int,
    showNextImage: () -> Unit,
    showPreviousImage: () -> Unit
) {
    Column(modifier = Modifier.fillMaxHeight(), Arrangement.SpaceBetween) {
        GImage(images[currentImageViewed])
        Spacer(modifier = Modifier.weight(1.0f))
        Row() {
            Button(
                onClick = showPreviousImage, modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth()
            ) {
                Text(text = "<- Forrige")
            }
            Button(
                onClick = showNextImage, modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth()
            ) {
                Text(text = "Neste ->")
            }
        }
    }
}

@Composable
fun GImage(image: ImageObject) {
    val painter = rememberImagePainter(
        data = image.url,
        builder = {
            crossfade(true)
        }
    )

    Box {
        Column {
            Image(
                painter = painter,
                contentDescription = image.label,
                modifier = Modifier.fillMaxWidth()
            )
            Text(image.label)
        }

        when (painter.state) {
            is ImagePainter.State.Loading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            else -> {
            }
        }
    }
}
