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

    fun generatePetitionPdf(context: Context, caseEntity: CaseEntity, signatureBitmap: android.graphics.Bitmap? = null): File {
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val marginX = 50f
        val marginY = 50f
        val maxTextWidth = pageWidth - (marginX * 2)
        val bottomMargin = pageHeight - marginY

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
        }
        val boldPaint = Paint(paint).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val headerPaint = Paint(titlePaint).apply {
            textSize = 14f
            textAlign = Paint.Align.LEFT
        }

        var y = marginY

        fun startNewPage() {
            document.finishPage(page)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = marginY
        }

        fun checkPageBreak(requiredSpace: Float) {
            if (y + requiredSpace > bottomMargin) {
                startNewPage()
            }
        }

        fun drawWrappedText(text: String, textPaint: Paint, indent: Float = 0f, spacing: Float = 18f) {
            val lines = wrapText(text, textPaint, maxTextWidth - indent)
            for (line in lines) {
                checkPageBreak(spacing)
                canvas.drawText(line, marginX + indent, y, textPaint)
                y += spacing
            }
        }

        // --- Cabeçalho e Endereçamento ---
        checkPageBreak(80f)
        canvas.drawText("EXCELENTÍSSIMO SENHOR DOUTOR JUIZ DE DIREITO DO", marginX, y, headerPaint)
        y += 20f
        canvas.drawText("JUIZADO ESPECIAL CÍVEL DA COMARCA", marginX, y, headerPaint)
        y += 60f

        // --- Título ---
        checkPageBreak(40f)
        canvas.drawText("PETIÇÃO INICIAL", pageWidth / 2f, y, titlePaint)
        y += 40f

        // --- Qualificação ---
        val cpf = if (caseEntity.authorCpf.isNotBlank()) caseEntity.authorCpf else "[Inserir CPF]"
        val defendant = if (caseEntity.defendantName.isNotBlank()) caseEntity.defendantName else "[Nome do Réu]"
        val authorName = if (caseEntity.authorName.isNotBlank()) caseEntity.authorName else "[Nome do Autor]"
        
        val qualificação = "$authorName, inscrito(a) no CPF sob o nº $cpf, residente e domiciliado(a) em [Endereço], por seu próprio direito, vem, respeitosamente, à presença de Vossa Excelência, propor a presente AÇÃO em face de $defendant, pelos fatos e fundamentos a seguir delineados."
        drawWrappedText(qualificação, paint, indent = 0f)
        y += 20f

        // --- 1. DOS FATOS ---
        checkPageBreak(40f)
        canvas.drawText("1. DOS FATOS", marginX, y, boldPaint)
        y += 25f
        val fatos = caseEntity.fatosText.ifBlank { caseEntity.description.ifBlank { "Sem descrição." } }
        drawWrappedText(fatos, paint, indent = 10f)
        y += 20f

        // --- 2. DOS FUNDAMENTOS ---
        checkPageBreak(40f)
        canvas.drawText("2. DOS FUNDAMENTOS", marginX, y, boldPaint)
        y += 25f
        val fundamentos = caseEntity.fundamentosText.ifBlank { caseEntity.legalBasis.ifBlank { "Fundamentado na legislação." } }
        drawWrappedText(fundamentos, paint, indent = 10f)
        y += 20f

        // --- 3. MEMÓRIA DE CÁLCULO ---
        checkPageBreak(40f)
        canvas.drawText("3. DA MEMÓRIA DE CÁLCULO (DANOS E JUROS)", marginX, y, boldPaint)
        y += 25f
        
        drawWrappedText("Abaixo, apresenta-se a memória de cálculo para a apuração do montante devido, considerando correção monetária e juros de mora legais:", paint, indent = 10f)
        y += 10f

        val calcIndent = 30f
        canvas.drawText(String.format("• Valor Histórico (Principal): R$ %,.2f", caseEntity.historicalValue), marginX + calcIndent, y, paint)
        y += 20f
        canvas.drawText(String.format("• Correção Monetária (INPC): R$ %,.2f", caseEntity.inpcCorrection), marginX + calcIndent, y, paint)
        y += 20f
        canvas.drawText(String.format("• Juros de Mora (1%% a.m.): R$ %,.2f", caseEntity.defaultInterest), marginX + calcIndent, y, paint)
        y += 20f
        canvas.drawText(String.format("• Dano Material Atualizado: R$ %,.2f", caseEntity.subtotalUpdated), marginX + calcIndent, y, boldPaint)
        y += 30f

        // --- 4. DOS PEDIDOS ---
        checkPageBreak(40f)
        canvas.drawText("4. DOS PEDIDOS", marginX, y, boldPaint)
        y += 25f
        
        drawWrappedText("Diante do exposto, requer a Vossa Excelência:", paint, indent = 10f)
        y += 10f
        
        val pedidosDefault = mutableListOf(
            "a) A citação da parte Requerida para comparecer à audiência de conciliação e, querendo, apresentar defesa, sob pena de revelia;",
            "b) A procedência da ação para condenar a Requerida ao pagamento do Dano Material atualizado no valor de R$ %,.2f;".format(caseEntity.subtotalUpdated)
        )
        if (caseEntity.suggestedMoralDamages > 0) {
            pedidosDefault.add("c) A condenação da Requerida ao pagamento de compensação por Danos Morais no importe de R$ %,.2f;".format(caseEntity.suggestedMoralDamages))
        }
        
        val pedidos = caseEntity.pedidosText.ifBlank { pedidosDefault.joinToString("\n") }
        drawWrappedText(pedidos, paint, indent = 30f)
        y += 20f

        val totalValue = caseEntity.subtotalUpdated + caseEntity.suggestedMoralDamages
        checkPageBreak(60f)
        canvas.drawText("Dá-se à causa o valor de R$ %,.2f.".format(totalValue), marginX, y, boldPaint)
        y += 40f
        
        // --- Assinatura ---
        checkPageBreak(120f)
        canvas.drawText("Termos em que,", marginX, y, paint)
        y += 20f
        canvas.drawText("Pede deferimento.", marginX, y, paint)
        y += 60f
        
                val centerX = pageWidth / 2f
        if (signatureBitmap != null) {
            val sigWidth = 240f
            val aspectRatio = signatureBitmap.height.toFloat() / signatureBitmap.width.toFloat()
            val sigHeight = sigWidth * aspectRatio
            val destRect = android.graphics.RectF(centerX - sigWidth / 2f, y - sigHeight, centerX + sigWidth / 2f, y)
            canvas.drawBitmap(signatureBitmap, null, destRect, null)
        }
        canvas.drawLine(centerX - 120f, y, centerX + 120f, y, paint)
        y += 20f

        
        val centerPaint = Paint(paint).apply { textAlign = Paint.Align.CENTER }
        canvas.drawText(authorName, centerX, y, centerPaint)
        y += 20f
        canvas.drawText("Requerente", centerX, y, centerPaint)

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
            if (paragraph.isBlank()) {
                result.add("")
                continue
            }
            val words = paragraph.split(" ")
            var currentLine = ""
            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                val measure = paint.measureText(testLine)
                if (measure <= maxWidth) {
                    currentLine = testLine
                } else {
                    if (currentLine.isNotEmpty()) {
                        result.add(currentLine)
                    }
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
