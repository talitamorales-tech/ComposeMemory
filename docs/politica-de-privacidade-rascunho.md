# Revisão da Política de Privacidade — MemoryLandGame

Texto atualizado para revisão: [Política de Privacidade](politica-de-privacidade.md).

A página da política foi publicada com autorização de Talita em 21 de outubro de 2026: https://memorylandgame-privacidade-talita.exactrat34.chatgpt.site/. Acesso público sem login verificado. Somente a página foi publicada no Sites; a visibilidade do GitHub do jogo não foi alterada. Nenhum pacote foi enviado ao Google Play nesta revisão.

## Informações confirmadas por Talita

- Responsável: Talita Morales.
- Contato público: talita.memoryland@gmail.com.
- Público pretendido: crianças pequenas, inclusive menores de 4 anos, crianças maiores e adultos. Isso não substitui a classificação indicativa do Google Play.
- Os anúncios exibidos são anúncios de teste e devem ser mantidos nesta etapa.
- O Firebase e o Crashlytics já haviam sido configurados no projeto.
- As conversas de suporte serão guardadas para acompanhamento e histórico, enquanto necessárias para essas finalidades.

## Alterações preparadas no aplicativo

- Pergunta neutra de ano de nascimento, sem resposta preenchida, com opção de continuar sem informar. Todos continuam podendo jogar.
- O ano de nascimento não é gravado ou enviado. Somente o grupo de idade permanece na memória da sessão, inclusive durante rotações.
- Como o ano não revela a data exata de aniversário, pessoas que ainda possam ter menos de 18 anos são tratadas de forma protetiva.
- Anúncios carregados apenas depois da resposta ou da opção de não informar.
- Tratamento infantil e limite de conteúdo G em todas as solicitações, inclusive para adultos, antes da inicialização do AdMob.
- Permissões de identificador de publicidade e sinais de publicidade do Android removidas do manifesto final.
- Crianças, adolescentes e pessoas de idade desconhecida recebem somente banner; anúncios de tela inteira ficam desativados para esses grupos.
- Envio automático de relatórios Crashlytics desativado nesta versão; relatórios pendentes são descartados na abertura. A integração continua no projeto e pode guardar falhas localmente. Isso não apaga relatórios enviados por versões anteriores.
- Política disponível sem conexão pela tela inicial de idade e pelo menu do jogo.
- Código de versão incrementado de 3 para 4 para diferenciar o novo pacote. Confirmar no Console se esse número ainda não foi utilizado antes do envio.

## O que falta para disponibilizar o teste fechado

1. Conferir o novo pacote antes de enviá-lo; a publicação da política foi autorizada.
2. Preencher no Play Console a URL pública já publicada: https://memorylandgame-privacidade-talita.exactrat34.chatgpt.site/.
3. Concluir as declarações de Conteúdo do app, Público-alvo, Segurança dos dados, Classificação de conteúdo e demais tarefas pendentes do painel, usando o comportamento desta nova versão.
4. Conferir os países e regiões e se todos os 14 endereços previstos estão nas listas selecionadas. Cadastrar o e-mail não equivale à aceitação do teste pelo participante.
5. Enviar a nova versão para a faixa de teste fechado, acompanhar a revisão e, quando disponível, compartilhar o link de participação.

Os ajustes locais não atualizam automaticamente o jogo já instalado a partir do Google Play. A compatibilidade com as políticas e a aprovação final dependem da configuração e da avaliação completas do aplicativo, não apenas destas alterações.

## Verificações realizadas

- Compilação de depuração e testes unitários concluídos, incluindo quatro testes dos limites de ano de nascimento e das proteções de idade desconhecida.
- Quatro testes de dispositivo concluídos no emulador Pixel 9 Pro, sem falhas: incluem verificações dos parâmetros do AdMob, permissões removidas, envio automático de falhas desativado, abertura da política antes da idade e acesso ao jogo por criança de 3 anos e por pessoa que não informa a idade.
- Pacote de lançamento `app/build/outputs/bundle/release/app-release.aab` gerado com código de versão 4; validação de assinatura concluída. O texto da política dentro do pacote corresponde ao documento gerado.
- Página preparada em `sites/memoryland-privacidade/dist/index.html` e publicada no Sites. A resposta HTTP anônima foi 200, sem cookies ou autenticação, e a página foi conferida no navegador. O pacote publicado contém somente HTML e o manifesto de hospedagem, sem código do jogo.

## Referências consultadas

- [Políticas para famílias](https://support.google.com/googleplay/android-developer/answer/9893335?hl=pt-BR).
- [Tratamento infantil no Google Mobile Ads](https://developers.google.com/admob/android/targeting).
- [SDKs de anúncios autocertificados](https://support.google.com/googleplay/android-developer/answer/12955712): a lista consultada inclui Google AdMob 19.0.0 ou superior; este projeto utiliza 24.0.0.
- [Controle de envio de relatórios Crashlytics](https://firebase.google.com/docs/crashlytics/android/customize-crash-reports).
- [Política de dados do Google Play](https://support.google.com/googleplay/android-developer/answer/10144311?hl=pt-BR).

Para atualizar o texto dentro do aplicativo depois de editar o documento, executar `python3 scripts/sync_privacy_policy.py` e gerar um novo pacote.
