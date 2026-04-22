\# 🎵 JMusicBox



Um player de música em \*\*Java (console)\*\* com arquitetura \*\*MVC\*\* e integração com \*\*MySQL\*\*.

Projeto desenvolvido para estudo de \*\*POO, JDBC e organização de código\*\*.



\---



\## 📌 Funcionalidades



\* 🎧 Reprodução de músicas (`.wav`)

\* 📂 Cadastro de músicas no banco

\* 📋 Listagem de músicas

\* 🗑️ Remoção de músicas

\* 🎼 Criação de playlists

\* 🔗 Associação de músicas às playlists

\* 💾 Persistência com MySQL



\---



\## 🧱 Tecnologias Utilizadas



\* Java (JDK 8+)

\* MySQL

\* JDBC (MySQL Connector/J)

\* FFmpeg (conversão de áudio)



\---



\## 📂 Estrutura do Projeto



```

JMusicBox/

├── src/

│   ├── model/

│   ├── dao/

│   ├── controller/

│   ├── view/

│   ├── player/

│   └── database/

│

├── database/

│   └── schema.sql

│

├── lib/

│   └── mysql-connector-j.jar

│

└── README.md

```



\---



\## ⚙️ Configuração do Banco de Dados



1\. Instale o MySQL

2\. Execute o arquivo:



```

database/jmusicbox.sql

```



Ou via terminal:



```

mysql -u root -p < database/ jmusicbox.sql

```



3\. O banco `JMusicBox` será criado com as tabelas:



\* `musicas`

\* `playlists`

\* `playlist\_musicas`



\---



\## 🔌 Configuração da Conexão



No arquivo `Conexao.java`, ajuste:



```java

private static final String URL = "jdbc:mysql://localhost:3306/JMusicBox";

private static final String USER = "root";

private static final String PASSWORD = "sua\_senha";

```



\---



\## ▶️ Como Executar



\### Compilar:



```

javac -d bin src/\*\*/\*.java

```



\### Executar:



```

java -cp bin main.Main

```



Ou via JAR:



```

java -jar PlayerMusica.jar

```



\---



\## 🎵 Sobre os Arquivos de Áudio



O Java não possui suporte nativo a MP3 usando `javax.sound.sampled`.

Por isso, é necessário converter para `.wav`.



\---



\## 🔄 Converter MP3 para WAV



Instale o FFmpeg e execute:



```

ffmpeg -i musica.mp3 musica.wav

```



\---



\## 🧰 Instalação do FFmpeg (Resumo)



\### Windows



1\. Baixe em: https://ffmpeg.org/download.html

2\. Extraia e adicione `bin` ao PATH

3\. Teste:



```

ffmpeg -version

```



\### Linux



```

sudo apt install ffmpeg

```



\### Mac



```

brew install ffmpeg

```



\---



\## ⚠️ Observações



\* Apenas arquivos `.wav` são suportados na reprodução

\* O caminho do arquivo deve ser válido no sistema

\* O projeto é focado em aprendizado (não produção)



\---



\## 🚀 Possíveis Melhorias



\* Interface gráfica (JavaFX/Swing)



\---



\## 📚 Objetivo do Projeto



Este projeto foi desenvolvido para praticar:



\* Arquitetura MVC

\* Integração com banco de dados (JDBC)

\* Manipulação de áudio em Java

\* Organização de código



\---



\## 👨‍💻 Autor



Rodrigo Carvalho Machado.



\---



