# Evidências da execução manual

Capturas fornecidas em 25/10/2026 durante a execução da Calculadora de Vendas no Windows. O projeto correspondente está aberto no Apache NetBeans IDE 25 ao fundo. Os arquivos foram preservados sem recorte ou alteração visual.

| Arquivo | Cenário e resultado observado |
| --- | --- |
| 01_janela_principal.png | Janela inicial com campo da venda e botão Calcular total. |
| 02_venda_sem_desconto.png | Venda de R$ 400,00; desconto 0%; total R$ 400,00. |
| 03_venda_no_limite.png | Venda de R$ 500,00; desconto 0%; total R$ 500,00. |
| 04_dialogo_desconto.png | Após venda de R$ 600,00, diálogo de percentual com 10 informado antes da confirmação. |
| 05_resultado_com_desconto.png | Venda de R$ 600,00; desconto 10%, equivalente a R$ 60,00; total R$ 540,00. |
| 06_validacao_venda.png | Após entrada abc, aviso para informar um número válido. |
| 07_validacao_desconto.png | Após percentual 101, aviso de que o desconto deve estar entre 0 e 100%. |

As capturas mostram o estado visível de cada cenário; a sequência foi acompanhada durante a execução. O log dos testes automatizados complementa a verificação das regras e inclui o cancelamento.
