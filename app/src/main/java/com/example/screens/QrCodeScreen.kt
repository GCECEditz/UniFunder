package com.example.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.ui.theme.LilacAsh
import com.example.ui.theme.Malachite
import com.example.ui.theme.PineBlue
import com.example.ui.theme.VintageGrape
import com.example.ui.theme.VintageGrapeLight
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import java.util.Locale

// ======================================================
// GENERATE REAL QR CODE
// ======================================================

fun generateQrBitmap(
    content: String,
    size: Int = 700
): Bitmap? {

    if (content.isBlank()) {
        return null
    }

    return try {

        val bitMatrix =
            MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size
            )

        Bitmap.createBitmap(
            size,
            size,
            Bitmap.Config.RGB_565
        ).apply {

            for (x in 0 until size) {

                for (y in 0 until size) {

                    setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) {
                            android.graphics.Color.BLACK
                        } else {
                            android.graphics.Color.WHITE
                        }
                    )
                }
            }
        }

    } catch (e: Exception) {
        null
    }
}

// ======================================================
// QR SCREEN
// ======================================================
@Composable
fun QrCodeScreen(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Controls NGO dropdown
    var dropdownExpanded by remember {
        mutableStateOf(false)
    }
    /*
     * Every time:
     *
     * selected NGO changes
     * OR
     * logged-in user changes
     *
     * reload Supabase progress.
     */
    LaunchedEffect(
        vm.selectedNgo.id,
        vm.loggedInEmail
    ) {

        vm.loadFundraisingProgress(
            vm.selectedNgo.id
        )
    }

    // ==================================================
    // CALCULATE PROGRESS
    // ==================================================

    val progressPercent =

        if (vm.qrGoalAmount > 0.0) {
            (
                    vm.qrRaisedAmount /
                            vm.qrGoalAmount
                    )
                .toFloat()
                .coerceIn(
                    0f,
                    1f
                )
        } else {
            0f
        }

    // ==================================================
    // GENERATE QR FROM SUPABASE qr_content
    // ==================================================

    val qrBitmap = remember(
        vm.qrContent
    ) {
        generateQrBitmap(
            vm.qrContent
        )
    }

    // ==================================================
    // MAIN SCREEN
    // ==================================================

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        contentPadding =
            PaddingValues(
                bottom = 32.dp
            )
    ) {

        // ==================================================
        // HEADER
        // ==================================================

        item {
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
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription =
                            "Back",
                        tint =
                            PineBlue
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Text(
                    text =
                        "TRACK PROGRESS & QR",

                    fontFamily =
                        GoogleSans,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        20.sp,

                    color =
                        PineBlue
                )
            }
        }


        // ==================================================
        // NGO DROPDOWN
        // ==================================================

        item {
            Text(
                text =
                    "Choose an NGO to support",

                fontFamily =
                    GoogleSans,

                fontWeight =
                    FontWeight.Bold,

                fontSize =
                    15.sp,

                color =
                    VintageGrape,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 30.dp
                        )
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Box(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 30.dp
                        )

            ) {

                // Dropdown button
                OutlinedButton(

                    onClick = {

                        dropdownExpanded =
                            true

                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    border =
                        BorderStroke(
                            1.dp,
                            PineBlue
                        )

                ) {
                    Text(

                        text =
                            vm.selectedNgo.name,

                        color =
                            PineBlue,

                        fontWeight =
                            FontWeight.Bold,

                        modifier =
                            Modifier.weight(1f),

                        textAlign =
                            TextAlign.Start

                    )
                    Text(
                        text = "▼",
                        color = PineBlue
                    )

                }


                // Actual dropdown
                DropdownMenu(
                    expanded =
                        dropdownExpanded,
                    onDismissRequest = {
                        dropdownExpanded =
                            false
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth(
                                0.85f
                            )
                            .background(
                                Color.White
                            )
                ) {
                    vm.ngos.forEach { ngo ->
                        DropdownMenuItem(
                            text = {

                                Text(

                                    text =
                                        ngo.name,

                                    fontWeight =

                                        if (
                                            ngo.id ==
                                            vm.selectedNgo.id
                                        ) {

                                            FontWeight.Bold

                                        } else {

                                            FontWeight.Normal

                                        }
                                )
                            },
                            onClick = {
                                dropdownExpanded =
                                    false
                                /*
                                 * Change NGO
                                 * +
                                 * load Supabase data
                                 */
                                vm.selectNgoForQr(
                                    ngo
                                )

                            }
                        )
                    }
                }
            }
            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            // ==================================================
            // SELECTED NGO NAME
            // ==================================================

            Text(
                text =
                    vm.selectedNgo.name,

                fontFamily =
                    GoogleSans,

                fontWeight =
                    FontWeight.Bold,

                fontSize =
                    24.sp,

                color =
                    PineBlue,

                textAlign =
                    TextAlign.Center,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 24.dp
                        )
            )
            Text(
                text =
                    "SDG 17: Partnerships for the Goals",

                fontFamily =
                    GoogleSans,

                fontWeight =
                    FontWeight.Medium,

                fontSize =
                    14.sp,

                color =
                    VintageGrapeLight,

                textAlign =
                    TextAlign.Center,

                modifier =
                    Modifier.fillMaxWidth()
            )
            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            // ==================================================
            // NGO INITIAL LOGO
            // ==================================================

            Box(
                contentAlignment =
                    Alignment.Center,
                modifier =
                    Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            LilacAsh.copy(
                                alpha = 0.2f
                            )
                        )
                        .border(
                            1.dp,
                            LilacAsh,
                            CircleShape
                        )
            ) {
                Text(
                    text =
                        vm.selectedNgo.initials,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        26.sp,

                    color =
                        VintageGrape
                )
            }
            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

        }


        // ==================================================
        // QR CODE
        // ==================================================

        item {
            Card(
                modifier =
                    Modifier.size(
                        230.dp
                    ),
                shape =
                    RoundedCornerShape(
                        16.dp
                    ),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    ),
                border =
                    BorderStroke(
                        2.dp,
                        PineBlue
                    )
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                14.dp
                            ),
                    contentAlignment =
                        Alignment.Center
                ) {
                    when {
                        // Loading Supabase
                        vm.qrIsLoading -> {

                            CircularProgressIndicator(
                                color =
                                    PineBlue
                            )
                        }
                        // QR available
                        qrBitmap != null -> {

                            Image(
                                bitmap = qrBitmap.asImageBitmap(),

                                contentDescription =
                                    "Donation QR for ${vm.selectedNgo.name}",

                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {

                                        // Simulate successful payment
                                        val donatedAmount =
                                            vm.simulateQrDonation()

                                        // Show notification to user
                                        Toast.makeText(
                                            context,
                                            "RM ${
                                                String.format(
                                                    Locale.US,
                                                    "%.2f",
                                                    donatedAmount
                                                )
                                            } has been donated to ${vm.selectedNgo.name}!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            )
                        }
                        // Error
                        else -> {

                            Text(

                                text =
                                    "QR not available",

                                color =
                                    VintageGrape,

                                textAlign =
                                    TextAlign.Center
                            )
                        }
                    }
                }
            }
            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )
            Text(
                text =
                    "Scan to support ${vm.selectedNgo.name}",

                color =
                    VintageGrapeLight,

                fontSize =
                    13.sp,

                textAlign =
                    TextAlign.Center,

                modifier =
                    Modifier.padding(
                        horizontal = 24.dp
                    )
            )
            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )
        }

        // ==================================================
        // PROGRESS BAR
        // ==================================================

        item {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 30.dp
                        ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {
                Text(
                    text =
                        "DONATION PROGRESS",

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
                        Modifier.height(12.dp)
                )

                // ==================================================
                // PROGRESS BAR
                // ==================================================

                LinearProgressIndicator(
                    progress = {
                        progressPercent
                    },
                    color =
                        Malachite,
                    trackColor =
                        LilacAsh.copy(
                            alpha = 0.3f
                        ),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(
                                16.dp
                            )
                            .clip(
                                RoundedCornerShape(
                                    8.dp
                                )
                            )
                )
                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )
                Text(
                    text =
                        "${(progressPercent * 100).toInt()}% RAISED",

                    fontFamily =
                        GoogleSans,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        18.sp,

                    color =
                        PineBlue
                )

                // ==================================================
                // TOTAL NGO PROGRESS
                // ==================================================

                Text(
                    text =
                        "RM ${
                            String.format(
                                Locale.US,
                                "%.2f",
                                vm.qrRaisedAmount
                            )
                        } / RM ${
                            String.format(
                                Locale.US,
                                "%.2f",
                                vm.qrGoalAmount
                            )
                        } Goal",
                    fontFamily =
                        GoogleSans,
                    fontWeight =
                        FontWeight.Medium,
                    fontSize =
                        14.sp,
                    color =
                        VintageGrape
                )
                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                // ==================================================
                // REFRESH BUTTON
                // ==================================================

                OutlinedButton(
                    onClick = {
                        vm.loadFundraisingProgress(
                            vm.selectedNgo.id
                        )
                    },
                    enabled =
                        !vm.qrIsLoading
                ) {
                    Text(
                        if (vm.qrIsLoading) {
                            "Loading..."
                        } else {
                            "Refresh Progress"
                        }
                    )
                }

                // ==================================================
                // ERROR
                // ==================================================

                vm.qrError?.let { message ->
                    Spacer(
                        modifier =
                            Modifier.height(
                                10.dp
                            )
                    )
                    Text(
                        text =
                            message,
                        color =
                            MaterialTheme
                                .colorScheme
                                .error,
                        fontSize =
                            12.sp,
                        textAlign =
                            TextAlign.Center
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )

                Text(
                    text =
                        "Thank you for supporting our partnership.",
                    fontFamily =
                        GoogleSans,
                    fontWeight =
                        FontWeight.Bold,
                    fontSize =
                        14.sp,
                    color =
                        VintageGrapeLight,
                    textAlign =
                        TextAlign.Center
                )
            }
        }
    }
}
