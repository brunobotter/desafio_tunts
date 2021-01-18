# Desafio para vaga de estagio na Tunts
# Projeto para edição de uma planilha do google sheets

## Tecnologias utilizadas

- spring boot
- Java 11
- Maven
- google-api-client
- google-oauth-client-jetty
- google-api-services-sheets


### O projeto realiza os calculos e insere as devidas informações na planilha

### Para devido funcionamento e necessario utilizar as seguintes ferramentas
- Link da planilha publica https://docs.google.com/spreadsheets/d/1SGp1_gxEcYxCh31-a-OO8g7AMXuy30AH1bMaUVWCTjU/edit#gid=0
- Java 11
- Adicionar o credential.json a pasta resouces
- As seguintes bibliotecas no maven
```json
<dependency>
<groupId>com.google.api-client</groupId>
<artifactId>google-api-client</artifactId>
<version>1.23.0</version>
</dependency>
<dependency>
<groupId>com.google.oauth-client</groupId>
<artifactId>google-oauth-client-jetty</artifactId>
<version>1.23.0</version>
</dependency>
<dependency>
<groupId>com.google.apis</groupId>
<artifactId>google-api-services-sheets</artifactId>
<version>v4-rev493-1.23.0</version>
</dependency>
```