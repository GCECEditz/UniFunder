package com.example.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.MainViewModel
import com.example.R
import com.example.ui.theme.LilacAsh
import com.example.ui.theme.PineBlue
import com.example.ui.theme.VintageGrape
import com.example.ui.theme.VintageGrapeLight
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

@Composable
fun SocialMediaScreen(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {

    val context =
        LocalContext.current
    val qrBitmap =
        remember(vm.qrContent) {

            generateQrBitmap(
                vm.qrContent
            )
        }

    // ==========================================
    // DEFAULT POST MESSAGE
    // ==========================================

    val defaultPostText =
        "Support ${vm.selectedNgo.name} in our TARUMT partnership! " +
                "We are raising funds to support our NGO partnership under SDG 17: " +
                "Partnerships for the Goals. Donate today with UniFunder!"


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // ==========================================
        // HEADER
        // ==========================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 15.dp,
                    vertical = 15.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    vm.navigateBack()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = PineBlue
                )
            }


            Spacer(
                modifier =
                    Modifier.weight(1f)
            )


            Text(
                text =
                    "SOCIAL MEDIA",

                fontFamily =
                    GoogleSans,

                fontWeight =
                    FontWeight.Bold,

                fontSize =
                    20.sp,

                color =
                    PineBlue
            )


            Spacer(
                modifier =
                    Modifier.weight(1f)
            )
        }


        HorizontalDivider(
            color =
                PineBlue,

            thickness =
                2.dp,

            modifier =
                Modifier.padding(
                    horizontal = 20.dp
                )
        )


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        // ==========================================
        // DESCRIPTION
        // ==========================================

        Text(
            text =
                "Choose a social media platform to create a fundraising post.",

            fontFamily =
                GoogleSans,

            fontWeight =
                FontWeight.Medium,

            fontSize =
                14.sp,

            color =
                VintageGrape,

            textAlign =
                TextAlign.Center,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp
                    )
        )


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        // ==========================================
        // SOCIAL MEDIA PLATFORM LIST
        // ==========================================

        val platforms =
            listOf(
                "INSTAGRAM",
                "FACEBOOK",
                "REDNOTE (小紅書)",
                "TIKTOK"
            )


        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(
                        horizontal = 20.dp
                    ),

            verticalArrangement =
                Arrangement.spacedBy(
                    15.dp
                )
        ) {


            items(platforms) { platform ->


                Card(
                    shape =
                        RoundedCornerShape(
                            8.dp
                        ),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        ),

                    border =
                        BorderStroke(
                            1.dp,
                            LilacAsh
                        ),

                    modifier =
                        Modifier
                            .fillMaxWidth()

                            // ==========================================
                            // CLICK SOCIAL MEDIA
                            // ==========================================

                            .clickable {

                                if (qrBitmap == null) {

                                    Toast.makeText(
                                        context,
                                        "QR code not available.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    return@clickable
                                }


                                val posterBitmap =
                                    generateFundraisingPoster(
                                        context = context,
                                        ngoName = vm.selectedNgo.name,
                                        qrBitmap = qrBitmap,
                                        raisedAmount = vm.qrRaisedAmount,
                                        goalAmount = vm.qrGoalAmount
                                    )


                                val imageUri =
                                    savePosterToCache(
                                        context = context,
                                        bitmap = posterBitmap
                                    )


                                if (imageUri != null) {

                                    shareFundraisingPoster(
                                        context = context,
                                        platform = platform,
                                        imageUri = imageUri,
                                        postText = defaultPostText
                                    )

                                } else {

                                    Toast.makeText(
                                        context,
                                        "Unable to generate fundraising poster.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                ) {


                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    16.dp
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {


                        // ==========================================
                        // PLATFORM ICON PLACEHOLDER
                        // ==========================================

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                        ) {

                            when (platform) {

                                "INSTAGRAM" -> {

                                    Image(
                                        painter = painterResource(
                                            id = R.drawable.instagram_logo
                                        ),
                                        contentDescription = "Instagram",
                                        modifier = Modifier.size(42.dp)
                                    )
                                }


                                "FACEBOOK" -> {

                                    Image(
                                        painter = painterResource(
                                            id = R.drawable.facebook_logo
                                        ),
                                        contentDescription = "Facebook",
                                        modifier = Modifier.size(42.dp)
                                    )
                                }


                                "REDNOTE (小紅書)" -> {

                                    Image(
                                        painter = painterResource(
                                            id = R.drawable.rednote_logo
                                        ),
                                        contentDescription = "REDnote",
                                        modifier = Modifier.size(42.dp)
                                    )
                                }


                                "TIKTOK" -> {

                                    Image(
                                        painter = painterResource(
                                            id = R.drawable.tiktok_logo
                                        ),
                                        contentDescription = "TikTok",
                                        modifier = Modifier.size(42.dp)
                                    )
                                }
                            }
                        }


                        Spacer(
                            modifier =
                                Modifier.width(
                                    15.dp
                                )
                        )


                        // ==========================================
                        // PLATFORM NAME
                        // ==========================================

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {


                            Text(
                                text =
                                    platform,

                                fontFamily =
                                    GoogleSans,

                                fontWeight =
                                    FontWeight.Bold,

                                fontSize =
                                    16.sp,

                                color =
                                    PineBlue
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(
                                        3.dp
                                    )
                            )


                            Text(
                                text =
                                    "Create fundraising post",

                                fontFamily =
                                    GoogleSans,

                                fontSize =
                                    12.sp,

                                color =
                                    VintageGrapeLight
                            )
                        }


                        // ==========================================
                        // OPEN INDICATOR
                        // ==========================================

                        Text(
                            text =
                                ">",

                            fontFamily =
                                GoogleSans,

                            fontWeight =
                                FontWeight.Bold,

                            fontSize =
                                22.sp,

                            color =
                                PineBlue
                        )
                    }
                }
            }
        }


        // ==========================================
        // BOTTOM INFORMATION
        // ==========================================

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    )
                    .border(
                        1.dp,
                        LilacAsh,
                        RoundedCornerShape(
                            8.dp
                        )
                    )
                    .background(
                        Color.White
                    ),

            contentAlignment =
                Alignment.Center
        ) {


            Text(
                text =
                    "Select a platform above to open its post creation page.",

                fontFamily =
                    GoogleSans,

                fontSize =
                    12.sp,

                color =
                    LilacAsh,

                textAlign =
                    TextAlign.Center,

                modifier =
                    Modifier.padding(
                        20.dp
                    )
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    15.dp
                )
        )
    }
}

