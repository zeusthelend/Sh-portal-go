# Shopping Hospitalar — Portal Corporativo Android

Aplicativo Android nativo desenvolvido em **Kotlin** e **Jetpack Compose** (Material Design 3), projetado para gestão corporativa, acompanhamento de DAVs (Documento Auxiliar de Venda), comunicação interna, reuniões e agendas operacionais do **Shopping Hospitalar**.

## Principais Módulos & Funcionalidades

1. **Início (Dashboard)**
   - Saudação personalizada com perfil do colaborador logado.
   - Indicadores em tempo real: Colaboradores online, DAVs em aberto, Reuniões agendadas e Lembretes pendentes.
   - Banner de alerta para DAVs com vencimento expirado.
   - Mural de Avisos Importantes e Próximas Reuniões.

2. **Gestão de DAVs (Documento Auxiliar de Venda)**
   - Filtros dinâmicos por categoria: *Todos*, *Geral*, *Prefeitura* e *Reservado*.
   - Busca instantânea por número do DAV, razão social do cliente ou vendedor.
   - Status do documento: *Aberto*, *Em andamento*, *Concluído* e *Desistiu*.
   - Totalizador financeiro em Reais (BRL) para propostas e faturamentos.
   - Modal para cadastro rápido de novo DAV e alteração rápida de status.

3. **Chat Corporativo**
   - Canais de equipe (*Equipe Geral*, *Vendas & Licitações*) e conversas diretas.
   - Troca de mensagens corporativas com timestamps e marcação de remetente.
   - Rolagem automática para mensagens recentes e suporte a anexos.

4. **Quadro de Colaboradores (Equipe)**
   - Lista completa com cargos, departamentos (*Vendas*, *Estoque*, *Administrativo*, *Diretoria*) e presença em tempo real (*Online*, *Ausente*, *Ocupado*, *Offline*).
   - Acesso a mensagens diretas e gestão de primeiro acesso com PIN.

5. **Reuniões & Alinhamentos**
   - Agenda corporativa com horário, duração e link de sala virtual ou local físico.
   - Agendamento de novas reuniões diretamente pelo aplicativo.

6. **Agendas Operacionais (Lembretes)**
   - **Agenda do Estoque**: Controle de conferência de insumos, separação de lotes cirúrgicos e expedição.
   - **Agenda Administrativa**: Emissão de notas fiscais, relatórios para auditoria e prestação de contas.
   - Marcação de conclusão com riscado e contadores de pendências.

7. **Minhas Vendas & Faturamento**
   - Métricas de conversão de DAVs concluídos vs. abertos.
   - Total faturado consolidado.
   - Ranking dos principais hospitais e clínicas atendidos.

8. **Perfil do Usuário & Preferências**
   - Alteração de status de presença e informações de contato.
   - Controle de notificações push e informações corporativas.

## Arquitetura Android

- **Linguagem**: Kotlin 2.1
- **UI Toolkit**: Jetpack Compose com Material 3 (M3)
- **Design System**: Paleta corporativa Shopping Hospitalar (`#3B1E54`, `#4A154B`, `#E94560`, `#10B981`)
- **Navegação**: ModalNavigationDrawer + NavigationBar com suporte a BackHandler
- **Arquitetura**: MVVM (Model-View-ViewModel) com StateFlow e Clean Architecture
- **Ícone do App**: Adaptive Launcher Icon personalizado com o emblema da cruz hospitalar
