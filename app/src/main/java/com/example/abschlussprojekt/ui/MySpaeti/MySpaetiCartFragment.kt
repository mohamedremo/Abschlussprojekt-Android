package com.example.abschlussprojekt.ui.MySpaeti

import android.animation.Animator
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.location.Geocoder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.abschlussprojekt.R
import com.example.abschlussprojekt.data.model.Order
import com.example.abschlussprojekt.databinding.FragmentMySpaetiCartBinding
import com.example.abschlussprojekt.formatToEuro
import com.example.abschlussprojekt.generateUUID
import com.example.abschlussprojekt.toast
import com.example.abschlussprojekt.ui.MySpaeti.adapter.MySpaetiCartAdapter
import com.example.abschlussprojekt.ui.MySpaeti.viewmodel.MySpaetiViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "MySpaetiCartFragment"

class MySpaetiCartFragment : Fragment() {

    private lateinit var binding: FragmentMySpaetiCartBinding
    private val viewModel: MySpaetiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // View Binding wird eingerichtet
        binding = FragmentMySpaetiCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Letzte Adresse aus ViewModel setzen
        viewModel.lastAdress.observe(viewLifecycleOwner) { address ->
            binding.etAddress.setText(address)
            Log.d(TAG, "Letzte Adresse geladen: $address")
        }

        // Beobachten des Warenkorbs
        viewModel.cart.observe(viewLifecycleOwner) { cart ->
            // Adapter wird an RecyclerView gebunden
            viewModel.calculateTotalPrice()
            binding.recyclerView.adapter = MySpaetiCartAdapter(cart, viewModel)
            // Preis und Steuern werden formatiert und gesetzt
            val totalPrice = formatToEuro(viewModel.totalPrice.value)
            val taxes = formatToEuro(viewModel.totalPrice.value?.times(0.19))
            binding.tvTotalPrice.text = totalPrice
            binding.tvTaxes.text = taxes
            Log.d(TAG, "Warenkorb aktualisiert - Preis: $totalPrice, Steuern: $taxes")
        }

