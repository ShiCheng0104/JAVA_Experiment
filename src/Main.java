package hust.cs.javacourse.search;

import hust.cs.javacourse.search.index.*;
import hust.cs.javacourse.search.index.impl.*;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;
import hust.cs.javacourse.search.query.impl.IndexSearcher;
import hust.cs.javacourse.search.query.impl.SimpleSorter;
import hust.cs.javacourse.search.util.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
public class Main {
    
    public static void main(String[] args) {

        System.out.println("这是一个搜索引擎的索引构建和查询程序");
        int flag = 0;
        System.out.println("索引构建和查询程序:");
        System.out.println("1. 构建索引并打印");
        System.out.println("2. 查询索引");
        System.out.println("请输入您的选择（1或2）：");
        Scanner scanner = new Scanner(System.in);
        flag = scanner.nextInt();

        if(flag == 1){
        AbstractDocumentBuilder documentBuilder = new DocumentBuilder();
        AbstractIndexBuilder indexBuilder = new IndexBuilder(documentBuilder);
        String rootDir = Config.DOC_DIR;
        System.out.println("Start build index ...");
        AbstractIndex index = indexBuilder.buildIndex(rootDir);
        index.optimize();
        System.out.println(index); //控制台打印 index 的内容
        //测试保存到文件
        String indexFile1 = Config.INDEX_DIR + "index.dat";
        index.save(new File(indexFile1)); //索引保存到文件
        //测试从文件读取
        AbstractIndex index2 = new Index(); //创建一个空的 index
        index2.load(new File(indexFile1)); //从文件加载对象的内容
        System.out.println("\n-------------------\n");
        }

        else if(flag == 2){
        Sort simpleSorter = new SimpleSorter();
        String indexFile = Config.INDEX_DIR + "index.dat";
        AbstractIndexSearcher searcher = new IndexSearcher();
        searcher.open(indexFile);
        System.out.println("开始查询,输入您想查询的词,按a退出");
        Scanner input=new Scanner(System.in);
        String word=input.nextLine();
        StringSplitter splitter=new StringSplitter();
        splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
        while (!word.equals("a")) {
        
        // 判断输入的单词是否在删除词列表中
        if (Arrays.asList(StopWords.STOP_WORDS).contains(word.toLowerCase())) {
            System.out.println("输入的单词是删除词，请输入其他查询词：");
            word = input.nextLine();
            continue;
        }   
            List<String> term = splitter.splitByRegex(word);
            AbstractHit[] hits;
        
            if (term.size() == 1) {
                // 单个关键词查询
                hits = searcher.search(new Term(word), simpleSorter);
            } else if (term.size() == 3 && term.get(1).equalsIgnoreCase("OR")) {
                // OR 查询
                hits = searcher.search(new Term(term.get(0)), new Term(term.get(2)), simpleSorter, AbstractIndexSearcher.LogicalCombination.OR);
            } else if (term.size() == 3 && term.get(1).equalsIgnoreCase("AND")) {
                // AND 查询
                hits = searcher.search(new Term(term.get(0)), new Term(term.get(2)), simpleSorter, AbstractIndexSearcher.LogicalCombination.AND);
            } else {
                // 输入格式错误处理
                System.out.println("输入格式错误，请输入单词或使用 'word1 AND word2' 或 'word1 OR word2' 格式");
                word = input.nextLine();
                continue;
            }
        
            boolean found = false;
        
            for (AbstractHit hit : hits) {
                if (hit.getDocId() == -1) {
                    continue;
                }
                found = true;
                System.out.println("\ndocID: " + hit.getDocId());
                System.out.println("doc scores: " + hit.getScore()); // 输出文档得分
                System.out.println(hit);
            }
        
            if (!found) {
                System.out.println("找不到该单词,请检查后重新输入");
            }
            System.out.println("查询结束，请输入下一个查询词，或输入 'a' 退出：");
            word = input.nextLine();
    }

    }
    else {
        System.out.println("输入错误，请输入1或2");
        }
    }

}

