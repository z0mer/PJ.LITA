# 🇮🇹 **Compilatore LITA (Linguaggio Italiano)** 🇮🇹

## 🎯 **Descrição do Projeto**

Benvenuti! O **Compilatore LITA** é um compilador para uma linguagem de programação única: **LITA (Linguaggio Italiano)**. Inspirada na musicalidade e na elegância da língua italiana, LITA oferece uma forma divertida, charmosa e funcional de programar — perfeita tanto para iniciantes quanto para experientes que querem explorar algo novo.

Este compilador traduz código LITA para **C++**, passando por todas as etapas clássicas da construção de compiladores:

* Análise Léxica
* Análise Sintática
* Análise Semântica
* Geração de Código
  
<br>

## ✨ **Funcionalidades Principais**

* **Análise Léxica:** Reconhecimento de tokens (palavras-chave, identificadores, números, strings, operadores).
* **Análise Sintática:** Validação da estrutura do código conforme a gramática da linguagem.
* **Análise Semântica:** Verificação de coerência (tipos, escopo, atribuições).
* **Geração de Código:** Tradução para código **C++ funcional**.
* **Sintaxe em Italiano:** Palavras-chave e operadores inspirados na língua italiana.

<br>

## 📘 **Guia da Linguagem LITA**

### 🧩 Palavras-chave

| C++                | LITA (Italiano) | Uso                      |
| ------------------ | --------------- | ------------------------ |
| `main`             | `programma`     | Início do programa       |
| ---                | `fineprog`      | Fim do programa          |
| ---                | `var`           | Declaração de variável   |
| `const`            | `cost`          | Constante                |
| `int`              | `intero`        | Tipo inteiro             |
| `float` / `double` | `decimale`      | Tipo decimal             |
| `string`           | `testo`         | Tipo string              |
| `bool`             | `booleano`      | Tipo booleano            |
| `cin`              | `leggi`         | Entrada (input)          |
| `cout`             | `scrivi`        | Saída (output)           |
| `if`               | `se`            | Condicional 'if'         |
| ---                | `allora`        | Início do bloco 'if'     |
| `else`             | `altrimenti`    | Bloco 'else'             |
| ---                | `finese`        | Fim do bloco condicional |
| `while`            | `mentre`        | Laço 'while'             |
| ---                | `fai`           | Início do laço 'while'   |
| ---                | `finementre`    | Fim do laço 'while'      |
| `for`              | `per`           | Laço 'for'               |
| ---                | `fineper`       | Fim do laço 'for'        |
| `return`           | `ritorna`       | Retorno de função        |
| `break`            | `interrompi`    | Interromper laço         |
| `true`             | `vero`          | Valor booleano true      |
| `false`            | `falso`         | Valor booleano false     |
| `=`                | `assegna`       | Atribuição               |
| `&&`               | `e`             | Operador lógico AND      |
| `!`                | `non`           | Operador lógico NOT      |

---

### ➕ Operadores

| C++  | LITA             | Significado    |
| ---- | ---------------- | -------------- |
| `+`  | `piu`            | Soma           |
| `-`  | `meno`           | Subtração      |
| `*`  | `moltiplica`     | Multiplicação  |
| `/`  | `diviso`         | Divisão        |
| `:=` | `assegna`        | Atribuição     |
| `==` | `uguale`         | Igualdade      |
| `!=` | `diverso`        | Diferença      |
| `<`  | `minore`         | Menor que      |
| `>`  | `maggiore`       | Maior que      |
| `<=` | `minoreuguale`   | Menor ou igual |
| `>=` | `maggioreuguale` | Maior ou igual |
| `%`  | `%`              | Módulo         |

<br>

## 🏗️ **Estrutura Geral de um Programa LITA**

```lita
programma nome_do_programma
    // Declarações de variáveis e constantes
    var nome_variavel: tipo.
    cost nome_constante: tipo assegna valor.

    // Bloco de comandos
    leggi(nome_variavel).
    scrivi("Algum texto", nome_variavel).

    se (condizione) allora
        // comandos
    altrimenti
        // comandos
    finese.

    mentre (condizione) fai
        // comandos
    finementre.

    per (var i: intero assegna 0. i minore 10. i assegna i piu 1) fai
        // comandos
    fineper.

    ritorna valore. // opcional
fineprog
```

**Observação:** Declarações e comandos geralmente terminam com um ponto `.` como finalizador de instrução.

<br>

## 🧪 **Exemplo de Código**

### Código LITA (arquivo `.cod`)

```lita
programma primo_verifier
    var numero: intero.
    var eh_primo: booleano assegna vero.
    var i: intero assegna 2.

    scrivi("Digite um numero:").
    leggi(numero).

    se (numero minoreuguale 1) allora
        eh_primo assegna falso.
    altrimenti
        mentre (i minoreuguale (numero diviso 2)) fai
            se ((numero % i) uguale 0) allora
                eh_primo assegna falso.
                interrompi.
            finese.
            i assegna i piu 1.
        finementre.
    finese.

    se (eh_primo) allora
        scrivi(numero, " e primo!").
    altrimenti
        scrivi(numero, " non e primo!").
    finese.
fineprog
```

### Código C++ Gerado

```cpp
#include <iostream>
using namespace std;

int main() {
    int numero;
    bool eh_primo = true;
    int i = 2;

    cout << "Digite um numero:" << endl;
    cin >> numero;

    if (numero <= 1) {
        eh_primo = false;
    } else {
        while (i <= (numero / 2)) {
            if ((numero % i) == 0) {
                eh_primo = false;
                break;
            }
            i = i + 1;
        }
    }

    if (eh_primo) {
        cout << numero << " e primo!" << endl;
    } else {
        cout << numero << " non e primo!" << endl;
    }

    return 0;
}
```

<br>

## 🛠️ **Como Compilar e Executar o Projeto**

### Pré-requisitos

* **Java JDK 24** (ou ajustável para versões anteriores).
* **NetBeans IDE** instalado.

### Executando no NetBeans

1. Clique com o botão direito no projeto `COMPILADORESPJ` > **Propriedades**.
2. Vá em **Executar** > no campo **Argumentos**, insira:

   ```
   caminho/entrada.cod saida.cpp
   ```

   Exemplo (maneira que utilizamos):

   ```
   src/compiladorespj/exemplo1.cod exemplo_saida.cpp
   ```
3. Clique em **OK**.
4. Primeiro **Clean and Build** o projeto.
5. Execute com:

   * Botão verde ▶️
   * Ou `F6`
   * Ou clique direito > **Executar**

<br>

## 👩‍💻 **Autores**

* Anna Carolina Ribeiro Pires Zomer – RA: 22.224.017-8
* Humberto de Oliveira Pellegrini – RA: 22.224.019-4