        // Checkout-Button wird gedrückt
        binding.btnCheckOut.setOnClickListener {
            // Adresse und Produkte aus dem Warenkorb holen
            val orderAddress = binding.etAddress.text.toString()
            val products = viewModel.cart.value?.keys?.toList() ?: return@setOnClickListener
            val totalPrice = viewModel.totalPrice.value!!

            // Checken ob der Warenkorb leer ist oder Adresse fehlt
            if (products.isEmpty()) {
                toast("Der Warenkorb ist leer!", requireContext())
                Log.w(TAG, "Checkout abgebrochen: Warenkorb ist leer")
                return@setOnClickListener
            }
            if (orderAddress.isEmpty()) {
                toast("Bitte geben Sie Ihre Lieferadresse ein!", requireContext())
                Log.w(TAG, "Checkout abgebrochen: Adresse fehlt")
                return@setOnClickListener
            }

            // Adresse speichern
            viewModel.setLastAdress(orderAddress)

            // Erfolgsmeldung und Animation anzeigen
            binding.cvSuccess.visibility = View.VISIBLE
            binding.cvSuccess.animate()
                .alpha(1f)
                .setDuration(2500)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {
                        // Bestellung erstellen und PDF generieren
                        val order = Order(
                            generateUUID(),
                            Date(),
                            orderAddress,
                            "open",
                            products,
                            totalPrice
                        )
                        createPdf(requireContext(), order)
                        viewModel.clearCart()
                        Log.d(TAG, "Bestellung erstellt: $order")
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        // Animation fertig, Erfolgsmeldung wieder ausblenden
                        toast("Check Out erfolgreich!", requireContext())
                        binding.cvSuccess.visibility = View.INVISIBLE
                        Log.d(TAG, "Checkout abgeschlossen")
                    }

                    override fun onAnimationCancel(p0: Animator) {
                        Log.d(TAG, "Animation abgebrochen")
                    }

                    override fun onAnimationRepeat(p0: Animator) {}
                })
        }
    }

    // Funktion um die PDF zu erstellen
    fun createPdf(context: Context, order: Order) {
        val pdfDocument = PdfDocument()
        val paint = Paint()

        try {
            // Neue PDF-Seite starten
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1)
                .create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            // Logo laden und zeichnen (proportionale Skalierung)
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.mybutler)
            val logoWidth = logoBitmap.width
            val logoHeight = logoBitmap.height

            // Zielbreite und -höhe setzen, um das Logo proportional zu skalieren
            val targetLogoWidth = 150 // Maximalbreite des Logos auf der PDF
            val targetLogoHeight =
                (targetLogoWidth * logoHeight) / logoWidth // Höhe proportional zur Breite
            val logoRect = Rect(50, 10, 50 + targetLogoWidth, 10 + targetLogoHeight)
            canvas.drawBitmap(logoBitmap, null, logoRect, paint)

            // Text für die Rechnung hinzufügen
            paint.color = Color.BLACK
            paint.textSize = 24f
            canvas.drawText("Rechnung", 250f, 130f, paint)

            // Bestellinformationen hinzufügen
            paint.textSize = 14f
            canvas.drawText("Bestellnummer: ${order.orderId}", 50f, 160f, paint)
            canvas.drawText("Lieferadresse: ${order.orderTo}", 50f, 180f, paint)

            // Datum formatieren und hinzufügen
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
            val date = dateFormat.format(order.orderDate)
            canvas.drawText("Datum: $date", 50f, 200f, paint)

            // Tabellenkopf für die Produkte
            paint.color = Color.DKGRAY
            paint.style = Paint.Style.FILL
            canvas.drawRect(50f, 230f, 545f, 250f, paint)

            paint.color = Color.WHITE
            paint.textSize = 16f
            canvas.drawText("Produkt", 60f, 245f, paint)
            canvas.drawText("Preis", 300f, 245f, paint)
            canvas.drawText("Details", 400f, 245f, paint)

            // Produkte aus der Bestellung durchgehen und eintragen
            paint.color = Color.BLACK
            paint.textSize = 14f
            var yPosition = 270f
            order.products.forEach { product ->
                // Produktname und Preis einfügen
                canvas.drawText(product.name, 60f, yPosition, paint)
                canvas.drawText("${product.price} €", 300f, yPosition, paint)

                // Details (z.B. Alkohol, Vegan, etc.)
                val details = mutableListOf<String>()
                if (product.isAlcoholic) details.add("Alkoholisch")
                if (product.isVegan) details.add("Vegan")
                if (product.isVegetarian) details.add("Vegetarisch")
                canvas.drawText(details.joinToString(" | "), 400f, yPosition, paint)

                yPosition += 30f
            }

            // Gesamtbetrag und Steuern richtig anzeigen
            yPosition += 30f // Abstand erhöhen, um sicherzustellen, dass genug Platz vorhanden ist

            // Hintergrund für Gesamtbetrag und Steuern
            paint.color = Color.DKGRAY
            canvas.drawRect(50f, yPosition + 10f, 545f, yPosition + 50f, paint)

            // Gesamtbetrag und Steuern schreiben
            paint.color = Color.WHITE
            paint.textSize = 16f
            canvas.drawText(
                "Gesamtbetrag: ${
                    String.format(
                        Locale.GERMANY,
                        "%.2f",
                        order.totalPrice
                    )
                } €", 60f, yPosition + 40f, paint
            )

            val taxes = order.totalPrice * 0.19
            paint.color = Color.WHITE
            canvas.drawText(
                "Steuern (19%): ${String.format(Locale.GERMANY, "%.2f", taxes)} €",
                300f,
                yPosition + 40f,
                paint
            )

            // Seite abschließen und zur PDF hinzufügen
            pdfDocument.finishPage(page)

            // PDF speichern
            val fileName = "Rechnung_${order.orderId}.pdf"
            val filePath =
                File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

            pdfDocument.writeTo(FileOutputStream(filePath))
            Log.d(TAG, "PDF gespeichert unter: ${filePath.absolutePath}")

            // PDF zu Firestore und Firebase Storage hochladen
            viewModel.saveOrderInFireStore(order)
            viewModel.uploadPdfToFirebaseStorage(filePath.absolutePath, order.orderId)
            Log.d(TAG, "PDF in Firestore und Firebase Storage gespeichert")
        } catch (e: IOException) {
            // Fehler beim Erstellen der PDF
            Log.e(TAG, "Fehler beim Schreiben der PDF: ${e.message}")
        } finally {
            // PDF-Dokument schließen
            pdfDocument.close()
        }
    }

}