package compiladorespj;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Uso: java -jar SeuCompilador.jar <arquivo_de_entrada.cod> <arquivo_de_saida.cpp>");
            System.err.println("Ou, se executando via IDE/linha de comando direto:");
            System.err.println("java compiladorespj.Main <arquivo_de_entrada.cod> <arquivo_de_saida.cpp>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            String sourceCode = Files.readString(Paths.get(inputFile));

            System.out.println("Iniciando Analise Lexica...");
            Lexer lexer = new Lexer(sourceCode);
            List<Token> tokens = lexer.tokenize();
            System.out.println("Analise Lexica concluida.");

            System.out.println("Iniciando Analise Sintatica...");
            Parser parser = new Parser(tokens);
            AST.Program programAst = parser.parseProgram(); 
            System.out.println("Analise Sintatica concluida.");

            System.out.println("Iniciando Analise Semantica...");
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            semanticAnalyzer.analyze(programAst); 
            System.out.println("Analise Semantica concluida.");

            System.out.println("Iniciando Geracao de Codigo C++...");
            CodeGenerator codeGenerator = new CodeGenerator();
            String cppCode = codeGenerator.generate(programAst); 
            System.out.println("Geracao de Codigo C++ concluida.");

            Files.writeString(Paths.get(outputFile), cppCode);

            System.out.println("\nCompilacao finalizada com sucesso! Codigo C++ gerado em: " + outputFile);

        } catch (Exception e) {
            System.err.println("\nERRO DURANTE A COMPILACAO:");
            e.printStackTrace();
        }
    }
}
