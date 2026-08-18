package com.example.util

import com.example.data.db.CaseEntity

object LegalCalculationEngine {

    enum class CalculationMode(val title: String, val description: String, val legalArticle: String) {
        PADRAO(
            title = "Cálculo Padrão Atualizado",
            description = "Correção Monetária (INPC) + Juros Legais Simples (1% a.m.)",
            legalArticle = "Art. 389 do Código Civil e Súmulas 43/54 do STJ"
        ),
        REPETICAO_DOBRO(
            title = "Repetição do Indébito em Dobro (2x)",
            description = "Devolução em dobro da quantia indevidamente cobrada e paga",
            legalArticle = "Art. 42, Parágrafo Único do CDC e EAREsp 676.608/STJ"
        ),
        EMPRESTIMO_BANCARIO(
            title = "Revisional de Juros Bancários / Empréstimo",
            description = "Recálculo expurgando juros abusivos acima da taxa média do BACEN",
            legalArticle = "Súmula 297/STJ e Recurso Especial 1.061.530/RS"
        ),
        TELECOM_SERVICOS(
            title = "Telecom & Serviços de Terceiros",
            description = "Repetição em dobro de SVA (Serviços de Valor Adicionado) não contratados",
            legalArticle = "Art. 39, III e Art. 42 do CDC, Resolução Anatel 632"
        )
    }

    data class CalculationResult(
        val historicalBase: Double,
        val multiplier: Double, // 1.0 or 2.0 (Repetição em dobro)
        val principalRecoverable: Double,
        val inpcCorrection: Double,
        val defaultInterest: Double,
        val bankAbusiveDifference: Double = 0.0,
        val totalRecoverable: Double,
        val suggestedMoralDamages: Double,
        val summaryNote: String
    )

    fun calculate(
        historicalValue: Double,
        mode: CalculationMode,
        months: Int = 12,
        bankContractRate: Double = 8.5, // % ao mês
        bacenAverageRate: Double = 2.1,  // % ao mês
        customInpcRatePercent: Double = 5.2, // % acumulado no período
        customInterestRatePercentPerMonth: Double = 1.0 // % ao mês
    ): CalculationResult {
        when (mode) {
            CalculationMode.REPETICAO_DOBRO -> {
                val principalInDouble = historicalValue * 2.0
                val inpcVal = principalInDouble * (customInpcRatePercent / 100.0)
                val jurosVal = principalInDouble * (customInterestRatePercentPerMonth * months / 100.0)
                val total = principalInDouble + inpcVal + jurosVal
                val moral = if (historicalValue > 1000) 5000.0 else 3000.0

                return CalculationResult(
                    historicalBase = historicalValue,
                    multiplier = 2.0,
                    principalRecoverable = principalInDouble,
                    inpcCorrection = inpcVal,
                    defaultInterest = jurosVal,
                    totalRecoverable = total,
                    suggestedMoralDamages = moral,
                    summaryNote = "Aplicação da Repetição em Dobro (Art. 42 do CDC) sobre o valor histórico de R$ %.2f (Total base 2x = R$ %.2f).".format(historicalValue, principalInDouble)
                )
            }

            CalculationMode.EMPRESTIMO_BANCARIO -> {
                // Cálculo de juros abusivos: diferença entre taxa pactuada e média de mercado BACEN
                val spreadRate = maxOf(0.0, bankContractRate - bacenAverageRate)
                val totalCompoundContract = historicalValue * Math.pow(1.0 + (bankContractRate / 100.0), months.toDouble())
                val totalCompoundBacen = historicalValue * Math.pow(1.0 + (bacenAverageRate / 100.0), months.toDouble())
                val abusiveOvercharge = maxOf(0.0, totalCompoundContract - totalCompoundBacen)
                
                // Repetição em dobro do excesso cobrado pelo banco
                val principalRecoverable = abusiveOvercharge * 2.0
                val inpcVal = principalRecoverable * (customInpcRatePercent / 100.0)
                val jurosVal = principalRecoverable * (customInterestRatePercentPerMonth * months / 100.0)
                val total = principalRecoverable + inpcVal + jurosVal
                val moral = 6000.0 // Abuso bancário com enriquecimento sem causa

                return CalculationResult(
                    historicalBase = abusiveOvercharge,
                    multiplier = 2.0,
                    principalRecoverable = principalRecoverable,
                    inpcCorrection = inpcVal,
                    defaultInterest = jurosVal,
                    bankAbusiveDifference = abusiveOvercharge,
                    totalRecoverable = total,
                    suggestedMoralDamages = moral,
                    summaryNote = "Taxa contratada (%.2f%% a.m.) superior à taxa média do BACEN (%.2f%% a.m.). Excesso apurado: R$ %.2f.".format(bankContractRate, bacenAverageRate, abusiveOvercharge)
                )
            }

            CalculationMode.TELECOM_SERVICOS -> {
                // SVA e tarifas não contratadas - restituição em dobro com jurisprudência pacificada
                val principalInDouble = historicalValue * 2.0
                val inpcVal = principalInDouble * (customInpcRatePercent / 100.0)
                val jurosVal = principalInDouble * (customInterestRatePercentPerMonth * months / 100.0)
                val total = principalInDouble + inpcVal + jurosVal
                val moral = 4000.0 // Desvio produtivo e desgaste reiterado

                return CalculationResult(
                    historicalBase = historicalValue,
                    multiplier = 2.0,
                    principalRecoverable = principalInDouble,
                    inpcCorrection = inpcVal,
                    defaultInterest = jurosVal,
                    totalRecoverable = total,
                    suggestedMoralDamages = moral,
                    summaryNote = "Serviços de Valor Adicionado (SVA), pacotes de dados avulsos ou seguros não solicitados na fatura telefônica."
                )
            }

            CalculationMode.PADRAO -> {
                val principal = historicalValue
                val inpcVal = principal * (customInpcRatePercent / 100.0)
                val jurosVal = principal * (customInterestRatePercentPerMonth * months / 100.0)
                val total = principal + inpcVal + jurosVal
                val moral = if (historicalValue > 2000) 4000.0 else 2500.0

                return CalculationResult(
                    historicalBase = historicalValue,
                    multiplier = 1.0,
                    principalRecoverable = principal,
                    inpcCorrection = inpcVal,
                    defaultInterest = jurosVal,
                    totalRecoverable = total,
                    suggestedMoralDamages = moral,
                    summaryNote = "Liquidação de débito com atualização monetária e juros moratórios legais."
                )
            }
        }
    }
}
