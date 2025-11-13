package com.shikidroid.uikit.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.RoundedCornersTransformation

@Composable
fun AsyncImageLoader(
    url: String?
) {

    /** контекст */
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(enable = true)
            .scale(scale = Scale.FILL)
            .transformations(RoundedCornersTransformation())
//            .diskCachePolicy(policy = CachePolicy.ENABLED)
            .build(),
        contentDescription = null
    )
}