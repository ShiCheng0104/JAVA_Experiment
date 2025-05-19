import java.util.Scanner;
import hust.cs.javacourse.search.run.*;
public class Main {
    public static void main(String[] args) {
        
        int flag = 0;
        Scanner sc = new Scanner(System.in);
        while(true)
        {
            
            System.out.println("这是一个基于内存的搜索引擎");
            System.out.println("输入1:进行倒排索引的构造");
            System.out.println("输入2:进行搜索功能");
            System.out.println("输入0:退出程序");
            System.out.print("请输入你的选择: ");
            flag = sc.nextInt();

            if(flag == 1)
            {
                TestBuildIndex.main(args);
            }
            else if(flag == 2)
            {
                TestSearchIndex.main(args);
            }
            else if(flag == 0)
            {
                System.out.println("退出程序");
                break;
            }
            else
            {
                System.out.println("输入错误，请重新输入");
            }
        }
        sc.close();
    }
}