// ==========================================
// OPEN WEBSITE IF APP IS NOT INSTALLED
// ==========================================
private fun generateFundraisingPoster(
    context: Context,
    ngoName: String,
    qrBitmap: Bitmap,
    raisedAmount: Double,
    goalAmount: Double
): Bitmap {

    val width = 1080
    val height = 1350

    val bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)

    canvas.drawColor(
        AndroidColor.WHITE
    )


    val titlePaint = Paint().apply {
        color = AndroidColor.rgb(30, 65, 90)
        textSize = 70f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }


    val ngoPaint = Paint().apply {
        color = AndroidColor.rgb(80, 40, 110)
        textSize = 48f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }


    val normalPaint = Paint().apply {
        color = AndroidColor.DKGRAY
        textSize = 42f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }


    val progressPaint = Paint().apply {
        color = AndroidColor.rgb(0, 135, 135)
        textSize = 46f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }


    canvas.drawText(
        "UniFunder",
        width / 2f,
        120f,
        titlePaint
    )


    canvas.drawText(
        "SUPPORT",
        width / 2f,
        220f,
        normalPaint
    )


    canvas.drawText(
        ngoName.uppercase(),
        width / 2f,
        300f,
        ngoPaint
    )


    val qrSize = 520

    val scaledQr =
        Bitmap.createScaledBitmap(
            qrBitmap,
            qrSize,
            qrSize,
            true
        )


    val qrLeft =
        (width - qrSize) / 2f

    val qrTop =
        380f


    canvas.drawBitmap(
        scaledQr,
        qrLeft,
        qrTop,
        null
    )


    canvas.drawText(
        "Scan to Donate",
        width / 2f,
        970f,
        normalPaint
    )


    canvas.drawText(
        "RM ${
            String.format(
                Locale.US,
                "%.2f",
                raisedAmount
            )
        } / RM ${
            String.format(
                Locale.US,
                "%.2f",
                goalAmount
            )
        } Raised",
        width / 2f,
        1060f,
        progressPaint
    )


    canvas.drawText(
        "TAR UMT × SDG 17 Partnership",
        width / 2f,
        1180f,
        normalPaint
    )


    canvas.drawText(
        "Powered by UniFunder",
        width / 2f,
        1260f,
        normalPaint
    )


    return bitmap
}

