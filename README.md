# Code Clone Refactoring

L’obbiettivo del progetto è la creazione di uno strumento in grado di analizzare il codice sorgente di un’applicazione software scritta in java ed effettuare un’analisi di similarità tra i metodi utilizzando l’algoritmo della distanza di Levenshtein per verificare la presenza di eventuali parti di codice ridondanti all'interno dell'applicativo.
Per l’analisi del codice si è fatto uso della libreria Javaparser.

# Diagramma UML delle classi

![Image](https://github.com/Luca683/CodeCloneRefactoring/blob/main/Assets/UML%20delle%20classi.jpeg)

# Guida all'uso

Premessa: nel progetto è stato utilizzato Maven come strumento di gestione delle dipendenze e di compilazione del codice Java. In questo modo è stato possibile specificare le dipendenze dalle varie librerie utilizzate nel file di configurazione 'pom.xml'.


Per eseguire il programma:
```shell
mvn exec:java
```

Per eseguire i test:
```shell
mvn test
```