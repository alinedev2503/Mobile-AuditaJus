package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.db.CaseEntity
import com.example.data.preferences.UserSettings
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfExportManager {

    enum class ExportDocumentType {
        PETICAO_ONLY,
        PROCURACAO_ONLY,
        COMBO_PETICAO_E_PROCURACAO,
        LAUDO_AUDITORIA_E_CALCULO
    }

    fun generateLegalDocument(
        context: Context,
        caseEntity: CaseEntity,
        userSettings: UserSettings = UserSettings(),
        signatureBitmap: Bitmap? = null,
        clientSignatureBitmap: Bitmap? = null,
        exportType: ExportDocumentType = ExportDocumentType.COMBO_PETICAO_E_PROCURACAO,
        watermarkText: String = "LAUDO PERICIAL JURÍDICO",
        showWatermark: Boolean = true
    ): File {
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val marginX = 48f
        val marginY = 45f
        val maxTextWidth = pageWidth - (marginX * 2)
        val bottomMargin = pageHeight - 65f

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        val paint = Paint().apply {
            color = Color.rgb(20, 25, 35)
            textSize = 10.5f
            isAntiAlias = true
        }
        val boldPaint = Paint(paint).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val titlePaint = Paint().apply {
            color = Color.rgb(15, 30, 65)
            textSize = 14.5f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val sectionHeaderPaint = Paint().apply {
            color = Color.rgb(20, 45, 90)
            textSize = 11.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val lawFirmHeaderPaint = Paint().apply {
            color = Color.rgb(15, 45, 90)
            textSize = 13.5f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        val lawFirmSubPaint = Paint().apply {
            color = Color.rgb(90, 100, 115)
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }
        val accentLinePaint = Paint().apply {
            color = Color.rgb(37, 99, 235) // primary blue
            strokeWidth = 2f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        val thinLinePaint = Paint().apply {
            color = Color.rgb(215, 225, 235)
            strokeWidth = 0.8f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        val footerPaint = Paint().apply {
            color = Color.rgb(120, 130, 145)
            textSize = 8f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // Watermark Paint
        val watermarkPaint = Paint().apply {
            color = Color.argb(22, 30, 60, 120) // Very subtle elegant watermark
            textSize = 42f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        fun drawWatermark(c: Canvas) {
            if (!showWatermark) return
            c.save()
            c.rotate(-35f, pageWidth / 2f, pageHeight / 2f)
            c.drawText(watermarkText, pageWidth / 2f, pageHeight / 2f, watermarkPaint)
            c.drawText("CONTADOR JURÍDICO PRO • ART. 42 CDC", pageWidth / 2f, (pageHeight / 2f) + 50f, watermarkPaint.apply { textSize = 18f })
            c.restore()
        }

        var y = marginY

        val logoBitmap: Bitmap? = if (userSettings.logoUri.isNotBlank()) {
            try {
                val uri = Uri.parse(userSettings.logoUri)
                val input: InputStream? = context.contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                null
            }
        } else null

        fun drawCustomLetterhead(documentTitle: String = "") {
            drawWatermark(canvas)

            val headerStartY = 35f
            var textStartX = marginX

            if (userSettings.useCustomLetterhead) {
                if (logoBitmap != null) {
                    val logoSize = 38f
                    val destRect = RectF(marginX, headerStartY, marginX + logoSize, headerStartY + logoSize)
                    canvas.drawBitmap(logoBitmap, null, destRect, null)
                    textStartX = marginX + logoSize + 12f
                }

                canvas.drawText(
                    if (userSettings.lawFirmName.isNotBlank()) userSettings.lawFirmName else "Silva & Associados Advocacia",
                    textStartX,
                    headerStartY + 14f,
                    lawFirmHeaderPaint
                )

                val lawyerInfo = "${userSettings.userName} • OAB/${userSettings.oabUf} nº ${userSettings.oabNumber}"
                canvas.drawText(lawyerInfo, textStartX, headerStartY + 27f, lawFirmSubPaint)

                val contactInfo = "${userSettings.officeAddress} | Tel: ${userSettings.officePhone}"
                canvas.drawText(contactInfo, textStartX, headerStartY + 39f, lawFirmSubPaint)

                canvas.drawLine(marginX, headerStartY + 46f, pageWidth - marginX, headerStartY + 46f, accentLinePaint)
                canvas.drawLine(marginX, headerStartY + 49f, pageWidth - marginX, headerStartY + 49f, thinLinePaint)

                y = headerStartY + 68f
            } else {
                canvas.drawText("CONTADOR JURÍDICO PRO — AUDITORIA CONTÁBIL E JUDICIAL", marginX, headerStartY + 14f, lawFirmHeaderPaint)
                canvas.drawText("Sistema Especializado em Direito do Consumidor e Repetição do Indébito (Lei 9.099/95)", marginX, headerStartY + 27f, lawFirmSubPaint)
                canvas.drawLine(marginX, headerStartY + 35f, pageWidth - marginX, headerStartY + 35f, accentLinePaint)
                y = headerStartY + 55f
            }
        }

        fun drawFooter() {
            val footerY = pageHeight - 28f
            canvas.drawLine(marginX, footerY - 10f, pageWidth - marginX, footerY - 10f, thinLinePaint)
            
            val footerText = if (userSettings.useCustomLetterhead) {
                "${userSettings.lawFirmName} • OAB/${userSettings.oabUf} ${userSettings.oabNumber} • ${userSettings.userEmail} • Pág. $pageNumber"
            } else {
                "Documento Gerado via Contador Jurídico Pro • Art. 42 CDC • Pág. $pageNumber"
            }
            canvas.drawText(footerText, pageWidth / 2f, footerY, footerPaint)
        }

        fun startNewPage() {
            drawFooter()
            document.finishPage(page)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(pageInfo)
            canvas = page.canvas
            drawCustomLetterhead()
        }

        fun checkPageBreak(requiredSpace: Float) {
            if (y + requiredSpace > bottomMargin) {
                startNewPage()
            }
        }

        fun drawWrappedText(text: String, textPaint: Paint, indent: Float = 0f, spacing: Float = 15f) {
            val lines = wrapText(text, textPaint, maxTextWidth - indent)
            for (line in lines) {
                checkPageBreak(spacing)
                canvas.drawText(line, marginX + indent, y, textPaint)
                y += spacing
            }
        }

        val authorName = if (caseEntity.authorName.isNotBlank()) caseEntity.authorName else "João Paulo da Silva"
        val authorCpf = if (caseEntity.authorCpf.isNotBlank()) caseEntity.authorCpf else "123.456.789-00"
        val defendant = if (caseEntity.defendantName.isNotBlank()) caseEntity.defendantName else "Companhia Fornecedora S/A"
        val todayStr = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR")).format(Date())

        // -------------------------------------------------------------
        // A. LAUDO PERICIAL DE AUDITORIA & CÁLCULO DE JUROS
        // -------------------------------------------------------------
        if (exportType == ExportDocumentType.LAUDO_AUDITORIA_E_CALCULO) {
            drawCustomLetterhead("LAUDO PERICIAL CONTÁBIL")

            // Title Banner
            checkPageBreak(45f)
            canvas.drawText("RELATÓRIO TÉCNICO DE AUDITORIA & LIQUIDAÇÃO CONTÁBIL", pageWidth / 2f, y, titlePaint)
            y += 20f

            // Case Info Summary Card
            val summaryCardY = y
            val cardHeight = 75f
            val bgPaint = Paint().apply {
                color = Color.rgb(243, 246, 252)
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(RectF(marginX, summaryCardY, pageWidth - marginX, summaryCardY + cardHeight), 8f, 8f, bgPaint)
            canvas.drawRoundRect(RectF(marginX, summaryCardY, pageWidth - marginX, summaryCardY + cardHeight), 8f, 8f, thinLinePaint)

            var cardInnerY = summaryCardY + 18f
            canvas.drawText("CASO / AUTOS: ${caseEntity.title.ifBlank { "Auditoria #${caseEntity.id}" }}", marginX + 14f, cardInnerY, boldPaint)
            cardInnerY += 16f
            canvas.drawText("AUTOR(A): $authorName (CPF: $authorCpf)", marginX + 14f, cardInnerY, paint)
            cardInnerY += 16f
            canvas.drawText("RÉU / FORNECEDOR: $defendant  |  CATEGORIA: ${caseEntity.category.uppercase()}", marginX + 14f, cardInnerY, paint)
            cardInnerY += 16f
            canvas.drawText("DATA DA ANÁLISE: $todayStr  |  STATUS: ${caseEntity.status}", marginX + 14f, cardInnerY, paint)

            y = summaryCardY + cardHeight + 22f

            // 1. Diagnóstico do Abuso
            checkPageBreak(40f)
            canvas.drawText("1. DIAGNÓSTICO DA AUDITORIA & ABUSIVIDADES IDENTIFICADAS", marginX, y, sectionHeaderPaint)
            y += 18f

            val abuseDesc = if (caseEntity.identifiedAbuseSummary.isNotBlank()) {
                caseEntity.identifiedAbuseSummary
            } else {
                "Identificada cobrança indevida de encargos contratuais sem autorização expressa do consumidor ou divergência na aplicação de índices de correção."
            }
            drawWrappedText(abuseDesc, paint, indent = 8f)
            y += 16f

            // 2. Tabela Estruturada de Liquidação e Juros
            checkPageBreak(170f)
            canvas.drawText("2. MEMÓRIA DISCRIMINADA DE CÁLCULO E JUROS MORATÓRIOS", marginX, y, sectionHeaderPaint)
            y += 16f

            // Table Box
            val tableY = y
            val tableHeight = 150f
            canvas.drawRoundRect(RectF(marginX, tableY, pageWidth - marginX, tableY + tableHeight), 8f, 8f, bgPaint)
            canvas.drawRoundRect(RectF(marginX, tableY, pageWidth - marginX, tableY + tableHeight), 8f, 8f, thinLinePaint)

            // Header row of Table
            val headerBg = Paint().apply {
                color = Color.rgb(225, 235, 250)
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(RectF(marginX, tableY, pageWidth - marginX, tableY + 24f), 8f, 8f, headerBg)
            canvas.drawText("RUBRICA / PARÂMETRO", marginX + 12f, tableY + 16f, boldPaint)
            canvas.drawText("PERCENTUAL / ÍNDICE", marginX + 220f, tableY + 16f, boldPaint)
            canvas.drawText("VALOR APURADO (R$)", pageWidth - marginX - 140f, tableY + 16f, boldPaint)

            var rowY = tableY + 42f
            canvas.drawText("Valor Histórico Original", marginX + 12f, rowY, paint)
            canvas.drawText("Base Cobrada", marginX + 220f, rowY, paint)
            canvas.drawText("R$ %,.2f".format(caseEntity.historicalValue), pageWidth - marginX - 140f, rowY, paint)
            rowY += 18f

            val isDobra = caseEntity.isRepeticaoEmDobro || caseEntity.calculationType == "REPETICAO_DOBRO" || caseEntity.calculationType == "TELECOM_SERVICOS"
            if (isDobra) {
                canvas.drawText("Dobra Legal do Indébito (Art. 42 CDC)", marginX + 12f, rowY, boldPaint)
                canvas.drawText("Multiplicador 2.0x", marginX + 220f, rowY, paint)
                canvas.drawText("R$ %,.2f".format(caseEntity.historicalValue * 2), pageWidth - marginX - 140f, rowY, boldPaint)
                rowY += 18f
            }

            canvas.drawText("Correção Monetária Oficial", marginX + 12f, rowY, paint)
            canvas.drawText("Tabela INPC / TJ", marginX + 220f, rowY, paint)
            canvas.drawText("R$ %,.2f".format(caseEntity.inpcCorrection), pageWidth - marginX - 140f, rowY, paint)
            rowY += 18f

            canvas.drawText("Juros Moratórios Legais", marginX + 12f, rowY, paint)
            canvas.drawText("1% a.m. (${caseEntity.monthsCalculated} meses)", marginX + 220f, rowY, paint)
            canvas.drawText("R$ %,.2f".format(caseEntity.defaultInterest), pageWidth - marginX - 140f, rowY, paint)
            rowY += 22f

            // Total row
            canvas.drawLine(marginX + 8f, rowY - 6f, pageWidth - marginX - 8f, rowY - 6f, thinLinePaint)
            canvas.drawText("TOTAL ATUALIZADO (DANO MATERIAL)", marginX + 12f, rowY + 8f, boldPaint)
            val primaryTotalPaint = Paint(boldPaint).apply { color = Color.rgb(20, 70, 180); textSize = 11.5f }
            canvas.drawText("R$ %,.2f".format(caseEntity.subtotalUpdated), pageWidth - marginX - 140f, rowY + 8f, primaryTotalPaint)

            y = tableY + tableHeight + 25f

            // 3. Fundamentos Legais
            checkPageBreak(50f)
            canvas.drawText("3. FUNDAMENTAÇÃO JURÍDICA E SÚMULAS VINCULANTES", marginX, y, sectionHeaderPaint)
            y += 18f
            val sumulasText = "• Art. 42, parágrafo único, CDC e STJ EAREsp 676.608/RJ: Restituição em dobro do indébito;\n• Súmula 54 STJ: Os juros moratórios fluem a partir do evento danoso, em caso de responsabilidade extracontratual;\n• Súmula 43 STJ: Incide correção monetária sobre dívida por ato ilícito a partir da data do efetivo prejuízo."
            drawWrappedText(sumulasText, paint, indent = 8f, spacing = 15f)
            y += 30f

            // Assinatura do Perito / Advogado
            checkPageBreak(100f)
            val centerX = pageWidth / 2f
            if (signatureBitmap != null) {
                val sigWidth = 180f
                val aspectRatio = signatureBitmap.height.toFloat() / signatureBitmap.width.toFloat()
                val sigHeight = sigWidth * aspectRatio
                val destRect = RectF(centerX - sigWidth / 2f, y - sigHeight, centerX + sigWidth / 2f, y)
                canvas.drawBitmap(signatureBitmap, null, destRect, null)
            }
            canvas.drawLine(centerX - 130f, y, centerX + 130f, y, paint)
            y += 16f
            val centerBold = Paint(boldPaint).apply { textAlign = Paint.Align.CENTER }
            val centerRegular = Paint(paint).apply { textAlign = Paint.Align.CENTER }
            canvas.drawText(if (userSettings.userName.isNotBlank()) userSettings.userName else "Auditor Responsável", centerX, y, centerBold)
            y += 14f
            canvas.drawText("OAB/${userSettings.oabUf} nº ${userSettings.oabNumber} • Perícia e Cálculos Judiciais", centerX, y, centerRegular)
        }

        // -------------------------------------------------------------
        // B. GERAÇÃO DA PROCURAÇÃO AD JUDICIA
        // -------------------------------------------------------------
        if (exportType == ExportDocumentType.PROCURACAO_ONLY || exportType == ExportDocumentType.COMBO_PETICAO_E_PROCURACAO) {
            drawCustomLetterhead("PROCURAÇÃO")

            checkPageBreak(40f)
            canvas.drawText("PROCURAÇÃO AD JUDICIA ET EXTRA", pageWidth / 2f, y, titlePaint)
            y += 24f

            // OUTORGANTE
            checkPageBreak(40f)
            canvas.drawText("OUTORGANTE:", marginX, y, boldPaint)
            y += 18f
            val outorganteText = "$authorName, brasileiro(a), inscrito(a) no CPF/MF sob o nº $authorCpf, residente e domiciliado(a) no endereço informado para os autos da presente demanda."
            drawWrappedText(outorganteText, paint, indent = 10f)
            y += 16f

            // OUTORGADO
            checkPageBreak(40f)
            canvas.drawText("OUTORGADO(A):", marginX, y, boldPaint)
            y += 18f
            val lawyerName = if (userSettings.userName.isNotBlank()) userSettings.userName else "Dra. Aline Oliveira"
            val oabNumber = if (userSettings.oabNumber.isNotBlank()) userSettings.oabNumber else "123456"
            val oabUf = if (userSettings.oabUf.isNotBlank()) userSettings.oabUf else "SP"
            val officeAddr = if (userSettings.officeAddress.isNotBlank()) userSettings.officeAddress else "Av. Paulista, 1000 - São Paulo/SP"
            val firm = if (userSettings.lawFirmName.isNotBlank()) userSettings.lawFirmName else "Silva & Associados"

            val outorgadoText = "$lawyerName, advogado(a) devidamente inscrito(a) nos quadros da Ordem dos Advogados do Brasil sob o nº OAB/$oabUf $oabNumber, integrante de $firm, com escritório profissional estabelecido em $officeAddr, onde recebe notificações e intimações de estilo."
            drawWrappedText(outorgadoText, paint, indent = 10f)
            y += 16f

            // PODERES
            checkPageBreak(50f)
            canvas.drawText("PODERES:", marginX, y, boldPaint)
            y += 18f
            val poderesText = "Por este instrumento particular de mandato, o(a) OUTORGANTE confere ao(à) OUTORGADO(A) os mais amplos poderes para o foro em geral, conferidos pela cláusula 'ad judicia et extra', em qualquer Juízo, Instância ou Tribunal, Juizados Especiais Cíveis (JEC), Procon e órgãos administrativos, para defender seus legítimos direitos e interesses em face de $defendant."
            drawWrappedText(poderesText, paint, indent = 10f)
            y += 12f

            // PODERES ESPECÍFICOS
            val poderesEsp = "PODERES ESPECÍFICOS: Confere poderes expressos para transigir, acordar, firmar compromissos, desistir, receber citações e notificações, dar e receber quitação, levantar alvarás judiciais, RPV, precatórios e praticar todos os atos necessários ao fiel cumprimento deste mandato."
            drawWrappedText(poderesEsp, paint, indent = 10f)
            y += 24f

            // Local e Data
            checkPageBreak(40f)
            canvas.drawText("Local e Data: São Paulo/SP, $todayStr.", marginX + 10f, y, paint)
            y += 45f

            // Assinatura do Cliente / Outorgante
            checkPageBreak(90f)
            val centerX = pageWidth / 2f
            val clientSig = clientSignatureBitmap ?: signatureBitmap
            if (clientSig != null) {
                val sigWidth = 170f
                val aspectRatio = clientSig.height.toFloat() / clientSig.width.toFloat()
                val sigHeight = sigWidth * aspectRatio
                val destRect = RectF(centerX - sigWidth / 2f, y - sigHeight, centerX + sigWidth / 2f, y)
                canvas.drawBitmap(clientSig, null, destRect, null)
            }
            canvas.drawLine(centerX - 120f, y, centerX + 120f, y, paint)
            y += 16f

            val centerPaint = Paint(paint).apply { textAlign = Paint.Align.CENTER }
            val centerBoldPaint = Paint(boldPaint).apply { textAlign = Paint.Align.CENTER }
            canvas.drawText(authorName, centerX, y, centerBoldPaint)
            y += 14f
            canvas.drawText("OUTORGANTE (CPF: $authorCpf)", centerX, y, centerPaint)
            y += 30f

            if (exportType == ExportDocumentType.COMBO_PETICAO_E_PROCURACAO) {
                startNewPage()
            }
        }

        // -------------------------------------------------------------
        // C. GERAÇÃO DA PETIÇÃO INICIAL
        // -------------------------------------------------------------
        if (exportType == ExportDocumentType.PETICAO_ONLY || exportType == ExportDocumentType.COMBO_PETICAO_E_PROCURACAO) {
            if (exportType == ExportDocumentType.PETICAO_ONLY) {
                drawCustomLetterhead("PETIÇÃO INICIAL")
            }

            // --- Endereçamento ---
            checkPageBreak(70f)
            canvas.drawText("EXCELENTÍSSIMO SENHOR DOUTOR JUIZ DE DIREITO DO", marginX, y, sectionHeaderPaint)
            y += 18f
            canvas.drawText("JUIZADO ESPECIAL CÍVEL DA COMARCA", marginX, y, sectionHeaderPaint)
            y += 45f

            // --- Título da Ação ---
            checkPageBreak(35f)
            canvas.drawText("AÇÃO DECLARATÓRIA DE INEXISTÊNCIA DE DÉBITO C/C", pageWidth / 2f, y, titlePaint)
            y += 18f
            canvas.drawText("REPETIÇÃO DO INDÉBITO E INDENIZAÇÃO POR DANOS MORAIS", pageWidth / 2f, y, titlePaint)
            y += 35f

            // --- Qualificação das Partes ---
            val qualificação = if (userSettings.useCustomLetterhead) {
                "$authorName, pessoa física, inscrito(a) no CPF/MF sob o nº $authorCpf, residente e domiciliado(a) no endereço informado nos autos, por intermédio de seu advogado e procurador infra-assinado (${userSettings.userName}, inscrito na OAB/${userSettings.oabUf} sob o nº ${userSettings.oabNumber}), com poderes outorgados na procuração anexa, com escritório profissional em ${userSettings.officeAddress}, onde recebe intimações e notificações, vem, respeitosamente, à presença de Vossa Excelência, propor a presente AÇÃO em face de $defendant, com base no Código de Defesa do Consumidor e nos fundamentos fáticos e jurídicos a seguir aduzidos."
            } else {
                "$authorName, inscrito(a) no CPF sob o nº $authorCpf, residente e domiciliado(a) em [Endereço], por seu próprio direito (Juizados Especiais Cíveis - Lei 9.099/95), vem propor a presente AÇÃO em face de $defendant, pelos fatos e fundamentos a seguir delineados."
            }
            
            drawWrappedText(qualificação, paint, indent = 0f)
            y += 18f

            // --- 1. DOS FATOS ---
            checkPageBreak(35f)
            canvas.drawText("1. DOS FATOS", marginX, y, boldPaint)
            y += 22f
            val fatos = caseEntity.fatosText.ifBlank { caseEntity.description.ifBlank { "Sem descrição dos fatos." } }
            drawWrappedText(fatos, paint, indent = 10f)
            y += 18f

            // --- 2. DOS FUNDAMENTOS JURÍDICOS ---
            checkPageBreak(35f)
            canvas.drawText("2. DOS FUNDAMENTOS JURÍDICOS", marginX, y, boldPaint)
            y += 22f
            val fundamentos = caseEntity.fundamentosText.ifBlank { caseEntity.legalBasis.ifBlank { "Fundamentado no Código de Defesa do Consumidor (Art. 6º, VI, Art. 14 e Art. 42)." } }
            drawWrappedText(fundamentos, paint, indent = 10f)
            y += 18f

            // --- 3. MEMÓRIA DISCRIMINADA DE CÁLCULO ---
            checkPageBreak(50f)
            canvas.drawText("3. DA MEMÓRIA DISCRIMINADA DE CÁLCULO", marginX, y, boldPaint)
            y += 22f
            
            val calcIntro = when (caseEntity.calculationType) {
                "REPETICAO_DOBRO" -> "Apresenta-se a liquidação com aplicação da Repetição do Indébito em Dobro (Art. 42, parágrafo único do CDC e EAREsp 676.608/STJ), acrescida de correção monetária (INPC) e juros moratórios de 1% a.m.:"
                "EMPRESTIMO_BANCARIO" -> "Apresenta-se o recálculo do contrato bancário com expurgo da taxa abusiva praticada (limitação à taxa média BACEN - Súmula 297/STJ), repetindo-se o excesso em dobro:"
                "TELECOM_SERVICOS" -> "Apresenta-se a liquidação dos valores cobrados indevidamente por Serviços de Valor Adicionado (SVA) e pacotes não solicitados, repetidos em dobro (Art. 42 do CDC):"
                else -> "Apresenta-se a liquidação dos valores indevidamente exigidos e pagos, com incidência de correção monetária pelo INPC e juros moratórios de 1% ao mês desde a data do evento danoso (Súmulas 43 e 54 do STJ):"
            }
            drawWrappedText(calcIntro, paint, indent = 10f)
            y += 12f

            val calcBoxY = y
            val calcBoxHeight = if (caseEntity.isRepeticaoEmDobro || caseEntity.calculationType == "EMPRESTIMO_BANCARIO") 110f else 90f
            
            val rectPaint = Paint().apply {
                color = Color.rgb(245, 248, 255)
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(RectF(marginX + 10f, calcBoxY, pageWidth - marginX - 10f, calcBoxY + calcBoxHeight), 8f, 8f, rectPaint)
            canvas.drawRoundRect(RectF(marginX + 10f, calcBoxY, pageWidth - marginX - 10f, calcBoxY + calcBoxHeight), 8f, 8f, thinLinePaint)

            val calcIndent = 25f
            var internalY = calcBoxY + 20f

            canvas.drawText(String.format("• Valor Histórico Cobrado Indevidamente: R$ %,.2f", caseEntity.historicalValue), marginX + calcIndent, internalY, paint)
            internalY += 18f
            if (caseEntity.isRepeticaoEmDobro || caseEntity.calculationType == "REPETICAO_DOBRO" || caseEntity.calculationType == "TELECOM_SERVICOS") {
                canvas.drawText(String.format("• Dobra Legal do Indébito (Art. 42 CDC): 2x = R$ %,.2f", caseEntity.historicalValue * 2), marginX + calcIndent, internalY, boldPaint)
                internalY += 18f
            } else if (caseEntity.calculationType == "EMPRESTIMO_BANCARIO") {
                canvas.drawText(String.format("• Diferença Excesso Taxa Contrato vs BACEN: R$ %,.2f (Dobra 2x = R$ %,.2f)", caseEntity.historicalValue, caseEntity.historicalValue * 2), marginX + calcIndent, internalY, boldPaint)
                internalY += 18f
            }
            canvas.drawText(String.format("• Atualização Monetária (Índice INPC): R$ %,.2f", caseEntity.inpcCorrection), marginX + calcIndent, internalY, paint)
            internalY += 18f
            canvas.drawText(String.format("• Juros Moratórios Legais (1%% a.m. - %d meses): R$ %,.2f", caseEntity.monthsCalculated, caseEntity.defaultInterest), marginX + calcIndent, internalY, paint)
            internalY += 20f
            canvas.drawText(String.format("TOTAL DO DANO MATERIAL LIQUIDADO: R$ %,.2f", caseEntity.subtotalUpdated), marginX + calcIndent, internalY, boldPaint)

            y = calcBoxY + calcBoxHeight + 25f

            // --- 4. DOS PEDIDOS ---
            checkPageBreak(40f)
            canvas.drawText("4. DOS PEDIDOS E REQUERIMENTOS", marginX, y, boldPaint)
            y += 22f
            
            drawWrappedText("Ante todo o exposto, respeitosamente requer a Vossa Excelência:", paint, indent = 10f)
            y += 10f
            
            val pedidosDefault = mutableListOf(
                "a) A juntada da procuração anexa e documentos constitutivos;",
                "b) A citação e intimação da Requerida para comparecer à audiência de conciliação e apresentar defesa, sob pena de revelia e confissão;",
                "c) A inversão do ônus da prova, com fulcro no Artigo 6º, inciso VIII do Código de Defesa do Consumidor;",
                "d) A total procedência dos pedidos para condenar a Requerida à restituição do dano material no valor atualizado de R$ %,.2f;".format(caseEntity.subtotalUpdated)
            )
            if (caseEntity.suggestedMoralDamages > 0) {
                pedidosDefault.add("e) A condenação da Requerida ao pagamento de indenização a título de Danos Morais no montante de R$ %,.2f;".format(caseEntity.suggestedMoralDamages))
            }
            
            val pedidos = caseEntity.pedidosText.ifBlank { pedidosDefault.joinToString("\n") }
            drawWrappedText(pedidos, paint, indent = 20f)
            y += 18f

            val totalValue = caseEntity.subtotalUpdated + caseEntity.suggestedMoralDamages
            checkPageBreak(50f)
            canvas.drawText("Dá-se à causa o valor de R$ %,.2f.".format(totalValue), marginX, y, boldPaint)
            y += 35f
            
            // --- Fechamento e Assinatura ---
            checkPageBreak(130f)
            canvas.drawText("Nestes termos,", marginX, y, paint)
            y += 18f
            canvas.drawText("Pede e espera deferimento.", marginX, y, paint)
            y += 50f
            
            val centerX = pageWidth / 2f
            if (signatureBitmap != null) {
                val sigWidth = 200f
                val aspectRatio = signatureBitmap.height.toFloat() / signatureBitmap.width.toFloat()
                val sigHeight = sigWidth * aspectRatio
                val destRect = RectF(centerX - sigWidth / 2f, y - sigHeight, centerX + sigWidth / 2f, y)
                canvas.drawBitmap(signatureBitmap, null, destRect, null)
            }
            canvas.drawLine(centerX - 130f, y, centerX + 130f, y, paint)
            y += 18f

            val centerPaint = Paint(paint).apply { textAlign = Paint.Align.CENTER }
            val centerBoldPaint = Paint(boldPaint).apply { textAlign = Paint.Align.CENTER }

            if (userSettings.useCustomLetterhead) {
                canvas.drawText(userSettings.userName, centerX, y, centerBoldPaint)
                y += 15f
                canvas.drawText("Advogado(a) • OAB/${userSettings.oabUf} nº ${userSettings.oabNumber}", centerX, y, centerPaint)
                y += 14f
                canvas.drawText(userSettings.lawFirmName, centerX, y, centerPaint)
            } else {
                canvas.drawText(authorName, centerX, y, centerBoldPaint)
                y += 15f
                canvas.drawText("Requerente (Jus Postulandi - Art. 9º, Lei 9.099/95)", centerX, y, centerPaint)
            }
        }

        drawFooter()
        document.finishPage(page)

        val pdfDir = File(context.cacheDir, "petitions")
        if (!pdfDir.exists()) pdfDir.mkdirs()
        
        val prefix = when (exportType) {
            ExportDocumentType.PETICAO_ONLY -> "Peticao"
            ExportDocumentType.PROCURACAO_ONLY -> "Procuracao"
            ExportDocumentType.COMBO_PETICAO_E_PROCURACAO -> "Peticao_e_Procuracao"
            ExportDocumentType.LAUDO_AUDITORIA_E_CALCULO -> "Laudo_Auditoria_Calculo"
        }
        val file = File(pdfDir, "${prefix}_${caseEntity.id}_${System.currentTimeMillis()}.pdf")
        val outputStream = FileOutputStream(file)
        document.writeTo(outputStream)
        document.close()
        outputStream.close()

        return file
    }

    fun generatePetitionPdf(
        context: Context,
        caseEntity: CaseEntity,
        userSettings: UserSettings = UserSettings(),
        signatureBitmap: Bitmap? = null
    ): File {
        return generateLegalDocument(
            context = context,
            caseEntity = caseEntity,
            userSettings = userSettings,
            signatureBitmap = signatureBitmap,
            clientSignatureBitmap = signatureBitmap,
            exportType = ExportDocumentType.PETICAO_ONLY
        )
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

    fun sharePdfFile(context: Context, file: File, title: String = "Compartilhar Documento Jurídico PDF") {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, title))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
