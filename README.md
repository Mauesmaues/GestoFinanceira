💰 Gestão Financeira App
Kotlin • Jetpack Compose • MVVM

Aplicativo acadêmico desenvolvido para a disciplina de Desenvolvimento de Aplicativos Móveis, com o objetivo de criar um sistema simples de gestão financeira.
O app permite registrar entradas e saídas, visualizar o saldo total e acompanhar transações em um único lugar.

✨ Funcionalidades

Registro de Transações: Adicione receitas (entradas) e despesas (saídas) rapidamente.

Visualização de Saldo: Acompanhe o saldo total em tempo real na tela principal.

Cotação de Moeda (API): Exibe o saldo convertido em Dólar (USD), utilizando cotação atual obtida via API externa.

Listagem de Transações: Veja listas separadas de todas as suas receitas e despesas.

CRUD Completo:

Create: Criar novas transações

Read: Ler e exibir transações

Update: Editar valores existentes

Delete: Excluir transações

Compartilhamento de Dados: Envie seu resumo financeiro (BRL e USD) para qualquer app (WhatsApp, Email, etc.) usando o compartilhamento nativo do Android.

📸 Capturas de Tela

(Adicione aqui as imagens do seu aplicativo)
Exemplo:

Tela principal exibindo saldo, inputs e lista de transações.

🛠️ Tecnologias e Arquitetura

Este projeto segue as práticas modernas recomendadas pelo Google para desenvolvimento Android.

Linguagem

Kotlin

Interface de Usuário

Jetpack Compose – toolkit declarativo moderno para construção de UI nativa.

Arquitetura

MVVM (Model–View–ViewModel)
Separação clara entre lógica de negócio e interface, garantindo organização, testabilidade e manutenção facilitada.

Gerenciamento de Estado

ViewModel: Mantém o estado da UI entre mudanças de configuração.

StateFlow: Fornece dados reativos da camada ViewModel para a UI.

Banco de Dados Local — Room

@Entity: Modelagem das tabelas Entrada e Saida

@Dao: Definição de operações CRUD

@TypeConverter: Tratamento de tipos personalizados (ex.: Date)

Consumo de API

Retrofit: Realiza chamadas HTTP para obter a cotação USD/BRL da AwesomeAPI.

Gson: Conversão automática de JSON para objetos Kotlin.

Assincronismo

Kotlin Coroutines: Executa operações de banco e rede sem bloquear a thread principal.

📁 Estrutura de Pacotes
• data/
   ├─ entities (Entrada, Saida)
   ├─ dao (TransacaoDao)
   ├─ AppDatabase
   └─ Converters

• api/
   ├─ CotacaoApiService
   ├─ modelos JSON
   └─ RetrofitClient

• repository/
   └─ FinanceiroRepository
      → Centraliza acesso aos dados locais e remotos

• ui/
   └─ telas e componentes Jetpack Compose
      ex: BalanceScreen.kt

▶️ Como Executar o Projeto

Clone este repositório:

git clone https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git


Abra o projeto no Android Studio.

Aguarde o Gradle sincronizar as dependências.

Execute o app em um emulador ou dispositivo físico
