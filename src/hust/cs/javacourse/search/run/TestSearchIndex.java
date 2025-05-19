package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;
import hust.cs.javacourse.search.query.impl.IndexSearcher;
import hust.cs.javacourse.search.query.impl.SimpleSorter;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StopWords;
import hust.cs.javacourse.search.util.StringSplitter;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;
/**
 * 测试搜索
 */
public class TestSearchIndex {
    /**
     *  搜索程序入口
     * @param args ：命令行参数
     */
    public static void main(String[] args){
        Sort simpleSorter = new SimpleSorter();
        String indexFile = Config.INDEX_DIR + "index.dat";
        AbstractIndexSearcher searcher = new IndexSearcher();
        searcher.open(indexFile);
        System.out.println("开始查询,查询格式如下:");
        System.out.println("单个搜索关键词");
        System.out.println("两个关键词的与查询 ( 格式：Word1 AND Word2 )");
        System.out.println("两个关键词的或查询 ( 格式：Word1 OR Word2 )");
        System.out.println("两个单词的短语检索 ( 格式：Word1 Word2 )");
        System.out.println("输入exit退出查询");
        System.out.print("请输入需要查询的单词: ");
        Scanner input=new Scanner(System.in);
        String word=input.nextLine();
        StringSplitter splitter=new StringSplitter();
        splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
        while (!word.equals("exit")) {
        
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
            
            if(hits == null || hits.length == 0) {
                System.out.println("没有找到相关文档");
                System.out.println("查询结束，请输入下一个查询词，或输入 'exit' 退出：");
                word = input.nextLine();
                continue;
            }

            for (AbstractHit hit : hits) {
                if (hit.getDocId() == -1) {
                    continue;
                }
                System.out.println("\n文档ID: " + hit.getDocId());
                System.out.println("文档得分: " + hit.getScore()); // 输出文档得分
                System.out.println(hit);
            }

            System.out.println("查询结束，请输入下一个查询词，或输入 'exit' 退出：");
            word = input.nextLine();
    }
}
}

