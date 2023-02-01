import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Client {
    static BufferedReader read;
    static PrintWriter prt;


    public static void main(String[] args) throws IOException, InterruptedException {


        try {
            // 서버 연결
            System.out.println("서버에 접속중입니다.");
            Socket clientSocket = new Socket("localhost", 8080);
            System.out.println("서버와 연결 완료.");
            System.out.print("이름을 입력해주세요: ");
            Scanner sc = new Scanner(System.in);
            String name = sc.nextLine();

            /*보내는 sender 쓰레드
            * 사용자에게 입력을 받아서 해당 메세지를 스트림에 보낸다.
            * 만약 'quit'라면 해당 메세지를 스트림에 올린 후 쓰레드를 끝낸다. */

            Thread s_Thread = new Thread() {
                    @Override
                    public void run() {
                    try {
                        OutputStream out = clientSocket.getOutputStream();
                        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")), true);
                        writer.println(name); //이름을 출력해줌
                        while (true) {
                            String msg = sc.nextLine();
                            if (msg.equals("quit")) {
                                writer.println(msg);
                                break;
                            }
                            writer.println(name + ": " + msg);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            s_Thread.start();

            /* 받는 reciever 쓰레드
             * 버퍼에 올려진 메세지를 받아 해당 클라이언트의 채팅창에 출력시킨다.
             * 마찬가지로 메세지가 quit이면 더이상 메세지를 읽지 않는다.
             */

            Thread r_Thread = new Thread() {
                @Override
                public void run() {
                    try{
                        InputStream input;//읽는 stream
                        BufferedReader read;// input 내용을 buffer로 받아옴
                        while(true){
                            String msg = null;
                            input = clientSocket.getInputStream();
                            read = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                            if ((msg = read.readLine())!=null){
                                if (msg.equals("quit")){
                                    break;}

                                System.out.println(msg);

                            }


                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            r_Thread.start();

            s_Thread.join();
            r_Thread.join();

            //쓰레드의 종료 순서가 엇갈리지 않도록 join을 통해 종료시킨다.

            clientSocket.close();





        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}