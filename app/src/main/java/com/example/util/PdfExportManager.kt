package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.db.CaseEntity
import java.io.File
import java.io.FileOutputStream

object PdfExportManager {

    fun generatePetitionPdf(context: Context, caseEntity: CaseEntity): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 dimensions in points
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 12f

        val titlePaint = Paint()
        titlePaint.color = Color.parseColor("#004AC6")
        titlePaint.textSize = 18f
        titlePaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)

        val headerPaint = Paint()
        headerPaint.color = Color.parseColor("#0F172A")
        headerPaint.textSize = 13f
        headerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        var y = 50f
        val startX = 50f
        val maxX = 545f

        // Header
        canvas.drawText("EXCELENTÍSSIMO SENHOR DOUTOR JUIZ DE DIREITO DO", startX, y, headerPaint)
        y += 20f
        canvas.drawText("JUIZADO ESPECIAL CÍVEL DA COMARCA", startX, y, headerPaint)
        y += 40f

        // Title
        canvas.drawText("PETIÇÃO INICIAL - JEC", startX, y, titlePaint)
        y += 18f
        paint.textSize = 11f
        paint.color = Color.DKGRAY
        canvas.drawText("Auditoria Jurídica Eletrônica • Contador Jurídico Pro", startX, y, paint)
        y += 30f

        // Divider
        canvas.drawLine(startX, y, maxX, y, paint)
        y += 25f

        paint.color = Color.BLACK
        paint.textSize = 11f

        // Author/Defendant info
        canvas.drawText("REQUERENTE: ${caseEntity.authorName} (${if (caseEntity.authorCpf.isNotBlank()) "CPF " + caseEntity.authorCpf else "Pessoa Física"})", startX, y, paint)
        y += 18f
        canvas.drawText("REQUERIDO: ${if (caseEntity.defendantName.isNotBlank()) caseEntity.defendantName else "Empresa Ré/Concessionária"}", startX, y, paint)
        y += 25f

        // Section 1: DOS FATOS
        canvas.drawText("1. DOS FATOS", startX, y, headerPaint)
        y += 20f
        val fatosLines = wrapText(caseEntity.fatosText.ifBlank { "Sem fatos registrados." }, paint, 490f)
        for (line in fatosLines) {
            if (y > 780f) break
            canvas.drawText(line, startX + 10f, y, paint)
            y += 16f
        }
        y += 15f

        // Section 2: DOS FUNDAMENTOS
        if (y < 780f) {
            canvas.drawText("2. DOS FUNDAMENTOS JURÍDICOS", startX, y, headerPaint)
            y += 20f
            val fundLines = wrapText(caseEntity.fundamentosText.ifBlank { "Fundamentado na legislação do consumidor e Código Civil." }, paint, 490f)
            for (line in fundLines) {
                if (y > 780f) break
                canvas.drawText(line, startX + 10f, y, paint)
                y += 16f
            }
            y += 15f
        }

        // Section 3: DOS CÁLCULOS E PEDIDOS
        if (y < 780f) {
            canvas.drawText("3. DOS PEDIDOS E VALOR DA CAUSA", startX, y, headerPaint)
            y += 20f
            canvas.drawText("• Dano Material (atualizado): R$ %.2f".format(caseEntity.subtotalUpdated), startX + 10f, y, paint)
            y += 16f
            canvas.drawText("• Danos Morais Sugeridos: R$ %.2f".format(caseEntity.suggestedMoralDamages), startX + 10f, y, paint)
            y += 16f
            val totalCausa = caseEntity.subtotalUpdated + caseEntity.suggestedMoralDamages
            canvas.drawText("• Valor Total da Causa: R$ %.2f".format(totalCausa), startX + 10f, y, paint)
            y += 20f

            val pedLines = wrapText(caseEntity.pedidosText, paint, 490f)
            for (line in pedLines) {
                if (y > 780f) break
                canvas.drawText(line, startX + 10f, y, paint)
                y += 16f
            }
            y += 30f
        }

        // Signature line
        if (y < 780f) {
            canvas.drawLine(200f, y, 400f, y, paint)
            y += 15f
            val sigPaint = Paint(paint)
            sigPaint.textSize = 10f
            canvas.drawText("Assinatura do Requerente", 240f, y, sigPaint)
        }

        document.finishPage(page)

        val pdfDir = File(context.cacheDir, "petitions")
        if (!pdfDir.exists()) pdfDir.mkdirs()
        val file = File(pdfDir, "Peticao_${caseEntity.id}_${System.currentTimeMillis()}.pdf")
        val outputStream = FileOutputStream(file)
        document.writeTo(outputStream)
        document.close()
        outputStream.close()

        return file
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val result = mutableListOf<String>()
        val paragraphs = text.split("\n")
        for (paragraph in paragraphs) {
            val words = paragraph.split(" ")
            var currentLine = ""
            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                val measure = paint.measureText(testLine)
                if (measure <= maxWidth) {
                    currentLine = testLine
                } else {
                    if (currentLine.isNotEmpty()) result.add(currentLine)
                    currentLine = word
                }
            }
            if (currentLine.isNotEmpty()) result.add(currentLine)
        }
        return result
    }

    fun sharePdfFile(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Compartilhar Petição PDF"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