private fun savePosterToCache(
    context: Context,
    bitmap: Bitmap
): Uri? {

    return try {

        val imagesFolder =
            File(
                context.cacheDir,
                "shared_images"
            )

        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs()
        }


        val imageFile =
            File(
                imagesFolder,
                "unifunder_fundraising.png"
            )


        FileOutputStream(
            imageFile
        ).use { outputStream ->

            bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                outputStream
            )
        }


        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )

    } catch (e: Exception) {

        null
    }
}

private fun shareFundraisingPoster(
    context: Context,
    platform: String,
    imageUri: Uri,
    postText: String
) {
    if (platform == "TIKTOK") {

        val packages =
            listOf(
                "com.zhiliaoapp.musically",
                "com.ss.android.ugc.trill"
            )

        var installedPackage: String? = null

        for (pkg in packages) {

            try {

                context.packageManager
                    .getPackageInfo(
                        pkg,
                        0
                    )

                installedPackage = pkg
                break

            } catch (e: Exception) {
            }
        }


        if (installedPackage != null) {

            try {

                val intent =
                    Intent(
                        Intent.ACTION_SEND
                    ).apply {

                        type = "image/*"

                        putExtra(
                            Intent.EXTRA_STREAM,
                            imageUri
                        )

                        clipData =
                            android.content.ClipData.newUri(
                                context.contentResolver,
                                "UniFunder Fundraising Poster",
                                imageUri
                            )

                        addFlags(
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )

                        setPackage(
                            installedPackage
                        )
                    }


                context.startActivity(
                    intent
                )

                return

            } catch (e: Exception) {

                e.printStackTrace()

                Toast.makeText(
                    context,
                    "TikTok is installed but cannot receive the image.",
                    Toast.LENGTH_LONG
                ).show()

                return
            }
        }


        // TikTok not installed
        try {

            val playStoreIntent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "market://details?id=com.zhiliaoapp.musically"
                    )
                )

            context.startActivity(
                playStoreIntent
            )

        } catch (e: Exception) {

            val browserIntent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "https://play.google.com/store/apps/details?id=com.zhiliaoapp.musically"
                    )
                )

            context.startActivity(
                browserIntent
            )
        }

        return
    }

    val packageName =
        when (platform) {

            "INSTAGRAM" ->
                "com.instagram.android"

            "FACEBOOK" ->
                "com.facebook.katana"

            "REDNOTE (小紅書)" ->
                "com.xingin.xhs"

            else ->
                null
        }


    try {

        val shareIntent =
            Intent(
                Intent.ACTION_SEND
            ).apply {

                type = "image/*"

                putExtra(
                    Intent.EXTRA_STREAM,
                    imageUri
                )

                putExtra(
                    Intent.EXTRA_TEXT,
                    postText
                )

                clipData =
                    android.content.ClipData.newUri(
                        context.contentResolver,
                        "UniFunder Fundraising Poster",
                        imageUri
                    )

                addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                if (packageName != null) {
                    setPackage(packageName)
                }
            }

        context.startActivity(
            shareIntent
        )

    } catch (e: Exception) {

        if (packageName != null) {

            try {

                val playStoreIntent =
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            "market://details?id=$packageName"
                        )
                    )

                context.startActivity(
                    playStoreIntent
                )

            } catch (e: Exception) {

                val browserIntent =
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            "https://play.google.com/store/apps/details?id=$packageName"
                        )
                    )

                context.startActivity(
                    browserIntent
                )
            }

        } else {

            Toast.makeText(
                context,
                "$platform could not be opened.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
