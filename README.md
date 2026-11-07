# nassauTicket

## Descrição
nassauTicket é um projeto que se consiste em um sistema de atendimento ao cliente.

### AVISO
O uso de qualquer ferramenta de Inteligência Artificial foi utilizando somente para auxiliar no tempo de desenvolvimento e entrega de sprints.

## Membros
- Carlos Vinicius - 01906085 - Scrum Master & Desenvolvedor;
- Anderson Ricardo - 01882762 - Testador;
- Pablo Leone - 01811660 - Testador;
- Lucas Vaz - 01928615 - Documentador.

## Tecnologias
- HTML;
- CSS;
- JavaScript;
- Spring Boot;
- MySQL.

## Login e sessão

O backend usa o MySQL configurado em `backend/src/main/resources/application.properties`.
Inicie-o com `cd backend && ./mvnw spring-boot:run`. Para abrir a interface, sirva a
pasta `frontend` em `http://localhost` (por exemplo, com a extensão Live Server do VS
Code); não abra os arquivos diretamente como `file://`.

Cadastro e login de clientes e atendentes criam uma sessão com validade de 30 dias.
O navegador guarda o token em cookie `HttpOnly`, e o backend persiste somente o hash
do token no banco. Ao voltar ao site, o perfil autenticado é enviado ao painel
correspondente. O botão **Sair** encerra a sessão no servidor e remove o cookie.

## Geração de códigos de senha

Envie `POST /senha/PRIORITARIA`, `POST /senha/GERAL` ou `POST /senha/EXAME` para
gerar um código no formato `YYMMDD-PPSSS` (por exemplo, `261107-PR001`). A sequência
é compartilhada entre os três tipos, reinicia a cada dia e fica limitada a 999
senhas por dia. O contador diário é persistido no MySQL para manter a numeração
mesmo após reiniciar o backend, e cada emissão é salva na tabela `senhas`.

Em bancos já existentes com as tabelas `senhasPrioritarias`, `senhasGerais` e
`senhasExames`, a atualização cria a nova tabela, mas não migra automaticamente
os registros antigos.